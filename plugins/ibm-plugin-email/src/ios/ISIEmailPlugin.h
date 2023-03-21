#import <Foundation/Foundation.h>
#import <MessageUI/MFMailComposeViewController.h>
#import <Cordova/CDVPlugin.h>

@interface ISIEmailPlugin : CDVPlugin <MFMailComposeViewControllerDelegate>

// Shows the email composer view with pre-filled data
- (void) sendMail:(CDVInvokedUrlCommand*)command;
// check if email is configured
- (void) isServiceAvailable:(CDVInvokedUrlCommand*)command;

@end