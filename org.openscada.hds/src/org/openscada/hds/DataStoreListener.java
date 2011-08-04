package org.openscada.hds;

import java.util.Date;

public interface DataStoreListener
{
    public void storeChanged ( Date start, Date end );
}
