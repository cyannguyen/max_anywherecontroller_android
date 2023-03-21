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
var log = require('npmlog');
var xcode = require('xcode');

// MFP modules
var AfterPrepare = require('./../utils/after-prepare');
var hookConsts = require('./../utils/hook-consts');
var externalizedStrings = require('./../externalizedStrings');
var MFPConfig = require('mfp-config-xml').mfpConfigXMLAPI;
var strings = require('ibm-strings');

/*
This class provides the hook script functionality for after prepare for
iOS.

projDirectory - Path to the project

After the hook is executed, the MFP project will have been prepared.
 */
function IOSAfterPrepare(projectDirectory) {
    var projName;           // Project name
    var mfpConfig;          // MFP configuration
    var projDir;            // Path to project
    var platformDir;        // Path to platform
    var pluginDir;          // Path to platform specific plugins
    var that;               // References this

    AfterPrepare.apply(this);

    that = this;
    projDir = projectDirectory;
    mfpConfig = new MFPConfig(path.join(projDir, hookConsts.CONFIG_XML),
        log.level);
    projName = mfpConfig.getWidgetName();
    platformDir = path.join(projDir, 'platforms', hookConsts.IOS);
    pluginDir = path.join(projDir, hookConsts.WORKLIGHT_DIR_IOS);

    logSilly('Project directory: ' + projDir);
    logSilly('Project name: ' + projName);
    logSilly('Platform directory: ' + platformDir);
    logSilly('Plugin directory: ' + pluginDir);

    /*
    Displays a log silly message. The log level must be set to silly.

    message - The message to log
     */
    function logSilly(message) {
        log.silly(hookConsts.IOS_AFTER_PREPARE, message);
    }

    /*
    Displays a log verbose message. The log level must be set to verbose.

    message - The message to log
     */
    function logVerbose(message) {
        log.verbose(hookConsts.IOS_AFTER_PREPARE, message);
    }

    /*
    Adds the MFP Frameworks to the Embedded Binaries section of the project.
     */
    function addEmbeddedMfpFrameworks(){

        logVerbose('Removing non-embedded frameworks from embedded frameworks section since they automatically get added');

        var projectPath = path.join(platformDir, projName + '.xcodeproj', 'project.pbxproj');
        var appProj = xcode.project(projectPath);

        try {

            appProj.parseSync();

            //temporary fix
            //remove and re-add non-embedded frameworks that get added to embedded section when removing and re-adding plugin
            appProj.removeFramework(path.join(projName, 'Plugins', 'cordova-plugin-mfp-jsonstore', 'SQLCipher.framework'), {customFramework: true});
            appProj.addFramework(path.join(projName, 'Plugins', 'cordova-plugin-mfp-jsonstore', 'SQLCipher.framework'), {customFramework: true});
            appProj.removeFramework(path.join(projName, 'Plugins', 'cordova-plugin-mfp-jsonstore', 'IBMMobileFirstPlatformFoundationHybridJSONStore.framework'), {customFramework: true});
            appProj.addFramework(path.join(projName, 'Plugins', 'cordova-plugin-mfp-jsonstore', 'IBMMobileFirstPlatformFoundationHybridJSONStore.framework'), {customFramework: true});

            that.writeFile(projectPath, appProj.writeSync());
        } catch (err){
            throw strings.format(externalizedStrings.failedPluginPrepare,
                hookConsts.IOS , err);
        }
    }

    /*
    Adds MFP Framework as embedded framework.

    An error is thrown if the hook fails.
     */
    this.invokeHook = function () {

        logVerbose('Performing iOS after prepare hook.');

        // Skip the hook if this is a platform install
        if (!this.exists(pluginDir)) {
            logVerbose(pluginDir + ' does not exist. Skipping hook.');
            return;
        }

        try {
            addEmbeddedMfpFrameworks();
        } catch (err){
            throw strings.format(externalizedStrings.failedPluginPrepare,
                hookConsts.IOS , err);
        }
    };

}

IOSAfterPrepare.prototype = new AfterPrepare();
module.exports = IOSAfterPrepare;
