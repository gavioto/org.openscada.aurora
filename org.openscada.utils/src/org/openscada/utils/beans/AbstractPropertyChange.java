/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * An abstract base class for property change support. Derive your class from this one if you
 * would like to add default property change support
 * @author Jens Reimann
 *
 */
public class AbstractPropertyChange
{

    private transient final PropertyChangeSupport listeners = new PropertyChangeSupport ( this );

    public void addPropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.listeners.addPropertyChangeListener ( listener );
    }

    public void removePropertyChangeListener ( final PropertyChangeListener listener )
    {
        this.listeners.removePropertyChangeListener ( listener );
    }

    public void addPropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.listeners.addPropertyChangeListener ( propertyName, listener );
    }

    public void removePropertyChangeListener ( final String propertyName, final PropertyChangeListener listener )
    {
        this.listeners.removePropertyChangeListener ( propertyName, listener );
    }

    protected void fireIndexedPropertyChange ( final String propertyName, final int index, final boolean oldValue, final boolean newValue )
    {
        this.listeners.fireIndexedPropertyChange ( propertyName, index, oldValue, newValue );
    }

    protected void fireIndexedPropertyChange ( final String propertyName, final int index, final int oldValue, final int newValue )
    {
        this.listeners.fireIndexedPropertyChange ( propertyName, index, oldValue, newValue );
    }

    protected void fireIndexedPropertyChange ( final String propertyName, final int index, final Object oldValue, final Object newValue )
    {
        this.listeners.fireIndexedPropertyChange ( propertyName, index, oldValue, newValue );
    }

    protected void firePropertyChange ( final PropertyChangeEvent evt )
    {
        this.listeners.firePropertyChange ( evt );
    }

    protected void firePropertyChange ( final String propertyName, final boolean oldValue, final boolean newValue )
    {
        this.listeners.firePropertyChange ( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange ( final String propertyName, final int oldValue, final int newValue )
    {
        this.listeners.firePropertyChange ( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange ( final String propertyName, final Object oldValue, final Object newValue )
    {
        this.listeners.firePropertyChange ( propertyName, oldValue, newValue );
    }

}