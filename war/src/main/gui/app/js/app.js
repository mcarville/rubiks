var app = angular.module('search', ['ngSanitize', 'ngRoute', 'rubiks_module']);

app.directive('fallbacksrc', function () {
  var fallbacksrc = {
    link: function postLink(scope, iElement, iAttrs) {
      iElement.bind('error', function() {
        angular.element(this).attr("src", iAttrs.fallbacksrc);
      });
    }
   }
   return fallbacksrc;
});

app.directive('hideonerror', function () {
  var hideonerror = {
    link: function postLink(scope, iElement, iAttrs) {
      iElement.bind('error', function() {
        angular.element(this).remove();
      });
    }
   }
   return hideonerror;
});

app.directive("rubkisspace", ["$q", "$http", function($q, $http) {
	return {
		restrict: 'E',
		templateUrl: 'templates/rubkis_space.html',
		scope: {
			zone: '=',
		},
		link: function (scope, element, attrs) {
			scope.menuSpaces = function () {
				return ('right' == scope.zone) ? [1,2,3] : [1,2,3,4];
			};
		}
	}
}])

app.directive("rubiksface", ["$q", "$http", function($q, $http) {
	return {
		restrict: 'E',
		templateUrl: 'templates/rubiks_face.html',
		scope: {
			facedata: '=',
		},
		link: function (scope, element, attrs) {
			scope.retrieveColorCode = function(color) {
				switch (color) {
					case 'blue':
						return '#82B1FF';
					case 'white':
						return '#fff';
					case 'red':
						return '#F4511E';
					case 'orange':
						return '#F7923A';
					case 'green':
						return '#8BC34A';
					case 'yellow':
						return '#FFEE58';
				}
			};
		}
	}
}])

app.filter('mathround', function() {
	return function (number){
		return Math.round(number);
	}
});

app.filter('encodeURIComponent', function() {
  return window.encodeURIComponent;
});

app.filter('bytes', function() {
	return function(bytes, precision) {
		if (isNaN(bytes) || ! bytes > 0) return '-';
		if (typeof precision === 'undefined') precision = 1;
		var units = ['bytes', 'kB', 'MB', 'GB', 'TB', 'PB'],
			number = Math.floor(Math.log(bytes) / Math.log(1024));
		return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) +  ' ' + units[number];
	}
});

app.config(['$routeProvider',
	function($routeProvider, $routeParams) {
		$routeProvider
		.when('/rubiks', {
			templateUrl: 'templates/rubiks.html',
			controller: 'RubiksCtrl'
		})
		.otherwise({
			redirectTo: '/rubiks'
		});
	}
]);

function executeAjaxQuery($scope, $http, $q, jsonParams, onSuccess, shouldReturnPromise, onError)
{
	var deferred = null;
	if(shouldReturnPromise)
		deferred = $q.defer();

	$http(buildHttpPostQuery(jsonParams, null)).
		success(function(data, status, headers, config) {
			onSuccess($scope, data, status, headers, config);
			
			if(shouldReturnPromise)
				deferred.resolve(data);
		})
		.error(function(data, status, headers, config) {
			if(onError != null)
				onError($scope, data, status, headers, config);
			
			if(shouldReturnPromise)
				deferred.reject({"data": data, "status": status, "headers": headers});
		});

	if(shouldReturnPromise)
		return deferred.promise;
}

function executeAjaxUpdateQuery($scope, $http, $q, data, onSuccess, shouldReturnPromise)
{
	var deferred = null;
	if(shouldReturnPromise)
		deferred = $q.defer();
	
	var formData = new FormData();
	formData.append('file', JSON.stringify(data));
	$http.post("../rest/documents", formData, {
		transformRequest: angular.identity,
		headers: {'Content-Type': undefined}
	})
	.success(function(data, status, headers, config) {
		onSuccess($scope, data, status, headers, config);
		
		if(shouldReturnPromise)
			deferred.resolve(data);
	})
	.error(function(data, status, headers, config) {
		if(shouldReturnPromise)
			deferred.reject({"data": data, "status": status, "headers": headers});
	});

	if(shouldReturnPromise)
		return deferred.promise;

	/* Multipart built manually
	var boundary = "---------------------------128151090031095";
	var contentType = "multipart/form-data; boundary=" + boundary;
	
	var data = "\r\n";
	data += "--" + boundary + "\r\n";
	data += 'Content-Disposition: form-data; name="file"' + "\r\n" + "\r\n";
	data += JSON.stringify(file) + "\r\n";
	data += "--" + boundary + "--";

	// "http://localhost:8080/engine/documents"
	
	$http({"method": "POST", "url": "../search/engineUpdateGuiEndPoint", "data": data, "headers": {"Content-type": contentType}});
	*/
}

