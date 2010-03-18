/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2009-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.sec.provider.script;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.UserInformation;
import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public class ScriptAuthorizationProvider implements AuthorizationService, ConfigurationFactory
{

    private static final class PriorityComparator implements Comparator<AuthorizationEntry>
    {
        @Override
        public int compare ( final AuthorizationEntry o1, final AuthorizationEntry o2 )
        {
            final int thisVal = o1.getPriority ();
            final int anotherVal = o2.getPriority ();
            return thisVal < anotherVal ? -1 : thisVal == anotherVal ? 0 : 1;
        }
    }

    private static class AuthorizationEntry
    {
        private final int priority;

        private final String id;

        private String script;

        private ScriptEngine engine;

        private CompiledScript compiledScript;

        private Pattern objectId;

        private Pattern objectType;

        private Pattern action;

        public AuthorizationEntry ( final String id, final int priority )
        {
            this.id = id;
            this.priority = priority;
        }

        public String getId ()
        {
            return this.id;
        }

        public int getPriority ()
        {
            return this.priority;
        }

        public void setPreFilter ( final String idFilter, final String typeFilter, final String actionFilter )
        {
            if ( idFilter != null )
            {
                this.objectId = Pattern.compile ( idFilter );
            }
            if ( typeFilter != null )
            {
                this.objectType = Pattern.compile ( typeFilter );
            }
            if ( actionFilter != null )
            {
                this.action = Pattern.compile ( actionFilter );
            }
        }

        public void setScript ( final ScriptEngine engine, final String script ) throws ScriptException
        {
            this.engine = engine;
            if ( engine instanceof Compilable )
            {
                this.compiledScript = ( (Compilable)engine ).compile ( script );
            }
            else
            {
                this.script = script;
            }
        }

        public AuthorizationResult run ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final Map<String, Object> context ) throws ScriptException
        {
            if ( this.objectId != null && !this.objectId.matcher ( objectId ).matches () )
            {
                return null;
            }

            if ( this.objectType != null && !this.objectType.matcher ( objectType ).matches () )
            {
                return null;
            }

            if ( this.action != null && !this.action.matcher ( action ).matches () )
            {
                return null;
            }

            final Bindings bindings = this.engine.createBindings ();

            bindings.put ( "id", objectId );
            bindings.put ( "type", objectType );
            bindings.put ( "action", action );
            bindings.put ( "user", userInformation );
            bindings.put ( "GRANTED", AuthorizationResult.GRANTED );
            bindings.put ( "context", context );

            if ( this.compiledScript != null )
            {
                return generateResult ( this.compiledScript.eval ( bindings ) );
            }
            else
            {
                return generateResult ( this.engine.eval ( this.script, bindings ) );
            }
        }

        private AuthorizationResult generateResult ( final Object eval )
        {
            if ( eval == null )
            {
                return null;
            }

            // boolean return
            if ( eval instanceof Boolean )
            {
                if ( (Boolean)eval )
                {
                    return AuthorizationResult.GRANTED;
                }
                else
                {
                    return AuthorizationResult.create ( new StatusCode ( "OSSEC", "SCRIPT", 1, SeverityLevel.ERROR ), "Request rejected" );
                }
            }

            // numeric return
            if ( eval instanceof Number )
            {
                if ( ( (Number)eval ).longValue () == 0 )
                {
                    return AuthorizationResult.GRANTED;
                }
                else
                {
                    return AuthorizationResult.create ( new StatusCode ( "OSSEC", "SCRIPT", 2, SeverityLevel.ERROR ), String.format ( "Request rejected (%s)", eval ) );
                }
            }

            // string return
            if ( eval instanceof String )
            {
                if ( ( (String)eval ).length () == 0 )
                {
                    return AuthorizationResult.GRANTED;
                }
                else
                {
                    return AuthorizationResult.create ( new StatusCode ( "OSSEC", "SCRIPT", 3, SeverityLevel.ERROR ), String.format ( eval.toString (), eval ) );
                }
            }

            if ( eval instanceof StatusCode )
            {
                return AuthorizationResult.create ( (StatusCode)eval, "Request rejected" );
            }

            if ( eval instanceof Throwable )
            {
                return AuthorizationResult.create ( (Throwable)eval );
            }

            if ( eval instanceof Result )
            {
                final Result result = (Result)eval;
                return AuthorizationResult.create ( result.getCode (), result.getMessage () );
            }

            // no more known results
            return AuthorizationResult.create ( new StatusCode ( "OSSEC", "SCRIPT", 4, SeverityLevel.ERROR ), String.format ( "Request rejected - unknown result type: %s", eval ) );
        }

    }

    private final Collection<AuthorizationEntry> configuration = new PriorityQueue<AuthorizationEntry> ( 1, new PriorityComparator () );

    private final Lock readLock;

    private final Lock writeLock;

    private final ScriptEngineManager manager;

    private final ClassLoader classLoader;

    public ScriptAuthorizationProvider ()
    {
        final ReadWriteLock lock = new ReentrantReadWriteLock ();
        this.readLock = lock.readLock ();
        this.writeLock = lock.writeLock ();

        this.classLoader = getClass ().getClassLoader ();

        final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
        try
        {
            Thread.currentThread ().setContextClassLoader ( this.classLoader );
            this.manager = new ScriptEngineManager ( this.classLoader );
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( currentClassLoader );
        }
    }

    @Override
    public AuthorizationResult authorize ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        try
        {
            this.readLock.lock ();

            for ( final AuthorizationEntry entry : this.configuration )
            {
                final AuthorizationResult result = entry.run ( objectId, objectType, action, userInformation, context );
                if ( result != null )
                {
                    return result;
                }
            }
        }
        catch ( final Throwable e )
        {
            return AuthorizationResult.create ( e );
        }
        finally
        {
            this.readLock.unlock ();
        }

        // default is: no result
        return null;
    }

    @Override
    public void delete ( final String configurationId ) throws Exception
    {
        try
        {
            this.writeLock.lock ();
            internalDelete ( configurationId );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    private void internalDelete ( final String configurationId )
    {
        for ( final Iterator<AuthorizationEntry> i = this.configuration.iterator (); i.hasNext (); )
        {
            final AuthorizationEntry entry = i.next ();
            if ( entry.getId ().equals ( configurationId ) )
            {
                i.remove ();
            }
        }
    }

    @Override
    public void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        final AuthorizationEntry entry = createEntry ( configurationId, new ConfigurationDataHelper ( properties ) );
        try
        {
            this.writeLock.lock ();
            internalDelete ( configurationId );
            this.configuration.add ( entry );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    private AuthorizationEntry createEntry ( final String id, final ConfigurationDataHelper cfg ) throws Exception
    {
        final AuthorizationEntry entry = new AuthorizationEntry ( id, cfg.getIntegerChecked ( "priority", "'priority' must be set" ) );

        final ScriptEngine engine = this.manager.getEngineByName ( cfg.getString ( "engine", "JavaScript" ) );
        if ( engine == null )
        {
            throw new IllegalArgumentException ( String.format ( "Script engine '%s' is unknown", engine ) );
        }

        entry.setPreFilter ( cfg.getString ( "for.id" ), cfg.getString ( "for.type" ), cfg.getString ( "for.action" ) );
        entry.setScript ( engine, cfg.getString ( "script" ) );

        return entry;
    }
}