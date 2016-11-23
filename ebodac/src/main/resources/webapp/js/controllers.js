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
     * Basic
     *
     */
    controllers.controller('EbodacBasicCtrl', function ($scope) {
        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });
    });

    /*
     *
     * Messages
     *
     */
    controllers.controller('EbodacMessagesCtrl', function ($scope) {
        $scope.getMessageFromData = function(responseData) {
            var messageCode, messageParams;

            if (responseData && (typeof(responseData) === 'string')) {
                if (responseData.startsWith('key:')) {
                    if (responseData.indexOf('params:') !== -1) {
                       messageCode = responseData.split('\n')[0].split(':')[1];
                       messageParams = responseData.split('\n')[1].split(':')[1].split(',');
                    } else {
                       messageCode = responseData.split(':')[1];
                    }
                } else {
                    messageCode = responseData;
                }
            }

            return $scope.msg(messageCode, messageParams);
        };
    });

    /*
     *
     * Lookups
     *
     */
    controllers.controller('EbodacLookupsCtrl', function ($scope, $http, MDSUtils) {
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
            .css({'top': ($("#lookupDialogButton").offset().top - $("#main-content").offset().top) - 40,
            'left': ($("#lookupDialogButton").offset().left - $("#main-content").offset().left) - 15})
            .toggle();
            $("div.arrow").css({'left': 50});
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
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = {min: '', max: ''};
                }
            } else if ($scope.isSetLookup(field)) {
                type = 'set';
                if (!$scope.lookupBy[$scope.buildLookupFieldName(field)]) {
                    $scope.lookupBy[$scope.buildLookupFieldName(field)] = [];
                }
            }

            return '../ebodac/resources/partials/lookups/{0}-{1}.html'
                .format(type, value);
        };

        $scope.isRangedLookup = function(field) {
            return $scope.isLookupFieldOfType(field, 'RANGE');
        };

        $scope.isSetLookup = function(field) {
            return $scope.isLookupFieldOfType(field, 'SET');
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

        $scope.getComboboxValues = function (settings) {
            var labelValues = MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value, keys = [], key;
            // Check the user supplied flag, if true return string set
            if (MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.allowUserSupplied'}], true).value === true){
                return labelValues;
            } else {
                if (labelValues[0].indexOf(":") === -1) {       // there is no colon, so we are dealing with a string set, not a map
                    return labelValues;
                } else {
                    labelValues =  $scope.getAndSplitComboboxValues(labelValues);
                    for(key in labelValues) {
                        keys.push(key);
                    }
                    return keys;
                }
            }
        };

        $scope.getComboboxDisplayName = function (settings, value) {
            var labelValues = MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.values'}], true).value;
            // Check the user supplied flag, if true return string set
            if (MDSUtils.find(settings, [{field: 'name', value: 'mds.form.label.allowUserSupplied'}], true).value === true){
                return value;
            } else {
                if (labelValues[0].indexOf(":") === -1) { // there is no colon, so we are dealing with a string set, not a map
                    return value;
                } else {
                    labelValues =  $scope.getAndSplitComboboxValues(labelValues);
                    return labelValues[value];
                }
            }

        };

        $scope.getAndSplitComboboxValues = function (labelValues) {
            var doublet, i, map = {};
            for (i = 0; i < labelValues.length; i += 1) {
                doublet = labelValues[i].split(":");
                map[doublet[0]] = doublet[1];
            }
            return map;
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

        $scope.boostRelCampaignsChanged = function(change) {
            var value;

            if (change.added) {
                value = change.added.text;
                $scope.config.boosterRelatedMessages.push(value);
            } else if (change.removed) {
                value = change.removed.text;
                $scope.config.boosterRelatedMessages.removeObject(value);
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
                            $('#boostRelCampaigns').select2('val', $scope.config.boosterRelatedMessages);
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
            $('#boostRelCampaigns').select2('val', $scope.config.boosterRelatedMessages);
        };

        function hideMsgLater(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        }

        $scope.deleteAgeRange = function(index) {
            $scope.config.subjectAgeRangeList.splice(index, 1);
        };

        $scope.addAgeRange = function () {
            var newAgeRange = {
                'minAge': null,
                'maxAge': null,
                'stageId': null
            };

            $scope.config.subjectAgeRangeList.push(newAgeRange);
        };

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
    controllers.controller('EbodacReportsCtrl', function ($scope, $http, $controller, $routeParams) {
        $controller('EbodacLookupsCtrl', {$scope: $scope});
        $scope.reportType = $routeParams.reportType;
        $scope.reportName = "";
        $scope.availableExportRecords = ['All','10', '25', '50', '100', '250'];
        $scope.availableExportFormats = ['csv','pdf','xls'];
        $scope.actualExportRecords = 'All';
        $scope.actualExportColumns = 'All';
        $scope.exportFormat = 'csv';
        $scope.checkboxModel = {
            exportWithLookup : true,
            exportWithOrder : false
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        var url;
        switch($scope.reportType){
            case "dailyClinicVisitScheduleReport":
                url = "../ebodac/getLookupsForDailyClinicVisitScheduleReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.dailyClinicVisitScheduleReport');
                break;
            case "followupsAfterPrimeInjectionReport":
                url = "../ebodac/getLookupsForFollowupsAfterPrimeInjectionReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.followupsAfterPrimeInjectionReport');
                break;
            case "followupsMissedClinicVisitsReport":
                url = "../ebodac/getLookupsForFollowupsMissedClinicVisitsReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.followupsMissedClinicVisitsReport');
                break;
            case "MandEMissedClinicVisitsReport":
                url = "../ebodac/getLookupsForMandEMissedClinicVisitsReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.MandEMissedClinicVisitsReport');
                break;
            case "ivrAndSmsStatisticReport":
                url = "../ebodac/getLookupsForIvrAndSmsStatisticReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.ivrAndSmsStatisticReport');
                break;
            case "optsOutOfMotechMessagesReport":
                url = "../ebodac/getLookupsForOptsOutOfMotechMessagesReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.optsOutOfMotechMessagesReport');
                break;
            case "screeningReport":
                url = "../ebodac/getLookupsForScreeningReport";
                $scope.reportName = $scope.msg('ebodac.web.reports.screeningReport');
                break;
            case "day8AndDay57Report":
                url = "../ebodac/getLookupsForDay8AndDay57Report";
                $scope.reportName = $scope.msg('ebodac.web.reports.day8AndDay57Report');
                break;
        }
         $scope.getLookups(url);

        $scope.buildColumnModel = function (colModel) {
            var newColModel = colModel;
            for (var i in colModel) {
              if(!colModel[i].hasOwnProperty('formatoptions') && colModel[i].hasOwnProperty('formatter')) {
                newColModel[i].formatter = eval("(" + colModel[i].formatter + ")");
              }
            }
            return newColModel;
        };

        $scope.buildColumnNames = function (colNames) {
            var newColNames = colNames;
            for(var i in colNames) {
                newColNames[i] = $scope.msg(colNames[i]);
            }
            return newColNames;
        };

        $scope.exportEntityInstances = function () {
            $scope.checkboxModel.exportWithLookup = true;
            $('#exportEbodacInstanceModal').modal('show');
        };

        $scope.changeExportRecords = function (records) {
            $scope.actualExportRecords = records;
        };

        $scope.changeExportFormat = function (format) {
            $scope.exportFormat = format;
        };

        $scope.closeExportEbodacInstanceModal = function () {
            $('#exportEbodacInstanceForm').resetForm();
            $('#exportEbodacInstanceModal').modal('hide');
        };

        /**
        * Exports selected entity's instances to CSV file
        */
        $scope.exportInstance = function() {
            var url, rows, page, sortColumn, sortDirection;

            switch($scope.reportType){
                case "dailyClinicVisitScheduleReport":
                    url = "../ebodac/exportDailyClinicVisitScheduleReport";
                    break;
                case "followupsAfterPrimeInjectionReport":
                    url = "../ebodac/exportFollowupsAfterPrimeInjectionReport";
                    break;
                case "followupsMissedClinicVisitsReport":
                    url = "../ebodac/exportFollowupsMissedClinicVisitsReport";
                    break;
                case "MandEMissedClinicVisitsReport":
                    url = "../ebodac/exportMandEMissedClinicVisitsReport";
                    break;
                case "optsOutOfMotechMessagesReport":
                    url = "../ebodac/exportOptsOutOfMotechMessagesReport";
                    break;
                case "ivrAndSmsStatisticReport":
                    url = "../ebodac/exportIvrAndSmsStatisticReport";
                    break;
                case "screeningReport":
                    url = "../ebodac/exportScreeningReport";
                    break;
                case "day8AndDay57Report":
                    url = "../ebodac/exportDay8AndDay57Report";
                    break;
            }
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

           if ($scope.checkboxModel.exportWithOrder === true) {
               sortColumn = $('#reportTable').getGridParam('sortname');
               sortDirection = $('#reportTable').getGridParam('sortorder');

               url = url + "&sortColumn=" + sortColumn;
               url = url + "&sortDirection=" + sortDirection;
           }

           if ($scope.selectedLookup !== undefined && $scope.checkboxModel.exportWithLookup === true) {
               url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
               url = url + "&fields=" + encodeURIComponent(JSON.stringify($scope.lookupBy));
           }

            $http.get(url)
            .success(function () {
                $('#exportEbodacInstanceForm').resetForm();
                $('#exportEbodacInstanceModal').modal('hide');
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

        $controller('EbodacMessagesCtrl', {$scope: $scope});
        $controller('EbodacLookupsCtrl', {$scope: $scope});

        var url = "../ebodac/getLookupsForEnrollments";
        $scope.getLookups(url);

        $scope.enrollInProgress = false;

        $scope.availableExportRecords = ['All','10', '25', '50', '100', '250'];
        $scope.availableExportFormats = ['csv','pdf','xls'];
        $scope.actualExportRecords = 'All';
        $scope.actualExportColumns = 'All';
        $scope.exportFormat = 'csv';
        $scope.checkboxModel = {
            exportWithLookup : true,
            exportWithOrder : false
        };

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.refreshGridAndStayOnSamePage = function() {
            $scope.gridRefresh = !$scope.gridRefresh;
        }

        $scope.enroll = function(subjectId) {
            motechConfirm("ebodac.enrollSubject.ConfirmMsg", "ebodac.enrollSubject.ConfirmTitle",
                function (response) {
                    if (!response) {
                        return;
                    } else {
                        $scope.enrollInProgress = true;
                        $http.post('../ebodac/enrollSubject', subjectId)
                        .success(function(response) {
                            motechAlert('ebodac.web.enrollment.enrollSubject.success', 'ebodac.web.enrollment.enrolledSubject');
                            $scope.refreshGridAndStayOnSamePage();
                            $scope.enrollInProgress = false;
                        })
                        .error(function(response) {
                            motechAlert('ebodac.web.enrollment.enrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                            $scope.refreshGridAndStayOnSamePage();
                            $scope.enrollInProgress = false;
                        });
                    }
                });
        }

        $scope.unenroll = function(subjectId) {
            motechConfirm("ebodac.unenrollSubject.ConfirmMsg", "ebodac.unenrollSubject.ConfirmTitle",
                function (response) {
                    if (!response) {
                        return;
                    } else {
                        $scope.enrollInProgress = true;
                        $http.post('../ebodac/unenrollSubject', subjectId)
                        .success(function(response) {
                            motechAlert('ebodac.web.enrollment.unenrollSubject.success', 'ebodac.web.enrollment.unenrolledSubject');
                            $scope.refreshGridAndStayOnSamePage();
                            $scope.enrollInProgress = false;
                        })
                        .error(function(response) {
                            motechAlert('ebodac.web.enrollment.unenrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                            $scope.refreshGridAndStayOnSamePage();
                            $scope.enrollInProgress = false;
                        });
                    }
                });
        }

        $scope.goToAdvanced = function(subjectId) {
            $.ajax({
                url: '../ebodac/checkAdvancedPermissions',
                success:  function(data) {
                    window.location.replace('#/ebodac/enrollmentAdvanced/' + subjectId);
                },
                async: false
            });
        }

        $scope.exportEntityInstances = function () {
            $scope.checkboxModel.exportWithLookup = true;
            $('#exportEbodacInstanceModal').modal('show');
        };

        $scope.changeExportRecords = function (records) {
            $scope.actualExportRecords = records;
        };

        $scope.changeExportFormat = function (format) {
            $scope.exportFormat = format;
        };

        $scope.closeExportEbodacInstanceModal = function () {
            $('#exportEbodacInstanceForm').resetForm();
            $('#exportEbodacInstanceModal').modal('hide');
        };

        $scope.exportInstance = function() {
            var url, rows, page, sortColumn, sortDirection;

            url = "../ebodac/exportSubjectEnrollment";
            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&exportRecords=" + $scope.actualExportRecords;

            if ($scope.checkboxModel.exportWithOrder === true) {
                sortColumn = $('#enrollmentTable').getGridParam('sortname');
                sortDirection = $('#enrollmentTable').getGridParam('sortorder');

                url = url + "&sortColumn=" + sortColumn;
                url = url + "&sortDirection=" + sortDirection;
            }

            if ($scope.selectedLookup !== undefined && $scope.checkboxModel.exportWithLookup === true) {
                url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
                url = url + "&fields=" + encodeURIComponent(JSON.stringify($scope.lookupBy));
            }

            $http.get(url)
            .success(function () {
                $('#exportEbodacInstanceForm').resetForm();
                $('#exportEbodacInstanceModal').modal('hide');
                window.location.replace(url);
            })
            .error(function (response) {
                handleResponse('mds.error', 'mds.error.exportData', response);
            });
        };
    });

    /*
     *
     * Enrollment Advanced
     *
     */
    controllers.controller('EbodacEnrollmentAdvancedCtrl', function ($scope, $http, $timeout, $routeParams, $controller) {

        $controller('EbodacMessagesCtrl', {$scope: $scope});

        $scope.enrollInProgress = false;

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.backToEnrolments = function() {
            window.location.replace('#/ebodac/enrollment');
        }

        $scope.selectedSubjectId = $routeParams.subjectId;

        $scope.refreshGrid = function() {
            $scope.lookupRefresh = !$scope.lookupRefresh;
        };

        $scope.enroll = function(campaignName) {
            $scope.enrollInProgress = true;
            $http.get('../ebodac/enrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName)
            .success(function(response) {
                motechAlert('ebodac.web.enrollment.enrollSubject.success', 'ebodac.web.enrollment.enrolledSubject');
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            })
            .error(function(response) {
                motechAlert('ebodac.web.enrollment.enrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            });
        }

        $scope.unenroll = function(campaignName) {
            $scope.enrollInProgress = true;
            $http.get('../ebodac/unenrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName)
            .success(function(response) {
                motechAlert('ebodac.web.enrollment.unenrollSubject.success', 'ebodac.web.enrollment.unenrolledSubject');
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            })
            .error(function(response) {
                motechAlert('ebodac.web.enrollment.unenrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            });
        }

        $scope.reenroll = function(campaignName, date) {
            $scope.enrollInProgress = true;
            $http.get('../ebodac/reenrollCampaign/' + $scope.selectedSubjectId + '/' + campaignName + '/' + date)
            .success(function(response) {
                motechAlert('ebodac.web.enrollment.reenrollSubject.success', 'ebodac.web.enrollment.reenrolledSubject');
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            })
            .error(function(response) {
                motechAlert('ebodac.web.enrollment.reenrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            });
        }

        $scope.enrollWithNewDate = function(campaignName, date) {
            $scope.enrollInProgress = true;
            $http.get('../ebodac/enrollCampaignWithNewDate/' + $scope.selectedSubjectId + '/' + campaignName + '/' + date)
            .success(function(response) {
                motechAlert('ebodac.web.enrollment.enrollSubject.success', 'ebodac.web.enrollment.enrolledSubject');
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            })
            .error(function(response) {
                motechAlert('ebodac.web.enrollment.enrollSubject.error', 'ebodac.web.enrollment.error', $scope.getMessageFromData(response));
                $scope.refreshGrid();
                $scope.enrollInProgress = false;
            });
        }
    });

    /*
     *
     * Statistics
     *
     */
    controllers.controller('EbodacStatisticsCtrl', function ($scope, $http, $routeParams, $controller, $filter, $timeout) {

        $controller('EbodacMessagesCtrl', {$scope: $scope});

        innerLayout({
            spacing_closed: 30,
            east__minSize: 200,
            east__maxSize: 350
        });

        $scope.availableExportFormats = ['csv','pdf','xls'];
        $scope.exportFormat = 'csv';

        $scope.filters = [{
            name: $scope.msg('ebodac.screening.yesterday'),
            dateFilter: "YESTERDAY"
        },{
            name: $scope.msg('ebodac.screening.lastSevenDays'),
            dateFilter: "LAST_7_DAYS"
        },{
            name: $scope.msg('ebodac.screening.lastWeek'),
            dateFilter: "LAST_WEEK"
        },{
            name: $scope.msg('ebodac.screening.lastThirtyDays'),
            dateFilter: "LAST_30_DAYS"
        },{
            name: $scope.msg('ebodac.screening.lastMonth'),
            dateFilter: "LAST_MONTH"
        },{
            name: $scope.msg('ebodac.screening.dateRange'),
            dateFilter: "DATE_RANGE"
        }];

        $scope.selectedFilter = $scope.filters[0];

        $scope.selectFilter = function(value) {
            $scope.selectedFilter = $scope.filters[value];
            if (value !== 5) {
                $scope.reloadData();
            }
        };

        $scope.reloadData = function() {
            $scope.loadData();
        }

        $scope.backToStatistics = function() {
            window.location.replace('#/ebodac/statistics');
        }

        $scope.drawLabel = function(ctx, x, y, text, textAlign, textBaseline) {
            var rx = x, ry = y, textWidth, fontSize = 14, textPadding = 4;

            ctx.font = fontSize + 'px Helvetica';
            ctx.textAlign = textAlign;
            ctx.textBaseline = textBaseline;
            textWidth = ctx.measureText(text).width;

            if (textAlign === 'center') {
                rx = x - textWidth / 2;
            } else if (textAlign === 'right') {
                rx = x - textWidth;
            }

            if (textBaseline === 'middle') {
                ry = y - fontSize / 2;
            } else if (textBaseline === 'bottom') {
                ry = y - fontSize;
            }

            ctx.fillStyle = 'rgba(255, 255, 255, 0.75)';
            ctx.fillRect(rx - textPadding, ry - textPadding, textWidth + textPadding * 2, fontSize + textPadding * 2);

            ctx.fillStyle = '#000000';
            ctx.fillText(text, x, y);
        }

        $scope.drawBarLabel = function(ctx, x, y, width, height, text) {
            var tx = x, ty,textWidth = ctx.measureText(text).width;
            ty = y - 5 + height / 2;

            ctx.save();
            if (width != null && textWidth > width) {
                ctx.translate(tx,ty);
                ctx.rotate(-Math.PI / 2);
                ctx.translate(-tx,-ty);

                $scope.drawLabel(ctx, tx, ty, text, 'left', 'middle');
            } else {
                $scope.drawLabel(ctx, tx, ty, text, 'center', 'bottom');
            }
            ctx.restore();
        }

        $scope.graphOptions = {
            showTooltips : false,
            tooltipCaretSize : 0,

            onAnimationProgress: function() {
                $scope.blockExportButton = true;
                $scope.$apply();
            },

            onAnimationComplete: function() {
                $scope.blockExportButton = false;
                $scope.$apply();

                var i, textAlign, textBaseline, ctx = this.chart.ctx;

                if (this.name === "Bar") {
                    this.eachBars(function(bar) {
                        if (bar.value > 0) {
                            $scope.drawBarLabel(ctx, bar.x, bar.y, bar.width, bar.height(), bar.value);
                        }
                    });
                } else if (this.name === "Line") {
                    this.datasets.forEach(function (dataset) {
                        for (i = 0; i < dataset.points.length; i++) {
                            if (dataset.points[i].value > 0) {
                                if (i === 0) {
                                    textAlign = "left";
                                } else {
                                    textAlign = "center";
                                }

                                var x = dataset.points[i].x, y = dataset.points[i].y;

                                if (y < 20) {
                                    textBaseline = "top";
                                    y = y + 8;
                                } else {
                                    textBaseline = "bottom";
                                    y = y - 6;
                                }

                                $scope.drawLabel(ctx, x, y, dataset.points[i].value, textAlign, textBaseline);
                            }

                        }
                    });
                } else {
                    var total = this.total;

                    if (total === undefined || total === null) {
                        total = 0;

                        this.segments.forEach(function (seg) {
                            total += seg.value;
                        });
                    }

                    this.segments.forEach(function (seg) {
                        var tooltipPosition = seg.tooltipPosition(), percentValue = Math.round(seg.value / total * 10000) / 100;

                        if (seg.value > 0) {
                            $scope.drawLabel(ctx, tooltipPosition.x, tooltipPosition.y, seg.value + ' (' + percentValue + '%)', 'center', 'middle');
                        }
                    });
                }
            },

            bezierCurve: false
        };

        $scope.graphColours = {
            statusGraph:[ '#97BBCD', // blue
                          '#F7464A', // red
                          '#00E500', // green
                          '#DCDCDC', // light grey
                          '#FDB45C', // yellow
                          '#949FB1', // grey
                          '#4D5360'  // dark grey
                        ],
            genderGraph:[ '#97BBCD', // blue
                          '#EE82EE', // violet
                          '#00E500', // green
                          '#DCDCDC', // light grey
                          '#FDB45C', // yellow
                          '#949FB1', // grey
                          '#4D5360'  // dark grey
                        ],
            successfulGenderGraph:[ '#97BBCD', // blue
                          '#EE82EE', // violet
                          '#00E500', // green
                          '#DCDCDC', // light grey
                          '#FDB45C', // yellow
                          '#949FB1', // grey
                          '#4D5360'  // dark grey
                        ]
        }



        $scope.tableType = $routeParams.tableType;
        $scope.graphType = $routeParams.graphType;

        $scope.tableHeaders = [];
        $scope.tableData = [];
        $scope.tableDataSum = {};
        $scope.sumHeader = null;
        $scope.graphs = [];
        $scope.graphData = {};
        $scope.graphDataSum = {};
        $scope.graphSeries = {};
        $scope.graphLabels = {};

        $scope.availableGraphsToExport = [];
        $scope.blockExportButton = false;
        $scope.graphExport = {
            exportToSeparateFiles: false,
            selectedGraphs: []
        }

        $scope.showExportGraphsModal = function() {
            $scope.graphExport.exportToSeparateFiles = false;
            $scope.graphExport.selectedGraphs = angular.copy($scope.availableGraphsToExport);
            $('#exportGraphsModal').modal('show');
            $timeout(function() {
                $('#exportGraphsSelect').select2('val', $scope.graphExport.selectedGraphs);
            }, 50);
        }

        $scope.closeExportGraphsModal = function () {
            $('#exportGraphsModal').modal('hide');
        };

        $scope.graphExportChanged = function(change) {
            var value;

            if (change.added) {
                value = change.added.id;
                $scope.graphExport.selectedGraphs.push(value);
            } else if (change.removed) {
                value = change.removed.id;
                $scope.graphExport.selectedGraphs.removeObject(value);
            }

            $scope.$apply();
        };

        $scope.availableGraphTypes = ["Pie", "Doughnut", "PolarArea", "Line", "Bar"];
        $scope.sumGraphTypes = ["Pie", "Doughnut", "PolarArea"];
        $scope.selectedType = "Pie";

        $scope.selectGraphType = function(type) {
            $scope.selectedType = type;
            $scope.setGraphsData();
        }

        $scope.loadData = function() {
            var url;

            if ($scope.tableType !== undefined && $scope.tableType !== null) {
                $scope.pageHeader = $scope.msg('ebodac.web.statistics.table.' + $scope.tableType);
                url = "../ebodac/statistic/table/" + $scope.tableType + "?dateFilter=" + $scope.selectedFilter.dateFilter;

                if ($scope.selectedFilter.startDate) {
                    url = url + "&startDate=" + $scope.selectedFilter.startDate;
                }

                if ($scope.selectedFilter.endDate) {
                    url = url + "&endDate=" + $scope.selectedFilter.endDate;
                }

                $http.post(url)
                .success(function(response) {
                    $scope.tableHeaders = response.headers;
                    if (response.data !== null && response.data !== undefined) {
                        $scope.tableData = response.data;
                    } else {
                        $scope.tableData = [];
                    }

                    if (response.dataSum !== null && response.dataSum !== undefined) {
                        $scope.tableDataSum = response.dataSum;
                    } else {
                        $scope.tableDataSum = {};
                    }

                    if (response.sumHeader !== null && response.sumHeader !== undefined && response.sumHeader !== "") {
                        $scope.sumHeader = response.sumHeader;
                    } else {
                        $scope.sumHeader = null;
                    }
                })
                .error(function(response) {
                    motechAlert('ebodac.web.statistics.getStatistics.' + $scope.tableType + '.error', 'ebodac.web.statistics.error', $scope.getMessageFromData(response));
                });
            } else if ($scope.graphType !== undefined && $scope.graphType !== null) {
                $scope.pageHeader = $scope.msg('ebodac.web.statistics.graphs.' + $scope.graphType);
                url = "../ebodac/statistic/graphs/" + $scope.graphType + "?dateFilter=" + $scope.selectedFilter.dateFilter;

                if ($scope.selectedFilter.startDate) {
                    url = url + "&startDate=" + $scope.selectedFilter.startDate;
                }

                if ($scope.selectedFilter.endDate) {
                    url = url + "&endDate=" + $scope.selectedFilter.endDate;
                }

                $http.post(url)
                .success(function(response) {
                    var i, j, k;

                    $scope.graphs = response.graphs;
                    $scope.sumHeader = response.sumHeader;
                    $scope.availableGraphsToExport = [];

                    $scope.graphData = {};
                    $scope.graphDataSum = {};
                    $scope.graphSeries = {};
                    $scope.graphLabels = {};

                    if (response.data !== null && response.data !== undefined) {
                        for (i = 0; i < $scope.graphs.length; i += 1) {
                            var headers = [], tmpData = [], tmpDataSum = [], tmpLabels = [];

                            for (j = 0; j < response.headers[i].length; j += 1) {
                                headers[j] = $scope.msg('ebodac.web.statistics.' + $scope.graphType + '.' + response.headers[i][j])
                                tmpDataSum[j] = response.dataSum[response.headers[i][j]];

                                tmpData[j] = [];
                                for (k = 0; k < response.data.length; k += 1) {
                                    tmpData[j][k] = response.data[k][response.headers[i][j]];
                                }
                            }

                            for (k = 0; k < response.data.length; k += 1) {
                                tmpLabels[k] = response.data[k][response.sumHeader];
                            }

                            $scope.graphSeries[$scope.graphs[i]] = headers;
                            $scope.graphLabels[$scope.graphs[i]] = tmpLabels;
                            $scope.graphData[$scope.graphs[i]] = tmpData;
                            $scope.graphDataSum[$scope.graphs[i]] = tmpDataSum;

                            if ($scope.isGraphNotEmpty($scope.graphDataSum, $scope.graphs[i]) === true) {
                                $scope.availableGraphsToExport.push($scope.graphs[i]);
                            }
                        }
                    }

                    $scope.setGraphsData();
                })
                .error(function(response) {
                    motechAlert('ebodac.web.statistics.getStatistics.' + $scope.graphType + '.error', 'ebodac.web.statistics.error', $scope.getMessageFromData(response));
                });
            } else {
                $scope.pageHeader = $scope.msg('ebodac.web.statistics.ivrEngagement');
            }
        }

        $scope.isSumGraph = function() {
            return $scope.sumGraphTypes.indexOf($scope.selectedType) !== -1;
        }

        $scope.setGraphsData = function() {
            if ($scope.isSumGraph()) {
                $scope.labels = $scope.graphSeries;
                $scope.data = $scope.graphDataSum;
                $scope.series = {};
            } else {
                $scope.labels = $scope.graphLabels;
                $scope.data = $scope.graphData;
                $scope.series = $scope.graphSeries;
            }
        }

        $scope.isGraphNotEmpty = function(data, graph) {
            if (data === null || data === undefined || data[graph] === null || data[graph] === undefined) {
                return false;
            }
            for(var i = 0; i < data[graph].length; i++) {
                if(data[graph][i] !== 0) {
                    return true;
                }
            }
            return false;
        }

        $scope.loadData();

        $scope.exportGraphs = function() {
            var i, pdf = null;

            $scope.blockExportButton = true;
            $scope.closeExportGraphsModal();

            var fileNameBeginning, fileNameEnd = "_" + $filter('date')(new Date(), "yyyyMMddHHmmss") + ".pdf";

            if ($scope.graphExport.selectedGraphs.length === 1) {
                fileNameBeginning = $scope.msg('ebodac.web.statistics.export.graphs.' + $scope.graphExport.selectedGraphs[0]);
            } else {
                fileNameBeginning = $scope.msg('ebodac.web.statistics.export.graphs.' + $scope.graphType);
            }

            var date = new Date();

            switch ($scope.selectedFilter.dateFilter) {
                case "YESTERDAY":
                    date.setDate(date.getDate() - 1);
                    $scope.selectedFilter.endDate = $filter('date')(date, "yyyy-MM-dd");
                    $scope.selectedFilter.startDate = $filter('date')(date, "yyyy-MM-dd");
                    break;
                case "LAST_7_DAYS":
                    date.setDate(date.getDate() - 1).toString();
                    $scope.selectedFilter.endDate = $filter('date')(date, "yyyy-MM-dd");
                    date.setDate(date.getDate() - 6).toString();
                    $scope.selectedFilter.startDate = $filter('date')(date, "yyyy-MM-dd");
                    break;
                case "LAST_WEEK":
                    var currentDay = (date.getDay() + 8) % 8;
                    date.setDate(date.getDate() - currentDay).toString();
                    $scope.selectedFilter.endDate = $filter('date')(date, "yyyy-MM-dd");
                    date.setDate(date.getDate() - 6).toString();
                    $scope.selectedFilter.startDate = $filter('date')(date, "yyyy-MM-dd");
                    break;
                case "LAST_30_DAYS":
                    date.setDate(date.getDate() - 1).toString();
                    $scope.selectedFilter.endDate = $filter('date')(date, "yyyy-MM-dd");
                    date.setDate(date.getDate() - 30).toString();
                    $scope.selectedFilter.startDate = $filter('date')(date, "yyyy-MM-dd");
                    break;
                case "LAST_MONTH":
                    date.setDate(0).toString();
                    $scope.selectedFilter.endDate = $filter('date')(date, "yyyy-MM-dd");
                    date.setDate(1).toString();
                    $scope.selectedFilter.startDate = $filter('date')(date, "yyyy-MM-dd");
                    break;
            }

            for (i = 0; i < $scope.graphExport.selectedGraphs.length; i += 1) {
                pdf = $scope.addGraphToPage(pdf, $scope.graphExport.selectedGraphs[i]);

                if ($scope.graphExport.exportToSeparateFiles === true) {
                    fileNameBeginning = $scope.msg('ebodac.web.statistics.export.graphs.' + $scope.graphExport.selectedGraphs[i]);
                    pdf.save(fileNameBeginning + fileNameEnd);
                    pdf = null;
                }
            }

            if (pdf !== null) {
                pdf.save(fileNameBeginning + fileNameEnd);
            }

            $scope.blockExportButton = false;
        }

        $scope.addGraphToPage = function(pdf, graph) {
            var image = $scope.getGraphFromCanvas(graph, 400, 400);

            if (image === null) {
                return pdf;
            }

            if (pdf === null) {
                pdf = new jsPDF('p', 'pt', 'a4');
            } else {
                pdf.addPage();
            }

            pdf.addImage(image, 'JPEG', 150, 70);

            var pageWidth = pdf.internal.pageSize.width;
            pdf.setTextColor(34, 68, 119);

            var header = $scope.msg('ebodac.web.statistics.graphs.' + $scope.graphType + '.' + graph);
            var fontSize = 16;

            var txtWidth = pdf.getStringUnitWidth(header) * fontSize;
            var x = (pageWidth - txtWidth) / 2;

            pdf.setFontSize(fontSize);
            pdf.setFont("helvetica", "bold");
            pdf.text(header, x, 40)

            fontSize = 10;
            pdf.setFontSize(fontSize);
            pdf.setFont("helvetica", "normal");

            var exportDateRange = $scope.msg('ebodac.web.statistics.export.graphs.dateRange', $scope.selectedFilter.startDate, $scope.selectedFilter.endDate);
            var i, r, g, b, legendRectSize = 12, spaceAfterRect = 5, spaceBetweenElements = 20, legendLabels = [];

            txtWidth = pdf.getStringUnitWidth(exportDateRange) * fontSize;
            x = (pageWidth - txtWidth) / 2;
            pdf.text(exportDateRange, x, 54)

            txtWidth = 0;

            for (i = 0; i < $scope.graphSeries[graph].length; i += 1) {
                legendLabels[i] = $scope.graphSeries[graph][i];
                txtWidth += pdf.getStringUnitWidth(legendLabels[i]);
            }

            txtWidth = txtWidth * fontSize + $scope.graphSeries[graph].length * (legendRectSize + spaceAfterRect + spaceBetweenElements) - spaceBetweenElements;
            x = (pageWidth - txtWidth) / 2;

            for (i = 0; i < $scope.graphSeries[graph].length; i += 1) {
                r = parseInt($scope.graphColours[graph][i].substring(1, 3), 16);
                g = parseInt($scope.graphColours[graph][i].substring(3, 5), 16);
                b = parseInt($scope.graphColours[graph][i].substring(5, 7), 16);

                pdf.setDrawColor(0);
                pdf.setFillColor(r, g, b);
                pdf.roundedRect(x, 390, legendRectSize, legendRectSize, 2, 2, 'F');

                x += legendRectSize + spaceAfterRect;
                pdf.text(legendLabels[i], x, 400)

                x += pdf.getStringUnitWidth(legendLabels[i]) * fontSize + spaceBetweenElements;
            }

            return pdf;
        }

        $scope.getGraphFromCanvas = function(graph, imgWidth, imgHeight) {
            var graphCanvas = $('#' + graph)[0];

            if (graphCanvas === null || graphCanvas === undefined) {
                return null;
            }

            var canvas = document.createElement("canvas");
            var context = canvas.getContext('2d');

            canvas.width = imgWidth;
            canvas.height = imgHeight;

            context.drawImage(graphCanvas, 0, 0, imgWidth, imgHeight);

            context.globalCompositeOperation = "destination-over";
            context.fillStyle = "#FFFFFF";
            context.fillRect(0, 0, imgWidth, imgHeight);

            return canvas.toDataURL("image/jpeg", 1.0);
        }

        $scope.exportEntityInstances = function () {
            $('#exportEbodacInstanceModal').modal('show');
        };

        $scope.changeExportFormat = function (format) {
            $scope.exportFormat = format;
        };

        $scope.closeExportEbodacInstanceModal = function () {
            $('#exportEbodacInstanceForm').resetForm();
            $('#exportEbodacInstanceModal').modal('hide');
        };

        $scope.exportInstance = function() {
            var url = "../ebodac/statistic/export/";

            if ($scope.tableType === "ivr") {
                url = url + "IvrKpi";
            } else if ($scope.tableType === "sms") {
                url = url + "SmsKpi";
            } else {
                url = url + "IvrEngagement";
            }

            url = url + "?outputFormat=" + $scope.exportFormat;
            url = url + "&dateFilter=" + $scope.selectedFilter.dateFilter;

            if ($scope.selectedFilter.startDate) {
                url = url + "&startDate=" + $scope.selectedFilter.startDate;
            }

            if ($scope.selectedFilter.endDate) {
                url = url + "&endDate=" + $scope.selectedFilter.endDate;
            }

            $http.get(url)
            .success(function () {
                $('#exportEbodacInstanceForm').resetForm();
                $('#exportEbodacInstanceModal').modal('hide');
                window.location.replace(url);
            })
            .error(function (response) {
                handleResponse('mds.error', 'mds.error.exportData', response);
            });
        };

    });

    /*
     *
     * Email Reports
     *
     */
    controllers.controller('EbodacEmailReportsCtrl', function ($scope, $http) {

        $scope.schedulePeriods = ['DAILY', 'WEEKLY', 'MONTHLY'];
        $scope.scheduleDayOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

        $scope.selectPeriod = function(report, value) {
            report.schedulePeriod = $scope.schedulePeriods[value];
        };

        $scope.selectDayOfWeek = function(report, value) {
            report.dayOfWeek = $scope.scheduleDayOfWeek[value];
        };

        $scope.accordions = [];
        $scope.recipients = [];
        $scope.emailReports = [];
        
        // Added for the EBODAC-1088
        $scope.showNullsCells = [];
        
        ////

        $scope.entities = [];
        $scope.selectedEntity = [];
        $scope.oldEntityId = [];

        $scope.newRecipient = {};
        $scope.addRecipientMsg = null;

        $scope.entityChanged = function(index) {
            $scope.emailReports[index].entity = angular.copy($scope.selectedEntity[index]);
            $scope.emailReports[index].entity.fields = [];
            $('#entityFields' + index).select2('val', $scope.emailReports[index].entity.fields);
        }

        $scope.setSelectedEntities = function() {
            var i;
            for (i = 0; i < $scope.emailReports.length; i = i + 1) {
                $scope.selectedEntity[i] = $scope.entities[$scope.emailReports[i].entity.className];
                $scope.oldEntityId[i] = $scope.emailReports[i].entity.id;
                $('#entityFields' + i).select2('val', $scope.emailReports[i].entity.fields);
            }
        }

        function autoExpandSingleAccordion() {
            if ($scope.accordions.length === 1) {
                $scope.accordions[0] = true;
            }
        }

        function setAccordions(emailReports) {
            var i;
            $scope.accordions = [];
            for (i = 0; i < emailReports.length; i = i + 1) {
                $scope.accordions.push(false);
            }
            autoExpandSingleAccordion();
        }

        $scope.getEmailReports = function() {
            $http.get('../ebodac/getEmailReports')
            .success(function(response) {
                $scope.emailReports = response;
                $scope.originalEmailReports = angular.copy($scope.emailReports);
                $scope.setSelectedEntities();
                setAccordions($scope.emailReports);
            })
            .error(function(response) {
                motechAlert('ebodac.web.emailReports.getEmailReports.error', 'ebodac.web.emailReports.error', response);
            });
        }

        $http.get('../ebodac/getEbodacEntities')
        .success(function(response) {
            $scope.entities = response;

            if ($scope.entities === undefined && $scope.entities === null) {
                motechAlert('ebodac.web.emailReports.getEntities.error', 'ebodac.web.emailReports.error', response);
            } else {
                $scope.getEmailReports();
            }
        })
        .error(function(response) {
            motechAlert('ebodac.web.emailReports.getEntities.error', 'ebodac.web.emailReports.error', response);
        });

        $http.get('../ebodac/getEmailRecipients')
        .success(function(response) {
            $scope.recipients = response;
        })
        .error(function(response) {
            motechAlert('ebodac.web.emailReports.getRecipients.error', 'ebodac.web.emailReports.error', response);
        });

        $scope.collapseAccordions = function () {
            var key;
            for (key in $scope.accordions) {
                $scope.accordions[key] = false;
            }
            autoExpandSingleAccordion();
        };

        $scope.deleteReport = function(index) {
            var reportId = $scope.emailReports[index].id;
            if (reportId === undefined || reportId === null || reportId === '') {
                $scope.removeReport(index);
            } else {
                motechConfirm("ebodac.web.emailReports.deleteReport.ConfirmMsg", "ebodac.web.emailReports.deleteReport.ConfirmTitle",
                    function (response) {
                        if (!response) {
                            return;
                        } else {
                            $http.post('../ebodac/deleteReport', reportId)
                            .success(function (response) {
                                $scope.removeReport(index);
                            })
                            .error (function (response) {
                                motechAlert('ebodac.web.emailReports.deleteReport.error', 'ebodac.web.emailReports.error', response);
                            });
                        }
                    });
            }
        };

        $scope.removeReport = function(index) {
            $scope.emailReports.splice(index, 1);
            $scope.originalEmailReports.splice(index, 1);
            $scope.accordions.splice(index, 1);
            autoExpandSingleAccordion();
        };

        $scope.isDirty = function (index) {
            if ($scope.originalEmailReports[index] === null || $scope.emailReports[index] === null) {
                return false;
            }

            return !angular.equals($scope.originalEmailReports[index], $scope.emailReports[index]);
        };

        $scope.reset = function (index) {
            $scope.emailReports[index] = angular.copy($scope.originalEmailReports[index]);
            $scope.selectedEntity[index] = $scope.entities[$scope.emailReports[index].entity.className];
            $('#entityFields' + index).select2('val', $scope.emailReports[index].entity.fields);
        };

        $scope.addReport = function () {
            var newReport = {
                'name': '',
                'subject': '',
                'messageContent': '',
                'showNullsCells': '',
                'recipients': [],
                'entity': null,
                'schedulePeriod': $scope.schedulePeriods[0],
                'scheduleTime': null,
                'dayOfWeek': $scope.scheduleDayOfWeek[0],
                'status': 'ENABLED'
            };

            $scope.selectedEntity[$scope.emailReports.length] = null;
            $scope.oldEntityId[$scope.emailReports.length] = null;

            $scope.emailReports.push(newReport);
            $scope.originalEmailReports.push(angular.copy(newReport));
            $scope.accordions.push(true);
            autoExpandSingleAccordion();
        };

        $scope.saveReport = function (index) {
            var report = {
                'report': $scope.emailReports[index],
                'oldEntityId': $scope.oldEntityId[index]
            }

            $http.post('../ebodac/saveReport', report)
            .success(function (response) {
                $scope.emailReports[index] = response;
                $scope.originalEmailReports[index] = angular.copy($scope.emailReports[index]);
                $scope.selectedEntity[index] = $scope.entities[$scope.emailReports[index].entity.className];
                $scope.oldEntityId[index] = $scope.emailReports[index].entity.id;
                $('#entityFields' + index).select2('val', $scope.emailReports[index].entity.fields);
                motechAlert('ebodac.web.emailReports.saved.successMsg', 'ebodac.web.emailReports.saved.successTitle');
            })
            .error (function (response) {
                motechAlert('ebodac.web.emailReports.saveReports.error', 'ebodac.web.emailReports.error', response);
            });
        };

        $scope.enableReport = function(index) {
            var reportId = $scope.emailReports[index].id;
            if (reportId === undefined || reportId === null || reportId === '') {
                $scope.emailReports[index].status = 'ENABLED';
                $scope.originalEmailReports[index].status = 'ENABLED';
            } else {
                $http.post('../ebodac/enableReport', reportId)
                .success(function (response) {
                    $scope.emailReports[index].status = 'ENABLED';
                    $scope.originalEmailReports[index].status = 'ENABLED';
                    motechAlert('ebodac.web.emailReports.enabled.successMsg', 'ebodac.web.emailReports.enabled.successTitle');
                })
                .error (function (response) {
                    motechAlert('ebodac.web.emailReports.enableReport.error', 'ebodac.web.emailReports.error', response);
                });
            }
        };

        $scope.disableReport = function(index) {
            var reportId = $scope.emailReports[index].id;
            if (reportId === undefined || reportId === null || reportId === '') {
                $scope.emailReports[index].status = 'DISABLED';
                $scope.originalEmailReports[index].status = 'DISABLED';
            } else {
                $http.post('../ebodac/disableReport', reportId)
                .success(function (response) {
                    $scope.emailReports[index].status = 'DISABLED';
                    $scope.originalEmailReports[index].status = 'DISABLED';
                    motechAlert('ebodac.web.emailReports.disabled.successMsg', 'ebodac.web.emailReports.disabled.successTitle');
                })
                .error (function (response) {
                    motechAlert('ebodac.web.emailReports.disableReport.error', 'ebodac.web.emailReports.error', response);
                });
            }
        };

        $scope.addRecipientModalShow = function () {
            $scope.addRecipientMsg = null;
            $scope.newRecipient = {};
            $('#addRecipientModal').modal('show');
        };

        $scope.closeAddRecipientModal = function () {
            $('#addRecipientForm').resetForm();
            $('#addRecipientModal').modal('hide');
        };

        $scope.saveNewRecipient = function () {
            $http.post('../ebodac/addRecipient', $scope.newRecipient)
            .success(function (response) {
                $scope.newRecipient = response;
                $scope.recipients.push($scope.newRecipient);
                $scope.addRecipientMsg = $scope.msg('ebodac.web.emailReports.addRecipient.success');
            })
            .error (function (response) {
                $scope.addRecipientMsg = $scope.msg('ebodac.web.emailReports.addRecipient.error', response);
            });
        }

    });

}());
