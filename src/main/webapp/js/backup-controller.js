var app=angular.module('app', []);

app.controller('ctrl', function ($scope, $http) {
    var defaultServerError = function errorCallback(response) {
        $scope.errorMessage = 'Oops! Something went wrong :-(';
    };

    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    $http.get('/service/public/authenticate').then(function successCallback(response) {
        $scope.auth = response.data;
        if ($scope.auth.authenticated) {
            $http.get('/service/secure/user/userService').then(function successCallback(response) {
                $scope.user = response.data;
                if ($scope.user.registered) {
                    $http.get('/service/secure/backup/retrieve').then(function successCallback(response) {
                        $scope.backups = response.data;
                    }, defaultServerError);
                }
            }, defaultServerError);
        }
    }, defaultServerError);

    $scope.createBackup = function () {
        $http.post('/service/secure/backup/create').then(function successCallback(response) {
            $scope.backups.push(response.data);
        }, defaultServerError);
    };

    $scope.prepareBackup = function (backup) {
        $scope.domainToBackup = backup;
    };

    $scope.restoreBackup = function () {
        var backupMasterPwdHash = md5($scope.backupMasterPwd);
        $http({
            method: "post",
            url: "/service/secure/backup/restore",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "id=" + $scope.domainToBackup.id + "&masterPasswordHash=" + backupMasterPwdHash
        }).then(function successCallback(response){
            $scope.successMessage = "All passwords successfully restored";
        }, function errorCallback(response) {
            $scope.errorMessage = "The Backup's Master Password is wrong!";
        });
    };

});