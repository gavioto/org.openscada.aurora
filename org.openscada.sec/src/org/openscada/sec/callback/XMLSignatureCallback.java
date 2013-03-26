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
public class XMLSignatureCallback extends AbstractCallback
{

    public static final String TYPE = "xmlsignature";

    public static final String PROP_DOCUMENT = "document";

    public static final String PROP_SIGNED_DOCUMENT = "signedDocument";

    public static final String PROP_MAXIMUM_REMEMBER_PERIOD = "maximumRememberPeriod";

    private String document;

    private String signedDocument;

    private int maximumRememberPeriod;

    public XMLSignatureCallback ()
    {
    }

    public XMLSignatureCallback ( final String document )
    {
        this.document = document;
    }

    public String getDocument ()
    {
        return this.document;
    }

    public String getSignedDocument ()
    {
        return this.signedDocument;
    }

    public void setSignedDocument ( final String signedDocument )
    {
        this.signedDocument = signedDocument;
    }

    public void setDocument ( final String document )
    {
        this.document = document;
    }

    @Override
    protected void injectRequestAttributes ( final Map<String, String> request )
    {
        super.injectRequestAttributes ( request );
        request.put ( PROP_DOCUMENT, this.document );
        request.put ( PROP_MAXIMUM_REMEMBER_PERIOD, "" + this.maximumRememberPeriod );
    }

    @Override
    public void parseRequestAttributes ( final Map<String, String> attributes )
    {
        super.parseRequestAttributes ( attributes );
        this.document = attributes.get ( PROP_DOCUMENT );
        this.maximumRememberPeriod = parseInteger ( attributes, PROP_MAXIMUM_REMEMBER_PERIOD, 5 * 1000 * 60 /* 5 minutes */);
    }

    @Override
    protected void injectResponseAttributes ( final Map<String, String> response )
    {
        super.injectResponseAttributes ( response );
        if ( this.signedDocument != null )
        {
            response.put ( PROP_SIGNED_DOCUMENT, this.signedDocument );
        }
    }

    @Override
    public void parseResponseAttributes ( final Map<String, String> attributes )
    {
        super.parseResponseAttributes ( attributes );
        this.signedDocument = attributes.get ( PROP_SIGNED_DOCUMENT );
    }

    @Override
    public String getType ()
    {
        return TYPE;
    }

    public void setMaximumRememberPeriod ( final int maximumRememberPeriod )
    {
        this.maximumRememberPeriod = maximumRememberPeriod;
    }

    public int getMaximumRememberPeriod ()
    {
        return this.maximumRememberPeriod;
    }

}
