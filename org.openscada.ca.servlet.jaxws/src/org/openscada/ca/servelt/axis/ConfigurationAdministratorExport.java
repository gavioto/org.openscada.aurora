package org.openscada.ca.servelt.axis;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface ConfigurationAdministratorExport
{
    @WebMethod
    public boolean hasService ();
}
