var app=angular.module('app', ['nonStringSelect', 'focus-if']);

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
                $scope.newKeyLength = $scope.user.keyLength;
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
            var masterKey = deriveKey($scope.modelMasterPwd, $scope.user.userId, $scope.user.iterations, $scope.user.keyLength, $scope.user.pbkdf2Algorithm);
            $scope.modelMasterPwd = null;
            $http.get('/service/secure/password/retrieve').then(function successCallback(response) {
                var domains = response.data;

                $http({
                    method: "get",
                    url: "/service/secure/user/recommendedSettings"
                }).then(function successCallback(response){
                    var newIterations = response.data.recommendedIterations;
                    var newPbkdf2Algorithm = response.data.recommendedPbkdf2Algorithm;
                    var newMasterPasswordHash = md5($scope.newMasterPassword1);
                    var newMasterKey = deriveKey($scope.newMasterPassword1, $scope.user.userId, newIterations, $scope.newKeyLength, newPbkdf2Algorithm);
                    for (i in domains) {
                        var domain = domains[i];
                        var decodedPassword = decode(domain.hex, masterKey,
                            domain.iv ? forge.util.hexToBytes(domain.iv) : "", $scope.user.cipherAlgorithm);
                        var newIv = forge.random.getBytesSync(16);
                        domain.iv = forge.util.bytesToHex(newIv);
                        domain.hex = encode(decodedPassword, newMasterKey, newIv, $scope.user.cipherAlgorithm);
                    }
                    $http({
                        method: "post",
                        url: "/service/secure/password/changeAllHex?md5Hash="+ newMasterPasswordHash + "&iterations=" + newIterations + "&cipherAlgorithm=" + $scope.user.cipherAlgorithm + "&keyLength=" + $scope.newKeyLength + "&pbkdf2Algorithm="+newPbkdf2Algorithm,
                        headers: {'Content-Type': 'application/json'},
                        data: domains
                    }).then(function successCallback(response){
                        $scope.successMessage = "Master Password successfully changed";
                    }, defaultServerError);
                }, defaultServerError);
            }, defaultServerError);
        }, function errorCallback(response) {
            $scope.errorMessage = 'Your Master Password is wrong!';
        });
    };
});