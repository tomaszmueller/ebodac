(function() {
    'use strict';

    /* Controllers */
    var controllers = angular.module('ebodac.controllers', []);

    $.postJSON = function(url, data, callback) {
        return jQuery.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'type': 'POST',
            'url': url,
            'data': JSON.stringify(data),
            'dataType': 'json',
            'success': callback,
        });
    };

    controllers.controller('EbodacController', function($scope, $http) {

    });

}());
