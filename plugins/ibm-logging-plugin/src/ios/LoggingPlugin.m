//
//  LogFilePlugin.m
//  MaximoAnywhereWorkExecutionIphone
//
//  Created by Amith on 6/11/20.
//
#import <Foundation/Foundation.h>
#import "LoggingPlugin.h"

@implementation LoggingPlugin

NSString *logPrefixD = @"MaximoMobile_Logs";
int aallowedFileCount = 10;
unsigned long long maxFileSizeInMB = 10;//MB
float tInt = 60.0;//seconds

NSString *pathForLog = nil;
NSString *documentsDirectory = nil;
FILE *fpSTDERR = nil;
FILE *fpSTDOUT = nil;


- (void)pluginInitialize
{
    [super pluginInitialize];
    [NSTimer scheduledTimerWithTimeInterval:tInt
                                     target:self
                                   selector:@selector(cleanFilesLargerThan)
                                   userInfo:nil
                                    repeats:YES];
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        [self writeLogsToFile];
        
    });
    
}


- (void) writeLogsToFile {
    
    if (!isatty(STDERR_FILENO)) {
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"yyyy-MM-dd  HH:mm:ss"];
        NSString *stringFromDate = [formatter stringFromDate:[NSDate date]];
        NSString *fileName = [NSString stringWithFormat:@"%@-%@.log", logPrefixD, stringFromDate];
        NSArray *allPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        documentsDirectory = [allPaths objectAtIndex:0];
        pathForLog = [documentsDirectory stringByAppendingPathComponent:fileName];
        fpSTDOUT =freopen([pathForLog cStringUsingEncoding:NSASCIIStringEncoding],"a+",stdout);
        fpSTDERR = freopen([pathForLog cStringUsingEncoding:NSASCIIStringEncoding],"a+",stderr);
    }
}

- (void) cleanFilesLargerThan
{
    NSFileManager* fileManager = [NSFileManager defaultManager];
    NSString *filepath = pathForLog;
    unsigned long long fileSize = [[fileManager attributesOfItemAtPath:filepath error:nil] fileSize];
    if(fileSize > (maxFileSizeInMB * 1024 * 1024 )){
        fclose(fpSTDOUT);
        fclose(fpSTDERR);
        [self writeLogsToFile];
        [self cleanupFiles:aallowedFileCount inDirectory:documentsDirectory];
    }
    
}

- (void) cleanupFiles:(int)allowedFileCount inDirectory:(NSString *)directory
{
    [self.commandDelegate runInBackground:^{
        NSFileManager* fileManager = [NSFileManager defaultManager];
        
        NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:directory error:nil];
        NSArray *filesWithSelectedPrefix = [files filteredArrayUsingPredicate:
                                            [NSPredicate predicateWithFormat:@"self CONTAINS 'MaximoMobile_Logs'"]];
        int countOfFiles = (int)[filesWithSelectedPrefix count];
        
        if(countOfFiles > allowedFileCount) {
            NSMutableArray *toBeSortedFiles = [NSMutableArray new];
            for(int i =0; i < [filesWithSelectedPrefix count]; i++){
                
                NSString *filepath=[NSString stringWithFormat:[directory stringByAppendingString:@"/%@"],[filesWithSelectedPrefix objectAtIndex:i]];
                
                NSLog(@"@Cleaning file: %@", filepath);
                
                NSDate   *creationDate =[[fileManager attributesOfItemAtPath:filepath error:nil] fileCreationDate];
                
                NSMutableDictionary *logFileObject = [[NSMutableDictionary alloc] init];
                logFileObject[@"path"] = filepath;
                logFileObject[@"createDate"] = creationDate;
                [toBeSortedFiles addObject:logFileObject];
            }
            
            NSSortDescriptor *sortByCreationDate = [NSSortDescriptor sortDescriptorWithKey:@"createDate"
                                                                                 ascending:NO];
            NSArray *fileSortDescriptors = [NSArray arrayWithObject:sortByCreationDate];
            NSArray *sortedFiles = [toBeSortedFiles sortedArrayUsingDescriptors:fileSortDescriptors];
            
            NSArray *logsFilesToRemove = [NSArray new];
            
            logsFilesToRemove = [sortedFiles subarrayWithRange:NSMakeRange(allowedFileCount,countOfFiles - allowedFileCount)];
            NSMutableDictionary *fp = nil;
            for(fp in logsFilesToRemove){
                NSString *rmPath = [fp valueForKey:@"path"];
                [[NSFileManager defaultManager] removeItemAtPath:rmPath error:nil];
            }
            
        }
    }];
    
}

- (void) saveLogs:(CDVInvokedUrlCommand*)command {
    
    NSString *callbackId = command.callbackId;
    
    //UIApplication* app = [UIApplication sharedApplication];
    NSArray *allPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *directory = [allPaths objectAtIndex:0];
    [self writeLogsToFile];
    NSFileManager* fileManager = [NSFileManager defaultManager];
    
    NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:directory error:nil];
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"self CONTAINS 'MaximoMobile_Logs'"];
    NSArray *filesWithSelectedPrefix = [files filteredArrayUsingPredicate:pred];
    NSArray *items = nil;
    NSMutableArray *filearray = [NSMutableArray arrayWithCapacity:1];
    for(int i =0; i < [filesWithSelectedPrefix count]; i++){
        
        NSString *filepath=[NSString stringWithFormat:[directory stringByAppendingString:@"/%@"],[filesWithSelectedPrefix objectAtIndex:i]];
        
        NSLog(@"@Prepare share for: %@", filepath);
        [filearray addObject:[NSURL fileURLWithPath:filepath]];
    }
    
    items = [NSArray arrayWithArray: filearray];
    
    UIActivityViewController* activityViewController =
    [[UIActivityViewController alloc] initWithActivityItems:items
                                      applicationActivities:nil];
    
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        [self.viewController presentViewController:activityViewController animated:YES completion:nil];
    }
    //if iPad
    else {
        // Change Rect to position Popover
        UIPopoverController *popup = [[UIPopoverController alloc] initWithContentViewController:activityViewController];
        [popup presentPopoverFromRect:CGRectMake(self.webView.frame.size.width/2, self.webView.frame.size.height/2, 0, 0)inView:self.webView permittedArrowDirections:UIPopoverArrowDirectionDown animated:YES];
    }
    
    
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}



@end
