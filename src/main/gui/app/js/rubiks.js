// automatic reload (globale)
// date display + sort
// type person / company
// linkedin company
// wikipedi + wikipedia company

angular.module('rubiks_module', [])
	
	.controller('RubiksCtrl', ["$rootScope", "$scope", "$http", "$q", "$location", "$timeout", function($rootScope, $scope, $http, $q, $location, $timeout) {
	
		$rootScope.header = "Contact 360 View / Customer Matrix";
		
		$scope.infofeedCollection = "infoFeeds";
		
		$scope.tabMenus = [
			{"id": "dashboardContent", "label": "dashboard", "selected": true},
			{"id": "apiSamples", "label": "api samples", "selected": false},
			{"id": "massiveCrawlerDatabaseInfo", "label": "Database Info", "selected": false},
		];
		
		$scope.removeTypeInfofeedRunning = false;
		
		$scope.isSelectedMenu = function (tabMenuId) {
			for(var i = 0 ; i < $scope.tabMenus.length ; i++) {
				if($scope.tabMenus[i].id == tabMenuId)
					return $scope.tabMenus[i].selected;
			}
			return false;
		};
		
		$scope.switchTabMenu = function (tabMenuId) {
			if(tabMenuId != null)
			{
				$scope.tabMenus.forEach(function(tabMenu){
					tabMenu.selected = (tabMenu.id == tabMenuId);
				});
			}
		}
		
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
			
			var toShares = ["isArray", "addOrRemoveFilterAndReload", "loadInfoFeeds", "normalizeImageName", "removeInfofeed", "infofeedCollection", "retrieveFirstValue", "removeTypeInfofeed"];
			
			toShares.forEach(function(toShare){
				if($scope[toShare] != null)
					$scope.shared[toShare] = $scope[toShare];
				else
					console.warn("$scope[" + toShare+ "] is null");
			});

			var jsonParams = {"command": "RetrieveCube", "devMode": true};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						
						$scope.rubiksFaces = data.cube_faces;
					}
				);
		}
		
		$scope.moveCube = function(axe, level, direction) {
			var jsonParams = {"command": "RetrieveCube", "axe": axe, "level": level, "direction": direction, "devMode": true};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						
						$scope.rubiksFaces = data.cube_faces;
					}
				);
		};
		
		$scope.moveCubeMagicMove = function(magicMove) {
			var jsonParams = {"command": "RetrieveCube", "magicMove": magicMove, "devMode": true};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						
						$scope.rubiksFaces = data.cube_faces;
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
		}
		
		$scope.loadInfoFeeds = function (page) {
			
			var deferred = $q.defer();
			
			if(page != null)
				$scope.infoFeedsQuery.page = page;
			
			var jsonParams = {"collection": $scope.infofeedCollection,
				"start": $scope.infoFeedsQuery.page * $scope.infoFeedsQuery.rows, "rows": $scope.infoFeedsQuery.rows, 
				"facet":true, "facet.field": $scope.buildFacetParams($scope.infoFeedsQuery), "facet.limit": 10, "facet.mincount": 1,
				"sort":"status_timestamp desc",
				"fq": $scope.addFacetFilters([], $scope.infoFeedsQuery),
			};
			$http(buildHttpPostQuery(jsonParams, "rest/engineEndPoint"))
			.success(
				function(data) {
					delete data.responseHeader;
					
					if($scope.handleAjaxResponseChange('loadInfoFeeds', data))
					{
						if($scope.infoFeeds == null)
							$scope.infoFeeds = {};
						if(data["response"] != null)
						{
							$scope.infoFeeds.docs = data["response"]["docs"];
							
							if( ! $scope.hasFacetFilter($scope.infoFeedsQuery))
								$scope.infoFeedsQuery.total = data["response"].numFound;
						
							$scope.infoFeedsQuery.filterTotal = data["response"].numFound;
						}
						
						if(data["facet_counts"] != null && data["facet_counts"]["facet_fields"] != null ){
							var facetFields = data["facet_counts"]["facet_fields"];
							if($scope.infoFeeds.facets == null)
								$scope.infoFeeds.facets = {};
							
							$scope.retrieveKeys(facetFields).forEach(function(key){
								if(facetFields[key] != null) {
									$scope.infoFeeds.facets[key] = buildFacetValues(facetFields[key]);
								}
							});
						}
					}
					
					deferred.resolve(data);
				})
			.error(
				function(data) {
					deferred.resolve(data);
				});
			
			return deferred.promise;
		};
		
		$scope.loadWorkflows = function () {
			
			var deferred = $q.defer();
			
			var jsonParams = {};
			$http(buildHttpPostQuery(jsonParams, "rest/senseBuilderWorflows"))
			.success(
				function(data) {
					
					if($scope.handleAjaxResponseChange('loadWorkflows', data)) 
					{
						if(data["workflows"] != null)
						{
							$scope.workflows = data["workflows"];
							$scope.statusWorkflows = {};
							$scope.workflows.forEach(function(workflow) {
								if(workflow.status != null)
								{
									if($scope.statusWorkflows[workflow.status] == null)
										$scope.statusWorkflows[workflow.status] = [];
									$scope.statusWorkflows[workflow.status].push(workflow);
								}
							});
						}
						else
						{
							workflows = {};
						}
					}
					
					deferred.resolve(data);
				})
			.error(
				function(data) {
					deferred.resolve(data);
				});;
				
			return deferred.promise;
		};

		$scope.loadServiceGroups = function() {
			var deferred = $q.defer();
			// mimeType group count
			var jsonParams = {"collection": $scope.infofeedCollection, "rows":0, "group":true, "group.field":"mimeType", "group.ngroups":true};
			$http(buildHttpPostQuery(jsonParams, "rest/engineEndPoint"))
				.success(
					function(data) {
						$scope.totalTypeGroups = null;
						
						if(data.grouped != null && data.grouped.mimeType != null && data.grouped.mimeType.ngroups != null)
							$scope.totalTypeGroups = data.grouped.mimeType.ngroups;
						
						deferred.resolve(data);
				})
				.error(
					function(data) {
						deferred.resolve(data);
				});
				
			return deferred.promise;
		}
		
		$scope.loadBindingCollections = function() {
			var deferred = $q.defer();
			$http({"method": 'GET', "url":  "rest/bindingCollectionsCount"})
				.success(
					function(data) {
						if(data.bindingCollections != null)
							$scope.bindingCollections = data.bindingCollections;
						deferred.resolve(data);
				})
				.error(
					function(data) {
						deferred.resolve(data);
				});
			return deferred.promise;
		}
		
		$scope.initializeWorkflow = function (workflow) {
			var jsonParams = {"workflow": (workflow != null) ? workflow.name : null};
			$http(buildHttpPostQuery(jsonParams, "rest/workflowInitialize"))
			.success(
				function(data) {
					
					
					$scope.loadWorkflows();
				}
			);
		};
		
		$scope.removeInfofeed = function (infoFeed) {
			
			var isConfirmed = confirm("Are you sure you to remove: " + infoFeed.url);
			if (isConfirmed == true) {
				var jsonParams = {"infofeed_uid": infoFeed.url, "command": "removeInfoFeed"};
				$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						
						$scope.loadDashboard();
					}
				);
			}
		};		
		
		$scope.removeTypeInfofeed = function (type) {
			
			var isConfirmed = confirm("Are you sure you to remove all for type: " + type);
			if (isConfirmed == true) {
				$scope.removeTypeInfofeedRunning = true;
				var jsonParams = {"type": type};
				$http(buildHttpPostQuery(jsonParams, "rest/removeTypeEntries"))
				.success(
					function(data) {
						$scope.removeTypeInfofeedRunning = false;
					}
				);
			}
		};
}]);

