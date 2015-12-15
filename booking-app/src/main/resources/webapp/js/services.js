(function () {
    'use strict';

    /* Services */

    var services = angular.module('bookingApp.services', ['ngResource']);

    services.factory('Screenings', function($resource) {
        return $resource('../booking-app/screenings', {}, {
            'get': {url: '../booking-app/screenings/:id', method: 'GET'}
        });
    });

    services.factory('Clinics', function($resource) {
        return $resource('../booking-app/clinics', {}, {});
    });

    services.factory('ScreenedParticipants', function($resource) {
        return $resource('../booking-app/participants/screened', {}, {});
    });

}());
