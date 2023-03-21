# Cordova plugin for [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/)

## Supported platforms
          
- Android
- iOS

## Installation

    1. create cordova application.
	   cordova create maximopushnotificationapp com.ibm.maximo.pushnotificationapp
	
    2. Go to folder maximopushnotification
	   cd maximopushnotificationapp
	
    3. Apply plugin	
	   cordova plugin add cordova-plugin-maximo-pushnotification
	
Android
    
    1. Add platform
	  cordova platform add android
	  
    2. In your root-level (project-level) Gradle file (build.gradle), add the following for Google Services plugin:
	   (https://firebase.google.com/docs/android/setup?gclid=CjwKCAiAlO7uBRANEiwA_vXQ-1elh4waF_WGimA5_EJdQdWAK0JNDJp7xfSF9Xb4fHHr945-8SkAzBoCVl4QAvD_BwE)
	
	   dependencies {
    	// ...

    	// Add the following line:
    	classpath 'com.google.gms:google-services:4.3.3'  // Google Services plugin
  	   }
	 
	3. In your module (app-level) Gradle file (usually app/build.gradle), add a line to the bottom of the file for Google Services plugin.

       apply plugin: 'com.android.application'

		android {
  			// ...
		}

		// Add the following line to the bottom of the file:
		apply plugin: 'com.google.gms.google-services'  // Google Play services Gradle plugin		
      

    4. Download the google-services.json for your firebase project from the firebase console link ( https://console.firebase.google.com/u/0/). 
       Copy the google-services.json file in the "app" folder.
	   
	5. If http is used for maximo url then set android:usesCleartextTraffic="true" in Android Manifest at application tag level.

    
iOS
    
    1. Add platform
        cordova platform add ios
    
    2. Download GoogleService-Info.plist for your firebase project. Copy it in "Resources" folder.
        link: https://console.firebase.google.com/u/0/
    
    3. Make the follwing change in the podfile.  
    
        pod 'BMSPush'
        pod 'BMSCore'
       
and 
       
       pre_install do |installer|
             installer.analysis_result.specifications.each do |s|
                 s.swift_version = '4.2'
             end
       end
       
Podfile:
       
       source 'https://github.com/brightcove/BrightcoveSpecs.git'
       source 'https://github.com/CocoaPods/Specs.git'
       platform :ios, '9.0'
       use_frameworks!
       target 'MaximoPushNotificationApp' do
           project 'MaximoPushNotificationApp.xcodeproj'
           pod 'Firebase/Core', '~> 6.3.0'
           pod 'Firebase/Messaging', '~> 6.3.0'
           pod 'Firebase/Auth', '~> 6.3.0'
           pod 'SwiftyJSON', '4.2'
           pod 'Firebase/Analytics', '~> 6.3.0'
           pod 'Firebase/Firestore', '~> 6.3.0'
           pod 'Firebase/InAppMessagingDisplay', '~> 6.3.0'
            pod 'Firebase/Database', '~> 6.3.0'
            pod 'Firebase/Storage', '~> 6.3.0'
           pod 'BMSPush'
           pod 'BMSCore'
       end
       pre_install do |installer|
            installer.analysis_result.specifications.each do |s|
                s.swift_version = '4.2'
            end
      end
    
    4. After applying the changes, to to platforms/ios folder and run
       pod install
    


## Methods 

* subcribe(maximourl, username, password): Method used for maximo native authentication

  Parameters:
		maximour: http(s)://ip-address:port/maximo
		username: logged-in username
		password: logged-in user password
		  
* subcribewithApiKey (maximourl, apikey): The caller must acquire apikey before calling this method. This method can be used for any type of authenication.

  Maximo rest api to create api key: 
   POST : http://ip-address:7001/maximo/oslc/apitoken/create?_lid=maxadmin&_lpwd=maxadmin
   Payload example : {"expiration": "-1","userid" : "maxadmin"}

  Parameters:
		maximourl: http(s)://ip-address:port/maximo
		apikey: api key for authentication
		 
    
* unsubscribe(maximourl, username, password): Method used to unsubscibe the user from getting any push notification messages. Maximo server will not send message to the provider for this device.

  Parameters:
        maximour: http(s)://ip-address:port/maximo
		username: logged-in username
		password: logged-in user password

* UnSubscribewithApiKey (maximourl, apikey): Method used to unsubscibe the user from getting any push notification messages. This method can be used for any type of authenication.
  Parameters:
		maximourl: http(s)://ip-address:port/maximo
		apikey: api key for authentication

* listen (true): Method adds the listener to receive a callback on message received.
  

Sample java script:

		var maxurl = "http://9.32.xx.xx:7001/maximo";
        var username = "maxadmin"
        var password = "maxadmin";
        var apikey = "ph84m49csbfupvdhr2skqlorf5grovmhfcdled6h";

        pushnotification.subscribewithApiKey(maxurl, apikey, success, failure);    

        pushnotification.listen(true,successL, failureL);

		var success = function(message) {
			alert("Posted to Maximo");
		};

		var failure = function() {
			alert("Error in Posting to Maximo");
		};


		var successL = function(message) {
        	alert(message.message);
        };

        var failureL = function() {
        	alert("Error calling Listen");
        };


