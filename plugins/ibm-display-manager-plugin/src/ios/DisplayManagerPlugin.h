//
//  DisplayManager.h
//  MaximoAnywhereWorkExecutionIphone
//
//  Created by local admin on 6/8/16.
//
//

#ifndef DisplayManager_h
#define DisplayManager_h


#endif /* DisplayManager_h */


#import <Cordova/CDV.h>
@interface DisplayManagerPlugin :CDVPlugin

- (void) keepDisplayAlive:(CDVInvokedUrlCommand*)command;

- (void) restoreSleepConfig:(CDVInvokedUrlCommand*)command;

@end