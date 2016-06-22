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
            }, defaultServerError);
        }
    }, defaultServerError);
});