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

import FirebaseCore
import FirebaseMessaging
import UIKit
import UserNotifications
import PushKit
import SwiftyJSON

public class MaximoPushFCM: NSObject, MessagingDelegate, UNUserNotificationCenterDelegate{
    let userID: String
    let mFirebase:CDVPlugin
   
    let gcmMessageIDKey = "gcm.message_id"
    
    private static var userName = ""
    private static var pass = ""
    private static var url = ""
    private static var apiKey = ""
    private var msgDisplayed = 0
    private static var eventName = ""
    
    
    init(userID: String,mFirebase:CDVPlugin) {
        self.userID = userID
        self.mFirebase = mFirebase
        super.init()
    }
    
    public func MaximoPush(url: String, user: String, pass: String, eventname: String) {
       
        NSLog("MaximoPushFCM(user): registerForPushNotifications")
        MaximoPushFCM.userName = user
        MaximoPushFCM.url = url
        MaximoPushFCM.pass = pass
        MaximoPushFCM.eventName = eventname
    
        //registerForPushNotifications()
    }
       
       
    public func MaximoPush(url: String, apiKey: String, eventname: String) {
        
        NSLog("MaximoPushFCM(apikey): registerForPushNotifications")
        MaximoPushFCM.url = url
        MaximoPushFCM.apiKey = apiKey
        MaximoPushFCM.eventName = eventname
        
        //registerForPushNotifications()
    }
    
    func registerForPushNotifications() {
         NSLog("MaximoPushFCM: registerForPushNotifications")
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
            // For iOS 10 data message (sent via FCM)
            Messaging.messaging().delegate = self
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            UIApplication.shared.registerUserNotificationSettings(settings)
        }

