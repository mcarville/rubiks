function initSearchEngineUtils($scope, $http, $q) {	
	$scope.addOrRemoveFilterAndReload = function(fieldName, filerValue, filterLabel, query, reloadFunction) {
		if(query.facetFilters == null)
			query.facetFilters = [];
		
		if(query.facetFilters[fieldName] != null && query.facetFilters[fieldName].value == filerValue)
			query.facetFilters[fieldName] = null;
		else
			query.facetFilters[fieldName] = {"value": filerValue, "label": filterLabel};
			
		reloadFunction(0);
	};
	
	$scope.addOrRemoveFilterQueryAndReload = function(fieldName, filterValue, query, reloadFunction) {
		query[fieldName] = filterValue;
		reloadFunction(0);
	};
	
	$scope.addFacetFilters = function (params, query) {
		if(query != null && query.facetFilters != null)
		{
			var fieldNames = Object.keys(query.facetFilters);
			for(var i = 0 ; i < fieldNames.length ; i++)
			{
				var fieldName = fieldNames[i];
				if(query.facetFilters[fieldName] != null && query.facetFilters[fieldName].value != null)
					params.push(fieldName + ":\"" + query.facetFilters[fieldName].value + "\"");
			}
		}
		return params;
	};
	
	$scope.buildFacetParams = function (query) {
		var params = [];
		if(query != null && query.facetFilters != null)
		{
			var fieldNames = Object.keys(query.facetFilters);
			for(var i = 0 ; i < fieldNames.length ; i++)
				if(query.facetFilters[fieldNames[i]] == null)
					params.push(fieldNames[i]);
		}
		return params;
	};
	
	$scope.hasFacetFilter = function (query) {
		var result = false;
		if(query != null && query.facetFilters != null)
		{
			var keys = Object.keys(query.facetFilters);
			for(var i = 0 ; i < keys.length ; i++)
			{
				if(query.facetFilters[keys[i]] != null)
				{
					result = true
					break;
				}
			}
		}	
		return result;
	}
	
	$scope.retrieveWidgetsConfig = function (viewConfig) {
		if(viewConfig != null)
		{
			if(viewConfig.view_config != null && viewConfig.view_config.widget_root != null && viewConfig.view_config.widget_root.widgets != null)
			{
				return viewConfig.view_config.widget_root.widgets;
			}
			else
				console.log("Can not find any widget config on viewConfig");
		}
		else
			console.warn("viewConfig is null");
	};
	
	$scope.addFacetLimitParams = function (jsonParams, viewConfig) {
		var widgetsConfig = $scope.retrieveWidgetsConfig(viewConfig);
		for(var i = 0 ; widgetsConfig != null && i < widgetsConfig.length ; i++)
		{
			if(widgetsConfig[i].id != null && widgetsConfig[i].normal_entries_threshold != null)
			{	
				jsonParams['psfacet.' + widgetsConfig[i].id + '.limit'] = widgetsConfig[i].normal_entries_threshold;
			}
		}
	};

	$scope.addFacetFilterParams = function (jsonParams, facetFilters) {
		var keys = Object.keys(facetFilters);
		for(var i = 0 ; i < keys.length ; i++)
		{
			var facetFilter = facetFilters[keys[i]];
			if(facetFilter != null && facetFilter.value != null)
				jsonParams['psfacet.' + keys[i] + '.fq'] = facetFilter.value;
		}
	};
}