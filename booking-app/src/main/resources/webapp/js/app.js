(function () {
    'use strict';

    /* App Module */
    var bookingApp = angular.module('bookingApp', ['bookingApp.controllers', 'bookingApp.services',
        'bookingApp.directives', 'motech-dashboard', 'data-services', 'ui.directives']), clinicId;

    $.ajax({
        url: '../mds/entities/getEntity/Booking App/Clinic',
        success:  function(data) {
            clinicId = data.id;
        },
        async: false
    });

    $.ajax({
            url: '../booking-app/available/bookingTabs',
            success:  function(data) {
                bookingApp.constant('BOOKING_AVAILABLE_TABS', data);
            },
            async:    false
        });

    bookingApp.run(function ($rootScope, BOOKING_AVAILABLE_TABS) {
            $rootScope.BOOKING_AVAILABLE_TABS = BOOKING_AVAILABLE_TABS;
        });

    bookingApp.config(function ($routeProvider, BOOKING_AVAILABLE_TABS) {

        var i, tab;

        for (i = 0; i < BOOKING_AVAILABLE_TABS.length; i = i + 1) {

            tab = BOOKING_AVAILABLE_TABS[i];

            if (tab === "visitLimitation") {
                $routeProvider.when('/bookingApp/{0}'.format(tab), {redirectTo: '/mds/dataBrowser/' + clinicId + '/booking-app'});
            } else if (tab === "reports") {
                $routeProvider
                    .when('/bookingApp/{0}'.format(tab),
                        {
                            templateUrl: '../booking-app/resources/partials/{0}.html'.format(tab)
                        }
                    )
                    .when('/bookingApp/reports/capacityReport',
                        {
                            templateUrl: '../booking-app/resources/partials/capacityReport.html',
                            controller: 'BookingApp{0}Ctrl'.format(tab.capitalize())
                        }
                    );
            } else {
                $routeProvider.when('/bookingApp/{0}'.format(tab),
                    {
                        templateUrl: '../booking-app/resources/partials/{0}.html'.format(tab),
                        controller: 'BookingApp{0}Ctrl'.format(tab.capitalize())
                    }
                );
            }
        }

        $routeProvider
            .when('/bookingApp/settings', {templateUrl: '../booking-app/resources/partials/settings.html', controller: 'BookingAppSettingsCtrl'})
            .when('/bookingApp/welcomeTab', { redirectTo: '/bookingApp/' + BOOKING_AVAILABLE_TABS[0] });

    });
}());
