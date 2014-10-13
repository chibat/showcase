var interval = 10000;

var ajaxPollingApp = angular.module('ajaxPollingApp', []);

ajaxPollingApp.controller('AjaxPollingCtrl', function($scope: any, $timeout: any) {

    $scope.disp = function() {
        $scope.tableFlag = true;
        $scope.detailFlag = false;
    }

    $scope.dispDetail = function(record: any) {
        $scope.tableFlag = false;
        $scope.detailFlag = true;
        $scope.detail = record;
    }

    $scope.startProgress = function(id: number) {
        $scope.tableFlag = false;
        $scope.detailFlag = false;

        $.ajax({
            url: "/ajaxPolling/startProgress.json",
            type: "POST",
            data: "id=" + id,
            async: false,
            success: function(data) {
                if (data.length > 0) {
                    $scope.requestCount = data.length;
                    $scope.records = data;
                } else if (data.length === 0) {
                    $scope.requestCount = null;
                }
            },
        });
    }

    var polling = function() {
        $.ajax({
            url: "/ajaxPolling/get.json",
            async: false,
            success: function(data) {
                if (data.length > 0) {
                    $scope.requestCount = data.length;
                    $scope.records = data;
                } else if (data.length === 0) {
                    $scope.requestCount = null;
                }
            },
            complete: function() {
                $timeout(polling, interval);
            }
        });
    };

    polling();

});
