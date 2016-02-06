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
            domain.decodedPassword = decode(domain.hex, $scope.masterPassword, $scope.user.userId);
        }
    };
    $scope.copyPassword = function(domain) {
        $scope.clearMessages();
        var decodedPwd = decode(domain.hex, $scope.masterPassword, $scope.user.userId);
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
        var hex = encode($scope.newPassword, $scope.masterPassword, $scope.user.userId);
        $http({
            method: "post",
            url: "/service/store",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "domain=" + $scope.newDomain + "&hex=" + hex
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
        var localEncodedUserId = encode($scope.user.userId, $scope.modelMasterPwd, $scope.user.userId);
        $http({
            method: "post",
            url: "/service/encodedUserId/check",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "encodedUserId=" + localEncodedUserId
        }).then(function successCallback(response) {
            $scope.clearMessages();
            $scope.masterPassword=$scope.modelMasterPwd;
            $http.get('/service/retrieve').then(function successCallback(response) {
                $scope.domains = response.data;
            }, defaultServerError);
            $timeout(function() {
                $scope.masterPassword=null;
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
        var hex = encode($scope.user.userId, $scope.newMasterPassword1, $scope.user.userId);
        $http({
            method: "post",
            url: "/service/encodedUserId/store",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "encodedUserId=" + hex
        }).then(function successCallback(response){
            $scope.user.encodedUserId = hex;
            $scope.masterPassword=$scope.newMasterPassword1;
        }, defaultServerError);
    };
    $scope.randomPassword = function() {
        $scope.clearMessages();
        $http.get('/service/secureRandom').then(function successCallback(response) {
            $scope.newPassword = response.data;
        }, defaultServerError);
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
        var hex = encode(data, $scope.masterPassword, $scope.user.userId);
        $http({
            method: "post",
            url: "/service/changeHex",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: "id=" + domain.id + "&hex=" + hex
        }).then(function successCallback(response){
            domain.hex = hex;
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