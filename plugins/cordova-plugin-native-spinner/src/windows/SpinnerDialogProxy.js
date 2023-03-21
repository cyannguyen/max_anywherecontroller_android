  //  cordova-plugin-spinnerdialog
  //  Copyright © 2015 filfat Studios AB
  //  Repo: https://github.com/filfat-Studios-AB/cordova-plugin-spinnerdialog
  /* global Windows, cordova */
  var progressIndicator;
  
  cordova.commandProxy.add("SpinnerDialog", {
    show: function (successCallback, errorCallback, data) {
      if (typeof Windows !== 'undefined' &&
        typeof Windows.UI !== 'undefined' /* Check that we have a UI to work with */ &&
        typeof Windows.UI.ViewManagement.StatusBar !== 'undefined' /* Check that we have the StatusBar to work with*/) {
  
        var data = data[0] || { title: undefined };
        progressIndicator = Windows.UI.ViewManagement.StatusBar.ProgressIndicator
          || Windows.UI.ViewManagement.StatusBar.getForCurrentView().progressIndicator;
  
        if (data.title == null)
          data.title = undefined;
        progressIndicator.text = typeof data.title !== 'undefined' ? data.title : 'Loading...';
        progressIndicator.showAsync();
        Windows.UI.ViewManagement.StatusBar.getForCurrentView().showAsync();
      } else if (typeof Windows !== 'undefined' &&
        typeof Windows.UI !== 'undefined' /* Check that we have a UI to work with */) {
          var progressBarId = document.getElementById("winProgressDiv");
  
          if (progressBarId) {
              progressBarId.style.visibility = "visible";
          } else {
             
              var bodyContent = document.getElementById("content");
              if (bodyContent) {
                  var progressDiv = document.createElement("div");
                  progressDiv.setAttribute('id', 'winProgressDiv');
  
                  var progressCenterDiv = document.createElement("div");
                  progressCenterDiv.setAttribute('id', 'winProgressCenterDiv');
                  progressCenterDiv.style.margin = "0 auto";
                  progressCenterDiv.style.width = "100%";
                  progressCenterDiv.style.top = "50%";
                  //progressCenterDiv.style.transform = "perspective(1px) translateY(-50%)";
                  progressCenterDiv.style.position = "relative";
                  progressCenterDiv.style.left = "50%";
                  
  
                  var progressBar = document.createElement("progress");
                  progressBar.setAttribute('id', 'winProgressBar');
                 
                  progressBar.textContent = typeof data.title !== 'undefined' ? data.title : 'Loading...';
                  progressBar.classList.add("win-ring");
                  progressBar.classList.add("win-medium");
                  
                  progressDiv.style.visibility = "visible"
                  progressDiv.style.top = "0px"
                  progressDiv.style.left = "0px"
                  progressDiv.style.position = "absolute"
                  progressDiv.style.backgroundColor = "black"
                  progressDiv.style.opacity = "0.7";
                  progressDiv.style.height = "100%";
                  progressDiv.style.width = "100%";
                  progressDiv.style.zIndex = "100";
                  
  
                  progressDiv.appendChild(progressCenterDiv);
                  progressCenterDiv.appendChild(progressBar);
  
                  bodyContent.appendChild(progressDiv);
              }
          }
         
         
         
         // progressBar.style.visibility = "visible";
      }
    },
  
    hide: function (successCallback, errorCallback, data) {
      if (typeof Windows !== 'undefined' &&
        typeof Windows.UI !== 'undefined' /* Check that we have a UI to work with */ &&
        typeof Windows.UI.ViewManagement.StatusBar !== 'undefined' /* Check that we have the StatusBar to work with*/) {
  
        progressIndicator.hideAsync();
        Windows.UI.ViewManagement.StatusBar.getForCurrentView().hideAsync(); //TODO
      } else if (typeof Windows !== 'undefined' &&
        typeof Windows.UI !== 'undefined' /* Check that we have a UI to work with */) {
  
          var progressBar = document.getElementById("winProgressDiv");
         // var bodyContent = document.getElementById("content");
          //if (bodyContent) {
              
          progressBar.style.visibility="hidden"
          //}
          
      }
    }
  });
  
