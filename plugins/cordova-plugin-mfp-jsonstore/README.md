<!---
   Licensed Materials - Property of IBM

   (C) Copyright 2015, 2016 IBM Corp.

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

# IBM MobileFirst Platform Foundation JSONStore Plug-in
This plug-in enables the JSONStore feature in an IBM MobileFirst&trade; Cordova application. 

JSONStore enables persistent, always-available storage, and access of JSON documents, even when there is no network connection. 

The cordova-plugin-mfp-jsonstore plug-in adds encrypted or unencrypted JSONStore support for iOS and Windows applications.

This plug-in adds only unencrypted JSONStore support for Android applications. To add encryption support, you must also add the cordova-plugin-mfp-fips plug-in.

Refer to the documentation below for more information. 

## Installation
Add this plug-in in the same way that you add any other Cordova plug-in to your app. For example, with the Cordova CLI:

    	cordova plugin add cordova-plugin-mfp-jsonstore
    
## Dependencies
- cordova-plugin-mfp

## Supported Platforms
- Android
- iOS
- Windows

## Documentation
- [JSONStore]
See the IBM MobileFirst Platform Foundation section of IBM KnowledgeCenter:

http://ibm.biz/knowctr#SSHS8R_8.0.0/wl_welcome.html

-[Installing plug-ins]
http://ibm.biz/knowctr#SSHS8R_8.0.0/com.ibm.worklight.dev.doc/dev/t_add_mfp_ex_cordova.html

- [API](http://www-01.ibm.com/support/knowledgecenter/SSHS8R_7.1.0/com.ibm.worklight.apiref.doc/html/refjavascript-client/html/WL.JSONStore.html?cp=SSHS8R_7.1.0%2F10-0-0-1-16&lang=en)
- [Examples](http://www-01.ibm.com/support/knowledgecenter/SSHS8R_7.1.0/com.ibm.worklight.dev.doc/devref/c_jsonstore_examples.html?lang=en)

- [FIPS]
http://ibm.biz/knowctr#SSHS8R_8.0.0/com.ibm.worklight.admin.doc/admin/c_using_FIPS_140-2_support.html

For details of the changes in this latest release, see http://www-01.ibm.com/support/docview.wss?uid=swg2C7000003
