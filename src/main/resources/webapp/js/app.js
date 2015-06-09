(function () {
    'use strict';

    /* App Module */
    var ebodac = angular.module('ebodac', ['motech-dashboard', 'ebodac.controllers', 'ebodac.directives',
    'ebodac.services', 'ngCookies' , 'ui.bootstrap', 'mds']), subjectId, visitId;

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/Subject',
        success:  function(data) {
            subjectId = data.id;
        },
        async: false
    });
    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/Visit',
        success:  function(data) {
            visitId = data.id;
        },
        async: false
    });

    ebodac.config(function ($routeProvider) {
        $routeProvider
            .when('/ebodac/subjects', { redirectTo: '/mds/dataBrowser/' + subjectId + '/ebodac' })
            .when('/ebodac/visits', { redirectTo: '/mds/dataBrowser/' + visitId + '/ebodac' })
            .when('/ebodac/settings', {templateUrl: '../ebodac/resources/partials/settings.html', controller: 'EbodacSettingsCtrl'});
    });

}());
