
angular.module('masterdb_person_module', ["ui.bootstrap"])
	.controller('MasterDBPersonCtrl', ["$rootScope", "$scope", "$http", "$q", "$location", "$timeout", function ($rootScope, $scope, $http, $q, $location, $timeout) {

		$rootScope.header = "Person 360 View / Customer Matrix";

		$scope.loadData = function () {

			$scope.cmxId = $location.search().cmxId;
			$scope.dbSchema = $location.search().dbSchema;

			$scope.data = {};

			$scope.sortByPip = function (input) {
				var result = {};
				if (input != null) {
					input.forEach(
						function (value) {
							if (result[value.pip] == null) {
								result[value.pip] = {};
								result[value.pip].values = [];
								result[value.pip].timestamp = value.source_date;
							}
							result[value.pip].values.push(value);
						}
					)
				}
				return result;
			};

			$scope.retrieveKeys = function (input) {
				if (input != null)
					return Object.keys(input);
				return [];
			};

			$scope.retrieveNameToDisplay = function (object) {
				return object.names.registered_name != null ? object.names.registered_name :
					(object.names.company_proper_name != null ? object.names.company_proper_name :
						(object.names.legal_name != null ? object.names.legal_name :
							(object.names.trade_name != null ? object.names.trade_name :
								(object.names.short_name != null ? object.names.short_name : object.cmx_company_id))));
			};

			$scope.retrieveHostname = function(connectionInfos) {
				
				var indexOfDot = connectionInfos.indexOf(".");
				
				if(connectionInfos.startsWith('jdbc:redshift://') && indexOfDot > 0)
					return connectionInfos.substring('jdbc:redshift://'.length, indexOfDot);
			}
			
			// keys
			var jsonParams = {
				"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT person_key_pip.cmx_person_id, pip_id, pip, linkage_rule, linkage_score, identifier_type, person_key_pip.cmx_id FROM person_key_pip JOIN person_key ON person_key.cmx_person_id = person_key_pip.cmx_person_id WHERE person_key_pip.cmx_person_id = ? ORDER BY pip_id", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
					.success(
					function (data) {
						if (data.results != null){
							$scope.connectionInfos = data.connectionInfos;
							$scope.data.keys = $scope.sortByPip(data.results);
							
							var keys = $scope.retrieveKeys($scope.data.keys);
							if(keys != null && keys.length > 0) {
								var value = $scope.data.keys[keys[0]];

								$scope.data.keys['cmx_person_id'] = value.values[0].cmx_person_id;
								$scope.data.keys['cmx_id'] = value.values[0].cmx_id;
							}
						}
					});
					
			// jobs
			
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT listagg(value, ' / ') as value, job_role.cmx_company_id, cmx_person_id, job_title, start_date, end_date, status, department, phone, email, job_role.pip, role_level, job_role.is_golden_record FROM job_role left outer join company_name on (company_name.cmx_company_id = job_role.cmx_company_id AND ((type = 'registered_name' and company_name.pip = 2) OR company_name.pip IN (1,4))) WHERE cmx_person_id = ? group by job_role.cmx_company_id, cmx_person_id, job_title, start_date, end_date, status, department, phone, email, job_role.pip, role_level, job_role.is_golden_record", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
					.success(
					function (data) {
						if (data.results != null){
							
							var active_jobs = [];
							var former_jobs = [];
							
							data.results.forEach(function(item){
								if(item.status == 'active')
									active_jobs.push(item);
								if(item.status == 'inactive')
									former_jobs.push(item);
							})
							
							$scope.data.active_jobs = $scope.sortByPip(active_jobs);
							$scope.data.former_jobs = $scope.sortByPip(former_jobs);
						}
					});

			// person
			var jsonParams = {
				"command": "retrieveDataFromMasterDB", "sqlQuery": "select pip, full_name, first_name, last_name, middle_name, salutation, suffix, suffix_pro, nick_name, birth_year, birth_month, biography, gender, marital_status, number_children,is_golden_record, pip_id FROM person WHERE cmx_person_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
				$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
					.success(
						function(data) {
							if(data.results != null) {
								
								data.results.forEach(function(item){
									if(item.biography != null) {
										item.biography = item.biography.replace(/(\\n)/g, '<br/>');
									}
								});
								
								$scope.data.persons = data.results;
							}
					});
			// person_type_value
			var jsonParams = {
				"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT * FROM person_type_value WHERE cmx_person_id = ? AND is_golden_record = true", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
				$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
					.success(
						function(data) {
							if(data.results != null) {
								$scope.data.person_type_values = $scope.sortByPip(data.results);
							}
					});			
			
			// shareholder
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "select shareholder.cmx_company_id, percent_held, shareholder_type, value FROM shareholder left join company_name on (company_name.cmx_company_id = shareholder.cmx_company_id) WHERE shareholder_cmx_person_id = ? AND type like 'registered_name' and company_name.pip = 2 ORDER BY value DESC;", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.shareholder = data.results;
				});
					
			// person_social_media
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT pip, type, value, is_golden_record FROM person_social_media WHERE cmx_person_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.person_social_media = $scope.sortByPip(data.results);
				});
			
			// Manager 
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT distinct manager_cmx_person_id, * FROM job_role JOIN person ON job_role.manager_cmx_person_id = person.cmx_person_id AND job_role.pip = person.pip WHERE job_role.cmx_person_id = ? AND manager_cmx_person_id is not null", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.person_manager = $scope.sortByPip(data.results);
				});
			
			// Team Members
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT distinct manager_cmx_person_id, * FROM job_role JOIN person ON job_role.cmx_person_id = person.cmx_person_id AND job_role.pip = person.pip WHERE job_role.manager_cmx_person_id = ? AND manager_cmx_person_id is not null", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.team_members = $scope.sortByPip(data.results);
				});
			}
			
			/*
			;
			*/

			$scope.loadData();
		}])
		;