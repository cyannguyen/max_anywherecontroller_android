
	module.exports = {};
  
	var pushClientString = "PushNotification";
	
	module.exports.listen= function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pushClientString, "listen", [name]);
    };

	module.exports.subscribe= function (maxurl, username, pass, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pushClientString, "subscribe", [maxurl, username, pass]);
    };

    module.exports.subscribewithApiKey= function (maxurl, apikey, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pushClientString, "subscribewithApiKey", [maxurl, apikey]);
    };
	
	
	module.exports.subscribeEvent= function (maxurl, username, pass, eventname, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pushClientString, "subscribeEvent", [maxurl, username, pass, eventname]);
    };

    module.exports.subscribeEventwithApiKey= function (maxurl, apikey, eventname, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pushClientString, "subscribeEventwithApiKey", [maxurl, apikey, eventname]);
    };
	
	
	module.exports.unsubscribe= function (maxurl, username, pass, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pushClientString, "unsubscribe", [maxurl, username, pass]);
    };	

	module.exports.unsubscribewithApiKey= function (maxurl,  apikey, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pushClientString, "unsubscribewithApiKey", [maxurl,  apikey]);
    };

	module.exports.getBackgroundMessages= function (successCallback, errorCallback) {
         cordova.exec(successCallback, errorCallback, pushClientString, "getBackgroundMessages", []);
    };
	