/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A wrapper to execute scripts
 * @author Jens Reimann
 * @sicne 0.17.0
 *
 */
public class ScriptExecutor
{
    private final ScriptEngine engine;

    private final String command;

    private CompiledScript compiledScript;

    private final ClassLoader classLoader;

    public ScriptExecutor ( final ScriptEngineManager engineManager, final String engineName, final String command, final ClassLoader classLoader ) throws ScriptException
    {
        this ( engineName == null ? null : engineManager.getEngineByName ( engineName ), engineName == null ? null : command, classLoader );
    }

    /**
     * Construct a new script executors
     * @param engine the script engine to use, must not be <code>null</code>
     * @param command the command to execute, may be <code>null</code>
     * @param classLoader the class loader to use when executing, may be <code>null</code>
     * @throws ScriptException
     */
    public ScriptExecutor ( final ScriptEngine engine, final String command, final ClassLoader classLoader ) throws ScriptException
    {
        this.engine = engine;
        this.command = command;
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

    private Object executeScript ( final ScriptContext scriptContext ) throws ScriptException
    {
        if ( this.compiledScript != null )
        {
            return this.compiledScript.eval ( scriptContext );
        }
        else if ( this.command != null )
        {
            return this.engine.eval ( this.command, scriptContext );
        }
        else
        {
            return null;
        }
    }

    public Object execute ( final ScriptContext scriptContext ) throws ScriptException
    {
        final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
        try
        {
            if ( this.classLoader != null )
            {
                Thread.currentThread ().setContextClassLoader ( this.classLoader );
            }
            return executeScript ( scriptContext );
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( currentClassLoader );
        }
    }
}
