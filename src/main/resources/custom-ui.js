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

$scope.exportEntityInstances = function() {
    $http.get("../ebodac/entities/" + $scope.selectedEntity.id + "/exportInstances")
    .success(function (data) {
         window.location.replace("../ebodac/entities/" + $scope.selectedEntity.id + "/exportInstances");
    });
};

$scope.closeImportSubjectModal = function () {
    $('#importSubjectForm').resetForm();
    $('#importSubjectModal').modal('hide');
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