// automatic reload (globale)
// date display + sort
// type person / company
// linkedin company
// wikipedi + wikipedia company

angular.module('rubiks_module', [])
	
	.controller('RubiksCtrl', ["$rootScope", "$scope", "$http", "$q", "$location", "$timeout", function($rootScope, $scope, $http, $q, $location, $timeout) {
	
		$rootScope.header = "Rubiks";
		
		$scope.infofeedCollection = "infoFeeds";
		
		$scope.highAvailability = false;
		
		$scope.isNotEmpty = function(object) {
			return object != null && Object.keys(object).length > 0;
		};
		
		$scope.getObjectSize = function (object) {
			if(object != null && object.items != null)
				return Object.keys(object.items).length;
			return 0;
		};
			
		$scope.retrieveKeys = function (object) {
			if(object != null)
				return Object.keys(object);
		};

		$scope.isArray = function(object) {
			return angular.isArray(object);
		};
			
		$scope.retrieveFirstValue = function(object, key) {
			if(object != null && key != null)
			{
				if(object[key] != null)
				{
					if($scope.isArray(object[key]) && object[key].length > 0)
						return object[key][0];
					else
						return object[key];
				}
			}
		};
			
		$scope.normalizeImageName = function (facetName, facetValue) {
			var folder = 'service_types';
			if(facetName == 'status') 
				folder = 'infofeed_status';
			
			if(facetValue != null)
			{
				facetValue = facetValue.replace(/^.{2,3}\./g, '');
				return 'images/' + folder + '/' + facetValue + '.png';
			}
		};
		
		$scope.initialize = function() {
			// bind the function and object to 'shared' to use them in directives
			$scope.shared = {};
			
			var toShares = ["isArray", "normalizeImageName", "infofeedCollection", "retrieveFirstValue"];
			
			toShares.forEach(function(toShare){
				if($scope[toShare] != null)
					$scope.shared[toShare] = $scope[toShare];
				else
					console.warn("$scope[" + toShare+ "] is null");
			});

			$scope.executeCubeMove(null, null, null, null);
		}
		
		$scope.moveCube = function(axe, level, direction) {
			$scope.executeCubeMove(axe, level, direction, null);
		};
		
		$scope.moveCubeMagicMove = function(magicMove) {
			$scope.executeCubeMove(null, null, null, magicMove);
		};
		
		$scope.executeCubeMove = function(axe, level, direction, magicMove) {
			var jsonParams = {"command": "RetrieveCube", "axe": axe, "level": level, "direction": direction, "magicMove": magicMove, "devMode": false, "cubeJSON": ($scope.rubiksData != null) ? $scope.rubiksData.cubeJSON : null};
			if($scope.highAvailability)
				jsonParams["highAvailability"] = $scope.highAvailability;
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data != null) {
							$scope.rubiksData = data;
							$scope.rubiksAnalysis = (data.cubeJSON != null) ? data.cubeJSON.cubeAnalysis : null;
						}
					}
				);
		};
		
		$scope.loadDashboard = function () {
			
			var promiseRelatedUrls = $scope.loadInfoFeeds();

			var promiseWorkflows = $scope.loadWorkflows();
			
			var promiseServiceGroups = $scope.loadServiceGroups();
			
			var promiseBindingCollections = $scope.loadBindingCollections();
			
			$q.all([promiseRelatedUrls, promiseWorkflows, promiseServiceGroups, promiseBindingCollections]).then(function(){
				$timeout($scope.loadDashboard, 1000 * 5);
			});
		};
		
		$scope.infoFeedsQuery = {
			"page": 0, "rows": 10, "total": null,
			"facetFilters": {"status": null, "mimeType": null}, "filterTotal": null
		};

		$scope.handleAjaxResponseChange = function (ajaxCallName, data) {
			if($scope.ajaxResponse == null)
				$scope.ajaxResponse = {};
			var result = $scope.ajaxResponse[ajaxCallName] == null || (! angular.equals($scope.ajaxResponse[ajaxCallName], data))
			$scope.ajaxResponse[ajaxCallName] = data;
			return result;
		};
}]);

