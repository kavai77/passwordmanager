var app=angular.module('app', ['ngResource', 'nonStringSelect', 'focus-if']);

app.controller('ctrl', function ($scope, $resource, $window) {
    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    var res = initResources($scope, $resource);

    $scope.user = res.PublicService.authenticate(function() {
        if (!$scope.user.registered) {
            $window.location = "/";
        }
        if ($scope.user.authenticated) {
            $scope.newKeyLength = $scope.user.keyLength;
        }
    });

    $scope.changeMasterPassword = function() {
        if (!$scope.newMasterPassword1) {
            $scope.errorMessage = 'The New Password is missing!';
            return;
        }
        if ($scope.newMasterPassword1 != $scope.newMasterPassword2) {
            $scope.errorMessage = 'The two passwords are not the same!';
            return;
        }
        var hash = messageDigest($scope.user.masterPasswordHashAlgorithm, $scope.user.userId, $scope.modelMasterPwd);
        var domains = res.PasswordService.retrieve({masterPasswordHash: hash}, function () {
            var masterKey = deriveKey($scope.modelMasterPwd, $scope.user.userId, $scope.user.iterations,
                                        $scope.user.keyLength, $scope.user.pbkdf2Algorithm);
            var data = res.PublicService.recommendedSettings(function () {
                var newIterations = data.recommendedIterations;
                var newPbkdf2Algorithm = data.recommendedPbkdf2Algorithm;
                var newMasterPasswordHashAlgorithm = data.recommendedMasterPasswordHashAlgorithm;
                var newMasterPasswordHash = messageDigest(newMasterPasswordHashAlgorithm, $scope.user.userId, $scope.newMasterPassword1);
                var newMasterKey = deriveKey($scope.newMasterPassword1, $scope.user.userId, newIterations,
                                                $scope.newKeyLength, newPbkdf2Algorithm);
                for (var i = 0; i < domains.length; i++) {
                    var domain = domains[i];
                    var decodedPassword = decode(domain.hex, masterKey,
                        domain.iv ? forge.util.hexToBytes(domain.iv) : "", $scope.user.cipherAlgorithm);
                    var newIv = forge.random.getBytesSync(16);
                    domain.iv = forge.util.bytesToHex(newIv);
                    domain.hex = encode(decodedPassword, newMasterKey, newIv, $scope.user.cipherAlgorithm);
                }
                res.PasswordService.changeAllHex({
                        masterPasswordHash: newMasterPasswordHash,
                        masterPasswordHashAlgorithm: newMasterPasswordHashAlgorithm,
                        iterations: newIterations,
                        cipherAlgorithm: $scope.user.cipherAlgorithm,
                        keyLength: $scope.newKeyLength,
                        pbkdf2Algorithm: newPbkdf2Algorithm
                    },
                    domains, function() {
                        $scope.successMessage = "Master Password successfully changed";
                });
            });
        }, function errorCallback(response) {
            $scope.errorMessage = 'Your Master Password is wrong!';
        });
    };
});