package org.openscada.hsdb.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openscada.hsdb.testing.compression.CalculatingStorageChannelTest;

/**
 * This class can be used as test entry point to perform all available tests for service classes.
 * @author Ludwig Straub
 */
@RunWith ( Suite.class )
@Suite.SuiteClasses ( { CalculatingStorageChannelTest.class } )
public class TestSuite
{
}
