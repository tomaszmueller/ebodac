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
     * Lookups
     *
     */
    controllers.controller('EbodacLookupsCtrl', function ($scope, $http) {
        $scope.lookupBy = {};
        $scope.selectedLookup = undefined;
        $scope.lookupFields = [];

        $scope.getLookups = function(url) {
            $http.get(url)
            .success(function(data) {
                        $scope.lookups = data;
            });
        }

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
            $scope.lookupFields = lookup.lookupFields;
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

        $scope.buildLookupFieldName = function (field) {
            if (field.relatedName !== undefined && field.relatedName !== '' && field.relatedName !== null) {
                return field.name + "." + field.relatedName;
            }
            return field.name;
        };

        /**
        * Depending on the field type, includes proper html file containing visual representation for
        * the object type. Radio input for boolean, select input for list and text input as default one.
        */
        $scope.loadInputForLookupField = function(field) {
            var value = "default", type = "field";

            if (field.className === "java.lang.Boolean") {
                value = "boolean";
            } else if (field.className === "java.util.Collection") {
                value = "list";
            } else if (field.className === "org.joda.time.DateTime" || field.className === "java.util.Date") {
                value = "datetime";
            } else if (field.className === "org.joda.time.LocalDate") {
                value = "date";
            }

            if ($scope.isRangedLookup(field)) {
                type = "range";
                value = "date";
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = {min: '', max: ''};
                }
            }

            return '../ebodac/resources/partials/lookups/{0}-{1}.html'
                .format(type, value);
        };

        $scope.isRangedLookup = function(field) {
            return $scope.isLookupFieldOfType(field, 'RANGE');
        };

        $scope.isLookupFieldOfType = function(field, type) {
            var i, lookupField;
            for (i = 0; i < $scope.selectedLookup.lookupFields.length; i += 1) {
                lookupField = $scope.selectedLookup.lookupFields[i];
                if ($scope.buildLookupFieldName(lookupField) === $scope.buildLookupFieldName(field)) {
                    return lookupField.type === type;
                }
            }
        };
    });

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

        $scope.availableCampaigns = [];

        $scope.campaignsChanged = function(change) {
            var value;

            if (change.added) {
                value = change.added.text;
                $scope.config.disconVacCampaignsList.push(value);
            } else if (change.removed) {
                value = change.removed.text;
                $scope.config.disconVacCampaignsList.removeObject(value);
            }
        };

        $http.get('../ebodac/ebodac-config')
            .success(function(response){
                var i;
                $scope.config = response;
                $scope.originalConfig = angular.copy($scope.config);

                $http.get('../ebodac/availableCampaigns')
                    .success(function(response){
                        $scope.availableCampaigns = response;
                        $timeout(function() {
                            $('#disconVacCampaigns').select2('val', $scope.config.disconVacCampaignsList);
                        }, 50);

                    })
                    .error(function(response) {
                        $scope.errors.push($scope.msg('ebodac.web.settings.enroll.disconVacCampaigns.error', response));
                    });
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.settings.noConfig', response));
            });

        $scope.reset = function () {
            $scope.config = angular.copy($scope.originalConfig);
            $('#disconVacCampaigns').select2('val', $scope.config.disconVacCampaignsList);
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
    controllers.controller('EbodacReportsCtrl', function ($scope, $http, $controller) {

        $controller('EbodacLookupsCtrl', {$scope: $scope});
        var url = "../ebodac/getLookupsForDailyClinicVisitScheduleReport";
        $scope.getLookups(url);

        $scope.availableExportRecords = ['All','10', '25', '50', '100', '250'];
        $scope.availableExportFormats = ['csv','pdf'];
        $scope.actualExportRecords = 'All';
        $scope.actualExportColumns = 'All';
        $scope.exportFormat = 'csv';
        $scope.checkboxModel = {
            exportWithLookup : false,
            exportWithOrder : false
        };

        $scope.exportEntityInstances = function () {
            $('#exportInstanceModal').modal('show');
        };

        $scope.changeExportRecords = function (records) {
            $scope.actualExportRecords = records;
        };

        $scope.changeExportFormat = function (format) {
            $scope.exportFormat = format;
        };

        $scope.closeExportInstanceModal = function () {
            $('#exportInstanceForm').resetForm();
            $('#exportInstanceModal').modal('hide');
        };

        /**
        * Exports selected entity's instances to CSV file
        */
        $scope.exportInstance = function() {
            var url, rows, page, sortColumn, sortDirection;

            url = "../ebodac/exportDailyClinicVisitScheduleReport";
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

           if ($scope.checkboxModel.exportWithOrder === true) {
               sortColumn = $('#dailyClinicVisitScheduleReportTable').getGridParam('sortname');
               sortDirection = $('#dailyClinicVisitScheduleReportTable').getGridParam('sortorder');

               url = url + "&sortColumn=" + sortColumn;
               url = url + "&sortDirection=" + sortDirection;
           }

           if ($scope.checkboxModel.exportWithLookup === true) {
               url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
               url = url + "&fields=" + JSON.stringify($scope.lookupBy);
           }

            $http.get(url)
            .success(function () {
                $('#exportInstanceForm').resetForm();
                $('#exportInstanceModal').modal('hide');
                window.location.replace(url);
            })
            .error(function (response) {
                handleResponse('mds.error', 'mds.error.exportData', response);
            });
        };

        $scope.backToEntityList = function() {
            window.location.replace('#/ebodac/reports');
        };
    });

    /*
     *
     * Enrollment
     *
     */
    controllers.controller('EbodacEnrollmentCtrl', function ($scope, $http, $timeout, $controller) {

        $controller('EbodacLookupsCtrl', {$scope: $scope});
        var url = "../ebodac/getLookupsForEnrollments";
        $scope.getLookups(url);

        $scope.errors = [];
        $scope.messages = [];

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        }

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.enroll = function(subjectId) {
            $http.post('../ebodac/enrollSubject', subjectId)
            .success(function(response) {
                var index = $scope.messages.push($scope.msg('ebodac.web.enrollment.enrollSubject.success'));
                hideMsgLater(index-1);
                $scope.refreshGrid();
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.enrollment.enrollSubject.error', response));
                $scope.refreshGrid();
            });
        }

        $scope.unenroll = function(subjectId) {
            $http.post('../ebodac/unenrollSubject', subjectId)
            .success(function(response) {
                var index = $scope.messages.push($scope.msg('ebodac.web.enrollment.unenrollSubject.success'));
                hideMsgLater(index-1);
                $scope.refreshGrid();
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.enrollment.unenrollSubject.error', response));
                $scope.refreshGrid();
            });
        }

        $scope.goToAdvanced = function(subjectId) {
            window.location.replace('#/ebodac/enrollmentAdvanced/' + subjectId);
        }
    });

    /*
     *
     * Enrollment Advanced
     *
     */
    controllers.controller('EbodacEnrollmentAdvancedCtrl', function ($scope, $http, $timeout, $routeParams) {
        $scope.errors = [];
        $scope.messages = [];

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        }

        $scope.backToEnrolments = function() {
            window.location.replace('#/ebodac/enrollment');
        }

        $scope.selectedSubjectId = $routeParams.subjectId;

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.enroll = function(campaignName) {
            $http.get('../ebodac/enrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName)
            .success(function(response) {
                var index = $scope.messages.push($scope.msg('ebodac.web.enrollment.enrollSubject.success'));
                hideMsgLater(index-1);
                $scope.refreshGrid();
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.enrollment.enrollSubject.error', response));
                $scope.refreshGrid();
            });
        }

        $scope.unenroll = function(campaignName) {
            $http.get('../ebodac/unenrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName)
            .success(function(response) {
                var index = $scope.messages.push($scope.msg('ebodac.web.enrollment.unenrollSubject.success'));
                hideMsgLater(index-1);
                $scope.refreshGrid();
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.enrollment.unenrollSubject.error', response));
                $scope.refreshGrid();
            });
        }

        $scope.reenroll = function(campaignName, date) {
            $http.get('../ebodac/reenrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName + '/' + date)
            .success(function(response) {
                var index = $scope.messages.push($scope.msg('ebodac.web.enrollment.reenrollSubject.success'));
                hideMsgLater(index-1);
                $scope.refreshGrid();
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.enrollment.reenrollSubject.error', response));
                $scope.refreshGrid();
            });
        }

        $scope.enrollWithNewDate = function(campaignName, date) {
            $http.get('../ebodac/enrollCampaignWithNewDate/' + $scope.selectedSubjectId + '/' + campaignName + '/' + date)
            .success(function(response) {
                var index = $scope.messages.push($scope.msg('ebodac.web.enrollment.enrollSubject.success'));
                hideMsgLater(index-1);
                $scope.refreshGrid();
            })
            .error(function(response) {
                $scope.errors.push($scope.msg('ebodac.web.enrollment.enrollSubject.error', response));
                $scope.refreshGrid();
            });
        }
    });

}());
