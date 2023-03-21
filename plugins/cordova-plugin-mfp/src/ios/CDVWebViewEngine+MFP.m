/**
 Licensed Materials - Property of IBM
 
 (C) Copyright 2020 IBM Corp.
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

//
//  CDVWebViewEngine+MFP.m
//  IBMMobileFirstPlatformFoundation
//
//  Created by Vittal R Pai on 28/09/20.
//

#if defined(__has_include)
#if __has_include("../../../CordovaLib/Classes/Private/Plugins/CDVWebViewEngine/CDVWebViewEngine.h")

#import "../../../CordovaLib/Classes/Private/Plugins/CDVWebViewEngine/CDVWebViewEngine.h"
#import <IBMMobileFirstPlatformFoundationHybrid/IBMMobileFirstPlatformFoundationHybrid.h>
#import <objc/runtime.h>

@interface CDVWebViewEngine(MFP)
@property (nonatomic, strong, readwrite) UIView* engineWebView;
@property (nonatomic, readwrite) Boolean cdvIsFileScheme;
@property (nonatomic, readwrite) NSString *CDV_ASSETS_URL;
@end

@implementation CDVWebViewEngine(MFP)
@dynamic engineWebView;
@dynamic cdvIsFileScheme;
@dynamic CDV_ASSETS_URL;

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wobjc-protocol-method-implementation"

// Override loadRequest Method to provide compatability with MF SDK to handle Directupdate web package
- (id)loadRequest:(NSURLRequest*)request
{
    if ([self canLoadRequest:request]) { // can load, differentiate between file urls and other schemes
        if(request.URL.fileURL && self.cdvIsFileScheme) {
            NSURL* readAccessUrl = [request.URL URLByDeletingLastPathComponent];
            // Check if the request url is MFP main html path. If yes, Change the readAccessUrl to handle direct update package.
            if([request.URL.absoluteString isEqualToString:[[WL sharedInstance] mainHtmlFilePath]] && [request.URL.absoluteString isEqualToString:[[[NSBundle mainBundle] bundleURL].absoluteString stringByAppendingString:@"www/index.html"]]){
                 readAccessUrl = [[[NSBundle mainBundle] bundleURL] URLByDeletingLastPathComponent];
            } else if([request.URL.absoluteString isEqualToString:[[WL sharedInstance] mainHtmlFilePath]]){
                 readAccessUrl = [[request.URL URLByDeletingLastPathComponent] URLByDeletingLastPathComponent];
            }
            return [(WKWebView*)self.engineWebView loadFileURL:request.URL allowingReadAccessToURL:readAccessUrl];
        } else if (request.URL.fileURL) {
            NSURL* startURL = [NSURL URLWithString:((CDVViewController *)self.viewController).startPage];
            NSString* startFilePath = [self.commandDelegate pathForResource:[startURL path]];
            NSURL *url = [[NSURL URLWithString:self.CDV_ASSETS_URL] URLByAppendingPathComponent:request.URL.path];
            if ([request.URL.path isEqualToString:startFilePath]) {
                url = [NSURL URLWithString:[NSString stringWithFormat:@"%@/%@", self.CDV_ASSETS_URL, startURL]];
            }
            if(request.URL.query) {
                url = [NSURL URLWithString:[@"?" stringByAppendingString:request.URL.query] relativeToURL:url];
            }
            if(request.URL.fragment) {
                url = [NSURL URLWithString:[@"#" stringByAppendingString:request.URL.fragment] relativeToURL:url];
            }
            request = [NSURLRequest requestWithURL:url];
        }
        return [(WKWebView*)self.engineWebView loadRequest:request];
    } else { // can't load, print out error
        NSString* errorHtml = [NSString stringWithFormat:
                               @"<!doctype html>"
                               @"<title>Error</title>"
                               @"<div style='font-size:2em'>"
                               @"   <p>The WebView engine '%@' is unable to load the request: %@</p>"
                               @"   <p>Most likely the cause of the error is that the loading of file urls is not supported in iOS %@.</p>"
                               @"</div>",
                               NSStringFromClass([self class]),
                               [request.URL description],
                               [[UIDevice currentDevice] systemVersion]
                               ];
        return [self loadHTMLString:errorHtml baseURL:nil];
    }
}

#pragma clang diagnostic pop

@end

#endif
#endif