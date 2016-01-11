var app=angular.module('app', []);
app.controller('ctrl', function ($scope, $http) {
    $http.get('/userService').success(function(data) {
        $scope.user = data;
    });
    $http.get('/retrieve').success(function(data) {
        $scope.domains = data;
    });
    $scope.showOrHide = function(domain) {
        $scope.errorMessage = '';
        if (domain.shown) {
            domain.shown = false;
            domain.decodedPassword = '';
        } else {
            domain.shown = true;
            domain.decodedPassword = decode(domain.hex, $scope.masterPassword, $scope.user.userId);
        }
    };
    $scope.addPassword = function () {
        if (!$scope.newDomain) {
            $scope.errorMessage = 'Please provide your New Domain!';
            return;
        }
        if (!$scope.newPassword) {
            $scope.errorMessage = 'Please provide your New Password!';
            return;
        }
        $scope.errorMessage = '';
        var hex = encode($scope.newPassword, $scope.masterPassword, $scope.user.userId);
        $http({
            method: "post",
            url: "/store",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "domain=" + $scope.newDomain + "&hex=" + hex
        }).success(function(domain){
            $scope.domains.push(domain);
            $scope.newDomain = null;
            $scope.newPassword = null;
        });
    };
    $scope.masterPasswordLogin = function () {
        if (!$scope.modelMasterPwd) {
            $scope.errorMessage = 'Please provide your Master Password!';
            return;
        }
        var localEncodedUserId = encode($scope.user.userId, $scope.modelMasterPwd, $scope.user.userId);
        if ($scope.user.encodedUserId != localEncodedUserId) {
            $scope.errorMessage = 'Your Master Password is wrong!';
            return;
        }
        $scope.errorMessage = '';
        $scope.masterPassword=$scope.modelMasterPwd;
    };
    $scope.addEncodedUserId = function () {
        if (!$scope.newMasterPassword1) {
            $scope.errorMessage = 'The New Password is missing!';
            return;
        }
        if ($scope.newMasterPassword1 != $scope.newMasterPassword2) {
            $scope.errorMessage = 'The two passwords are not the same!';
            return;
        }
        $scope.errorMessage = '';
        var hex = encode($scope.user.userId, $scope.newMasterPassword1, $scope.user.userId);
        $http({
            method: "post",
            url: "/encodedUserId",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "encodedUserId=" + hex
        }).success(function(domain){
            $scope.user.encodedUserId = hex;
            $scope.masterPassword=$scope.newMasterPassword1;
        });
    };
    $scope.randomPassword = function() {
        $http.get('/secureRandom').success(function(data) {
            $scope.newPassword = data;
        });
    }
});