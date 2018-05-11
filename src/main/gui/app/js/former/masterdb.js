
angular.module('masterdb_module', [])
	
	.controller('MasterDBCtrl', ["$rootScope", "$scope", "$http", "$q", "$location", "$timeout", function($rootScope, $scope, $http, $q, $location, $timeout) {
	
		$rootScope.header = "Company 360 View / Customer Matrix";
		
		$scope.isNumeric = function(n) {
			return ! isNaN(parseFloat(n)) && isFinite(n);
		}
		
		$scope.loadData = function () {
			
			$scope.cmxId = $location.search().cmxId;
			$scope.dbSchema = $location.search().dbSchema;
			
			$scope.data = {};

			$scope.sortByPip = function(input) {
				var result = {};
				if(input != null) {
					input.forEach(
						function(value) {
							if(result[value.pip] == null) {
								result[value.pip] = {};
								result[value.pip].values = [];
								result[value.pip].timestamp = value.source_date;
								result[value.pip].is_golden_record = value.is_golden_record;
							}
							result[value.pip].values.push(value);
						}
					)
				}
				return result;
			};
			
			$scope.sortByPipAndTimestamp = function(input) {
				var result = {};
				if(input != null) {
					input.forEach(
						function(value) {
							if(value.pip != null && value.source_date != null) {
								if(result[value.pip] == null)
									result[value.pip] = {};
								if(result[value.pip][value.source_date] == null)
									result[value.pip][value.source_date] = [];
								result[value.pip][value.source_date].push(value);
							}
						}
					)
				}
				return result;
			};
			
			$scope.sortByPerson = function(input) {
				var result = {};
				if(input != null) {
					input.forEach(
						function(value) {
							if(result[value.pip] == null) {
								result[value.pip] = {};
							}
							
							if(result[value.pip][value.status] == null) {
								result[value.pip][value.status] = {};
							}
							
							if(result[value.pip][value.status][value.cmx_person_id] == null) {
								var person = value;
								person.jobroles = {};
								result[value.pip][value.status][value.cmx_person_id] = person;
							}
							result[value.pip][value.status][value.cmx_person_id].jobroles[value.job_title] = value;
						}
					)
				}
				return result;
			};
			
			$scope.sortByShareholder = function(input) {
				var result = {};
				if(input != null) {
					input.forEach(
						function(value) {
							if(result[value.id] == null) {
								result[value.id] = value;
								if(value.shareholder_cmx_company_id != null) {
									result[value.id].shareholdertype = 'company';
								}
								else if(value.shareholder_cmx_person_id != null) {
									result[value.id].shareholdertype = 'person';
								}

								result[value.id].names = {};
							}
	
							if(value.type != null && value.value != null)
								result[value.id].names[value.type] = value.value;
						}
					)
				}
				
				var result2 = [];
				Object.keys(result).forEach(
					function(id) {
						result2.push(result[id]);
					}
				);
				
				result2.sort(function (a, b) {
					return (b.pctheld != null ? b.pctheld : 0) - (a.pctheld != null ? a.pctheld : 0);
				});
				
				return result2;
			};
			
			$scope.sortByCompanyRelation = function (input) {
				var result = {};
				if(input != null) {
					input.forEach(
						function(value) {
							if(value.pip != null && value.type != null) {
								if(result[value.pip] == null)
									result[value.pip] = {};
								
								if(result[value.pip][value.type] == null)
									result[value.pip][value.type] = [];
								
								result[value.pip][value.type].push(value);
							}
						}
					)
				}
				return result;
			};
			
			$scope.sortForRevenues = function(input) {
				var result = {};
				if(input != null) {
					input.forEach(
						function(value) {
							if(value.pip != null && value.fiscal_date != null && value.type != null) {
								if(result[value.pip] == null)
									result[value.pip] = {};
								if(result[value.pip][value.fiscal_date] == null)
									result[value.pip][value.fiscal_date] = {};
								if(result[value.pip][value.fiscal_date][value.type] == null)
									result[value.pip][value.fiscal_date][value.type] = [];
								
								result[value.pip][value.fiscal_date][value.type].push(value);
							}
						}
					)
				}
				return result;
			};
			
			$scope.retrieveKeys = function (input) {
				if(input != null)
					return Object.keys(input);
				return [];
			};

			$scope.retrieveNameToDisplay = function (object) {
				return object.names.legal_company_name != null ? object.names.legal_company_name : 
					(object.names.company_proper_name != null ? object.names.company_proper_name : 
					(object.names.legal_name != null ? object.names.legal_name : 
					(object.names.trade_name != null ? object.names.trade_name : 
					(object.names.short_name != null ? object.names.short_name : object.cmx_company_id))));
			};

			$scope.retrieveCompanyMainName = function() {
				var result = '';
				if($scope.data.names != null) {
					var pips = [1, 2, 6];
					pips.forEach(
						function(pip) {
							if(result.length > 0)
								return;
							
							var ts_values = $scope.data.names[pip];
							if(ts_values != null) {
								Object.keys(ts_values).forEach(
									function(ts) {
										if(result.length > 0)
											return;
										
										if(ts_values[ts] != null && ts_values[ts].length > 0) {
											ts_values[ts].forEach(
												function(value) {
													result += value.value + " ";
												}
											)
										}
									}
								);
							}
						}
					);
				}
				return result;
			};
	
			$scope.retrieveHostname = function(connectionInfos) {
				
				var indexOfDot = connectionInfos.indexOf(".");
				
				if(connectionInfos.startsWith('jdbc:redshift://') && indexOfDot > 0)
					return connectionInfos.substring('jdbc:redshift://'.length, indexOfDot);
			}
	
			// keys
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT cmx_company_id, cmx_id, country_iso_alpha2, duns_number, factset_id, duns_factset_link_rule, source_file, source_file_index, psm_url, psm_link_rule, local_id, toboard_id, toboard_id_link_rule, creditsafe_id, creditsafe_id_link_rule FROM company_key WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.connectionInfos = data.connectionInfos;
							$scope.data.keys = data.results;
						}
				});
			
			// names
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT type, value, pip, source_date, is_golden_record FROM company_name WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.names = $scope.sortByPipAndTimestamp(data.results);
				});
			

			// addresses
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT street1,street2,po_box,city,county,zip_code,CASE WHEN company_address.country_iso_alpha2 is not null and company_address.country_iso_alpha2 != ''  then company_address.country_iso_alpha2 ELSE country_iso.country_code_alpha2 END as country_iso_alpha2,PIP, company_address.source_date, is_golden_record, address_status, address_type FROM company_address LEFT OUTER JOIN country_iso ON lower(country_iso.name) = lower(company_address.country) WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.addresses = data.results;
				});
			
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT * FROM company_phone WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.phones = data.results;
				});
			
			
			// address_normalized
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT street_number,street1,address_normalized.country_iso_alpha2,zip_code,address_normalized.country,street_address,intersection,colloquial_area,locality,ward,latitude,longitude, formatted_address, 10 as pip,place_id FROM company_address join update_staging.address_normalized ON address_normalized.address_checksum = company_address.address_checksum WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.address_normalized = data.results;
				});
			
			// identifiers
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT type, value, pip, source_date, is_golden_record FROM company_identifier WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.identifiers = $scope.sortByPipAndTimestamp(data.results);
				});

			// legal information
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT incorporated_year,company_legal_information.legal_form_code,company_legal_information_form_map.legal_form_label,structure_type,legal_type,status,company_legal_information.PIP, company_legal_information.source_date, share_capital, is_golden_record FROM company_legal_information LEFT JOIN company_legal_information_form_map ON company_legal_information_form_map.legal_form_code = company_legal_information.legal_form_code WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.legalinformations = data.results;
				});
			
			// activity
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT Activity,company_activity.PIP,company_activity.sic, company_activity_sic_map.*, company_activity.sector, company_activity.industry, company_activity.nace_code,naceRef.label naceDescription,company_activity.activity_description description, naceRef.group_code, naceRef.group_label, naceRef.division_code, naceRef.division_label, naceRef.section_code, naceRef.section_label, company_activity.source_date, company_activity.is_golden_record FROM company_activity LEFT JOIN company_activity_sic_map ON company_activity_sic_map.sic = company_activity.sic  LEFT JOIN nace_map naceRef on naceRef.nace_code = company_activity.nace_code WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.activities = data.results;
				});
			
			// Social Media
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT cmx_company_id, pip, type, value, is_golden_record FROM company_social_media WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.socialmedias = $scope.sortByPip(data.results);
				});
					
			
			// addresses.CountrycodeAlpha2,
			// group - parents
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT company_group.parent, parent.value as ParentName, parent2.value as ParentTradeMark "
			+ " ,street1,street2,city,po_box,country, country_iso.country_code_alpha2 as country_iso_alpha2"
			+ " , direct_subsidiaries_count, total_subsidiaries_count"
			+ " FROM company_group "
			+ " LEFT JOIN company_name parent ON parent.cmx_company_id = company_group.Parent AND parent.Type = 'LegalCompanyName' "
			+ " LEFT JOIN company_name parent2 ON parent2.cmx_company_id = company_group.Parent AND parent2.Type = 'TradeMark'" 
			+ " LEFT JOIN company_address ON company_address.cmx_company_id = company_group.Parent AND company_address.is_golden_record = 1 "
			+ " LEFT JOIN country_iso ON lower(country_iso.Name) = lower(company_address.Country) "
			+ " WHERE company_group.cmx_company_id = ? AND company_group.is_golden_record = 1", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.groups_parents = data.results;
				});
			
			// group - subsidiaries
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT distinct company_group.cmx_company_id, subsidiaries.Value as LegalCompanyName, subsidiaries2.Value as TradeMark, Street1,Street2,City,Region,po_box,Country,country_iso.country_code_alpha2 as country_iso_alpha2, structure_type "
			+ " FROM company_group " 
			+ " LEFT JOIN company_name subsidiaries ON company_group.cmx_company_id = subsidiaries.cmx_company_id AND subsidiaries.Type = 'LegalCompanyName' "
			+ " LEFT JOIN company_name subsidiaries2 ON company_group.cmx_company_id = subsidiaries2.cmx_company_id AND subsidiaries2.Type = 'TradeMark' "
			+ " LEFT JOIN company_address ON company_address.cmx_company_id = company_group.cmx_company_id  AND company_address.pip = company_group.pip AND company_address.is_golden_record = 1" 
			+ " LEFT JOIN company_legal_information ON company_legal_information.cmx_company_id = company_group.cmx_company_id  AND company_legal_information.pip = 1 AND company_legal_information.is_golden_record = 1 "
			+ " LEFT JOIN country_iso ON lower(country_iso.Name) = lower(company_address.Country) WHERE company_group.Parent = ? AND company_group.is_golden_record = 1", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.groups_subsiriaries = data.results;
				});
			
			// job role contacts
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT p.cmx_person_id, jr.pip, p.full_name,jr.job_title, jr.start_date, jr.end_date, jr.status, jr.phone, jr.email, p.salutation, p.birth_year, jr.is_golden_record FROM job_role jr inner join person p on jr.cmx_person_id = p.cmx_person_id where jr.cmx_company_id = ? and jr.is_last = true", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null)
							$scope.data.jrcontacts = $scope.sortByPerson(data.results);
				});
			
			// shareholder contacts
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "select shareholder.id, shareholder.percent_held, shareholder.pip, shareholder.shareholder_cmx_company_id, shareholder.shareholder_cmx_person_id, p.cmx_person_id,p.full_name, company.type, company.value from shareholder shareholder left outer join person p on shareholder.shareholder_cmx_person_id = p.cmx_person_id left outer join company_name company on shareholder.shareholder_cmx_company_id = company.cmx_company_id where shareholder.cmx_company_id = ? and (shareholder_cmx_company_id is not null OR shareholder_cmx_person_id is not null) and shareholder.percent_held is not null and (company.type = 'registered_name' OR shareholder_cmx_person_id is not null)", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.data.shcontacts = $scope.sortByShareholder(data.results);
						}
				});
	
			// market participations
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "select shareholder.cmx_company_id as id, shareholder.percent_held, shareholder.pip, shareholder.cmx_company_id, company.type, company.value from shareholder shareholder left outer join company_name company on shareholder.cmx_company_id = company.cmx_company_id where shareholder.shareholder_cmx_company_id = ? and shareholder.percent_held is not null;", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.data.market_participations = $scope.sortByShareholder(data.results);
						}
				});
				
			// company relation
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT target_cmx_company_id, company_relation.pip, company_relation.type, company_name.value as target_name FROM company_relation JOIN company_name ON company_name.cmx_company_id = company_relation.target_cmx_company_id AND company_name.id = (SELECT id FROM company_name WHERE cmx_company_id = company_relation.target_cmx_company_id LIMIT 1) WHERE company_relation.cmx_company_id = ? ORDER BY target_name", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.data.company_relation = $scope.sortByCompanyRelation(data.results);
						}
					});

			// company revenue
			
			$scope.revenue_types = ["geography", "activity"];
			
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT pip, fiscal_date, type, value, currency, usd_rate, revenue, revenue_percent FROM company_revenue WHERE cmx_company_id = ? AND fiscal_date IN (SELECT DISTINCT fiscal_date FROM company_revenue WHERE cmx_company_id = ? ORDER BY fiscal_date DESC LIMIT 3)", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema, "paramsToSet": 2};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.data.company_revenue = $scope.sortForRevenues(data.results);
						}
					});
			
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT pip, fiscal_date, frequency_period, coverage, type, revenue, revenue_export, operating_costs, ebit, ebitda, net_income_before_tax, tax, net_income, dividends, minority_interests, other_appropriations, retained_profit, wages_and_salaries, pension_costs, depreciation_lands_and_buildings, depreciation_plants_machinery_equipment, depreciation_other_tangible_fixed_assets, subtotal_depreciation_fixed_tangible_assets, depreciation_goodwill, depreciation_other_intangible_fixed_assets, subtotal_depreciation_fixed_intangible_assets, depreciation_financial_assets, depreciation_loans_to_group, depreciation_other_loans, subtotal_depreciation_financial_assets, depreciation_other_fixed_assets, total_fixed_assets_depreciation, depreciation_inventory, depreciation_customer_receivables_bad_debt, other_current_assets_depreciation, total_current_assets_depreciation, currency_gain_loss, other_financial_income, total_financial_income, interests_paid, other_financial_expenses, total_financial_expenses, gain_loss_fixed_assets_sales, extraordinary_income_cost, employee_total, net_land_buildings, net_plant_machinery, net_other_tangible_assets, net_total_tangible_assets, net_goodwill, net_other_intangible_assets, net_total_intangible_assets, net_investments, net_loans_to_group, net_other_loans, net_miscellaneous_fixed_assets, net_total_other_fixed_assets, net_total_fixed_assets, net_raw_materials, net_work_in_progress, net_finished_goods, net_other_inventories, net_total_inventories, net_trade_receivables, net_group_receivables, net_receivables_due_after_1_year, net_miscellaneous_receivables, net_total_receivables, cash, deferred_expenses, accrued_income, net_other_current_assets, net_total_current_assets, net_total_assets, trade_payable, bank_liabilities, other_loans_finance, group_payable, deferred_income, accrued_expenses, miscellaneous_liabilities, total_current_liabilities, trade_payable_due_after_1_year, bank_liabilities_due_after_1_year, other_loans_finance_due_after_1_year, group_payable_due_after_1_year, miscellaneous_liabilities_due_after_1_year, total_long_term_liabilities, total_liabilities, called_up_share_capital, share_premium, revenue_reserves, other_reserves, total_shareholders_equity, total_debt, yield_dividend_to_shareprice, market_capitalization, enterprise_value FROM company_cmx_statement WHERE cmx_company_id = ? AND fiscal_date IN (SELECT DISTINCT fiscal_date FROM company_cmx_statement WHERE cmx_company_id = ? ORDER BY fiscal_date DESC LIMIT 3)", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema, "paramsToSet": 2};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.data.company_cmx_statement = $scope.sortForRevenues(data.results);
						}
					});			
			
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT cmx_company_id, pip, source_date, id, inserted_date, type, is_last, year, semester, quarter, distribution_type, distribution_value, coverage, value, currency, growth, is_golden_record FROM company_cmx_kpi WHERE cmx_company_id = ? AND is_last = 1", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema, "paramsToSet": 1};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.data.company_cmx_kpi = $scope.sortByPip(data.results);
						}
					});	
					
			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT cmx_company_id, pip, event_generation_date, value_update_date, event_type, event_description FROM cmx_mdb_update_event WHERE cmx_company_id = ?", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema, "paramsToSet": 1};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.data.events = $scope.sortByPip(data.results);
							$scope.data.eventCount = data.results.length;
						}
					});	

			var jsonParams = {"command": "retrieveDataFromMasterDB", "sqlQuery": "SELECT * FROM company_scoring WHERE cmx_company_id = ? ORDER BY source_date DESC", "cmxId": $scope.cmxId, "dbSchema": $scope.dbSchema, "paramsToSet": 1};
			$http(buildHttpPostQuery(jsonParams, "rest/apiEndPoint"))
				.success(
					function(data) {
						if(data.results != null) {
							$scope.data.scoring = $scope.sortByPip(data.results);
						}
					});
		}
		
		$scope.loadData();
	}])
	.directive("address", ["$q", "$http", function($q, $http) {
		return {
			restrict: 'E',
			templateUrl: 'templates/address.html',
			scope: {
				address: '=',
			},
			link: function (scope, element, attrs) {
				scope.isNumeric = function (n) {
					return !isNaN(parseFloat(n)) && isFinite(n);
				};
				
				scope.formatPhone = function(phone) {
					if(phone != null) {
						var result = "";
						for(var i = 0 ; i < phone.length; i++)
						{
							result += phone.charAt(i);
							if(i % 3 == 0 && i + 1 < phone.length)
								result += ".";
						}
						return result;
					}
					else
						return phone;
				};
			}
		}
	}])
	.directive("contact", ["$q", "$http", function($q, $http) {
		return {
			restrict: 'E',
			templateUrl: 'templates/contact.html',
			scope: {
				person: '=',
			},
			link: function (scope, element, attrs) {
				
			}
		}
	}])
	;
	