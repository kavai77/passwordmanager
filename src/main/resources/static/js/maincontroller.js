var app=angular.module('app', ["ngResource", "xeditable", "nonStringSelect", "ui.bootstrap-slider", "focus-if", "ui.bootstrap"]);

app.run(function(editableOptions) {
    editableOptions.theme = 'bs3';
});

app.directive('bsPopover', function() {
    return function(scope, element, attrs) {
        $('[data-toggle="popover"]').popover();
    };
});

app.controller('ctrl', function ($scope, $interval, $window, $timeout, $resource, $http) {
    $scope.copySupported = document.queryCommandSupported('copy');

    $scope.isActive = function (viewLocation) {
        return viewLocation === window.location.pathname;
    };

    var res = initResources($scope, $resource);

    initFirebase($scope, $http, res);

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
    $scope.copyUserName = function(domain, index) {
        clearMessages();
        new Clipboard('.btn', {
            text: function(trigger) {
                return domain.userName;
            }
        });
        $timeout(function () {
            $("#copyUserName1Button" + index).popover('hide');
            $("#copyUserName2Button" + index).popover('hide');
        }, 3000);
    };
    $scope.copyPassword = function(domain, index) {
        clearMessages();
        var iv = domain.iv ? forge.util.hexToBytes(domain.iv) : "";
        var decodedPwd = decode(domain.hex, $scope.masterKey, iv, $scope.user.cipherAlgorithm);
        new Clipboard('.btn', {
            text: function(trigger) {
                return decodedPwd;
            }
        });
        $timeout(function () {
            $("#copyPassword1Button" + index).popover('hide');
            $("#copyPassword2Button" + index).popover('hide');
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
        if (!$scope.newUserName) {
            $scope.newUserName = "";
        } else if ($scope.userNames.indexOf($scope.newUserName) == -1) {
            $scope.userNames.push($scope.newUserName);
        }
        var iv = forge.random.getBytesSync(16);
        var hex = encode($scope.newPassword, $scope.masterKey, iv, $scope.user.cipherAlgorithm);
        var newDomain = res.PasswordService.store({domain: $scope.newDomain, userName: $scope.newUserName, hex: hex,
                                                    iv: forge.util.bytesToHex(iv)}, function(){
            $scope.domains.unshift(newDomain);
            $scope.newDomain = null;
            $scope.newUserName = null;
            $scope.newPassword = null;
            $scope.serverPassword = null;
            $scope.successMessage = "Your password has been successfully stored."
        });
        $('#newPasswordDialog').modal('hide');

    };
    $('#newPasswordDialog').on('shown.bs.modal', function (e) {
        $scope.newPasswordDialogOpen = true;
    });
    $('#newPasswordDialog').on('hidden.bs.modal', function (e) {
        $scope.newPasswordDialogOpen = false;
    });
    $scope.masterPasswordLogin = function () {
        if (!$scope.modelMasterPwd) {
            $scope.errorMessage = 'Please provide your Master Password!';
            return;
        }
        $('#loadingDialog').modal('show');
        var hash = messageDigest($scope.user.masterPasswordHashAlgorithm, $scope.user.userId, $scope.modelMasterPwd);
        $scope.domains = res.PasswordService.retrieve({masterPasswordHash: hash}, function successCallback() {
            clearMessages();
            $scope.masterKey = deriveKey($scope.modelMasterPwd, $scope.user.salt, $scope.user.iterations, $scope.user.keyLength, $scope.user.pbkdf2Algorithm);
            $scope.modelMasterPwd = null;
            var userNames = [];
            for (i = 0; i < $scope.domains.length; i++) {
                userName = $scope.domains[i].userName;
                if (userName && userNames.indexOf(userName) == -1) {
                    userNames.push(userName);
                }
            }
            $scope.userNames = userNames;

            $interval(function() {
                $scope.timeLockExpires = $scope.lastAction + $scope.user.userSettings.timeoutLengthSeconds * 1000 - new Date().getTime();
                if ($scope.timeLockExpires < 0) {
                    $window.location.reload();
                }
            }, 1000);
            $('#loadingDialog').modal('hide');
        }, function errorCallback(response) {
            $scope.errorMessage = 'Your Master Password is wrong!';
            $('#loadingDialog').modal('hide');
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
        var hash = messageDigest($scope.user.masterPasswordHashAlgorithm, $scope.user.userId, $scope.newMasterPassword1);
        res.UserService.register({
            masterPasswordHash: hash,
            masterPasswordHashAlgorithm: $scope.user.masterPasswordHashAlgorithm,
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
        var serverPasswordResource = res.PublicService.secureRandom(function() {
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
    $scope.updateDomain = function(domain, domainName) {
        if (!domainName) {
            return false;
        }
        clearMessages();
        var beforeUpdate = domain.domain;
        res.PasswordService.changeDomain({id: domain.id, domain: domainName}, function() {}, function errorCallback() {
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
    $scope.updateUserName = function(domain, userName) {
        if (!userName) {
            userName = "";
        } else if ($scope.userNames.indexOf(userName) == -1) {
             $scope.userNames.push(userName);
        }
        clearMessages();
        var beforeUpdate = domain.userName;
        res.PasswordService.changeUserName({id: domain.id, userName: userName}, function() {}, function errorCallback() {
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
    $scope.updatePassword = function(domain, password) {
        if (!password) {
            return false;
        }
        clearMessages();
        var beforeUpdate = domain.decodedPassword;
        var iv = forge.random.getBytesSync(16);
        var hex = encode(password, $scope.masterKey, iv, $scope.user.cipherAlgorithm);
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
    $scope.daysElapsed = function (date) {
        return (new Date().getTime() - date) / 86400000;
    }
});