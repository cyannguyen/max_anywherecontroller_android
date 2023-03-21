#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const readline = require('readline');

const TARGET_ACTIVITY = '<activity android:clearTaskOnLaunch="true" android:configChanges="orientation|keyboardHidden|screenSize" android:exported="false" android:name="com.google.zxing.client.android.CaptureActivity" android:screenOrientation="sensor" android:stateNotNeeded="true" android:theme="@android:style/Theme.NoTitleBar.Fullscreen" android:windowSoftInputMode="stateAlwaysHidden">' +
    '<intent-filter>' +
    '<action android:name="com.ibm.tivoli.si.codeScanner.barcode.anywhere.SCAN"/>' +
    '<category android:name="android.intent.category.DEFAULT"/>' +
    '</intent-filter>' +
    '</activity> ';

const setBarcodeIntent = function(context) {
    return new Promise((resolve, reject) => {
        let appRoot = context.opts.projectRoot.toLowerCase().split(path.sep);
        let appname = appRoot[appRoot.length - 1];
        let appintent = TARGET_ACTIVITY.replace('anywhere', appname);

        console.log('Barcode intent configuring for ' + appRoot);

        let platformRoot = path.join(context.opts.projectRoot, 'platforms/android/app/src/main');
        let manifestFile = path.join(platformRoot, 'AndroidManifest.xml');
        let maniFestCopy = path.join(platformRoot, 'AndroidManifestCopy.xml');
        let targetContent = ''
        if (fs.existsSync(manifestFile)) {
            const fileStream = fs.createReadStream(manifestFile);
            const rl = readline.createInterface({
                input: fileStream
            });

            rl.on('line', function(line) {
                if (line.indexOf('<application') > -1) {
                    line += appintent;
                }
                targetContent += line + '\n';
            })

            rl.on('close', function() {

                fs.writeFile(maniFestCopy, targetContent, 'utf8', function(err) {
                    if (err)
                        reject('Unable to write AndroidManifest.xml: ' + err);
                    fs.unlinkSync(manifestFile);
                    fs.renameSync(maniFestCopy, manifestFile);
                    resolve();
                })
            });
        }

    })

};

module.exports = setBarcodeIntent;