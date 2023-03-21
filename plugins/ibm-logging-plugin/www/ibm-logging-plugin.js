var exec = require('cordova/exec');

exports.saveLogs = function(arg0, success, error) {
    exec(success, error, 'LoggingPlugin', 'saveLogs', []);
};

