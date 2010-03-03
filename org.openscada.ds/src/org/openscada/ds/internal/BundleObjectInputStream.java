package org.openscada.ds.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.osgi.framework.Bundle;

/**
 * An object input stream which loads its classes from the provided bundle.
 * @author Jens Reimann
 *
 */
public class BundleObjectInputStream extends ObjectInputStream
{
    private final Bundle bundle;

    public BundleObjectInputStream ( final InputStream in, final Bundle bundle ) throws IOException
    {
        super ( in );
        this.bundle = bundle;
    }

    @Override
    protected Class<?> resolveClass ( final ObjectStreamClass desc ) throws IOException, ClassNotFoundException
    {
        return this.bundle.loadClass ( desc.getName () );
    }

}
