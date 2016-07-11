(function () {
    'use strict';

    if(!$('#chartJs').length) {
        var s = document.createElement("script");
        s.id = "chartJs";
        s.type = "text/javascript";
        s.src = "../ebodac/resources/js/Chart.js";
        $("head").append(s);
    };

    if(!$('#angularChartJs').length) {
        var s = document.createElement("script");
        s.id = "angularChartJs";
        s.type = "text/javascript";
        s.src = "../ebodac/resources/js/angular-chart.js";
        $("head").append(s);
    };

    if(!$('#jsPdf').length) {
        var s = document.createElement("script");
        s.id = "jsPdf";
        s.type = "text/javascript";
        s.src = "../ebodac/resources/js/jspdf.min.js";
        $("head").append(s);
    };

    /* App Module */
    var ebodac = angular.module('ebodac', ['motech-dashboard', 'ebodac.controllers', 'ebodac.directives',
    'ebodac.services', 'ngCookies' , 'ui.bootstrap', 'sms', 'chart.js']), subjectId, visitId,
    reportBoosterVaccinationId, reportPrimerVaccinationId, callDetailRecordId, smsRecordId;

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/Participant',
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

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/ReportPrimerVaccination',
        success:  function(data) {
            reportPrimerVaccinationId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/EBODAC Module/ReportBoosterVaccination',
        success:  function(data) {
            reportBoosterVaccinationId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/IVR Module/CallDetailRecord',
        success:  function(data) {
            callDetailRecordId = data.id;
        },
        async: false
    });

    $.ajax({
        url: '../mds/entities/getEntity/SMS Module/SmsRecord',
        success:  function(data) {
            smsRecordId = data.id;
        },
        async: false
    });

    $.ajax({
            url: '../ebodac/available/ebodacTabs',
            success:  function(data) {
                ebodac.constant('EBODAC_AVAILABLE_TABS', data);
            },
            async:    false
        });

    ebodac.run(function ($rootScope, EBODAC_AVAILABLE_TABS) {
            $rootScope.EBODAC_AVAILABLE_TABS = EBODAC_AVAILABLE_TABS;
        });

    ebodac.config(function ($routeProvider, EBODAC_AVAILABLE_TABS) {

        var i, tab;

        for (i = 0; i < EBODAC_AVAILABLE_TABS.length; i = i + 1) {

            tab = EBODAC_AVAILABLE_TABS[i];

            if (tab === "subjects") {
                $routeProvider.when('/ebodac/subjects', {
                                                            templateUrl: '../ebodac/resources/partials/ebodacInstances.html',
                                                            controller: 'MdsDataBrowserCtrl',
                                                            resolve: {
                                                                        entityId: function ($route) { $route.current.params.entityId = subjectId; },
                                                                        moduleName: function ($route) { $route.current.params.moduleName = 'ebodac'; }
                                                                     }
                                                        });
            } else if (tab === "visits") {
                $routeProvider.when('/ebodac/visits', {
                                                            templateUrl: '../ebodac/resources/partials/ebodacInstances.html',
                                                            controller: 'MdsDataBrowserCtrl',
                                                            resolve: {
                                                                        entityId: function ($route) { $route.current.params.entityId = visitId; },
                                                                        moduleName: function ($route) { $route.current.params.moduleName = 'ebodac'; }
                                                                     }
                                                       });
            } else if (tab === "reports") {
                $routeProvider
                    .when('/ebodac/reports', {templateUrl: '../ebodac/resources/partials/reports.html', controller: 'EbodacBasicCtrl' })
                    .when('/ebodac/reportPrimerVaccination', { redirectTo: '/mds/dataBrowser/' + reportPrimerVaccinationId + '/ebodac' })
                    .when('/ebodac/reportBoosterVaccination', { redirectTo: '/mds/dataBrowser/' + reportBoosterVaccinationId + '/ebodac' })
                    .when('/ebodac/reports/:reportType', { templateUrl: '../ebodac/resources/partials/report.html', controller: 'EbodacReportsCtrl' })
                    .when('/ebodac/callDetailRecord', { redirectTo: '/mds/dataBrowser/' + callDetailRecordId + '/ebodac' })
                    .when('/ebodac/SMSLog', { redirectTo: '/mds/dataBrowser/' + smsRecordId + '/ebodac' });
            } else if (tab === "enrollment") {
                $routeProvider
                    .when('/ebodac/enrollment', {templateUrl: '../ebodac/resources/partials/enrollment.html', controller: 'EbodacEnrollmentCtrl'})
                    .when('/ebodac/enrollmentAdvanced/:subjectId', {templateUrl: '../ebodac/resources/partials/enrollmentAdvanced.html', controller: 'EbodacEnrollmentAdvancedCtrl'});
            } else if (tab === "statistics") {
                $routeProvider
                    .when('/ebodac/statistics', {templateUrl: '../ebodac/resources/partials/statistics.html', controller: 'EbodacBasicCtrl' })
                    .when('/ebodac/statistics/tables/:tableType', {templateUrl: '../ebodac/resources/partials/statisticsTables.html', controller: 'EbodacStatisticsCtrl' })
                    .when('/ebodac/statistics/graphs/:graphType', {templateUrl: '../ebodac/resources/partials/statisticsGraphs.html', controller: 'EbodacStatisticsCtrl' })
                    .when('/ebodac/statistics/ivrEngagement', {templateUrl: '../ebodac/resources/partials/ivrEngagement.html', controller: 'EbodacStatisticsCtrl' })
            } else {
                $routeProvider.when('/ebodac/{0}'.format(tab),
                    {
                        templateUrl: '../ebodac/resources/partials/{0}.html'.format(tab),
                        controller: 'Ebodac{0}Ctrl'.format(tab.capitalize())
                    }
                );
            }
        }

        $routeProvider
            .when('/ebodac/settings', {templateUrl: '../ebodac/resources/partials/settings.html', controller: 'EbodacSettingsCtrl'})
            .when('/ebodac/welcomeTab', { redirectTo: '/ebodac/' + EBODAC_AVAILABLE_TABS[0] });

    });

}());
