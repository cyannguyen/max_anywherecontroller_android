/**
 * PrivacyScreenPlugin.m
 * Created by Tommy-Carlos Williams on 18/07/2014
 * Copyright (c) 2014 Tommy-Carlos Williams. All rights reserved.
 * MIT Licensed
 */
 /**
 * original code for PrivacyScreenPlugin is removed because of blackscreen issue in iphone 5 & iphone 6
 * meanwhile, introduce source code from https://github.com/lifeofcoding/cordova-blur-app-privacy-screen/blob/master/src/ios/BlurPrivacyScreen.m
 * and the code is under MIT Licensed
 */
#import "AppDelegate.h"

@interface AppDelegate (PrivacyScreenPlugin) {}

@end

@implementation AppDelegate (PrivacyScreenPlugin)

- (void)applicationWillResignActive:(UIApplication *)application {
    self.window.backgroundColor = [UIColor clearColor];

    UIBlurEffect *blurEffect = [UIBlurEffect effectWithStyle:UIBlurEffectStyleExtraLight];
    UIVisualEffectView *blurEffectView = [[UIVisualEffectView alloc] initWithEffect:blurEffect];
    blurEffectView.frame = self.window.bounds;
    blurEffectView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;

    blurEffectView.tag = 1234;
    blurEffectView.alpha = 0;
    [self.window addSubview:blurEffectView];
    [self.window bringSubviewToFront:blurEffectView];

    // fade in the view
    [UIView animateWithDuration:0.5 animations:^{
        blurEffectView.alpha = 1;
    }];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    NSLog(@"Hiding blur");
    // grab a reference to our custom blur view
    UIView *blurEffectView = [self.window viewWithTag:1234];

    // fade away colour view from main view
    [UIView animateWithDuration:0.5 animations:^{
        blurEffectView.alpha = 0;
    } completion:^(BOOL finished) {
        // remove when finished fading
        [blurEffectView removeFromSuperview];
    }];
}

@end