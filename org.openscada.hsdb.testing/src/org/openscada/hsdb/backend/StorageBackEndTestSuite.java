package org.openscada.hsdb.backend;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class can be used as test entry point to perform all available tests for service classes.
 * @author Ludwig Straub
 */
@RunWith ( Suite.class )
@Suite.SuiteClasses ( { FileBackEndTest.class, FileBackEndMultiplexorTest.class } )
public class StorageBackEndTestSuite
{
}
