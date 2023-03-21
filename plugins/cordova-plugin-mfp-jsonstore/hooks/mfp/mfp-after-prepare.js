/*
   Licensed Materials - Property of IBM

   (C) Copyright 2016 IBM Corp.

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

// Public modules
var path = require('path');
var strings = require('ibm-strings');
var log = require('npmlog');

// MFP modules
var strings = require('ibm-strings');
var hookConsts = require('./../utils/hook-consts');
var externalizedStrings = require('./../externalizedStrings');
var MFPHook = require('./mfp-hook');
var IOSAfterPrepare = require('./../ios/ios-after-prepare');
var MFPConfig = require('mfp-config-xml').mfpConfigXMLAPI;

/*
 This class determines which platform specific after_prepare hook to
 instantiate, and invoke.
 */
function MFPAfterPrepare(context) {
    var platformPath;           // Path to platform
    var currentPlatforms;       // Install platforms
    var projectRoot;            // Path to project
    var args;                   // User arguments
    var pluginName;             // Name of plugin
    var mfpConfig;              // Config object

    MFPHook.apply(this);
    MFPAfterPrepare.prototype = MFPHook.prototype;

    currentPlatforms = context.opts.cordova.platforms;
    projectRoot = path.resolve(context.opts.projectRoot);
    args = MFPAfterPrepare.prototype.getArguments(context.cmdLine);
    pluginName = context.opts.plugin.id;
    mfpConfig = new MFPConfig(path.join(projectRoot, 'config.xml'));

    // If the user did not supply any platforms, use all the installed
    // platforms
    if (currentPlatforms.length === 0) {
        currentPlatforms = MFPAfterPrepare.prototype.getInstalledPlatforms(
            path.join(projectRoot, 'platforms')
        );
    }

    MFPAfterPrepare.prototype.setLogLevel(args);
    logSilly('Cordova context: ' + JSON.stringify(context, null, 2));
    logSilly('Project root: ' + projectRoot);
    logSilly('Current platforms: ' + currentPlatforms);
    logSilly('Arguments: ' + args);

    /*
    Displays a log silly message. The log level must be set to silly.

    message - The message to log
     */
    function logSilly(message) {
        log.silly(hookConsts.MFP_AFTER_PREPARE, message);
    }

    /*
    Displays a log verbose message. The log level must be set to verbose.

    message - The message to log
     */
    function logVerbose(message) {
        log.verbose(hookConsts.MFP_AFTER_PREPARE, message);
    }

    /*
    Calls the platform specific hooks bassed on the platforms based. If an
    unsupported platform is passed, a warning message is displayed.

    currentPlatforms - Platforms to invoke hooks for
     */
    function invokePlatformHooks(currentPlatforms) {
        logVerbose('Invoking platform specific hooks.');

        // For each installed platform, invoke platform specific hook
        currentPlatforms.forEach(
            function (platformId) {
                platformPath = path.join(projectRoot, 'platforms', platformId);

                // Determine which hook to invoke based on the current platform
                if (platformId === hookConsts.IOS)
                    new IOSAfterPrepare(projectRoot).invokeHook();
                else if (platformId === hookConsts.ANDROID || platformId === hookConsts.WINDOWS)
                    ; //no hook script needed for this platform but no warning message needed since this platform is supported for this plugin
                else
                    console.warn(strings.format(externalizedStrings.hookNotImpl,
                        platformId, pluginName));
            }
        );
    }

    /*
    Determines which hook platform specific after_prepare hook to
    instantiate, and invoke.
     */
    this.invokeHook = function() {
        logVerbose('Preforming MFP after prepare hook.');
        invokePlatformHooks(currentPlatforms);
    };
}

module.exports = MFPAfterPrepare;
