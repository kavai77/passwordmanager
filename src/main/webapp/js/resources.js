function initResources($scope, $resource) {
    var defaultServerError = function () {
        $scope.errorMessage = 'Oops! Something went wrong :-(';
    };

    var urlEncoded = 'application/x-www-form-urlencoded';
    var urlEncodedTransform = function(data) {
        var str = [];
        for(var p in data)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(data[p]));
        return str.join("&");
    };

    var resource = new Object();

    resource.Authenticate = $resource('/service/public/authenticate', {}, {
        get: {interceptor: {responseError : defaultServerError}}
    });

    resource.UserService = $resource('/service/secure/user/:action', {}, {
        getUserData: {params: {action: 'userService'}, interceptor: {responseError : defaultServerError}},
        recommendedSettings: {params: {action: 'recommendedSettings'}, interceptor: {responseError : defaultServerError}},
        checkMd5Hash: {method: 'POST', params: {action: 'check'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform},
        register: {method: 'POST', params: {action: 'register'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}}
    });

    resource.SecureRandom = $resource('/service/public/secureRandom', {}, {
        get: {transformResponse: function(data) {return {value: data};}, interceptor: {responseError : defaultServerError}}
    });

    resource.PasswordService = $resource('/service/secure/password/:action', {}, {
        store: {method: 'POST', params: {action: 'store'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeDomain: {method: 'POST', params: {action: 'changeDomain'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeHex: {method: 'POST', params: {action: 'changeHex'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeAllHex: {method: 'POST', params: {action: 'changeAllHex'}, interceptor: {responseError : defaultServerError}},
        deletePassword: {method: 'POST', params: {action: 'deletePassword'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        retrieve: {params: {action: 'retrieve'}, isArray:true, interceptor: {responseError : defaultServerError}}
    });

    resource.BackupService = $resource('/service/secure/backup/:action', {}, {
        retrieve: {params: {action: 'retrieve'}, isArray:true, interceptor: {responseError : defaultServerError}},
        create: {method: 'POST', params: {action: 'create'}, interceptor: {responseError : defaultServerError}},
        restore: {method: 'POST', params: {action: 'restore'}, headers: {'Content-Type': urlEncoded}, transformRequest: urlEncodedTransform},
        remove: {method: 'DELETE', params: {action: 'remove'}, interceptor: {responseError : defaultServerError}},
    });

    return resource;

}