function showOrHideContent(divId)
{
	var div = $( "#" + divId );
	if(div != null)
	{
		if(div.is(":visible"))
			div.hide();
		else
			div.show();
	}
}

function buildParamString(jsonParams)
{
	var paramString = '';
	if(jsonParams != null)
	{
		var keys = Object.keys(jsonParams);
		for(var i = 0 ; i < keys.length ; i++)
		{
			if(jsonParams[keys[i]] instanceof Array)
			{
				for(var j = 0 ; j < jsonParams[keys[i]].length ; j++)
					paramString += keys[i] + "=" + encodeURIComponent(jsonParams[keys[i]][j]) + "&";
			}
			else if(null != jsonParams[keys[i]]){
				paramString += keys[i] + "=" + encodeURIComponent(jsonParams[keys[i]]) + "&";
			}
		}
	}
	return paramString;
}

function buildHttpPostQuery(jsonParams, url)
{
	if(url == null)
	{
		//url = "../../search/engineSearchGuiEndPoint360" //used a proxy in local polyspot
		url = "/rest/api/select" //used the proxy in grunt serve in ReadME
	}
	return {"method": 'POST', "url":  url, "params": jsonParams, data: '', "headers": {'Content-Type': 'application/x-www-form-urlencoded'}};
}



function buildJsonFromParams(httpParams) {
  var result = {};
  if(httpParams != null)
  {
	  httpParams.split("&").forEach(function(part) {
		var item = part.split("=");
		result[item[0]] = decodeURIComponent(item[1]);
	  });
  }
  return result;
}

function getQueryParams(queryParams) {
	queryParams = queryParams.split("+").join(" ");

	var params = {}, tokens,
		re = /[?&]?([^=]+)=([^&]*)/g;

	while (tokens = re.exec(queryParams)) {
		params[decodeURIComponent(tokens[1])]
			= decodeURIComponent(tokens[2]);
	}

	return params;
}
function getNumberOfDayFromNow(date){
	var today = new Date();
	var last = new Date(date);
	var msDay = 60*60*24*1000;
	var dayCount = Math.abs(Math.floor((today - last) / msDay));
	return dayCount;
}

function guid() {
	function s4() {
		return Math.floor((1 + Math.random()) * 0x10000)
		  .toString(16)
		  .substring(1);
	}
	var result = "";
	for(var  i = 0 ; i < 10 ; i++)
		result += s4();
	return result;
}

function decodeHtml(input){

  // replace by ? $('<textarea />').html(input).text()	 

  var e = document.createElement('div');
  e.innerHTML = input;
  return e.childNodes.length === 0 ? "" : e.childNodes[0].nodeValue;
}

function replaceBrTagByNewLine(input) {
	if(input != null)
	{
		return input.replace(/\<br\>/g, "\r\n");
	}
}

String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

function clone(sourceObject) {
    var copy = null;
	if (null == sourceObject || "object" != typeof sourceObject) 
		return copy;
	copy = sourceObject.constructor();
	for (var attr in sourceObject) {
		if (sourceObject.hasOwnProperty(attr)) 
			copy[attr] = sourceObject[attr];
    }
    return copy;
}

function isKeyValidNumeric(event)
{
	return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0) // numeric and delete
		|| (event.ctrlKey && (event.charCode == 99 || event.charCode == 118 || event.charCode == 120)); // Ctrl+X +C +V
}