(function () {
    'use strict';

    var directives = angular.module('bookingApp.directives', []);

    function handleUndefined(value) {
        if (value == undefined) {
            value = "";
        }
        return value;
    };

    directives.directive('gridReloadTrigger', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs) {
                scope.$watch("$parent." + attrs.ngModel, function () {
                    $(".booking-app-grid").trigger('reloadGrid');
                });
            }
        };
    });

    directives.directive('bookingAppDatePicker', ['$timeout', function($timeout) {

        return {
            restrict: 'A',
            scope: {
                min: '@',
                max: '@'
            },
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {
                var isReadOnly = scope.$eval(attr.ngReadonly);
                if(!isReadOnly) {
                    angular.element(element).datepicker({
                        changeYear: true,
                        showButtonPanel: true,
                        dateFormat: 'yy-mm-dd',
                        onSelect: function (dateTex) {
                            $timeout(function() {
                                ngModel.$setViewValue(dateTex);
                            })
                        },
                        onChangeMonthYear: function (year, month, inst) {
                            var curDate = $(this).datepicker("getDate");
                            if (curDate === null) {
                                return;
                            }
                            if (curDate.getFullYear() !== year || curDate.getMonth() !== month - 1) {
                                curDate.setYear(year);
                                curDate.setMonth(month - 1);
                                $(this).datepicker("setDate", curDate);
                            }
                        },
                        onClose: function (dateText, inst) {
                            var viewValue = element.val();
                            $timeout(function() {
                                ngModel.$setViewValue(viewValue);
                            })
                        }
                    });
                }

                scope.$watch("$parent." + scope.min, function(value) {
                    if (value) {
                        angular.element(element).datepicker('option', 'minDate', value);
                    }
                });

                scope.$watch("$parent." + scope.max, function(value) {
                    if (value) {
                        angular.element(element).datepicker('option', 'maxDate', value);
                    }
                });
            }
        };
    }]);

    directives.directive('screeningGrid', function ($compile) {

        function createButton(id) {
            return '<button type="button" class="btn btn-primary btn-sm ng-binding printBtn" ng-click="printRow(' +
                               id + ')"><i class="fa fa-fw fa-print"></i></button>';
        };

        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                elem.jqGrid({
                    url: "../booking-app/screenings",
                    datatype: "json",
                    mtype: "GET",
                    colNames: [
                        scope.msg("bookingApp.screening.bookingId"),
                        scope.msg("bookingApp.location"),
                        scope.msg("bookingApp.screening.date"),
                        scope.msg("bookingApp.startTime"),
                        ""],
                    colModel: [
                        { name: "volunteer.id" },
                        { name: "clinic.location" },
                        { name: "date" },
                        { name: "startTime" },
                        { name: "print", align: "center", sortable: false, width: 40}
                    ],
                    gridComplete: function() {
                        var ids = elem.getDataIDs();
                        for(var i = 0; i < ids.length; i++){
                            elem.setRowData(ids[i], {print: createButton(ids[i])})
                        }
                        $compile($('.printBtn'))(scope);
                        $('#screeningTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                        $('#screeningTable .ui-jqgrid-btable').addClass("table-lightblue");
                    },
                    pager: "#pager",
                    rowNum: 50,
                    rowList: [10, 20, 50, 100],
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    sortname: null,
                    sortorder: "desc",
                    viewrecords: true,
                    gridview: true,
                    loadOnce: false,
                    postData: {
                        startDate: function() {
                            return handleUndefined(scope.selectedFilter.startDate);
                        },
                        endDate: function() {
                            return handleUndefined(scope.selectedFilter.endDate);
                        },
                        dateFilter: function() {
                            return handleUndefined(scope.selectedFilter.dateFilter);
                        }
                    },
                    beforeSelectRow: function() {
                        return false;
                    },
                    onCellSelect: function (id, iCol, cellContent, e) {
                        if (iCol !== 4) {
                            scope.editScreening(id);
                        }
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

    directives.directive('unscheduledVisitGrid', function ($compile) {

        var gridDataExtension;

        function extendGrid(cellValue, options, rowObject) {
            var rowExtraData = {};

            rowExtraData.id = rowObject.id;
            rowExtraData.siteId = rowObject.siteId;

            gridDataExtension[options.rowId] = rowExtraData;

            return cellValue;
        }

        function createButton(id) {
            return '<button type="button" class="btn btn-primary btn-sm ng-binding printBtn" ng-click="printFrom(' +
                               id + ')"><i class="fa fa-fw fa-print"></i></button>';
        };

        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                elem.jqGrid({
                    url: "../booking-app/unscheduledVisits",
                    datatype: "json",
                    mtype: "GET",
                    colNames: [
                        scope.msg("bookingApp.uncheduledVisit.participantId"),
                        scope.msg("bookingApp.location"),
                        scope.msg("bookingApp.date"),
                        scope.msg("bookingApp.startTime"),
                        scope.msg("bookingApp.uncheduledVisit.purpose"),
                        ""],
                    colModel: [
                        {
                            name: "participantId",
                            formatter: extendGrid,
                            index: 'subject.subjectId'
                        },
                        {
                            name: "clinicName",
                            index: 'clinic.location'
                        },
                        {
                            name: "date"
                        },
                        {
                            name: "startTime"
                        },
                        {
                            name: "purpose"
                        },
                        {
                            name: "print",
                            align: "center",
                            sortable: false,
                            width: 40
                        }
                    ],
                    gridComplete: function() {
                        var ids = elem.getDataIDs();
                        for(var i = 0; i < ids.length; i++){
                            elem.setRowData(ids[i], {print: createButton(ids[i])})
                        }
                        $compile($('.printBtn'))(scope);
                        $('#unscheduledVisitTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                        $('#unscheduledVisitTable .ui-jqgrid-btable').addClass("table-lightblue");
                    },
                    pager: "#pager",
                    rowNum: 50,
                    rowList: [10, 20, 50, 100],
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    sortname: null,
                    sortorder: "desc",
                    viewrecords: true,
                    gridview: true,
                    loadOnce: false,
                    beforeRequest: function() {
                        gridDataExtension = [];
                    },
                    onCellSelect: function(rowId, iCol, cellContent, e) {
                        if (iCol !== 5) {
                            var rowData = elem.getRowData(rowId),
                                extraRowData = gridDataExtension[rowId];

                            scope.newForm();
                            scope.form.dto.id = extraRowData.id;
                            scope.form.dto.participantId = rowData.participantId;
                            scope.form.dto.date = rowData.date;
                            scope.form.dto.startTime = rowData.startTime;
                            scope.form.dto.purpose = rowData.purpose;
                            scope.reloadSelects();
                            $('#unscheduledVisitModal').modal('show');
                        }
                    },
                    postData: {
                        startDate: function() {
                            return handleUndefined(scope.selectedFilter.startDate);
                        },
                        endDate: function() {
                            return handleUndefined(scope.selectedFilter.endDate);
                        },
                        dateFilter: function() {
                            return handleUndefined(scope.selectedFilter.dateFilter);
                        }
                    },
                    beforeSelectRow: function() {
                        return false;
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

    directives.directive('primeVaccinationGrid', function ($compile) {

        var gridDataExtension;

        function createButton(rowId) {
            return '<button type="button" class="btn btn-primary btn-sm ng-binding printBtn" ng-click="printCardFrom(' +
            rowId + ')">' + '<i class="fa fa-fw fa-print"></i>' + '</button>'
        }

        function extendGrid(cellValue, options, rowObject) {
            var rowExtraData = {};

            rowExtraData.visitBookingDetailsId = rowObject.visitBookingDetailsId;
            rowExtraData.siteId = rowObject.siteId;
            rowExtraData.visitId = rowObject.visitId;
            rowExtraData.participantGender = rowObject.participantGender;

            gridDataExtension[options.rowId] = rowExtraData;

            return cellValue;
        }

        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                elem.jqGrid({
                    url: "../booking-app/primeVaccinationSchedule",
                    datatype: "json",
                    mtype: "GET",
                    colNames: [
                        scope.msg("bookingApp.location"),
                        scope.msg("bookingApp.primeVaccination.participantId"),
                        scope.msg("bookingApp.primeVaccination.participantName"),
                        scope.msg("bookingApp.primeVaccination.femaleChildBearingAge"),
                        scope.msg("bookingApp.primeVaccination.screeningActualDate"),
                        scope.msg("bookingApp.primeVaccination.primeVacDate"),
                        scope.msg("bookingApp.startTime"),
                        ""],
                    colModel: [
                        {
                            name: "location",
                            index: 'clinic.location'
                        },
                        {
                            name: "participantId",
                            formatter: extendGrid,
                            index: 'subject.subjectId'
                        },
                        {
                            name: "participantName",
                            index: 'subject.name'
                        },
                        {
                            name: "femaleChildBearingAge",
                            index: 'subjectBookingDetails.femaleChildBearingAge'
                        },
                        {
                            name: "bookingScreeningActualDate",
                            sortable: false
                        },
                        {
                            name: "date",
                            index: 'bookingPlannedDate'
                        },
                        {
                            name: "startTime"
                        },
                        {
                            name: "print",
                            align: "center",
                            sortable: false,
                            width: 40
                        }
                    ],
                    gridComplete: function(){
                        var ids = elem.getDataIDs();
                            for(var i=0;i<ids.length;i++){
                                elem.setRowData(ids[i],{print: createButton(ids[i])})
                            }
                        $compile($('.printBtn'))(scope);
                        $('#primeVaccinationTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                        $('#primeVaccinationTable .ui-jqgrid-btable').addClass("table-lightblue");
                    },
                    pager: "#pager",
                    rowNum: 50,
                    rowList: [10, 20, 50, 100],
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    sortname: null,
                    sortorder: "desc",
                    viewrecords: true,
                    gridview: true,
                    loadOnce: false,
                    beforeSelectRow: function() {
                        return false;
                    },
                    beforeRequest: function() {
                        gridDataExtension = [];
                    },
                    onCellSelect: function(rowId, iCol, cellContent, e) {
                        if (iCol !== 7) {
                            var rowData = elem.getRowData(rowId),
                                extraRowData = gridDataExtension[rowId];

                            scope.newForm("edit");
                            scope.form.dto.visitBookingDetailsId = extraRowData.visitBookingDetailsId;
                            scope.form.dto.participantId = rowData.participantId;
                            scope.form.dto.participantName = rowData.participantName;
                            scope.form.dto.femaleChildBearingAge = rowData.femaleChildBearingAge;
                            scope.form.dto.actualScreeningDate = rowData.actualScreeningDate;
                            scope.form.dto.bookingScreeningActualDate = rowData.bookingScreeningActualDate;
                            scope.form.dto.date = rowData.date;
                            scope.form.dto.startTime = rowData.startTime;
                            scope.form.dto.visitId = extraRowData.visitId;
                            scope.form.dto.participantGender = extraRowData.participantGender;
                            scope.form.range = scope.calculateRange(scope.form.dto.bookingScreeningActualDate,
                                scope.form.dto.femaleChildBearingAge);
                            scope.reloadSelects();
                            $('#primeVaccinationScheduleModal').modal('show');
                        }
                    },
                    postData: {
                        startDate: function() {
                            return handleUndefined(scope.selectedFilter.startDate);
                        },
                        endDate: function() {
                            return handleUndefined(scope.selectedFilter.endDate);
                        },
                        dateFilter: function() {
                            return handleUndefined(scope.selectedFilter.dateFilter);
                        }
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

    directives.directive('visitRescheduleGrid', function ($compile) {

        var gridDataExtension;

        function createButton(id) {
            return '<button type="button" class="btn btn-primary btn-sm ng-binding printBtn" ng-click="print(' +
                               id + ')"><i class="fa fa-fw fa-print"></i></button>';
        };

        function extendGrid(cellValue, options, rowObject) {
            var rowExtraData = {};

            rowExtraData.siteId = rowObject.siteId;
            rowExtraData.visitId = rowObject.visitId;
            rowExtraData.visitBookingDetailsId = rowObject.visitBookingDetailsId;
            rowExtraData.earliestDate = rowObject.earliestDate;
            rowExtraData.latestDate = rowObject.latestDate;

            gridDataExtension[options.rowId] = rowExtraData;

            return cellValue;
        }

        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                elem.jqGrid({
                    url: "../booking-app/visitReschedule",
                    datatype: "json",
                    mtype: "GET",
                    colNames: [
                        scope.msg("bookingApp.location"),
                        scope.msg("bookingApp.visitReschedule.participantId"),
                        scope.msg("bookingApp.visitReschedule.participantName"),
                        scope.msg("bookingApp.visitReschedule.visitType"),
                        scope.msg("bookingApp.visitReschedule.actualDate"),
                        scope.msg("bookingApp.visitReschedule.plannedDate"),
                        scope.msg("bookingApp.startTime"),
                        ""],
                    colModel: [
                        {
                            name: "location",
                            index: 'clinic.location'
                        },
                        {
                            name: "participantId",
                            formatter: extendGrid,
                            index: 'subject.subjectId'
                        },
                        {
                            name: "participantName",
                            index: 'subject.name'
                        },
                        {
                            name: "visitType",
                            index: 'visit.type'
                        },
                        {
                            name: "actualDate",
                            index: 'visit.date'
                        },
                        {
                            name: "plannedDate",
                            index: 'visit.motechProjectedDate'
                        },
                        {
                            name: "startTime"
                        },
                        {
                            name: "print", align: "center", sortable: false, width: 40
                        }
                        ],
                    gridComplete: function(){
                        var ids = elem.getDataIDs();
                        for(var i = 0; i < ids.length; i++){
                            elem.setRowData(ids[i], {print: createButton(ids[i])})
                        }
                        $compile($('.printBtn'))(scope);
                        $('#visitRescheduleTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                        $('#visitRescheduleTable .ui-jqgrid-btable').addClass("table-lightblue");
                    },
                    pager: "#pager",
                    rowNum: 50,
                    rowList: [10, 20, 50, 100],
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    sortname: null,
                    sortorder: "desc",
                    viewrecords: true,
                    gridview: true,
                    loadOnce: false,
                    beforeSelectRow: function() {
                        return false;
                    },
                    beforeRequest: function() {
                        gridDataExtension = [];
                    },
                    onCellSelect: function(rowId, iCol, cellContent, e) {
                        var rowData = elem.getRowData(rowId),
                            extraRowData = gridDataExtension[rowId];

                        if ((rowData.actualDate === undefined || rowData.actualDate === null || rowData.actualDate === "")
                            && extraRowData.earliestDate !== undefined && extraRowData.earliestDate !== null && extraRowData.earliestDate !== "") {
                            scope.newForm();
                            scope.form.dto.participantId = rowData.participantId;
                            scope.form.dto.participantName = rowData.participantName;
                            scope.form.dto.visitType = rowData.visitType;
                            scope.form.dto.plannedDate = rowData.plannedDate;
                            scope.form.dto.startTime = rowData.startTime;
                            scope.form.dto.visitId = extraRowData.visitId;
                            scope.form.dto.visitBookingDetailsId = extraRowData.visitBookingDetailsId;
                            scope.form.dto.minDate = scope.parseDate(extraRowData.earliestDate);
                            scope.form.dto.maxDate = scope.parseDate(extraRowData.latestDate);
                            $('#visitRescheduleModal').modal('show');
                        }
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