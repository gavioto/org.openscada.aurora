/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.utils.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A wrapper to execute scripts
 * 
 * @author Jens Reimann
 * @since 0.17.0
 */
public class ScriptExecutor
{
    private final ScriptEngine engine;

    private final String command;

    private CompiledScript compiledScript;

    private final ClassLoader classLoader;

    private final URL commandUrl;

    public ScriptExecutor ( final ScriptEngineManager engineManager, final String engineName, final String command, final ClassLoader classLoader ) throws ScriptException
    {
        this ( engineName == null ? null : engineManager.getEngineByName ( engineName ), engineName == null ? null : command, classLoader );
    }

    public ScriptExecutor ( final ScriptEngineManager engineManager, final String engineName, final URL commandUrl, final ClassLoader classLoader ) throws ScriptException, IOException
    {
        this ( engineName == null ? null : engineManager.getEngineByName ( engineName ), engineName == null ? null : commandUrl, classLoader );
    }

    /**
     * Construct a new script executors
     * 
     * @param engine
     *            the script engine to use, must not be <code>null</code>
     * @param command
     *            the command to execute, may be <code>null</code>
     * @param classLoader
     *            the class loader to use when executing, may be <code>null</code>
     * @throws ScriptException
     */
    public ScriptExecutor ( final ScriptEngine engine, final String command, final ClassLoader classLoader ) throws ScriptException
    {
        this.engine = engine;
        this.command = command;
        this.commandUrl = null;
        this.classLoader = classLoader;

        if ( command != null && engine instanceof Compilable && !Boolean.getBoolean ( "org.openscada.ScriptExecutor.disableCompile" ) )
        {
            final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
            try
            {
                if ( classLoader != null )
                {
                    Thread.currentThread ().setContextClassLoader ( classLoader );
                }
                this.compiledScript = ( (Compilable)engine ).compile ( command );
            }
            finally
            {
                Thread.currentThread ().setContextClassLoader ( currentClassLoader );
            }
        }
    }

    public ScriptExecutor ( final ScriptEngine engine, final URL commandUrl, final ClassLoader classLoader ) throws ScriptException, IOException
    {
        this.engine = engine;
        this.command = null;
        this.commandUrl = commandUrl;
        this.classLoader = classLoader;

        if ( commandUrl != null && engine instanceof Compilable && !Boolean.getBoolean ( "org.openscada.ScriptExecutor.disableCompile" ) )
        {
            final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
            try
            {
                if ( classLoader != null )
                {
                    Thread.currentThread ().setContextClassLoader ( classLoader );
                }
                this.compiledScript = ( (Compilable)engine ).compile ( new InputStreamReader ( commandUrl.openStream () ) );
            }
            finally
            {
                Thread.currentThread ().setContextClassLoader ( currentClassLoader );
            }
        }
    }

    protected Map<String, Object> applyVars ( final ScriptContext context, final Map<String, Object> scriptObjects )
    {
        if ( scriptObjects == null || scriptObjects.isEmpty () )
        {
            return null;
        }

        final Map<String, Object> replaced = new HashMap<String, Object> ();
        for ( final Map.Entry<String, Object> entry : scriptObjects.entrySet () )
        {
            final Object original = context.getAttribute ( entry.getKey (), ScriptContext.ENGINE_SCOPE );
            replaced.put ( entry.getKey (), original );
            context.setAttribute ( entry.getKey (), entry.getValue (), ScriptContext.ENGINE_SCOPE );
        }
        return replaced;
    }

    protected void restoreVars ( final ScriptContext context, final Map<String, Object> vars )
    {
        if ( vars == null )
        {
            return;
        }

        for ( final Map.Entry<String, Object> entry : vars.entrySet () )
        {
            if ( entry.getValue () == null )
            {
                context.removeAttribute ( entry.getKey (), ScriptContext.ENGINE_SCOPE );
            }
            else
            {
                context.setAttribute ( entry.getKey (), entry.getValue (), ScriptContext.ENGINE_SCOPE );
            }
        }
    }

    private Object executeScript ( final ScriptContext scriptContext, final Map<String, Object> scriptObjects ) throws ScriptException, IOException
    {
        Map<String, Object> vars = null;
        try
        {
            vars = applyVars ( scriptContext, scriptObjects );

            if ( this.compiledScript != null )
            {
                return this.compiledScript.eval ( scriptContext );
            }
            else if ( this.command != null )
            {
                return this.engine.eval ( this.command, scriptContext );
            }
            else if ( this.commandUrl != null )
            {
                return this.engine.eval ( new InputStreamReader ( this.commandUrl.openStream () ) );
            }
            else
            {
                return null;
            }
        }
        finally
        {
            restoreVars ( scriptContext, vars );
        }
    }

    public Object execute ( final ScriptContext scriptContext ) throws ScriptException, IOException
    {
        return execute ( scriptContext, null );
    }

    public Object execute ( final ScriptContext scriptContext, final Map<String, Object> scriptObjects ) throws ScriptException, IOException
    {
        final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
        try
        {
            if ( this.classLoader != null )
            {
                Thread.currentThread ().setContextClassLoader ( this.classLoader );
            }
            return executeScript ( scriptContext, scriptObjects );
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( currentClassLoader );
        }
    }
}
