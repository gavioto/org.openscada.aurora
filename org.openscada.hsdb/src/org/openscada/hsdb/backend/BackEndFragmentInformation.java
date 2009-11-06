package org.openscada.hsdb.backend;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.hsdb.calculation.CalculationMethod;

public class BackEndFragmentInformation<B extends BackEnd> implements Comparable<BackEndFragmentInformation<?>>
{
    private String configurationId;

    private long detailLevelId;

    private CalculationMethod calculationMethod;

    private long startTime;

    private long endTime;

    private boolean isCorrupt;

    private String fragmentName;

    private B backEndFragment;

    private ReentrantReadWriteLock lock;

    public String getConfigurationId ()
    {
        return configurationId;
    }

    public void setConfigurationId ( final String configurationId )
    {
        this.configurationId = configurationId;
    }

    public long getDetailLevelId ()
    {
        return detailLevelId;
    }

    public void setDetailLevelId ( final long detailLevelId )
    {
        this.detailLevelId = detailLevelId;
    }

    public CalculationMethod getCalculationMethod ()
    {
        return calculationMethod;
    }

    public void setCalculationMethod ( final CalculationMethod calculationMethod )
    {
        this.calculationMethod = calculationMethod;
    }

    public long getStartTime ()
    {
        return startTime;
    }

    public void setStartTime ( final long startTime )
    {
        this.startTime = startTime;
    }

    public long getEndTime ()
    {
        return endTime;
    }

    public void setEndTime ( final long endTime )
    {
        this.endTime = endTime;
    }

    public boolean getIsCorrupt ()
    {
        return isCorrupt;
    }

    public void setIsCorrupt ( final boolean isCorrupt )
    {
        this.isCorrupt = isCorrupt;
    }

    public String getFragmentName ()
    {
        return fragmentName;
    }

    public void setFragmentName ( final String fragmentName )
    {
        this.fragmentName = fragmentName;
    }

    public B getBackEndFragment ()
    {
        return backEndFragment;
    }

    public void setBackEndFragment ( final B backEndFragment )
    {
        this.backEndFragment = backEndFragment;
    }

    public ReentrantReadWriteLock getLock ()
    {
        return lock;
    }

    public void setLock ( final ReentrantReadWriteLock lock )
    {
        this.lock = lock;
    }

    public int compareTo ( final BackEndFragmentInformation<?> o )
    {
        final long otherStartTime = o.getStartTime ();
        if ( startTime < otherStartTime )
        {
            return 1;
        }
        if ( startTime > otherStartTime )
        {
            return -1;
        }
        return 0;
    }
}
