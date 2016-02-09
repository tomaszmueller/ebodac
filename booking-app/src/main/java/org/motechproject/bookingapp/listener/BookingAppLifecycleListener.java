package org.motechproject.bookingapp.listener;


import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.mds.annotations.InstanceLifecycleListener;
import org.motechproject.mds.annotations.InstanceLifecycleListenerType;

public interface BookingAppLifecycleListener {

    @InstanceLifecycleListener(value = InstanceLifecycleListenerType.POST_CREATE)
    void createVisitBookingDetails(Visit visit);

    @InstanceLifecycleListener(value = InstanceLifecycleListenerType.POST_CREATE)
    void addClinicToVisitBookingDetails(Clinic clinic);
}
