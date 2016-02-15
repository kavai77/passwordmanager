var app=angular.module('app', ["xeditable"]);

app.run(function(editableOptions) {
    editableOptions.theme = 'bs3';
});

app.controller('ctrl', function ($scope, $http, $timeout) {
    var timeLockInMillis = 300000; // 5 minutes

    var defaultServerError = function errorCallback(response) {
        $scope.errorMessage = 'Oops! Something went wrong :-(';
    };

    $scope.copySupported = document.queryCommandSupported('copy');

    $http.get('/service/userService').then(function successCallback(response) {
        $scope.user = response.data;
    }, defaultServerError);

    $scope.showOrHidePassword = function(domain) {
        $scope.clearMessages();
        var thisShown = domain.shownPassword;
        for (i in $scope.domains) {
            $scope.domains[i].shownPassword = false;
            $scope.domains[i].decodedPassword = '';
        }
        if (!thisShown) {
            domain.shownPassword = true;
            var iv = domain.iv ? forge.util.hexToBytes(domain.iv) : "";
            domain.decodedPassword = decode(domain.hex, $scope.masterKey, iv);
        }
    };
    $scope.copyPassword = function(domain) {
        $scope.clearMessages();
        var iv = domain.iv ? forge.util.hexToBytes(domain.iv) : "";
        var decodedPwd = decode(domain.hex, $scope.masterKey, iv);
        var successful = copyTextToClipboard(decodedPwd);
        if (successful) {
            $scope.successMessage = 'Password copied.'
        } else {
            $scope.errorMessage = 'Oops! Unable to copy. Make the password visible and copy it manually :-('
        }
    };
    $scope.addPassword = function () {
        $scope.clearMessages();
        $scope.newDomainClass = '';
        $scope.newPasswordClass = '';
        if (!$scope.newDomain) {
            $scope.errorMessage = 'Please provide your New Domain!';
            $scope.newDomainClass = 'has-error';
            return;
        }
        if (!$scope.newPassword) {
            $scope.errorMessage = 'Please provide your New Password!';
            $scope.newPasswordClass = 'has-error';
            return;
        }
        var iv = forge.random.getBytesSync(16);
        var hex = encode($scope.newPassword, $scope.masterKey, iv);
        $http({
            method: "post",
            url: "/service/store",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "domain=" + $scope.newDomain + "&hex=" + hex + "&iv=" + forge.util.bytesToHex(iv)
        }).then(function successCallback(response){
            $scope.domains.push(response.data);
            $scope.newDomain = null;
            $scope.newPassword = null;
        }, defaultServerError);
    };
    $scope.masterPasswordLogin = function () {
        if (!$scope.modelMasterPwd) {
            $scope.errorMessage = 'Please provide your Master Password!';
            return;
        }
        var md5Hash = md5($scope.modelMasterPwd);
        $http({
            method: "post",
            url: "/service/encodedUserId/check",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "md5Hash=" + md5Hash
        }).then(function successCallback(response) {
            $scope.clearMessages();
            $scope.masterKey = deriveKey($scope.modelMasterPwd, $scope.user.userId, $scope.user.iterations);
            $scope.modelMasterPwd = null;
            $http.get('/service/retrieve').then(function successCallback(response) {
                $scope.domains = response.data;
            }, defaultServerError);
            $timeout(function() {
                $scope.masterKey=null;
            }, timeLockInMillis); // 5 minutes
        }, function errorCallback(response) {
            $scope.errorMessage = 'Your Master Password is wrong!';
        });
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
        $scope.clearMessages();
        var hex = md5($scope.newMasterPassword1);
        $http({
            method: "post",
            url: "/service/encodedUserId/store",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "md5Hash=" + hex
        }).then(function successCallback(response){
            $scope.user.encodedUserId = true;
            $scope.masterPassword = $scope.newMasterPassword1;
            $scope.newMasterPassword1 = null;
            $scope.newMasterPassword2 = null;
        }, defaultServerError);
    };
    $scope.randomPassword = function() {
        $scope.clearMessages();
        $http.get('/service/secureRandom').then(function successCallback(response) {
            $scope.newPassword = $scope.jsRandomPasswordEnhancer(response.data);
        }, defaultServerError);
    };
    $scope.jsRandomPasswordEnhancer = function(password) {
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for( var i = 0; i < 7; i++ ) {
            var char = possible.charAt(Math.floor(Math.random() * possible.length));
            var index = Math.floor(Math.random() * password.length);
            password = password.slice(0, index) + char + password.slice(index);
        }

        return password;
    };
    $scope.updateDomain = function(domain, data) {
        if (!data) {
            return false;
        }
        var beforeUpdate = domain.domain;
        $http({
            method: "post",
            url: "/service/changeDomain",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "id=" + domain.id + "&domain=" + data
        }).then(function successCallback(response){
        }, function errorCallback(response) {
            $scope.errorMessage = 'Oops! Something went wrong :-(';
            domain.domain = beforeUpdate;
        });
        return true;
    };
    $scope.hoverOverDomain = function(domain) {
        domain.showDomainEditButton = true;
    };
    $scope.leaveHoverOverDomain = function(domain) {
        domain.showDomainEditButton = false;
    };
    $scope.hoverOrLeaveOverDomain = function(domain) {
        domain.showDomainEditButton = !domain.showDomainEditButton;
    };
    $scope.updatePassword = function(domain, data) {
        if (!data) {
            return false;
        }
        var beforeUpdate = domain.decodedPassword;
        var iv = forge.random.getBytesSync(16);
        var hex = encode(data, $scope.masterKey, iv);
        $http({
            method: "post",
            url: "/service/changeHex",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "id=" + domain.id + "&hex=" + hex + "&iv=" + forge.util.bytesToHex(iv)
        }).then(function successCallback(response){
            domain.hex = hex;
            domain.iv = forge.util.bytesToHex(iv);
        }, function errorCallback(response) {
            $scope.errorMessage = 'Oops! Something went wrong :-(';
            domain.decodedPassword = beforeUpdate;
        });
        return true;
    };
    $scope.prepareDeleteDomain = function(domain) {
        $scope.domainToBeDeleted = domain;
    };
    $scope.deleteDomain = function() {
        $http({
            method: "post",
            url: "/service/deletePassword",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "id=" + $scope.domainToBeDeleted.id
        }).then(function successCallback(response){
            var index = $scope.domains.indexOf($scope.domainToBeDeleted);
            $scope.domains.splice(index, 1);
        }, defaultServerError);
    };
    $scope.clearMessages = function() {
        $scope.errorMessage = null;
        $scope.successMessage = null;
    };
});