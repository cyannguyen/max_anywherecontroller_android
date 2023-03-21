
/*
*
* IBM Confidential
*
* OCO Source Materials
*
* 5724-U18
*
* (C) COPYRIGHT IBM CORP. 2019
*
* The source code for this program is not published or otherwise
* divested of its trade secrets, irrespective of what has been
* deposited with the U.S. Copyright Office.
*
*/

import FirebaseMessaging
import FirebaseCore
import SwiftyJSON

@objc(PushNotification) public class PushNotification : CDVPlugin {
    static var pushManagerFCM:MaximoPushFCM? = nil
    static var pushManagerPN:MaximoPushPN? = nil
    static var notificationCallback:CDVInvokedUrlCommand? = nil
    
    private static var providerName = ""
    private static var userName = ""
    private static var clientSecret = ""
    private static var appGuid = ""
    private static var bluemixRegion = ""
    static var userId = ""
    private static var apiKey = ""
    private static var eventName = "ALL"
    private static var pass = ""
    private static var route = ""
    static var deviceId = ""
    
    let pushAPI1 = "/api/pushnotif/ios"
    
    public typealias ServiceResponse = (JSON,Error?) -> Void
    
    @objc(subscribe:)
    public func subscribe(command: CDVInvokedUrlCommand) {

        NSLog("PushNotification: subscribe")
        let maxUrl = String(command.argument(at: 0) as! String)
        PushNotification.userName = String(command.argument(at: 1) as! String)
        PushNotification.pass = String(command.argument(at: 2) as! String)
        PushNotification.route = maxUrl + "/api/pushnotif/ios"
        
        /*let name = UserDefaults.standard.string(forKey: "name") as? String
        // print(name as Any)
        
        let alertController = UIAlertController(title: "Hey AppCoda", message: name ?? "Hello!!!", preferredStyle: .alert)
        let defaultAction = UIAlertAction(title: "OK", style: .default, handler: nil)
        alertController.addAction(defaultAction)
        // Where to get viewController?
        viewController.present(alertController, animated: true, completion: nil)
        */
        
        NSLog("route : " + PushNotification.route);
        NSLog("useranme : " + PushNotification.userName);
       
        MaximoRestAPIData.sharedInstance.makeHTTPGetRequest(path: PushNotification.route, user: PushNotification.userName, pass: PushNotification.pass, onCompletion: { json, err in
                     print("response", json)
                     self.initAndRegister(json: json, callbackId: command.callbackId)
        })
    }

    @objc(subscribeEvent:)
    public func subscribeEvent(command: CDVInvokedUrlCommand) {

        NSLog("PushNotification: subscribeEvent")
        let maxUrl = String(command.argument(at: 0) as! String)
        PushNotification.userName = String(command.argument(at: 1) as! String)
        PushNotification.pass = String(command.argument(at: 2) as! String)
        PushNotification.eventName = String(command.argument(at: 3) as! String)
        PushNotification.route = maxUrl + "/api/pushnotif/ios"
        
        NSLog("route : " + PushNotification.route);
        NSLog("useranme : " + PushNotification.userName);
        NSLog("eventname : " + PushNotification.eventName);
       
        MaximoRestAPIData.sharedInstance.makeHTTPGetRequest(path: PushNotification.route, user: PushNotification.userName, pass: PushNotification.pass, onCompletion: { json, err in
                     print("response", json)
                     self.initAndRegister(json: json, callbackId: command.callbackId)
        })
    }
    
    @objc(subscribewithApiKey:)
    public func subscribewithApiKey(command: CDVInvokedUrlCommand) {
        
        NSLog("PushNotification: subscribewithApiKey")
        let maxurl = String(command.argument(at: 0) as! String)
        PushNotification.apiKey = String(command.argument(at: 1) as! String)
        PushNotification.route = maxurl + "/api/pushnotif/ios"
        
        NSLog("PushNotification : " + PushNotification.route);
                  
        MaximoRestAPIData.sharedInstance.makeHTTPGetRequest(path: PushNotification.route, apiKey: PushNotification.apiKey, onCompletion: { json, err in
                 print("response", json)
                 self.initAndRegister(json: json, callbackId: command.callbackId)
        })
    }
    
