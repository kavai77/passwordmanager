var app=angular.module('app', ['ngResource', 'ui.bootstrap-slider']);

app.controller('ctrl', function ($scope, $resource, $window, $http) {
    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    var res = initResources($scope, $resource);

    let authFunction = function() {
        if (!$scope.user.registered) {
            $window.location = "/";
        }
    };

    initFirebase($scope, $http, res, authFunction);

    $scope.saveSettings = function () {
        clearMessages();
        res.UserService.updateUserSettings($scope.user.userSettings, function() {
            $scope.successMessage = "Your settings has been updated."
        });
    };

    var clearMessages = function() {
        $scope.errorMessage = null;
        $scope.successMessage = null;
    };
});