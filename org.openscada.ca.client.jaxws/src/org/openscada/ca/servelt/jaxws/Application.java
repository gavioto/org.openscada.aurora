package org.openscada.ca.servelt.jaxws;

import java.net.MalformedURLException;

import org.openscada.ca.servelt.jaxws.Configuration;
import org.openscada.ca.servelt.jaxws.Factory;
import org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator;

public class Application
{

    public static void main ( final String[] args ) throws MalformedURLException
    {
        final RemoteConfigurationClient client = new RemoteConfigurationClient ( "localhost", 9999 );

        final RemoteConfigurationAdministrator port = client.getPort ();

        System.out.println ( "HasService: " + port.hasService () );

        System.out.println ( "Start request" );

        for ( final Factory factory : port.getFactories () )
        {
            System.out.println ( String.format ( "Factory: %s", factory.getId () ) );
            final Factory data = port.getFactory ( factory.getId () );
            for ( final Configuration configuration : data.getConfigurations () )
            {
                System.out.println ( configuration.getId () + " -> " + configuration.getData () );
            }
        }

        System.out.println ( "End request" );
    }
}
