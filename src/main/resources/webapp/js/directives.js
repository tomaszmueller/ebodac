(function () {
    'use strict';

    /* Directives */
    var directives = angular.module('ebodac.directives', []);

    directives.directive('timePicker', function($timeout) {
        return {
            restrict: 'A',
            require: 'ngModel',
            transclude: true,
            link: function(scope, element, attrs, ngModel) {
                $timeout(function() {
                    var elem = angular.element(element);

                    elem.datetimepicker({
                        dateFormat: "",
                        timeOnly: true,
                        timeFormat: "HH:mm",
                        onSelect: function (selectedTime) {
                            scope.$apply(function() {
                                ngModel.$setViewValue(selectedTime);
                            });
                        }
                    });
                });
            }
        };
    });

    directives.directive('datePicker', function($timeout) {
        return {
            restrict: 'A',
            require: 'ngModel',
            transclude: true,
            link: function(scope, element, attrs, ngModel) {
                $timeout(function() {
                    var elem = angular.element(element);

                    elem.datetimepicker({
                        dateFormat: "yy-mm-dd",
                        changeMonth: true,
                        changeYear: true,
                        showTimepicker: false,
                        onSelect: function (selectedDate) {
                            scope.$apply(function() {
                                ngModel.$setViewValue(selectedDate);
                            });
                        }
                    });
                });
            }
        };
    });

    directives.directive('dailyClinicVisitScheduleReportGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../ebodac/visitsRecords',
                    headers: {
                        'Accept': 'application/x-www-form-urlencoded',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    datatype: 'json',
                    mtype: "POST",
                    postData: {
                        fields: JSON.stringify(scope.lookupBy)
                    },
                    jsonReader:{
                        repeatitems: false
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    colNames: ['rowId', scope.msg('ebodac.web.reports.dailyClinicVisitScheduleReport.visitDate'), scope.msg('ebodac.web.reports.dailyClinicVisitScheduleReport.subjectId'),
                        scope.msg('ebodac.web.reports.dailyClinicVisitScheduleReport.subjectName'), scope.msg('ebodac.web.reports.dailyClinicVisitScheduleReport.subjectPhoneNumber'),
                        scope.msg('ebodac.web.reports.dailyClinicVisitScheduleReport.subjectAddress'), scope.msg('ebodac.web.reports.dailyClinicVisitScheduleReport.visitType')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                        name: 'date',
                        index: 'date',
                        align: 'center',
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d', newformat:'Y-m-d'}
                    }, {
                        name: 'subject',
                        index: 'subject.subjectId',
                        align: 'center',
                        formatter: function(cellValue, options, rowObject) {
                                       if (!cellValue){
                                           return '';
                                       }
                                       return cellValue.subjectId;
                                   }
                    }, {
                        name: 'subjectName',
                        jsonmap: 'subject',
                        index: 'subject.name',
                        align: 'center',
                        formatter: function(cellValue, options, rowObject) {
                                       if (!cellValue){
                                           return '';
                                       }
                                       return cellValue.name;
                                   }
                    }, {
                        name: 'subjectPhoneNumber',
                        jsonmap: 'subject',
                        index: 'subject.phoneNumber',
                        align: 'center',
                        formatter: function(cellValue, options, rowObject) {
                                       if (!cellValue){
                                           return '';
                                       }
                                       return cellValue.phoneNumber;
                                   }
                    }, {
                        name: 'subjectAddress',
                        jsonmap: 'subject',
                        index: 'subject.address',
                        align: 'center',
                        formatter: function(cellValue, options, rowObject) {
                                       if (!cellValue){
                                           return '';
                                       }
                                       return cellValue.address;
                                   }
                    }, {
                        name: 'type',
                        index: 'type',
                        align: 'center',
                    }],
                    pager: '#' + attrs.dailyClinicVisitScheduleReportGrid,
                    viewrecords: true,
                    loadonce: false,
                    resizeStop: function() {
                        $('.ui-jqgrid-htable').width('100%');
                        $('.ui-jqgrid-btable').width('100%');
                        elem.jqGrid('setGridWidth', '100%');
                    },
                    gridComplete: function () {
                        $('.ui-jqgrid-htable').width('100%');
                        $('.ui-jqgrid-btable').width('100%');
                        elem.jqGrid('setGridWidth', '100%');
                    }
                });

                scope.$watch("lookupRefresh", function () {
                    $('#' + attrs.id).jqGrid('setGridParam', {
                        page: 1,
                        postData: {
                            fields: JSON.stringify(scope.lookupBy),
                            lookup: (scope.selectedLookup) ? scope.selectedLookup.lookupName : ""
                        }
                    }).trigger('reloadGrid');
                });
            }
        };
    });

}());
