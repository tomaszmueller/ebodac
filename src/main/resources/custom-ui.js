if ($scope.selectedEntity.name === "Subject" || $scope.selectedEntity.name === "Visit") {
    $scope.showBackToEntityListButton = false;
} else {
    $scope.showImportButton = false;
    $scope.backToEntityList = function() {
        window.location.replace('#/ebodac/reports');
    };
}
$scope.showAddInstanceButton = false;
$scope.showDeleteInstanceButton = false;
var importCsvModal = '../ebodac/resources/partials/modals/import-csv.html';
var editSubjectModal = '../ebodac/resources/partials/modals/edit-subject.html';

$scope.customModals.push(importCsvModal);
$scope.customModals.push(editSubjectModal);

$scope.importEntityInstances = function() {
    $('#importSubjectModal').modal('show');
};

$scope.importSubject = function () {
    blockUI();

    $('#importSubjectForm').ajaxSubmit({
        success: function () {
            $("#instancesTable").trigger('reloadGrid');
            $('#importSubjectForm').resetForm();
            $('#importSubjectModal').modal('hide');
            unblockUI();
        },
        error: function (response) {
            handleResponse('mds.error', 'mds.error.importCsv', response);
        }
    });
};

$scope.closeImportSubjectModal = function () {
    $('#importSubjectForm').resetForm();
    $('#importSubjectModal').modal('hide');
};

$scope.exportInstance = function() {
    var selectedFieldsName = [], url, rows, page, sortColumn, sortDirection;

    angular.forEach($scope.selectedFields, function(selectedField) {
        selectedFieldsName.push(selectedField.basic.name);
    });

    url = "../ebodac/entities/" + $scope.selectedEntity.id + "/exportInstances";
    url = url + "?range=" + $scope.actualExportRange;
    url = url + "&outputFormat=" + $scope.exportFormat;

    if ($scope.actualExportRange === 'table') {
        rows = $('#instancesTable').getGridParam('rowNum');
        page = $('#instancesTable').getGridParam('page');
        sortColumn = $('#instancesTable').getGridParam('sortname');
        sortDirection = $('#instancesTable').getGridParam('sortorder');

        url = url + "&selectedFields=" + selectedFieldsName;
        url = url + "&rows=" + rows;
        url = url + "&lookup=" + (($scope.selectedLookup) ? $scope.selectedLookup.lookupName : "");
        url = url + "&fields=" + JSON.stringify($scope.lookupBy);
        url = url + "&page=" + page;
        url = url + "&sortColumn=" + sortColumn;
        url = url + "&sortDirection=" + sortDirection;
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

$scope.addEntityInstanceDefault = function () {
    blockUI();

    var values = $scope.currentRecord.fields;
    angular.forEach (values, function(value, key) {
        value.value = value.value === 'null' ? null : value.value;

        if (value.name === "changed") {
            value.value = true;
        }
    });

    $scope.currentRecord.$save(function() {
        $scope.unselectInstance();
        unblockUI();
    }, angularHandler('mds.error', 'mds.error.cannotAddInstance'));
};

$scope.addEntityInstance = function() {

    $http.get('../ebodac/ebodac-config')
    .success(function(response){
        $scope.ebodacConfig = response;
    })
        .error(function(response) {
            $scope.ebodacConfig.showWarnings = true;
    });

    if ($scope.selectedEntity.name === "Subject" && $scope.selectedInstance !== undefined && $scope.ebodacConfig.showWarnings) {
        $('#editSubjectModal').modal('show');
    } else {
        $scope.addEntityInstanceDefault();
    }
};
