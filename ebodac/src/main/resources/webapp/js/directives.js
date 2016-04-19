(function () {
    'use strict';

    /* Directives */
    var directives = angular.module('ebodac.directives', []);


    /*
    * This function checks if the next column is last of the jqgrid.
    */
    function isLastNextColumn(colModel, index) {
        var result;
        $.each(colModel, function (i, val) {
            if ((index + 1) < i) {
                if (colModel[i].hidden !== false) {
                    result = true;
                } else {
                    result = false;
                }
            }
            return (result);
        });
        return result;
    }

    /*
    * This function checks if the field name is reserved for jqgrid (subgrid, cb, rn)
    * and if true temporarily changes that name.
    */
    function changeIfReservedFieldName(fieldName) {
        if (fieldName === 'cb' || fieldName === 'rn' || fieldName === 'subgrid') {
            return fieldName + '___';
        } else {
            return fieldName;
        }
    }

    /*
    * This function checks if the field name was changed
    * and if true changes this name to the original.
    */
    function backToReservedFieldName(fieldName) {
        if (fieldName === 'cb___' || fieldName === 'rn___' || fieldName === 'subgrid___') {
            var fieldNameLength = fieldName.length;
            return fieldName.substring(0, fieldNameLength - 3);
        } else {
            return fieldName;
        }
    }

    /*
    * This function calculates width parameters
    * for fit jqGrid on the screen.
    */
    function resizeGridWidth(gridId) {
        var intervalWidthResize, tableWidth;
        clearInterval(intervalWidthResize);
        intervalWidthResize = setInterval( function () {
            tableWidth = $('.overrideJqgridTable').width();
            $('#' + gridId).jqGrid("setGridWidth", tableWidth);
            clearInterval(intervalWidthResize);
        }, 200);
    }

    /*
    * This function calculates height parameters
    * for fit jqGrid on the screen.
    */
    function resizeGridHeight(gridId) {
        var intervalHeightResize, gap, tableHeight;
        clearInterval(intervalHeightResize);
        intervalHeightResize = setInterval( function () {
            if ($('.overrideJqgridTable').offset() !== undefined) {
                gap = 1 + $('.overrideJqgridTable').offset().top - $('.inner-center .ui-layout-content').offset().top;
                tableHeight = Math.floor($('.inner-center .ui-layout-content').height() - gap - $('.ui-jqgrid-pager').outerHeight() - $('.ui-jqgrid-hdiv').outerHeight());
                $('#' + gridId).jqGrid("setGridHeight", tableHeight);
                resizeGridWidth(gridId);
            }
            clearInterval(intervalHeightResize);
        }, 250);
     }

    /*
    * This function checks grid width
    * and increase this width if possible.
    */
    function resizeIfNarrow(gridId) {
        var intervalIfNarrowResize;
        setTimeout(function() {
            clearInterval(intervalIfNarrowResize);
        }, 950);
        intervalIfNarrowResize = setInterval( function () {
            if (($('#' + gridId).jqGrid('getGridParam', 'width') - 20) > $('#gbox_' + gridId + ' .ui-jqgrid-btable').width()) {
                $('#' + gridId).jqGrid('setGridWidth', ($('#' + gridId).jqGrid('getGridParam', 'width') - 4), true);
                $('#' + gridId).jqGrid('setGridWidth', $('#inner-center.inner-center').width() - 2, false);
            }
        }, 550);
    }

    /*
    * This function checks the name of field
    * whether is selected for display in the jqGrid
    */
    function isSelectedField(name, selectedFields) {
        var i;
        if (selectedFields) {
            for (i = 0; i < selectedFields.length; i += 1) {
                if (name === selectedFields[i].basic.name) {
                    return true;
                }
            }
        }
        return false;
    }

    function handleGridPagination(pgButton, pager, scope) {
        var newPage = 1, last, newSize;
        if ("user" === pgButton) { //Handle changing page by the page input
            newPage = parseInt(pager.find('input:text').val(), 10); // get new page number
            last = parseInt($(this).getGridParam("lastpage"), 10); // get last page number
            if (newPage > last || newPage === 0) { // check range - if we cross range then stop
                return 'stop';
            }
        } else if ("records" === pgButton) { //Page size change, we must update scope value to avoid wrong page size in the trash screen
            newSize = parseInt(pager.find('select')[0].value, 10);
            scope.entityAdvanced.userPreferences.gridRowsNumber = newSize;
        }
    }

    function buildGridColModel(colModel, fields, scope, ignoreHideFields) {
        var i, cmd, field;

        for (i = 0; i < fields.length; i += 1) {
            field = fields[i];

            if (!field.nonDisplayable) {
                //if name is reserved for jqgrid need to change field name
                field.basic.name = changeIfReservedFieldName(field.basic.name);

                cmd = {
                   label: field.basic.displayName,
                   name: field.basic.name,
                   index: field.basic.name,
                   jsonmap: field.basic.name,
                   width: 220,
                   hidden: ignoreHideFields? false : !isSelectedField(field.basic.name, scope.selectedFields)
                };

                colModel.push(cmd);
            }
        }
    }

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

    directives.directive('ebodacReloadTrigger', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs) {
                scope.$watch("$parent." + attrs.ngModel, function () {
                    scope.reloadData();
                });
            }
        };
    });

    directives.directive('reportGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                 $.ajax({
                    type: "GET",
                    url: "../ebodac/getReportModel/" + scope.reportType,
                    dataType: "json",
                    success: function (result) {
                        var jsonColNames = scope.buildColumnNames(result.colNames);
                        var jsonColModel = scope.buildColumnModel(result.colModel);
                        elem.jqGrid({
                            url: "../ebodac/getReport/" + scope.reportType,
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
                            colNames: jsonColNames,
                            colModel: jsonColModel,
                            pager: '#' + attrs.reportGrid,
                            rowNum: 50,
                            rowList: [10, 20, 50, 100],
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

    directives.directive('enrollmentGrid', function($http, $compile) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../ebodac/getEnrollments',
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
                    colNames: ['rowId', scope.msg('ebodac.web.enrollment.subjectId'), scope.msg('ebodac.web.enrollment.subjectName'),
                        scope.msg('ebodac.web.enrollment.status'), scope.msg('ebodac.web.enrollment.action')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                        name: 'subject',
                        index: 'subject.subjectId',
                        classes: 'pointer',
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
                        classes: 'pointer',
                        align: 'center',
                        formatter: function(cellValue, options, rowObject) {
                                       if (!cellValue || !cellValue.name){
                                           return '';
                                       }
                                       return cellValue.name;
                                   }
                    }, {
                        name: 'status',
                        index: 'status',
                        classes: 'pointer',
                        align: 'center'
                    }, {
                        name: 'action',
                        jsonmap: 'status',
                        align: 'center',
                        sortable: false,
                        formatter: function(cellValue, options, rowObject) {
                                       if (rowObject.status === 'Enrolled') {
                                           return "<button ng-click='unenroll(\"" + rowObject.subject.subjectId + "\")'" +
                                                   " type='button' class='btn btn-danger compileBtn' ng-disabled='enrollInProgress'>" +
                                                   scope.msg('ebodac.web.enrollment.btn.unenroll') + "</button>";
                                       } else if (rowObject.status === 'Unenrolled' || rowObject.status === 'Initial') {
                                           return "<button ng-click='enroll(\"" + rowObject.subject.subjectId + "\")'" +
                                                   " type='button' class='btn btn-success compileBtn' ng-disabled='enrollInProgress'>" +
                                                   scope.msg('ebodac.web.enrollment.btn.enroll') + "</button>";
                                       }
                                       return '';
                                   }
                    }],
                    onCellSelect: function (id, iCol, cellContent, e) {
                        if (iCol !== 4) {
                            var rowValue = elem.jqGrid('getRowData', id);
                            scope.goToAdvanced(rowValue.subject);
                        }
                    },
                    pager: '#' + attrs.enrollmentGrid,
                    rowNum: 50,
                    rowList: [10, 20, 50, 100],
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
                        $compile($('.compileBtn'))(scope);
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

                scope.$watch("gridRefresh", function () {
                    $('#' + attrs.id).jqGrid('setGridParam', {
                        postData: {
                        }
                    }).trigger('reloadGrid');
                });
            }
        };
    });

    directives.directive('enrollmentAdvancedGrid', function($http, $compile) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../ebodac/getEnrollmentAdvanced/' + scope.selectedSubjectId,
                    headers: {
                        'Accept': 'application/x-www-form-urlencoded',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    datatype: 'json',
                    mtype: "POST",
                    postData: {
                    },
                    jsonReader:{
                        repeatitems: false
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    colNames: ['rowId', scope.msg('ebodac.web.enrollment.subjectId'), scope.msg('ebodac.web.enrollment.campaignName'),
                        scope.msg('ebodac.web.enrollment.date'), scope.msg('ebodac.web.enrollment.status'), scope.msg('ebodac.web.enrollment.action')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                        name: 'externalId',
                        index: 'externalId',
                        align: 'center'
                    }, {
                        name: 'campaignName',
                        index: 'campaignName',
                        align: 'center'
                    }, {
                        name: 'referenceDate',
                        index: 'referenceDate',
                        classes: 'pointer',
                        align: 'center',
                        editable: true,
                        edittype: 'text',
                        editoptions: {
                            dataInit: function(elem) {
                                $(elem).addClass("pointer");
                                $(elem).datetimepicker({
                                    dateFormat: "yy-mm-dd",
                                    changeMonth: true,
                                    changeYear: true,
                                    showTimepicker: false
                                });
                            }
                        }
                    }, {
                        name: 'status',
                        index: 'status',
                        align: 'center'
                    }, {
                        name: 'action',
                        jsonmap: 'referenceDate',
                        align: 'center',
                        sortable: false,
                        formatter: function(cellValue, options, rowObject) {
                                       if (rowObject.action !== undefined &&  rowObject.action.startsWith("<button")) {
                                           return rowObject.action;
                                       } else if (rowObject.status === 'Enrolled') {
                                           return "<button ng-click='unenroll(\"" + rowObject.campaignName + "\")'" +
                                                  " type='button' class='btn btn-danger compileBtn' ng-disabled='enrollInProgress'>" +
                                                  scope.msg('ebodac.web.enrollment.btn.unenroll') + "</button>";
                                       } else if (rowObject.status === 'Unenrolled' || rowObject.status === 'Initial') {
                                           return "<button ng-click='enroll(\"" + rowObject.campaignName + "\")'" +
                                                  " type='button' class='btn btn-success compileBtn' ng-disabled='enrollInProgress'>" +
                                                  scope.msg('ebodac.web.enrollment.btn.enroll') + "</button>";
                                       }
                                       return '';
                                   }
                    }],
                    viewrecords: true,
                    cellEdit: true,
                    cellsubmit : 'clientArray',
                    rowNum: 100,
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
                        $compile($('.compileBtn'))(scope);
                    },
                    beforeSaveCell: function (rowId, name, val, iRow, iCol) {
                        var rowData = elem.jqGrid('getRowData', rowId);
                        var action = '';
                        if (rowData.status === 'Enrolled') {
                            action = "<button ng-click='reenroll(\"" + rowData.campaignName + "\", \"" + val + "\")'" +
                                     " type='button' class='btn btn-primary compileBtn' ng-disabled='enrollInProgress'>" +
                                     scope.msg('ebodac.web.enrollment.btn.reenroll') + "</button>"
                        } else if (rowData.status === 'Unenrolled' || rowData.status === 'Initial') {
                            action = "<button ng-click='enrollWithNewDate(\"" + rowData.campaignName + "\", \"" + val + "\")'" +
                                     " type='button' class='btn btn-primary compileBtn' ng-disabled='enrollInProgress'>" +
                                     scope.msg('ebodac.web.enrollment.btn.enroll') + "</button>"
                        }
                        rowData.action = action;
                        elem.jqGrid('setRowData', rowId, rowData);
                        $compile($('.compileBtn'))(scope);
                        return val;
                    }
                });

                scope.$watch("lookupRefresh", function () {
                    $('#' + attrs.id).jqGrid('setGridParam', {
                        page: 1,
                        postData: {
                        }
                    }).trigger('reloadGrid');
                });
            }
        };
    });

    directives.directive('ivrEngagementStatisticGrid', function($http, $timeout) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../ebodac/getIvrEngagementStatistic',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: function (obj) {
                            return obj;
                        }
                    },
                    mtype: "POST",
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 50,
                    rowList: [10, 20, 50, 100],
                    colNames: ['rowId', scope.msg('ebodac.web.statistics.subjectId'), scope.msg('ebodac.web.statistics.callsExpected'),
                               scope.msg('ebodac.web.statistics.pushedSuccessfully'), scope.msg('ebodac.web.statistics.received'),
                               scope.msg('ebodac.web.statistics.activelyListened'), scope.msg('ebodac.web.statistics.failed')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                       name: 'subjectId',
                       index: 'subjectId',
                       align: 'center'
                    }, {
                        name: 'callsExpected',
                        index: 'callsExpected',
                        align: 'center'
                    }, {
                        name: 'pushedSuccessfully',
                        index: 'pushedSuccessfully',
                        align: 'center'
                    }, {
                        name: 'received',
                        index: 'received',
                        align: 'center'
                    }, {
                        name: 'activelyListened',
                        index: 'activelyListened',
                        align: 'center'
                    }, {
                        name: 'failed',
                        index: 'failed',
                        align: 'center'
                    }],
                    pager: '#' + attrs.ivrEngagementStatisticGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'subjectId',
                    sortorder: 'asc',
                    viewrecords: true,
                    loadonce: true,
                    gridview: true,
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        if (elem.getGridParam('datatype') === "json") {
                            setTimeout(function () {
                               elem.trigger("reloadGrid");
                            }, 10);
                        }
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    }
                });
            }
        };
    });

    directives.directive('ebodacInstancesGrid', function ($rootScope, $route, $timeout) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element), tableWidth, eventResize, eventChange,
                gridId = attrs.id,
                firstLoad = true;

                $.ajax({
                    type: "GET",
                    url: "../mds/entities/" + scope.selectedEntity.id + "/entityFields",
                    dataType: "json",
                    success: function (result) {
                        var colModel = [], i, noSelectedFields = true, spanText,
                        noSelectedFieldsText = scope.msg('mds.dataBrowsing.noSelectedFieldsInfo');

                        buildGridColModel(colModel, result, scope, false);

                        elem.jqGrid({
                            url: "../ebodac/instances/" + scope.selectedEntity.name,
                            headers: {
                                'Accept': 'application/x-www-form-urlencoded',
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            datatype: 'json',
                            mtype: "POST",
                            postData: {
                                fields: JSON.stringify(scope.lookupBy)
                            },
                            rowNum: scope.entityAdvanced.userPreferences.gridRowsNumber,
                            onPaging: function (pgButton) {
                                handleGridPagination(pgButton, $(this.p.pager), scope);
                            },
                            jsonReader: {
                                repeatitems: false
                            },
                            prmNames: {
                               sort: 'sortColumn',
                               order: 'sortDirection'
                            },
                            onSelectRow: function (id) {
                                firstLoad = true;
                                scope.editInstance(id, scope.selectedEntity.module, scope.selectedEntity.name);
                            },
                            resizeStop: function (width, index) {
                                var widthNew, widthOrg, colModel = $('#' + gridId).jqGrid('getGridParam','colModel');
                                if (colModel.length - 1 === index + 1 || (colModel[index + 1] !== undefined && isLastNextColumn(colModel, index))) {
                                    widthOrg = colModel[index].widthOrg;
                                    if (Math.floor(width) > Math.floor(widthOrg)) {
                                        widthNew = colModel[index + 1].width + Math.floor(width - widthOrg);
                                        colModel[index + 1].width = widthNew;

                                        $('.ui-jqgrid-labels > th:eq('+(index + 1)+')').css('width', widthNew);
                                        $('#' + gridId + ' .jqgfirstrow > td:eq('+(index + 1)+')').css('width', widthNew);
                                    }
                                    colModel[index].widthOrg = width;
                                }
                                tableWidth = $('#entityInstancesTable').width();
                                $('#' + gridId).jqGrid("setGridWidth", tableWidth);
                            },
                            loadonce: false,
                            headertitles: true,
                            colModel: colModel,
                            pager: '#' + attrs.ebodacInstancesGrid,
                            viewrecords: true,
                            autowidth: true,
                            shrinkToFit: false,
                            gridComplete: function () {
                                scope.setDataRetrievalError(false);
                                spanText = $('<span>').addClass('ui-jqgrid-status-label ui-jqgrid ui-widget hidden');
                                spanText.append(noSelectedFieldsText).css({padding: '3px 15px'});
                                $('#entityInstancesTable .ui-paging-info').append(spanText);
                                $('.ui-jqgrid-status-label').addClass('hidden');
                                $('#pageInstancesTable_center').addClass('page_instancesTable_center');
                                if (scope.selectedFields !== undefined && scope.selectedFields.length > 0) {
                                    noSelectedFields = false;
                                } else {
                                    noSelectedFields = true;
                                    $('#pageInstancesTable_center').hide();
                                    $('#entityInstancesTable .ui-jqgrid-status-label').removeClass('hidden');
                                }
                                if ($('#instancesTable').getGridParam('records') > 0) {
                                    $('#pageInstancesTable_center').show();
                                    $('#entityInstancesTable .ui-jqgrid-hdiv').show();
                                    $('.jqgfirstrow').css('height','0');
                                } else {
                                    if (noSelectedFields) {
                                        $('#pageInstancesTable_center').hide();
                                        $('#entityInstancesTable .ui-jqgrid-hdiv').hide();
                                    }
                                    $('.jqgfirstrow').css('height','1px');
                                }
                                $('#entityInstancesTable .ui-jqgrid-hdiv').addClass("table-lightblue");
                                $('#entityInstancesTable .ui-jqgrid-btable').addClass("table-lightblue");
                                $timeout(function() {
                                    resizeGridHeight(gridId);
                                    resizeGridWidth(gridId);
                                }, 550);
                                if (firstLoad) {
                                    resizeIfNarrow(gridId);
                                    firstLoad = false;
                                }
                            },
                            loadError: function() {
                                scope.setDataRetrievalError(true);
                            }
                        });

                        scope.$watch("lookupRefresh", function () {
                            $('#' + attrs.id).jqGrid('setGridParam', {
                                page: 1,
                                postData: {
                                    fields: JSON.stringify(scope.lookupBy),
                                    lookup: (scope.selectedLookup) ? scope.selectedLookup.lookupName : "",
                                    filter: (scope.filterBy) ? JSON.stringify(scope.filterBy) : ""
                                }
                            }).trigger('reloadGrid');
                        });

                        elem.on('jqGridSortCol', function (e, fieldName) {
                            // For correct sorting in jqgrid we need to convert back to the original name
                            e.target.p.sortname = backToReservedFieldName(fieldName);
                        });

                        $(window).on('resize', function() {
                            clearTimeout(eventResize);
                            eventResize = $timeout(function() {
                                $(".ui-layout-content").scrollTop(0);
                                resizeGridWidth(gridId);
                                resizeGridHeight(gridId);
                            }, 200);
                        }).trigger('resize');

                        $('#inner-center').on('change', function() {
                            clearTimeout(eventChange);
                            eventChange = $timeout(function() {
                                resizeGridHeight(gridId);
                                resizeGridWidth(gridId);
                            }, 200);
                        });
                    }
                });
            }
        };
    });

}());
