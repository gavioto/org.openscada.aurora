package org.openscada.ds.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ClassLoaderObjectInputStream extends ObjectInputStream
{
    private final ClassLoader classLoader;

    public ClassLoaderObjectInputStream ( final InputStream in, final ClassLoader classLoader ) throws IOException
    {
        super ( in );
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> resolveClass ( final ObjectStreamClass desc ) throws IOException, ClassNotFoundException
    {
        return this.classLoader.loadClass ( desc.getName () );
    }

}
