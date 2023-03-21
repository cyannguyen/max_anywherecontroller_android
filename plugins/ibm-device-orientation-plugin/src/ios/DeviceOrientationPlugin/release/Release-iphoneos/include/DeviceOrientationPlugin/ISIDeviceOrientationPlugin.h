//
//  ISIDeviceOrientationManager.h
//  AnywhereWorkManagerWorkExecutionIphone
//
//  Created by Leandro Cassa on 29/7/2014.
//
//

#import <Cordova/CDV.h>

@interface ISIDeviceOrientationPlugin : CDVPlugin

@property NSString *callbackId;

-(void) SCREEN_ORIENTATION:(CDVInvokedUrlCommand*)command;

@end