        UIApplication.shared.registerForRemoteNotifications()
        //updateFirestorePushTokenIfNeeded()
    }

    public func updateFirestorePushTokenIfNeeded() {
        
        NSLog("MaximoPushFCM: updateFirestorePushTokenIfNeeded")
        if let token = Messaging.messaging().fcmToken {
            
            if ( MaximoPushFCM.apiKey == "") {
                MaximoRestAPIData.sharedInstance.sendDeviceId(deviceid: token, eventname: MaximoPushFCM.eventName, url: MaximoPushFCM.url, user: MaximoPushFCM.userName, pass: MaximoPushFCM.pass)
            } else {
                MaximoRestAPIData.sharedInstance.sendDeviceIdwithApiKey(deviceid: token, eventname: MaximoPushFCM.eventName, url: MaximoPushFCM.url, apiKey: MaximoPushFCM.apiKey)
            }
           //let usersRef = //Firestore.firestore().collection("users_table").document(userID)
            //usersRef.setData(["fcmToken": token], merge: true)
        }
    }

    public func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
         NSLog("MaximoPushFCM: didReceive")
            print(remoteMessage)
    }

    public func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        NSLog("MaximoPushFCM: didReceiveRegistrationToken")
        updateFirestorePushTokenIfNeeded()
    }
    

    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
      print("Unable to register for remote notifications: \(error.localizedDescription)")
    }

    
    //foreground
    public func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void)  {
        
        NSLog("MaximoPushFCM: UNUserNotificationCenter")
        let userInfo = notification.request.content.userInfo
       
        //let json = try? JSONSerialization.data(withJSONObject: notification.request.content.userInfo).base64EncodedData()
        //let jsonData = String(data: json, encoding: String.Encoding.utf8) ?? "{}"
        
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)
   
        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
          print("Message ID: \(messageID)")
        }
        
        print("Received message with response ",notification.request.content.userInfo)
        //let message = extractUserInfo(userInfo: notification.request.content.userInfo)
        
        //print("message :  ", message)
    
        setMessageinPluginResult(userInfo: notification.request.content.userInfo)
        completionHandler([.alert, .badge, .sound])
        
    }

    
    public func setMessageinPluginResult (userInfo: [AnyHashable : Any]) {
        
        NSLog ("setMessageinPluginResult:userInfo: ", userInfo)
        
        let payloaddata = userInfo["extradata"]
        print ("setMessageinPluginResult:extradata: ", payloaddata as Any)
        
        let message = extractUserInfo(userInfo: userInfo)
        var msgObject:[String: String] = ["title": message.title, "message":message.body]
        
        var pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: msgObject)
        
        if (payloaddata != nil ) {
            do {
            
                // here "jsonData" is the dictionary encoded in JSON data
                let jsonData = try JSONSerialization.data(withJSONObject: payloaddata, options: .prettyPrinted)
                   
                // here "decoded" is of type `Any`, decoded from JSON data
                let decoded = try JSONSerialization.jsonObject(with: jsonData, options: [])
                  print ("setMessageinPluginResult:decoded: ", decoded)
            
                // you can now cast it with the right type
                if let dictFromJSON = decoded as? [String:Any] {
                    /*for (key, value) in dictFromJSON {
                       print("\(key) -> \(value)")
                    }*/
                
                    print ("setMessageinPluginResult:extradata: ", dictFromJSON)

                    pluginResult = CDVPluginResult(
                        status: CDVCommandStatus_OK,
                        messageAs: dictFromJSON)
                }
            } catch {
                   print(error.localizedDescription)
            }
        }
       
        pluginResult?.setKeepCallbackAs(true)
                      
        print ("callbackid in messagereceived : " +
                    (PushNotification.notificationCallback?.callbackId)!)
                                      
        mFirebase.commandDelegate!.send(
                            pluginResult,
                            callbackId: PushNotification.notificationCallback?.callbackId )
    }
    
    
    func extractUserInfo(userInfo: [AnyHashable : Any]) -> (title: String, body: String) {
        var info = (title: "", body: "")
        guard let aps = userInfo["aps"] as? [String: Any] else { return info }
        guard let alert = aps["alert"] as? [String: Any] else { return info }
        let title = alert["title"] as? String ?? ""
        let body = alert["body"] as? String ?? ""
        info = (title: title, body: body)
        return info
    }
    
    public func unregister() {
        UIApplication.shared.unregisterForRemoteNotifications()
    }
    
    //background
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                       fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        print("MaximoPushFCM: didReceiveRemoteNotification")
        // If you are receiving a notification message while your app is in the background,
        // this callback will not be fired till the user taps on the notification launching the application.
        // TODO: Handle data of notification

        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)

        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
          print("Message ID: \(messageID)")
        }
    
        /*let message = extractUserInfo(userInfo: userInfo)
    
        let msgObject:[String: String] = ["title": message.title, "message":message.body]
          
        let pluginResult = CDVPluginResult(
                        status: CDVCommandStatus_OK,
                        messageAs: msgObject
        )
        
        pluginResult?.setKeepCallbackAs(true)
         
        print ("callbackid in messagereceived : " + (PushNotification.notificationCallback?.callbackId)!)
        mFirebase.commandDelegate!.send(
                 pluginResult,
                 callbackId: PushNotification.notificationCallback?.callbackId
        )*/
        
        setMessageinPluginResult(userInfo: userInfo)

        // Print full message.
        print(userInfo)

        completionHandler(UIBackgroundFetchResult.newData)
      }
    
    
      public func userNotificationCenter(_ center: UNUserNotificationCenter,
                                  didReceive response: UNNotificationResponse,
                                  withCompletionHandler completionHandler: @escaping () -> Void) {
        NSLog("MaximoPushFCM: didReceive")
    
        let userInfo = response.notification.request.content.userInfo
        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
          print("Message ID: \(messageID)")
        }

        // Print full message.
        print(userInfo)
        /*let message = extractUserInfo(userInfo: userInfo)
        
        let msgObject:[String: String] = ["title": message.title, "message":message.body]
              
        let pluginResult = CDVPluginResult(
                            status: CDVCommandStatus_OK,
                            messageAs: msgObject
        )
            
        pluginResult?.setKeepCallbackAs(true)
             
        print ("callbackid in messagereceived : " + (PushNotification.notificationCallback?.callbackId)!)
        mFirebase.commandDelegate!.send(
                pluginResult,
                callbackId: PushNotification.notificationCallback?.callbackId
        )*/
        
        setMessageinPluginResult(userInfo: userInfo)
       
        completionHandler()
    }
}

              


