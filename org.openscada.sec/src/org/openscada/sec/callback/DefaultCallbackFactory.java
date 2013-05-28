/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec.callback;

import java.util.Map;

/**
 * @since 1.1
 */
public class DefaultCallbackFactory implements CallbackFactory
{

    @Override
    public Callback createCallback ( final String type, final Map<String, String> attributes )
    {
        if ( UserNameCallback.TYPE.equals ( type ) )
        {
            return applyAttributes ( new UserNameCallback (), attributes );
        }
        if ( TextCallback.TYPE.equals ( type ) )
        {
            return applyAttributes ( new TextCallback (), attributes );
        }
        else if ( PasswordCallback.TYPE.equals ( type ) )
        {
            return applyAttributes ( new PasswordCallback (), attributes );
        }
        else if ( ConfirmationCallback.TYPE.equals ( type ) )
        {
            return applyAttributes ( new ConfirmationCallback (), attributes );
        }
        else if ( XMLSignatureCallback.TYPE.equals ( type ) )
        {
            return applyAttributes ( new XMLSignatureCallback (), attributes );
        }
        return null;
    }

    private Callback applyAttributes ( final Callback callback, final Map<String, String> attributes )
    {
        if ( callback != null )
        {
            callback.parseRequestAttributes ( attributes );
        }
        return callback;
    }
}