    @objc(subscribeEventwithApiKey:)
    public func subscribeEventwithApiKey(command: CDVInvokedUrlCommand) {
        
        NSLog("PushNotification: subscribeEventwithApiKey")
        let maxurl = String(command.argument(at: 0) as! String)
        PushNotification.apiKey = String(command.argument(at: 1) as! String)
        PushNotification.eventName = String(command.argument(at: 2) as! String)
        PushNotification.route = maxurl + "/api/pushnotif/ios"
        NSLog("PushNotification : " + PushNotification.route);
                  
        MaximoRestAPIData.sharedInstance.makeHTTPGetRequest(path: PushNotification.route, apiKey: PushNotification.apiKey, onCompletion: { json, err in
                 print("response", json)
                 self.initAndRegister(json: json, callbackId: command.callbackId)
        })
    }
    
    @objc(listen:)
    public func listen(command: CDVInvokedUrlCommand) {
        NSLog("PushNotification: listen")
        PushNotification.notificationCallback = command
    }

    @objc(unsubscribe:)
    public func unsubscribe(command: CDVInvokedUrlCommand) {
        NSLog("PushNotification: unsubscribe")
        unregisterDevice()
        MaximoRestAPIData.sharedInstance.setUserInactive(userId: PushNotification.userId, url: PushNotification.route, user: PushNotification.userName, pass: PushNotification.pass, deviceid: PushNotification.deviceId, eventname: PushNotification.eventName)
    }

    @objc(unsubscribewithApiKey:)
    public func unsubscribewithApiKey(command: CDVInvokedUrlCommand) {
        NSLog("PushNotification: unsubscribewithApiKey")
        unregisterDevice()
        MaximoRestAPIData.sharedInstance.setUserInactive(userId: PushNotification.userId, url: PushNotification.route, apiKey: PushNotification.apiKey, deviceid: PushNotification.deviceId, eventname: PushNotification.eventName)
    }

    @objc public func unregisterDevice() -> Void {
        NSLog("PushNotification: unRegisterDevice")
        if ( PushNotification.providerName == "IPN") {
            PushNotification.pushManagerPN?.unregister();
        }
    }

    @objc public func getBackgroundMessages() ->Void {
        
    }
    
    private func initAndRegister(json: JSON, callbackId: String) {
        NSLog("PushNotification: initAndRegister")
        var errorMsg = self.setData(json: json)
        DispatchQueue.main.async {
                   let name = UserDefaults.standard.string(forKey: "name")
                                 print(name as Any)
            var pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK,
                // messageAs: token
                messageAs: name
            )
               
            print("provider : ", PushNotification.providerName)
            
            //AppDelegate(UIApplication.shared)
            if ( PushNotification.providerName == "FCM") {
                self.initAndRegisterFCM()
                if (Messaging.messaging().fcmToken == nil ){
                    errorMsg = "Firebase token is not generated"
                }
            } else if ( PushNotification.providerName == "IPN") {
                AppDelegate(UIApplication.shared)
                self.initAndRegisterIPN()
            }  else if ( PushNotification.providerName == "APN") {
                AppDelegate(UIApplication.shared)
                self.initAndRegisterAPN()
            
            } else {
                    errorMsg = "Error in registering provider." + errorMsg
            }
               
            if ( errorMsg != "") {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: errorMsg
                )
            }
                   
            // MaximoRestAPIData.sharedInstance.sendDeviceId(deviceid: token!, url: route, user: user, pass: pass)
               
