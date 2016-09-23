var app=angular.module('app', []);

app.controller('ctrl', function ($scope, $http) {
    var defaultServerError = function errorCallback(response) {
        $scope.errorMessage = 'Oops! Something went wrong :-(';
    };

    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    $scope.hexes = [];

    for (i = 0; i < 50; i++) {
        var key = deriveKey("abc123abc", "salt", 2000, 192);
        var obj = new Object();
        obj.index = i;
        obj.value = forge.util.bytesToHex(key);
        $scope.hexes.push(obj);
    }
});