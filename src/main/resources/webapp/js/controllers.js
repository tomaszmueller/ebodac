(function() {
    'use strict';

    /* Controllers */
    var controllers = angular.module('ebodac.controllers', []);

    $.postJSON = function(url, data, callback) {
        return jQuery.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'type': 'POST',
            'url': url,
            'data': JSON.stringify(data),
            'dataType': 'json',
            'success': callback,
        });
    };

    /*
     *
     * Settings
     *
     */
    controllers.controller('EbodacSettingsCtrl', function ($scope, $http, $timeout) {
        $scope.errors = [];
        $scope.messages = [];

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $http.get('../ebodac/ebodac-config')
            .success(function(response){
                var i;
                $scope.config = response;
                $scope.originalConfig = angular.copy($scope.config);
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.settings.noConfig', response));
            });

        $scope.reset = function () {
            $scope.config = angular.copy($scope.originalConfig);
        };

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        }

        $scope.submit = function () {
            $http.post('../ebodac/ebodac-config', $scope.config)
                .success(function (response) {
                    $scope.config = response;
                    $scope.originalConfig = angular.copy($scope.config);
                    var index = $scope.messages.push($scope.msg('ebodac.web.settings.saved'));
                    hideMsgLater(index-1);
                })
                .error (function (response) {
                    //todo: better than that!
                    handleWithStackTrace('ebodac.error.header', 'ebodac.error.body', response);
                });
        };
    });

    /*
     *
     * Reports
     *
     */
    controllers.controller('EbodacReportsCtrl', function ($scope) {
        $scope.lookupBy = {};
        $scope.selectedLookup = undefined;
        $scope.lookupFields = [];
        $scope.lookups = [{"lookupName" : "Find Visit By Date", "fields" : [{"name" : "Date", "type" : "dateTime"}]},
                          {"lookupName" : "Find Visit By Type", "fields" : [{"name" : "Type", "type" : "list",
                         "values" : ["Screening", "Prime Vaccination Day", "Prime Vaccination Follow-up visit", "Boost Vaccination Day",
                         "Boost Vaccination First Follow-up visit", "Boost Vaccination Second Follow-up visit", "Boost Vaccination Third Follow-up visit",
                         "First Long-term Follow-up visit", "Second Long-term Follow-up visit", "Third Long-term Follow-up visit", "Unscheduled Visit(n)"]}]},
                         {"lookupName" : "Find Visit By SubjectId", "fields" : [{"name" : "SubjectId", "type" : "string"}]},
                         {"lookupName" : "Find Visit By Subject Name", "fields" : [{"name" : "Name", "type" : "string"}]},
                         {"lookupName" : "Find Visit By Subject Address", "fields" : [{"name" : "Address", "type" : "string"}]}];

        /**
        * Shows/Hides lookup dialog
        */
        $scope.showLookupDialog = function() {
            $("#lookup-dialog")
            .css({'top': ($("#lookupDialogButton").offset().top - $("#main-content").offset().top)-40,
            'left': ($("#lookupDialogButton").offset().left - $("#main-content").offset().left)-70})
            .toggle();
        };

        /**
        * Marks passed lookup as selected. Sets fields that belong to the given lookup and resets lookupBy object
        * used to filter instances by given values
        */
        $scope.selectLookup = function(lookup) {
            $scope.selectedLookup = lookup;
            $scope.lookupFields = lookup.fields;
            $scope.lookupBy = {};
        };

        /**
        * Removes lookup and resets all fields associated with a lookup
        */
        $scope.removeLookup = function() {
            $scope.lookupBy = {};
            $scope.selectedLookup = undefined;
            $scope.lookupFields = [];
            $scope.filterInstancesByLookup();
        };

        /**
        * Hides lookup dialog and sends signal to refresh the grid with new data
        */
        $scope.filterInstancesByLookup = function() {
            $scope.showLookupDialog();
            $scope.refreshGrid();
        };

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        /**
        * Depending on the field type, includes proper html file containing visual representation for
        * the object type. Radio input for boolean, select input for list and text input as default one.
        */
        $scope.loadInputForLookupField = function(field) {
            var value = "default", type = "field";

            if (field.type === "boolean") {
                value = "boolean";
            } else if (field.type === "list") {
                value = "list";
            } else if (field.type === "dateTime" || field.type === "date") {
                value = "datetime";
            } else if (field.type === "localDate") {
                value = "date";
            }

            return '../ebodac/resources/partials/lookups/{0}-{1}.html'
                .format(type, value);
        };

        $scope.backToEntityList = function() {
            window.location.replace('#/ebodac/reports');
        };
    });

}());
