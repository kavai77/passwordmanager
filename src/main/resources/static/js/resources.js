function initResources($scope, $resource) {
    let defaultServerError = function () {
        $scope.errorMessage = 'Oops! Something went wrong :-(';
    };

    let urlEncodedHeaders = {'Content-Type': 'application/x-www-form-urlencoded'}
    var urlEncodedTransform = function(data) {
        var str = [];
        for(var p in data)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(data[p]));
        return str.join("&");
    };

    let resource = {};

    resource.PublicService = $resource('/public/:action', {}, {
        recommendedSettings: {params: {action: 'recommendedSettings'}, interceptor: {responseError : defaultServerError}},
        authenticate: {params: {action: 'authenticate'}, interceptor: {responseError : defaultServerError}},
        secureRandom: {params: {action: 'secureRandom'}, transformResponse: function(data) {return {value: data};}, interceptor: {responseError : defaultServerError}}
    });


    resource.UserService = $resource('/secure/user/:action', {}, {
        register: {method: 'POST', params: {action: 'register'}, headers: urlEncodedHeaders, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        updateUserSettings: {method: 'POST', params: {action: 'userSettings'}, interceptor: {responseError : defaultServerError}}
    });

    resource.PasswordService = $resource('/secure/password/:action', {}, {
        store: {method: 'POST', params: {action: 'store'}, headers: urlEncodedHeaders, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeDomain: {method: 'POST', params: {action: 'changeDomain'}, headers: urlEncodedHeaders, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeUserName: {method: 'POST', params: {action: 'changeUserName'}, headers: urlEncodedHeaders, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeHex: {method: 'POST', params: {action: 'changeHex'}, headers: urlEncodedHeaders, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        changeAllHex: {method: 'POST', params: {action: 'changeAllHex'}, interceptor: {responseError : defaultServerError}},
        deletePassword: {method: 'POST', params: {action: 'deletePassword'}, headers: urlEncodedHeaders, transformRequest: urlEncodedTransform, interceptor: {responseError : defaultServerError}},
        retrieve: {params: {action: 'retrieve'}, isArray:true}
    });

    resource.BackupService = $resource('/secure/backup/:action', {}, {
        retrieve: {params: {action: 'retrieve'}, isArray:true, interceptor: {responseError : defaultServerError}},
        create: {method: 'POST', params: {action: 'create'}, interceptor: {responseError : defaultServerError}},
        restore: {method: 'POST', params: {action: 'restore'}, headers: urlEncodedHeaders, transformRequest: urlEncodedTransform},
        remove: {method: 'DELETE', params: {action: 'remove'}, interceptor: {responseError : defaultServerError}},
    });

    return resource;

}