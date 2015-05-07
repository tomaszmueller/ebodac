(function () {
    'use strict';

    /* App Module */
    var ebodac = angular.module('ebodac', ['motech-dashboard', 'ebodac.controllers', 'ebodac.directives',
    'ebodac.services', 'ngCookies' , 'ui.bootstrap', 'mds']), subjectId;

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/Subject',
        success:  function(data) {
            subjectId = data.id;
        },
        async: false
    });

    ebodac.config(function ($routeProvider) {
        $routeProvider
            .when('/ebodac/registration', { redirectTo: '/mds/dataBrowser/' + subjectId + '/ebodac' })
    });

}());
