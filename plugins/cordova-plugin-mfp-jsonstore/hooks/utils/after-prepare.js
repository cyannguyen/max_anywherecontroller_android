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

// Private modules
var Hook = require('./hook');
var externalizedStrings = require('./../externalizedStrings');
var hookConsts = require('./hook-consts');

/*
 This class serves as an abstract class for a hook script for after prepare.
 */
function AfterPrepare() {

    // Enforce abstraction
    if (this.constructor === AfterPrepare)
        throw new TypeError('Cannot instantiate an abstract class.');

    Hook.apply(this);
}

AfterPrepare.prototype = Hook.prototype;

module.exports = AfterPrepare;
