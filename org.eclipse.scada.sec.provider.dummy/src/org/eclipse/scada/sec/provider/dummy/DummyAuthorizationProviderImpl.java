package org.eclipse.scada.sec.provider.dummy;

import org.eclipse.scada.sec.AuthorizationReply;
import org.eclipse.scada.sec.AuthorizationResult;
import org.eclipse.scada.sec.authz.AuthorizationContext;
import org.eclipse.scada.sec.osgi.AuthorizationManager;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;

public class DummyAuthorizationProviderImpl implements AuthorizationManager
{

    @Override
    public NotifyFuture<AuthorizationReply> authorize ( final AuthorizationContext context, final AuthorizationResult defaultResult )
    {
        return new InstantFuture<AuthorizationReply> ( AuthorizationReply.createGranted ( context ) );
    }

}
