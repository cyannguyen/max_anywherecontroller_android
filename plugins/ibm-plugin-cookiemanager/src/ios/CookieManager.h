//
//  ISIOfflineLogoutPlugin.h
//  AnywhereWorkManagementWorkExecutionIphone
//
//  Created by Leonardo Leite de Melo on 4/7/14.
//
//

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

@interface CookieManager : CDVPlugin

// Clears all cookies
- (void) clearAllCookies:(CDVInvokedUrlCommand*)command;
@end
