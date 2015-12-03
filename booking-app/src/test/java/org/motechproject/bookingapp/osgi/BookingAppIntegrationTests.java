package org.motechproject.bookingapp.osgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Booking App bundle integration tests suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        BookingAppLifecycleListenerIT.class,
})
public class BookingAppIntegrationTests {
}
