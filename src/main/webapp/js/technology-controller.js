var app=angular.module('app', ['ngResource']);

app.controller('ctrl', function ($scope, $resource) {
    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    var res = initResources($scope, $resource);

    $scope.auth = res.Authenticate.get(function() {
        if ($scope.auth.authenticated) {
            $scope.user = res.UserService.getUserData(function () {
                $scope.newKeyLength = $scope.user.keyLength;
            });
        }
    });

});