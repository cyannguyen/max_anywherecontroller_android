/*
 Keyboard plugin that controls the keyboard and
 textinput for the when the laser scanner is enabled.
 */

#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVAvailability.h>


@interface KeyboardPlugin : CDVPlugin <UIKeyInput, UITextViewDelegate>
    



@end
