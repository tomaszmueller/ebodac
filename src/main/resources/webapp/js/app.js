(function () {
    'use strict';

    /* App Module */

    var ebodac = angular.module('ebodac', ['motech-dashboard', 'ebodac.controllers', 'ebodac.directives',
    'ebodac.services', 'ngCookies' , 'ui.bootstrap']);

    ebodac.config(function ($routeProvider) {
            $routeProvider.
                when('/ebodac/registration', {templateUrl: '../ebodac/resources/partials/registration-form.html', controller: 'EbodacController'});
    });
}());
