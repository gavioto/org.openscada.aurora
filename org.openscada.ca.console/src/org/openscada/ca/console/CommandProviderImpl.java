package org.openscada.ca.console;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.openscada.ca.FreezableConfigurationAdministrator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class CommandProviderImpl implements CommandProvider
{

    private ServiceRegistration reg;

    private ServiceTracker caTracker;

    public CommandProviderImpl ()
    {
    }

    public void start ( final BundleContext context )
    {
        this.caTracker = new ServiceTracker ( context, FreezableConfigurationAdministrator.class.getName (), null );
        this.caTracker.open ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        this.reg = context.registerService ( CommandProvider.class.getName (), this, properties );
    }

    public void stop ( final BundleContext context )
    {
        this.caTracker.close ();
        this.caTracker = null;

        this.reg.unregister ();
        this.reg = null;
    }

    public String getHelp ()
    {
        final StringBuilder sb = new StringBuilder ();
        sb.append ( "\n---Configuration Administrator Commands---\n" ); //$NON-NLS-1$
        sb.append ( "\tfreezeCfgAdmin - stop announcing changes\n" ); //$NON-NLS-1$
        sb.append ( "\tthawCfgAdmin - start announcing changes\n" ); //$NON-NLS-1$
        return sb.toString ();
    }

    public void _freezeCfgAdmin ( final CommandInterpreter cmd )
    {
        final Object[] services = this.caTracker.getServices ();
        final int count = services != null ? services.length : 0;
        cmd.print ( String.format ( "Freezing %s configuration administrators", count ) );
        if ( services != null )
        {
            for ( final Object o : services )
            {
                if ( o instanceof FreezableConfigurationAdministrator )
                {
                    cmd.println ( String.format ( "Freeze: {}", o ) );
                    try
                    {
                        ( (FreezableConfigurationAdministrator)o ).freeze ();
                    }
                    catch ( final Exception e )
                    {
                        cmd.println ( "Failed to freeze" );
                        cmd.printStackTrace ( e );
                    }
                }
            }
        }
    }

    public void _thawCfgAdmin ( final CommandInterpreter cmd )
    {
        final Object[] services = this.caTracker.getServices ();
        final int count = services != null ? services.length : 0;
        cmd.print ( String.format ( "Thawing %s configuration administrators", count ) );
        if ( services != null )
        {
            for ( final Object o : services )
            {
                if ( o instanceof FreezableConfigurationAdministrator )
                {
                    cmd.println ( String.format ( "Thaw: {}", o ) );
                    try
                    {
                        ( (FreezableConfigurationAdministrator)o ).thaw ();
                    }
                    catch ( final Exception e )
                    {
                        cmd.println ( "Failed to thaw" );
                        cmd.printStackTrace ( e );
                    }
                }
            }
        }
    }

}
