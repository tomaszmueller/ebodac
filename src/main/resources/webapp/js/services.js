(function () {
    'use strict';

    /* Services */

    var services = angular.module('ebodac.services', ['ngResource']);

    services.factory('Ebodac', function($resource) {
        return $resource('../ebodac/sayHello');
    });
}());
