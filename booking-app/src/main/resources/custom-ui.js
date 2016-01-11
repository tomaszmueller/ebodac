$scope.showBackToEntityListButton = false;
$scope.showAddInstanceButton = false;
$scope.showLookupButton = true;
$scope.showFieldsButton = false;
$scope.showImportButton = false;
$scope.showExportButton = false;
$scope.showViewTrashButton = false;
$scope.showFiltersButton = false;
$scope.showDeleteInstanceButton = false;

$scope.showAdvanced = false;
$scope.advancedButtonIndex = 6;

$scope.showOrHideAdvanced = function() {
    var i, lastFieldIndex = 16;
    $scope.showAdvanced = !$scope.showAdvanced;

    for (i = $scope.advancedButtonIndex + 1; i <= lastFieldIndex; i += 1) {
        if ($scope.showAdvanced) {
            $scope.fields[i].nonDisplayable = false;
        } else {
            $scope.fields[i].nonDisplayable = true;
        }
    }
};

$scope.getAdvancedButtonLabel = function() {
    if ($scope.showAdvanced) {
        return $scope.msg('bookingApp.hideAdvanced');
    } else {
        return $scope.msg('bookingApp.showAdvanced');
    }
};

$scope.editInstance = function(id, module, entityName) {
    blockUI();
    $scope.setHiddenFilters();
    $scope.instanceEditMode = true;
    $scope.setModuleEntity(module, entityName);
    $scope.loadedFields = Instances.selectInstance({
        id: $scope.selectedEntity.id,
        param: id
        },
        function (data) {
            $scope.selectedInstance = id;
            $scope.currentRecord = data;
            $scope.fields = data.fields;
            if (entityName === "Clinic") {
                var i, showAdvancedButton = {
                    'name': 'showAdvanced',
                    'displayName': '',
                    'tooltip': '',
                    'value': '',
                    'type': {
                        'displayName': 'mds.field.string'
                    },
                    'required': false,
                    'nonEditable': false,
                    'nonDisplayable': false
                };

                $scope.showAdvanced = false;
                $scope.fields.splice($scope.advancedButtonIndex, 0, showAdvancedButton);

                for (i = 0; i < $scope.fields.length; i += 1) {
                    if ($scope.fields[i].name === "location" || $scope.fields[i].name === "siteId") {
                        $scope.fields[i].nonEditable = true;
                    }
                    if (i > $scope.advancedButtonIndex) {
                        $scope.fields[i].nonDisplayable = true;
                    }
                }
            }

            unblockUI();
        }, angularHandler('mds.error', 'mds.error.cannotUpdateInstance'));
};

$scope.addEntityInstanceDefault = $scope.addEntityInstance;

$scope.addEntityInstance = function () {
    $scope.fields.splice($scope.advancedButtonIndex, 1);
    $scope.addEntityInstanceDefault();
};

$scope.showLookupDialog = function() {
    $("#lookup-dialog")
    .css({'top': ($("#lookupDialogButton").offset().top - $("#main-content").offset().top) - 40,
    'left': ($("#lookupDialogButton").offset().left - $("#main-content").offset().left) - 15})
    .toggle();
    $("div.arrow").css({'left': 50});
};

$scope.loadEditValueFormDefault = $scope.loadEditValueForm;

$scope.loadEditValueForm = function (field) {
    if (field.name === 'showAdvanced') {
        return '../booking-app/resources/partials/widgets/field-show-advanced.html';
    }

    return $scope.loadEditValueFormDefault(field);
};