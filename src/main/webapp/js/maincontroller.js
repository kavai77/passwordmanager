var app=angular.module('app', ["ngResource", "xeditable", "nonStringSelect", "ui.bootstrap-slider", "focus-if"]);

app.run(function(editableOptions) {
    editableOptions.theme = 'bs3';
});

app.directive('bsPopover', function() {
    return function(scope, element, attrs) {
        $('[data-toggle="popover"]').popover();
    };
});

app.controller('ctrl', function ($scope, $interval, $window, $timeout, $resource) {
    var timeLockInMillis = 300000; // 5 minutes

    var defaultServerError = function () {
        $scope.errorMessage = 'Oops! Something went wrong :-(';
    };

    $scope.copySupported = document.queryCommandSupported('copy');
    $scope.passwordLength = 27;

    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    var urlEncoded = 'application/x-www-form-urlencoded';
    var urlEncodedTransform = function(data) {
        var str = [];
        for(var p in data)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(data[p]));
        return str.join("&");
    };
    var Authenticate = $resource('/service/public/authenticate', {}, {
        get: {interceptor: {responseError : defaultServerError}}
    });
    var UserService = $resource('/service/secure/user/:action', {}, {
        getUserData: {params: {action: 'userService'}, interceptor: {responseError : defaultServerError}},
        checkMd5Hash: {method: 'POST', params: {action: 'check'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform},
        register: {method: 'POST', params: {action: 'register'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}}
    });
    var SecureRandom = $resource('/service/public/secureRandom', {}, {
        get: {transformResponse: function(data) {return {value: data};}, interceptor: {responseError : defaultServerError}}
    });
    var PasswordService = $resource('/service/secure/password/:action', {}, {
        store: {method: 'POST', params: {action: 'store'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeDomain: {method: 'POST', params: {action: 'changeDomain'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeHex: {method: 'POST', params: {action: 'changeHex'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        deletePassword: {method: 'POST', params: {action: 'deletePassword'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        retrieve: {params: {action: 'retrieve'}, isArray:true, interceptor: {responseError : defaultServerError}}
    });

    $scope.auth = Authenticate.get(function() {
        if ($scope.auth.authenticated) {
            $scope.user = UserService.getUserData();
        }
    });

    $scope.showOrHidePassword = function(domain) {
        clearMessages();
        var thisShown = domain.shownPassword;
        for (i in $scope.domains) {
            $scope.domains[i].shownPassword = false;
            $scope.domains[i].decodedPassword = '';
        }
        if (!thisShown) {
            domain.shownPassword = true;
            var iv = domain.iv ? forge.util.hexToBytes(domain.iv) : "";
            domain.decodedPassword = decode(domain.hex, $scope.masterKey, iv, $scope.user.cipherAlgorithm);
        }
    };
    $scope.copyPassword = function(domain, event) {
        clearMessages();
        var iv = domain.iv ? forge.util.hexToBytes(domain.iv) : "";
        var decodedPwd = decode(domain.hex, $scope.masterKey, iv, $scope.user.cipherAlgorithm);
        new Clipboard('.btn', {
            text: function(trigger) {
                return decodedPwd;
            }
        });
        $timeout(function () {
            $("#" + event.target.id).popover('hide');
        }, 3000);
    };
    $scope.addPassword = function () {
        clearMessages();
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
        var hex = encode($scope.newPassword, $scope.masterKey, iv, $scope.user.cipherAlgorithm);
        var newDomain = PasswordService.store({domain: $scope.newDomain, hex: hex, iv: forge.util.bytesToHex(iv)}, function(){
            $scope.domains.push(newDomain);
            $scope.newDomain = null;
            $scope.newPassword = null;
            $scope.serverPassword = null;
        });
    };
    $scope.masterPasswordLogin = function () {
        if (!$scope.modelMasterPwd) {
            $scope.errorMessage = 'Please provide your Master Password!';
            return;
        }
        UserService.checkMd5Hash({md5Hash: md5($scope.modelMasterPwd)}, function successCallback() {
            clearMessages();
            $scope.masterKey = deriveKey($scope.modelMasterPwd, $scope.user.userId, $scope.user.iterations, $scope.user.keyLength, $scope.user.pbkdf2Algorithm);
            $scope.modelMasterPwd = null;

            $scope.domains = PasswordService.retrieve();

            $interval(function() {
                $scope.timeLockExpires = $scope.lastAction + timeLockInMillis - new Date().getTime();
                if ($scope.timeLockExpires < 0) {
                    $window.location.reload();
                }
            }, 1000);
        }, function errorCallback(response) {
            $scope.errorMessage = 'Your Master Password is wrong!';
        });
    };
    $scope.registerUser = function () {
        if (!$scope.newMasterPassword1) {
            $scope.errorMessage = 'The New Password is missing!';
            return;
        }
        if ($scope.newMasterPassword1 != $scope.newMasterPassword2) {
            $scope.errorMessage = 'The two passwords are not the same!';
            return;
        }
        clearMessages();
        UserService.register({
            md5Hash: md5($scope.newMasterPassword1),
            iterations: $scope.user.iterations,
            cipherAlgorithm: $scope.user.cipherAlgorithm,
            keyLength: $scope.user.keyLength,
            pbkdf2Algorithm: $scope.user.pbkdf2Algorithm
        }, $window.location.reload());
    };
    $scope.generateRandomPassword = function() {
        clearMessages();
        var serverPasswordResource = SecureRandom.get(function() {
            $scope.serverPassword = serverPasswordResource.value;
            $scope.jsRandomPassword();
        });
    };
    $scope.jsRandomPassword = function() {
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        var startIndex = Math.floor(Math.random() * $scope.serverPassword.length );
        var password = $scope.serverPassword.concat($scope.serverPassword).slice(startIndex, startIndex + $scope.passwordLength / 2);
        while( password.length < $scope.passwordLength ) {
            var char = possible.charAt(Math.floor(Math.random() * possible.length));
            var index = Math.floor(Math.random() * password.length);
            password = password.slice(0, index) + char + password.slice(index);
        }

        $scope.newPassword =  password;
    };
    $scope.updateDomain = function(domain, data) {
        if (!data) {
            return false;
        }
        clearMessages();
        var beforeUpdate = domain.domain;
        PasswordService.changeDomain({id: domain.id, domain: data}, function() {}, function errorCallback() {
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
        clearMessages();
        var beforeUpdate = domain.decodedPassword;
        var iv = forge.random.getBytesSync(16);
        var hex = encode(data, $scope.masterKey, iv, $scope.user.cipherAlgorithm);
        var ivHex = forge.util.bytesToHex(iv);
        PasswordService.changeHex({id: domain.id, hex: hex, iv: ivHex}, function successCallback() {
            domain.hex = hex;
            domain.iv = ivHex;
        }, function errorCallback() {
            domain.decodedPassword = beforeUpdate;
        });
        return true;
    };
    $scope.prepareDeleteDomain = function(domain) {
        $scope.domainToBeDeleted = domain;
    };
    $scope.deleteDomain = function() {
        clearMessages();
        PasswordService.deletePassword({id: $scope.domainToBeDeleted.id} , function() {
            var index = $scope.domains.indexOf($scope.domainToBeDeleted);
            $scope.domains.splice(index, 1);
        });
    };
    var clearMessages = function() {
        $scope.errorMessage = null;
        $scope.successMessage = null;
        $scope.lastAction = new Date().getTime();
    };
    $scope.stripString = function (string, limit) {
        if (string.length > limit) {
            return string.substring(0, limit) + "...";
        } else {
            return string;
        }
    }
});