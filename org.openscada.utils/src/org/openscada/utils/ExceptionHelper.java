/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHelper
{

    /**
     * Get the root cause of an exception
     * 
     * @param e
     *            the exception to check, must not be <code>null</code>
     * @return the root cause, is never <code>null</code>
     */
    public static Throwable getRootCause ( Throwable e )
    {
        if ( e == null )
        {
            return null;
        }

        while ( e.getCause () != null )
        {
            e = e.getCause ();
        }
        return e;
    }

    /**
     * Get the message of the root cause
     * 
     * @param e
     *            the exception from which the message should be extracted
     * @return the message of the root cause
     * @see StatusHelper#extractMessage(Throwable)
     */
    public static String getMessage ( final Throwable e )
    {
        return extractMessage ( getRootCause ( e ) );
    }

    /**
     * Extract the message from an exception
     * 
     * @param e
     *            the exception to extract the message from
     * @return either the localized message, the message or the class name (in
     *         that order)
     */
    public static String extractMessage ( final Throwable e )
    {
        if ( e == null )
        {
            return null;
        }

        if ( e.getLocalizedMessage () != null )
        {
            return e.getLocalizedMessage ();
        }
        else if ( e.getMessage () != null )
        {
            return e.getMessage ();
        }
        else
        {
            return e.getClass ().getName ();
        }
    }

    /**
     * Format exception as string
     * 
     * @param e
     *            the exception to format, may be <code>null</code>
     * @return the formatted exception or <code>null</code> if the exception was
     *         <code>null</code>.
     */
    public static String formatted ( final Throwable e )
    {
        if ( e == null )
        {
            return null;
        }

        final StringWriter sw = new StringWriter ();
        final PrintWriter pw = new PrintWriter ( sw );

        e.printStackTrace ( pw );

        pw.close ();

        return sw.toString ();
    }
}