            pluginResult?.setKeepCallbackAs(true)
            self.commandDelegate!.send(
                    pluginResult,
                    callbackId: callbackId
            )
        }
    }
    
    private func initAndRegisterFCM() {
        
        NSLog("PushNotification: initAndRegisterFCM")
        FirebaseApp.configure()
        PushNotification.pushManagerFCM = MaximoPushFCM(userID: PushNotification.userId,mFirebase: self)
        if (PushNotification.apiKey == "") {
            PushNotification.pushManagerFCM?.MaximoPush(url: PushNotification.route, user: PushNotification.userName, pass: PushNotification.pass, eventname: PushNotification.eventName)
        } else {
            PushNotification.pushManagerFCM?.MaximoPush(url: PushNotification.route, apiKey: PushNotification.apiKey, eventname: PushNotification.eventName)
        }
        PushNotification.pushManagerFCM?.registerForPushNotifications()
    }
    
    private func initAndRegisterIPN() {
        
        NSLog("PushNotification: initAndRegisterIPN")
        PushNotification.pushManagerPN = MaximoPushPN(userID: PushNotification.userId,mPN: self)
        PushNotification.pushManagerPN?.initApp(regionUrl: PushNotification.bluemixRegion, appGUID: PushNotification.appGuid, clientSecret: PushNotification.clientSecret)
        PushNotification.pushManagerPN?.registerForPushNotifications()
    }
    
    private func initAndRegisterAPN() {
        
        NSLog("PushNotification: initAndRegisterAPN")
        PushNotification.pushManagerPN = MaximoPushPN(userID: PushNotification.userId,mPN: self)
        PushNotification.pushManagerPN?.registerForPushNotifications()
    }
    

    static func setPNDeviceId(deviceId: String) {
        
        NSLog("PushNotification: setPNDeviceId")
        PushNotification.deviceId = deviceId
        if ( PushNotification.apiKey == "") {
            MaximoRestAPIData.sharedInstance.sendDeviceId(deviceid: deviceId, eventname: PushNotification.eventName, url: PushNotification.route, user: PushNotification.userName, pass: PushNotification.pass)
        } else {
             MaximoRestAPIData.sharedInstance.sendDeviceIdwithApiKey(deviceid: deviceId, eventname: PushNotification.eventName, url: PushNotification.route, apiKey: PushNotification.apiKey)
        }
        
    }
    
    private func setData(json: JSON) -> String{

        NSLog("PushNotification: setData")
        var msg = ""
        PushNotification.providerName = json["providername"].stringValue
        if (PushNotification.providerName.isEmpty)
        {
            msg = "Provider name is not set"
            print (msg)
            return msg
        }
       
        print ("Provider Name",PushNotification.providerName);
        
        PushNotification.userId = json["username"].stringValue
        if (PushNotification.userId.isEmpty)
        {
            msg = "User Id is not set"
            print (msg)
            return msg
        }
        print ("User Name",PushNotification.userId)
        
        if ( PushNotification.providerName == "IPN") {
            
            PushNotification.clientSecret = json["clientsecret"].stringValue
            if (PushNotification.clientSecret.isEmpty)
            {
                msg = "IBM Push Notification Clientsecret is not set"
                print(msg)
                return msg
            }

            PushNotification.appGuid = json["appguid"].stringValue
            if (PushNotification.appGuid.isEmpty)
            {
                msg = "IBM Push Notification AppGuid is not set"
                return msg
            }
            
            PushNotification.bluemixRegion = json["bluemixregion"].stringValue
            if (PushNotification.bluemixRegion.isEmpty)
            {
                msg = "IBM Push Notification Bluemixregion is not set"
                return msg
            }
        }
        print("SetData done")
        return msg
    }

    public static func getProviderName() -> String{
        return PushNotification.providerName
    }
    
    public static func getUserId() -> String{
        return PushNotification.userId
    }
    
    
   /* @objc(setUserId:)
    func setUserId(command: CDVInvokedUrlCommand) {
        let myUserId = String(command.argument(at: 0) as! String)
        print("MLOG USERID : "+myUserId)
        Analytics.setUserID(myUserId)
    }
    
    @objc(logEvent:)
    func logEvent(command: CDVInvokedUrlCommand) {
        let event = String(command.argument(at: 0) as! String)
        let data : Dictionary<String, Any> = command.argument(at: 1) as! Dictionary<String, Any>
        print("MLOG EVENT : "+event)
        print("MLOG data : ",data)

        Analytics.logEvent(event, parameters:data)
    }*/
}