angular.module('dashboard_module')
	.directive("cmresultpager", ["$q", "$http", function($q, $http) {
		return {
			restrict: 'E',
			templateUrl: 'templates/resultPager.html',
			scope: {
				page: '=',
				resultrows: '=',
				total: '=',
				functiontocall: '&'
			},
			link: function (scope, element, attrs) {
			
				scope.goToPage = function (page) {
					scope.functiontocall({"page": page});
				};
				
				scope.isNextEnabled = function () {
					return ((scope.page + 1) * scope.resultrows) < scope.total;
				};
				
				scope.isPagerEnabled = function () {
					return scope.resultrows < scope.total;
				};
			}
		}
	}])
	.directive("facetvalues", ["$q", "$http", function($q, $http) {
		return {
			restrict: 'E',
			templateUrl: 'templates/facetValues.html',
			scope: {
				shared: '=',
				facets: '=',
				query: '=',
				fieldname: '=',
			},
			link: function (scope, element, attrs) {
				scope.isSelected = function(facetValue) {
					return scope.query != null && scope.query.facetFilters != null && scope.query.facetFilters[scope.fieldname] != null 
						&& scope.query.facetFilters[scope.fieldname].value == facetValue.label;
				};
			}
		}
	}])
	.directive("headermenu", ["$q", "$http", function($q, $http){
		return {
			restrict: 'E',
			templateUrl: (undefined !== angular._templateUrlPrefix ? angular._templateUrlPrefix : '') + 'templates/headerMenu.html',
			scope: false,
			transclude: true,
			link: function (scope, element, attrs) {

			}
		}}])
	.directive("dashboardcontent", ["$q", "$http", function($q, $http){
		return {
			restrict: 'E',
			templateUrl: (undefined !== angular._templateUrlPrefix ? angular._templateUrlPrefix : '') + 'templates/dashboardContent.html',
			scope: false,
			transclude: true,
			link: function (scope, element, attrs) {

			}
		}}])
	.directive("infofeed", ["$q", "$http", function($q, $http) {
		return {
			restrict: 'E',
			templateUrl: 'templates/infoFeed.html',
			scope: {
				shared: '=',
				infofeed: '=',
			},
			link: function (scope, element, attrs) {
				if(scope.infofeed.details != null)
					scope.infofeed.details = JSON.parse(scope.infofeed.details);
			}
		}
	}])
	.directive("apisamples", ["$q", "$http", function($q, $http){
		return {
			restrict: 'E',
			templateUrl: (undefined !== angular._templateUrlPrefix ? angular._templateUrlPrefix : '') + 'templates/apisamples.html',
			scope: false,
			transclude: true,
			link: function (scope, element, attrs) {
				
				scope.jobTitleNormalizationInput = JSON.stringify([{'id':'1', 'jobTitle':'Group Treasurer', 'organization':'Organisation X'}]);
				
				scope.sendJobTitleNormalization = function() {
					$http(
						{
							"method": 'POST', 
							"url":  "rest/apiEndPoint?command=jobTitleNormalization&service=JobMiner&operation=normalize", 
							"data": scope.jobTitleNormalizationInput, 
							"headers": {'Content-Type': 'application/json;charset=UTF-8'}
						}
					).success(
						function(data) {
							
							scope.jobTitleNormalization = data;
						}
					);
				};
			}
		}}])
	.directive("massivecrawlerdatabaseinfo", ["$q", "$http", function($q, $http){
		return {
			restrict: 'E',
			templateUrl: (undefined !== angular._templateUrlPrefix ? angular._templateUrlPrefix : '') + 'templates/massiveCrawlerDatabaseInfo.html',
			scope: false,
			transclude: true,
			link: function (scope, element, attrs) {

				if(scope.databaseInfo == null)
					scope.databaseInfo = {};
			
				scope.entryTypes = ["crawling", "content", "company", "person"];
				scope.entryStatus = ["new", "processed", "downloaded", "parsed", "total", "onerror"];
				scope.statsKey = [];
				
				scope.initializeDatabaseInfo = function(type) {
					
					scope.databaseInfo[type] = null;
					
					$http({"method": 'POST', "url":  'rest/databaseInfoServlet', "params": {"type": type}})
						.success(function(data){
							if(data.databaseInfo != null && data.type != null)
								scope.databaseInfo[type] = data.databaseInfo[data.type];
							else
								console.warn("data.databaseInfo is null or data.type is null");
						});
				};
				
				scope.initializeDatabaseInfo("globalStats");
				
				scope.entryTypes.forEach(function(type){
					scope.initializeDatabaseInfo(type);
				});
			}
		}}])
	.directive("databaseinfostatus", ["$q", "$http", function($q, $http){
		return {
			restrict: 'E',
			templateUrl: (undefined !== angular._templateUrlPrefix ? angular._templateUrlPrefix : '') + 'templates/databaseInfoStatus.html',
			scope: {
				collection: '=',
				status: '=',
				count: '=',
			},
			transclude: true,
			link: function (scope, element, attrs) {

			}
		}}]);