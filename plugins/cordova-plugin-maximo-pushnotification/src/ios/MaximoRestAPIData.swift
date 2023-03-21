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

import UIKit
import SwiftyJSON

public typealias ServiceResponse = (JSON,Error?) -> Void

public class MaximoRestAPIData: NSObject {
    static let sharedInstance = MaximoRestAPIData()
    
    func sendDeviceId(deviceid: String, eventname: String, url: String, user: String, pass: String) {
                      //onCompletion: @escaping (JSON) -> Void) {
        NSLog("MaximoRestAPIData: sendDeviceId")
        let postItems:[String: String] = ["api": "subscribe", "deviceid": deviceid, "eventname":eventname]
        NSLog("postItem", postItems)
      
        NSLog("MaximoRestAPIData: url: ", url)
        NSLog("MaximoRestAPIData: user: ", user)
        
        makeHTTPPostRequest(path: url, user: user, pass: pass, body: postItems as [String : AnyObject], onCompletion: { json, err in //  onCompletion(json as JSON)
            print("response", json)
        })
    }
    
    func sendDeviceIdwithApiKey(deviceid: String, eventname: String, url: String, apiKey: String) {
    
        NSLog("MaximoRestAPIData: sendDeviceIdwithApiKey")
        
        let postItems:[String: String] = ["api": "subscribe", "deviceid": deviceid, "eventname":eventname]
        
        print("postItem", postItems)
         
        makeHTTPPostRequest(path: url, apiKey: apiKey, body: postItems as [String : AnyObject], onCompletion: { json, err in //  onCompletion(json as JSON)
               print("response", json)
        })
    }
    
    func setUserInactive(userId: String, url: String, user: String, pass: String, deviceid: String, eventname: String) {
                         //onCompletion: @escaping (JSON) -> Void) {
        
        
        NSLog("MaximoRestAPIData: setUserInactive")
        let postItems:[String: String] = ["api": "unsubscribe", "deviceid":deviceid, "eventname":eventname]
        print("postItem", postItems)
         
        makeHTTPPostRequest(path: url, user: user, pass: pass, body: postItems as [String : AnyObject], onCompletion: { json, err in //  onCompletion(json as JSON
            print("response", json)
        })
    }
    
    
    func setUserInactive(userId: String, url: String, apiKey: String, deviceid: String, eventname: String) {
    //onCompletion: @escaping (JSON) -> Void) {
        
        NSLog("MaximoRestAPIData: setUserInactive")
        
        let postItems:[String: String] = ["api": "subscribe", "deviceid": deviceid, "eventname":eventname]
     /*   NSLog("postItem", postItems)
         
       makeHTTPPostRequest(path: url, apiKey: apiKey, body: postItems as [String : AnyObject], onCompletion: { json, err in //  onCompletion(json as JSON
            print("response", json)
        })*/
     }
    
    
    //GET request with username/password
    func makeHTTPGetRequest(path: String, user: String, pass: String, onCompletion: @escaping ServiceResponse) {
        
        NSLog("MaximoRestAPIData(user): makeHTTPGetRequest")
        //using URL and request geeting a json
        var request = URLRequest(url: NSURL(string: path)! as URL)
        request.httpMethod = "GET"
        let authStr = user + ":" + pass
        let authUtf8Str = authStr.data(using: .utf8)
        
        let authEncodedStr = authUtf8Str?.base64EncodedString()
        request.addValue(authEncodedStr!, forHTTPHeaderField: "maxauth")
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        URLSession.shared.dataTask(with: request) { (data:Data?, response: URLResponse?,
            error:Error?) in
      
            if let jsonData = data { //if data has data and success
                do {
                        let json: JSON = try JSON(data: jsonData)
                        onCompletion(json, nil)
                } catch { //error
                    onCompletion(JSON(),error)
                }
            } else { //if the data is nil
                    onCompletion(JSON(),error)
            }
        
        }.resume()
    }
    
    //GET request with apikey
    func makeHTTPGetRequest(path: String, apiKey: String, onCompletion: @escaping ServiceResponse)
    {
        NSLog("MaximoRestAPIData(apikey): makeHTTPGetRequest")
        //using URL and request getting a json
        var request = URLRequest(url: NSURL(string: path)! as URL)
        request.httpMethod = "GET"
        
        request.addValue(apiKey as String, forHTTPHeaderField: "apikey")
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
          
        URLSession.shared.dataTask(with: request) { (data:Data?, response: URLResponse?,
            error:Error?) in
            if let jsonData = data { //if data has data and success
                do {
                      let json: JSON = try JSON(data: jsonData)
                      onCompletion(json, nil)
                } catch { //error
                      onCompletion(JSON(),error)
                }
            } else { //if the data is nil
                onCompletion(JSON(),error)
            }
          }.resume()
      }
      
    //POST request with username/password
    func makeHTTPPostRequest(path: String, user: String, pass: String, body:[String:AnyObject], onCompletion: @escaping ServiceResponse) {
        
        NSLog("MaximoRestAPIData(user): makeHTTPPostRequest")
        var request = URLRequest(url: NSURL(string: path)! as URL)
        request.httpMethod = "POST"
        do { // add body
        
            let jsonBody = try JSONSerialization.data(withJSONObject: body, options: JSONSerialization.WritingOptions.prettyPrinted)
            
            request.httpBody = jsonBody
            let authStr = user + ":" + pass
            let authUtf8Str = authStr.data(using: .utf8)
            
            let authEncodedStr = authUtf8Str?.base64EncodedString()
            
            request.addValue(authEncodedStr!, forHTTPHeaderField: "maxauth")
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            
            print("maxauth: \(String(describing: authEncodedStr))")
            print("request: \(String(describing: request.httpBody))")
            
            //print("Device Token: \(token)")
            URLSession.shared.dataTask(with: request) { (data:Data?, response:URLResponse?, error:Error?) in
                if let jsonData = data {
                    do {
                        let json:JSON = try JSON(data:jsonData)
                        onCompletion(json, nil)
                    }catch {
                        onCompletion(JSON(), error)
                    }
                }
                else {
                    onCompletion(JSON(), error)
                }
            }.resume()
        }catch {
            onCompletion(JSON(), error)
        }
        
    }
    
    //POST request with apikey
    func makeHTTPPostRequest(path: String, apiKey: String, body:[String:AnyObject], onCompletion: @escaping ServiceResponse) {
        
        NSLog("MaximoRestAPIData(apikey): makeHTTPPostRequest")
        var request = URLRequest(url: NSURL(string: path)! as URL)
        request.httpMethod = "POST"
        do { // add body
            
            let jsonBody = try JSONSerialization.data(withJSONObject: body, options: JSONSerialization.WritingOptions.prettyPrinted)
            
            request.httpBody = jsonBody
            request.addValue(apiKey as String, forHTTPHeaderField: "apikey")
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            
            print("request: \(String(describing: request.httpBody))")
            
            //print("Device Token: \(token)")
            URLSession.shared.dataTask(with: request) { (data:Data?, response:URLResponse?, error:Error?) in
                if let jsonData = data {
                    do {
                        let json:JSON = try JSON(data:jsonData)
                        onCompletion(json, nil)
                    }catch {
                        onCompletion(JSON(), error)
                    }
                }
                else {
                    onCompletion(JSON(), error)
                }
            }.resume()
        }catch {
            onCompletion(JSON(), error)
        }
    }
}


