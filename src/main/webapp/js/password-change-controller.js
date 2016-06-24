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

    $scope.changeMasterPassword = function() {
        if (!$scope.newMasterPassword1) {
            $scope.errorMessage = 'The New Password is missing!';
            return;
        }
        if ($scope.newMasterPassword1 != $scope.newMasterPassword2) {
            $scope.errorMessage = 'The two passwords are not the same!';
            return;
        }

        var md5Hash = md5($scope.modelMasterPwd);

        $http({
            method: "post",
            url: "/service/secure/user/check",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "md5Hash=" + md5Hash
        }).then(function successCallback() {
            var masterKey = deriveKey($scope.modelMasterPwd, $scope.user.userId, $scope.user.iterations);
            $scope.modelMasterPwd = null;
            $http.get('/service/secure/password/retrieve').then(function successCallback(response) {
                var domains = response.data;

                $http({
                    method: "get",
                    url: "/service/secure/user/recommendedIterations"
                }).then(function successCallback(response){
                    var newIterations = response.data;
                    var newMasterPasswordHash = md5($scope.newMasterPassword1);
                    var newMasterKey = deriveKey($scope.newMasterPassword1, $scope.user.userId, newIterations);
                    for (i in domains) {
                        var domain = domains[i];
                        var decodedPassword = decode(domain.hex, masterKey,
                            domain.iv ? forge.util.hexToBytes(domain.iv) : "");
                        var newIv = forge.random.getBytesSync(16);
                        domain.iv = forge.util.bytesToHex(newIv);
                        domain.hex = encode(decodedPassword, newMasterKey, newIv);
                    }
                    $http({
                        method: "post",
                        url: "/service/secure/password/changeAllHex?md5Hash="+ newMasterPasswordHash + "&iterations=" + newIterations,
                        headers: {'Content-Type': 'application/json'},
                        data: domains
                    }).then(function successCallback(response){
                        $scope.successMessage = "Master Password successfully changed"
                    }, defaultServerError);
                }, defaultServerError);
            }, defaultServerError);
        }, function errorCallback(response) {
            $scope.errorMessage = 'Your Master Password is wrong!';
        });



    };

});