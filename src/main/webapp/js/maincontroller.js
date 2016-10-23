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
    $scope.copySupported = document.queryCommandSupported('copy');

    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    var res = initResources($scope, $resource);

    $scope.user = res.Authenticate.get();

    $scope.showOrHidePassword = function(domain) {
        clearMessages();
        var thisShown = domain.shownPassword;
        for (var i = 0; i < $scope.domains.length; i++) {
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
            $scope.newDomainClass = 'has-error';
            return;
        }
        if (!$scope.newPassword) {
            $scope.newPasswordClass = 'has-error';
            return;
        }
        var iv = forge.random.getBytesSync(16);
        var hex = encode($scope.newPassword, $scope.masterKey, iv, $scope.user.cipherAlgorithm);
        var newDomain = res.PasswordService.store({domain: $scope.newDomain, userName: $scope.newUserName, hex: hex,
                                                    iv: forge.util.bytesToHex(iv)}, function(){
            $scope.domains.push(newDomain);
            $scope.newDomain = null;
            $scope.newUserName = null;
            $scope.newPassword = null;
            $scope.serverPassword = null;
            $scope.successMessage = "Your password has been successfully stored."
        });
        $('#newPasswordDialog').modal('hide');
    };
    $scope.masterPasswordLogin = function () {
        if (!$scope.modelMasterPwd) {
            $scope.errorMessage = 'Please provide your Master Password!';
            return;
        }
        res.UserService.checkMd5Hash({md5Hash: md5($scope.modelMasterPwd)}, function successCallback() {
            clearMessages();
            $scope.masterKey = deriveKey($scope.modelMasterPwd, $scope.user.userId, $scope.user.iterations, $scope.user.keyLength, $scope.user.pbkdf2Algorithm);
            $scope.modelMasterPwd = null;

            $scope.domains = res.PasswordService.retrieve();

            $interval(function() {
                $scope.timeLockExpires = $scope.lastAction + $scope.user.userSettings.timeoutLengthSeconds * 1000 - new Date().getTime();
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
        res.UserService.register({
            md5Hash: md5($scope.newMasterPassword1),
            iterations: $scope.user.iterations,
            cipherAlgorithm: $scope.user.cipherAlgorithm,
            keyLength: $scope.user.keyLength,
            pbkdf2Algorithm: $scope.user.pbkdf2Algorithm
        }, $window.location.reload());
    };
    $scope.generateRandomPassword = function() {
        clearMessages();
        if ($scope.passwordLength == null) {
            $scope.passwordLength = $scope.user.userSettings.defaultPasswordLength;
        }
        var serverPasswordResource = res.SecureRandom.get(function() {
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
        res.PasswordService.changeDomain({id: domain.id, domain: data}, function() {}, function errorCallback() {
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
    $scope.updateUserName = function(domain, data) {
        if (!data) {
            return false;
        }
        clearMessages();
        var beforeUpdate = domain.userName;
        res.PasswordService.changeUserName({id: domain.id, userName: data}, function() {}, function errorCallback() {
            domain.userName = beforeUpdate;
        });
        return true;
    };
    $scope.hoverOverUserName = function(domain) {
        domain.showUserNameEditButton = true;
    };
    $scope.leaveHoverOverUserName = function(domain) {
        domain.showUserNameEditButton = false;
    };
    $scope.hoverOrLeaveOverUserName = function(domain) {
        domain.showUserNameEditButton = !domain.showUserNameEditButton;
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
        res.PasswordService.changeHex({id: domain.id, hex: hex, iv: ivHex}, function successCallback() {
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
        res.PasswordService.deletePassword({id: $scope.domainToBeDeleted.id} , function() {
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