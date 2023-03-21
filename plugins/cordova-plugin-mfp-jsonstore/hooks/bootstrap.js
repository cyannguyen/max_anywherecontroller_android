/*
   Licensed Materials - Property of IBM

   (C) Copyright 2016 IBM Corp.

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

// Private modules
var hookConsts = require('./utils/hook-consts');

/*
Instantiates, and invokes a hook script that corresponds to the context hook
passed. Supported hooks consist of after_prepare.

context - Cordova context

An MFP hook will be instantiated, and invoked.
 */
module.exports = function(context) {
    var MFPAfterPrepare;            // MFP after prepare hook
    var promise;

    var MFPBeforePrepare;


    // Uncomment the following line to get a breakpoint since it gets loaded dynamically
    // debugger;

    // Determine which hook to invoke

    if (context.hook === hookConsts.BEFORE_PREPARE) {
        MFPBeforePrepare = require('./mfp/mfp-before-prepare');
        new MFPBeforePrepare(context).invokeHook();
    }else if (context.hook === hookConsts.AFTER_PREPARE) {
       MFPAfterPrepare = require('./mfp/mfp-after-prepare');
        promise = new MFPAfterPrepare(context).invokeHook();
    }
    return promise;
};

