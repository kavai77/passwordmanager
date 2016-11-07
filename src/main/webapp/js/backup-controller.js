var app=angular.module('app', ['ngResource']);

app.controller('ctrl', function ($scope, $resource, $window) {
    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    var res = initResources($scope, $resource);

    $scope.user = res.PublicService.authenticate(function() {
        if (!$scope.user.registered) {
            $window.location = "/";
        } else {
            $scope.backups = res.BackupService.retrieve();
        }
    });

    $scope.createBackup = function () {
        clearMessages();
        var newBackup = res.BackupService.create(function () {
            $scope.backups.push(newBackup);
        });
    };

    $scope.formatDate = function (backupDate) {
        return new Date(backupDate).toLocaleString();
    };

    $scope.prepareBackup = function (backup) {
        $scope.backupToBeRestored = backup;
    };

    $scope.prepareDelete = function (backup) {
        $scope.backupToBeDeleted = backup;
    };

    $scope.restoreBackup = function () {
        clearMessages();
        var hash = messageDigest($scope.backupToBeRestored.masterPasswordHashAlgorithm, $scope.backupMasterPwd);
        res.BackupService.restore({
            id: $scope.backupToBeRestored.id,
            masterPasswordHash: hash
        },
        function successCallback(response){
            $scope.successMessage = "All passwords successfully restored";
        }, function errorCallback(response) {
            $scope.errorMessage = "The Backup's Master Password is wrong!";
        });
    };

    $scope.deleteBackup = function () {
        clearMessages();
        res.BackupService.remove({id: $scope.backupToBeDeleted.id}, function () {
            var index = $scope.backups.indexOf($scope.backupToBeDeleted);
            $scope.backups.splice(index, 1);
        });
    };

    var clearMessages = function() {
        $scope.errorMessage = null;
        $scope.successMessage = null;
    };

});