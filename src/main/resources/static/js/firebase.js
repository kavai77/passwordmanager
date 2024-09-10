function initFirebase($scope, $http, res, authFunction) {
    var firebaseConfig = {
        apiKey: "AIzaSyCxK28KvWtW8A_aOm1TVw65Q3MOzLxomRo",
        authDomain: "passwordmanager-1166.firebaseapp.com",
        databaseURL: "https://passwordmanager-1166.firebaseio.com",
        projectId: "passwordmanager-1166",
        storageBucket: "passwordmanager-1166.appspot.com",
        messagingSenderId: "229154784415",
        appId: "1:229154784415:web:0d3563dc7db8ad12690eaa"
    };
    firebase.initializeApp(firebaseConfig);

    firebase.auth().onAuthStateChanged(function (user) {
        if (user) { // User is signed in!);
            user.getIdToken()
                .then(function(result) {
                    $http.defaults.headers.common['X-Authorization-Firebase'] = result;
                    $scope.user = res.PublicService.authenticate(authFunction);
                });
        } else {
            $http.defaults.headers.common['X-Authorization-Firebase'] = null;
            $scope.user = {}
            $scope.user.authenticated = false;
            $scope.user.$resolved = true;
        }
    });
    firebase.auth().onIdTokenChanged(function (user) {
        if (user) { // User is signed in!);
            user.getIdToken()
                .then(function(result) {
                    $http.defaults.headers.common['X-Authorization-Firebase'] = result;
                });
        }
    });
    $scope.loginWithGoogle = function () {
        let provider = new firebase.auth.GoogleAuthProvider();
        firebase.auth().signInWithPopup(provider);
    }
    $scope.logout = function () {
        firebase.auth().signOut();
    }
}
