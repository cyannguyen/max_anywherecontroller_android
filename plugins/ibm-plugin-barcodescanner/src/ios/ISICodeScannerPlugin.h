//
//
//

#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>

@interface ISICodeScannerPlugin : CDVPlugin  <AVCaptureMetadataOutputObjectsDelegate>

- (void)scan:(CDVInvokedUrlCommand*)command;

@end
