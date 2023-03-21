# Building the Maximo Anywhere application container

The IBM Maximo Anywhere application container is used to create the Android, iOS, and Windows application packages (binary files) that are used to install Maximo Anywhere apps on mobile devices.

## **Prerequisites**

The Maximo Anywhere application container must be installed on the computer that contains the Maximo Anywhere apps. For environments with restricted internet access please also read the [Special Considerations](#Special_Considerations) section below

### General

* Download and install __[Node JS v10.16.3](https://nodejs.org/en/)__
  
### Android

You can build Android apps on a Windows 10, OS X, or Linux machine that contains the following components:

* Java SDK 1.8.
* [Android Studio v3.5 or higher](https://developer.android.com/studio)

After you install Android Studio, open the Android SDK Manager. Open Android Studio. If the Android Studio is a new install go to (Configure > SDK Manager) when Android Studio opens up. If you are inside the IDE you can access it from (Tools > SDK Manager), and ensure that the following components are installed:

1. On the Platforms tab, verify that Android Platform SDK 28 or higher is installed.
2. On the SDK Tools tab, click **Check Show Package Details**  and select Android SDK build-tools version 28.0.1 or higher.
3. On the SDK Tools tab,  verify that Android Support Repository is installed.

In some cases Android will install SDKs without prompting you to accept the licenses. This can fail the build later. So before proceeding,please read the Android SDK Licenses part in the [Troubleshooting](#Troubleshooting) section below.

For Linux machines, you will need to download gradle and export the gradle directory path to GRADLE_HOME variable in your environment variable.

### iOS

You can build iOS apps on a Mac OS X machine that contains the following components:

* [Xcode version 10.1 or later](https://developer.apple.com/xcode/resources/)
* Xcode command line tools. Xcode prompts you to install the command line tools when you open it for the first time after installation. To install the Xcode command line tools manually, run the following command from Terminal:
  
    ```bash
    xcode-select --install
    ```

* Valid provisioning profiles for the apps ([Refer to Apple Code Signing Support Page](https://developer.apple.com/support/code-signing/) for more info)

### Windows

You can build Windows apps on a Windows 10 machine that contains the following components:

* Microsoft Visual Studio 2015 including Visual Studio Tools and Windows 10 SDK v10.0.14393

To confirm that you have Windows 10 SDK installed, complete the following steps:
1. Go to Control Panel -> Apps (Apps & Features).
2. Search for Visual Studio 2015, and select **Modify**.
3. When the installer opens, select **Modify**. 
4. Search for **Windows and Web Development** -> **Expand** -> **Universal Windows App Development Tools** -> **Tools (1.4.1)** and **Windows 10 SDK (10.0.14393)**.
5. If Windows 10 SDK (10.0.14393) is not selected, select it
6. In the same section **Windows and Web Development**, also select the option **Windows 8.1 and Windows Phone 8.0/8.1 Tools**.
7. Click on **UPDATE** button to apply the changes.

## Procedure

1. Open a command line and change the directory to the MaximoAnywhereContainer directory.

2. Verify that all of the properties in the __MaximoAnywhereContainer > Container.properties__ file are correct.

3. Ensure that the build file can be executed, and run the following command:

   ```bash
   For OSX/Linux: ./build.sh
   For Windows: build.bat
   ```

The application binaries are created in the bin folder.

## Running the application binaries

### Running on Android devices

* Android 9 or later is supported.

### Running on iOS devices

* iOS 12.1 or later is supported.

### Running on Windows devices

* Windows 10 is supported.

## Special_Considerations

### Restricted Internet Access

If you have restricted internet access, the build engine will need access to the following repositories.

1. <https://registry.npmjs.org>
2. <https://services.gradle.org/distributions>

### Installing Anywhere behind a proxy
Anywhere build engine needs connection to the internet to download dependent packages. If connections are managed by a proxy, do the following steps

1. Collect the "proxy Host", "port Information", username and password if set. 
2. Navigate to the container directory e.g /AnywhereAppCntr.
3. Run the following npm commands

- If proxy does not require a  username/password

    ```bash
        npm config set proxy http://<proxyhost>:<port>
        npm config set https-proxy http://<proxyhost>:<port>
    ```

- If proxy requires a username and password:

    ```bash
        npm config set proxy http://<username>:<password>@<proxyhost>:<port>
        npm config set https-proxy http://<username>:<password>@<proxyhost>:<port>
    ```

Edit the proxy host, port, username, password in the commands above as applicable.

4. In the terminal navigate to (ContanierDirectory)/scripts/internal/android and open the gradle.properties in a text editor. Add the following properties to the end of the file

   ```
    systemProp.http.proxyHost=
    systemProp.http.proxyPort=
    systemProp.https.proxyHost=
    systemProp.https.proxyPort=
    systemProp.http.proxyUser=
    systemProp.http.proxyPassword=
   ```

Fill up the details for each of the property above after the =  sign. If you do not require username, password remove those fields.

5. Open Android Studio to install Gradle using proxy, and navigate to "Configure->SDK Manager -> System Settings ->HTTP Proxy"

## Troubleshooting

### Android SDK Licenses

Android Studio when setting itself up will download and install the latest sdk along with it without prompting to accept the licenses corresponding to the SDKs. This may cause the build to fail.

In order to ensure the build runs successfully, please ensure the licenses for the SDKs and tools are accepted. Follow the steps below to check if they were accepted and if not, then review and accept it.

1. Open the terminal/command line
2. Navigate to your Android Home location. If you do not know, you can open up the SDK Manager in Android Studio as mentioned above and look for Android SDK Location. Navigate to this location from the terminal/command line. e.g cd ANDROID_HOME_LOCATION
3. From this location navigate to tools/bin directory e.g cd tools/bin
4. Run

   For Mac/Linux

   ```bash
    ./sdkmanager --licenses
   ```

   For Windows

   ```dos
   sdkmanager.bat --licenses
   ```

5. If you have licenses to accept it will ask you yes/no questions to review. Type y or N to review/accept or decline according to your enterprise policies. As long as you have accepted licenses to all the prerequisite Android SDKs and tools, the build will run fine.