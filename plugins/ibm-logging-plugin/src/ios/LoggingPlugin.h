//
//  LogFilePlugin.h
//  MaximoAnywhereWorkExecutionIphone
//
//  Created by Amith on 6/11/20.
//

#ifndef LoggingPlugin_h
#define LoggingPlugin_h


#endif /* LoggingPlugin_h */

#import <Cordova/CDVPlugin.h>


@interface LoggingPlugin : CDVPlugin
    
- (void) saveLogs:(CDVInvokedUrlCommand*)command;
- (void) writeLogsToFile;

@end
