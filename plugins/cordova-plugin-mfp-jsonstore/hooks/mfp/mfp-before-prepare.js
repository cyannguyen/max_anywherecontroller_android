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


// MFP modules
var strings = require('ibm-strings');
var hookConsts = require('./../utils/hook-consts');
var externalizedStrings = require('./../externalizedStrings');
var MFPHook = require('./mfp-hook');

var MFPConfig = require('mfp-config-xml').mfpConfigXMLAPI;
var fs=require('fs');
/*
 This class determines which platform specific after_prepare hook to
 instantiate, and invoke.
 */
function MFPBeforePrepare(context) {
    var platformPath;           // Path to platform
    var currentPlatforms;       // Install platforms
    var projectRoot;            // Path to project
    var args;                   // User arguments
    var pluginName;             // Name of plugin
    var mfpConfig;              // Config object

    MFPHook.apply(this);
    MFPBeforePrepare.prototype = MFPHook.prototype;

    currentPlatforms = context.opts.cordova.platforms;
    projectRoot = path.resolve(context.opts.projectRoot);
    args = MFPBeforePrepare.prototype.getArguments(context.cmdLine);
    pluginName = context.opts.plugin.id;
    mfpConfig = new MFPConfig(path.join(projectRoot, 'config.xml'));
    
    var flag =getAndroidVersion();//true if cordova android version is >=7

    if (flag==true){
  
     try{
           var libs='libs';
           var dirLib =path.join(projectRoot,hookConsts.SRC_LIB,libs);
           deleteLibs(dirLib);//deletes content of libs dir
         }catch(err){
          //do nothing
         } 
         
       }else{
          
         try{
           
            var jniLibs='jniLibs';
            var dirLib=path.join(projectRoot,hookConsts.PLATFORM_ANDROID,jniLibs);

            deleteLibs(dirLib);//deletes content of jniLibs dir
            fs.rmdirSync(dirLib);//deletes jniLibs dir 
          
          }catch(err){
            //do nothing
          }
         }







/*Deletes libs or jniLibs dir content bases on cordova android version flag */

       function deleteLibs(dirLib){
           
            try{
         
                     if(fs.existsSync(dirLib)){
                      
                      var fileFips='libopenssl_fips.so';
                      var fileCipher='libsqlcipher.so';
                      var fileCrypto='libcrypto.so';
                     
                      
                      fs.unlinkSync(dirLib+'/armeabi/'+fileFips);
                      fs.unlinkSync(dirLib+'/armeabi/'+fileCipher);
                      fs.unlinkSync(dirLib+'/armeabi/'+fileCrypto);
                      
                      fs.unlinkSync(dirLib+'/armeabi-v7a/'+fileFips);
                      fs.unlinkSync(dirLib+'/armeabi-v7a/'+fileCipher);
                      fs.unlinkSync(dirLib+'/armeabi-v7a/'+fileCrypto);
                      
                      fs.unlinkSync(dirLib+'/x86/'+fileFips);
                      fs.unlinkSync(dirLib+'/x86/'+fileCipher);
                      fs.unlinkSync(dirLib+'/x86/'+fileCrypto);

                      fs.unlinkSync(dirLib+'/x86_64/'+fileFips);
                      fs.unlinkSync(dirLib+'/x86_64/'+fileCipher);
                      fs.unlinkSync(dirLib+'/x86_64/'+fileCrypto);
                      
                      fs.unlinkSync(dirLib+'/arm64-v8a/'+fileFips);
                      fs.unlinkSync(dirLib+'/arm64-v8a/'+fileCipher);
                      fs.unlinkSync(dirLib+'/arm64-v8a/'+fileCrypto);

                    
                      fs.rmdirSync(dirLib+'/armeabi');
                      fs.rmdirSync(dirLib+'/armeabi-v7a');
                      fs.rmdirSync(dirLib+'/mips');
                      fs.rmdirSync(dirLib+'/mips64');
                      fs.rmdirSync(dirLib+'/x86');
                      fs.rmdirSync(dirLib+'/x86_64');
                      fs.rmdirSync(dirLib+'/arm64-v8a');
                  
                    }
               }catch(err){
                //do nothing 
               
                       
               }



           }

   /*checks cordova android version */
   function getAndroidVersion(){
      
            var nodeModulePath;//path to node_mode folder
            var flag=false;//true if cordova android version is >=7
            var cordovaAndroidVersion;
        try {
            nodeModulePath=path.join(projectRoot,hookConsts.NODE_MODULES);
            
            if(fs.existsSync(nodeModulePath)){
            
            cordovaAndroidVersion=fs.readFileSync(path.join(projectRoot,hookConsts.NODE_MODULES));
            cordovaAndroidVersion=parseInt(cordovaAndroidVersion);

            if(cordovaAndroidVersion>=7){
              flag=true;
             }
        }
       return flag;

         }catch (err) {
            throw strings.format(hookConsts.ANDROID, '/n error is : ' ,err.message);
        }
    }




    

    this.invokeHook = function() {
      //do nothing here
    };
}

module.exports = MFPBeforePrepare;
