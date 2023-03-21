var exec = require('cordova/exec');

var cookieManager = {
    clearAllCookies: function (arg0, success, error) {
        exec(success, error, 'CDVCookieManager', 'clearAllCookies', [arg0]);
    },

    setCookie: function (url, cookieName, cookieValue, success, error) {
        exec(success, error, 'CDVCookieManager', 'setCookie', [url, cookieName, cookieValue]);
    },

    getCookie: function (url, cookieName, success, error) {
        exec(success, error, 'CDVCookieManager', 'getCookie', [url, cookieName]);
    }
};

module.exports = cookieManager;
