const readline = require('readline');
const fs = require('fs');
const path = require("path");

module.exports = function(){
    return new Promise((resolve, reject)=>{
        let ControllerPath = "platforms/ios/Work Execution/Classes/"
        let ControllerFile = "MainViewController.m";
        let buffer = "";
        let found = false;

        let rl = readline.createInterface({
            input: fs.createReadStream(path.resolve(ControllerPath, ControllerFile))
        });

        let line_no = 0;

        // event is emitted after each line
        rl.on('line', function(line) {
            if(line)
                buffer += "\n" + line;
            
            if(line.indexOf('@implementation MainViewController') > -1 && !found){
                buffer += "\n" + template;
                found = true;
            }
            line_no++;
            console.log(line);
        });

        // end
        rl.on('close', function(line) {
            console.log('Total lines : ' + line_no);
            fs.writeFileSync(path.resolve(ControllerPath, 'ViewRewrtie.m'), buffer)
            fs.renameSync(path.resolve(ControllerPath, ControllerFile), path.resolve(ControllerPath, ControllerFile + '.bak'));
            fs.renameSync(path.resolve(ControllerPath, 'ViewRewrtie.m'), path.resolve(ControllerPath, ControllerFile));
            resolve();
        });
    })
}


let template = "@synthesize inputView;"
template += "\n"
template += "- (void)webViewDidStartLoad:(UIWebView *)theWebView\n"
template += "{\n"
template += "scanner = NO;\n"
template += "return [ super webViewDidStartLoad:theWebView ];\n"
template += "}"
template += "\n"
template += "- (void) insertText:(NSString* )text\n"
template += '    NSLog(@"%@", text)\;\n'
template += "    if(ionicl != nil)\n"
template += "        [ionicl notifyKeyStorke:text];\n"
template += "}\n"
template += "\n"
template += "- (void) viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator{\n"
template += "    if (keyboard_isVisible) {\n"
template += "        keyboardHideTiggeredByOrientation = YES;\n"
template += "    }\n"
template += "}\n"
template += "\n\n"

template += "- (BOOL) canBecomeFirstResponder{\n"
   //return isResponder\;
template += "    return scanner;\n"
template += "}\n"
template += "\n"
template += "- (BOOL) resignFirstResponder{\n"
template += "    return !scanner;\n"
template += "}\n"
template += "\n"
template += "- (BOOL) hasText{\n"
template += "    return YES;\n"
template += "}\n"

template += "- (UIView *)inputView{\n"
template += "    if (scanner == NO)\n"
template += "        return nil;\n"
template += "    else{\n"
template += "    if (!inputView) {\n"
template += "        CGRect accessFrame = CGRectMake(0.0, 0.0, 10.0, 10.0);\n"
template += "        inputView = [[UIView alloc] initWithFrame:accessFrame];\n"
template += "    }\n" 
template += "    return inputView;\n"
template += "    }\n"
template += "}\n"


