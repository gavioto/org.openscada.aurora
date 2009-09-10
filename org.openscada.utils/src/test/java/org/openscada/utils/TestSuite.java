package org.openscada.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openscada.utils.concurrent.ListeningFutureTest;
import org.openscada.utils.exec.AsyncOperationTest;
import org.openscada.utils.exec.SyncOperationTest;
import org.openscada.utils.str.EncoderTest;

@RunWith ( Suite.class )
@Suite.SuiteClasses ( { ListeningFutureTest.class, AsyncOperationTest.class, EncoderTest.class, SyncOperationTest.class } )
public class TestSuite
{
}
