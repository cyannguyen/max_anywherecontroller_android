#!/bin/bash

build_errors_file=build_errors.log

## Fix for xcode hang issue

##open "BarcodeScanner.xcodeproj"
##sleep 120
##killall Xcode

## Fix for xcode hang issue

outputdir=$PWD

## cd to the xcodeproj folder
cd $1

rm ../$build_errors_file

xcodebuild -workspace 'project.xcworkspace' -scheme 'Anywhere' -configuration Release -sdk iphoneos ONLY_ACTIVE_ARCH=NO OBJROOT=$outputdir SYMROOT=$outputdir 2>&1 | tee -a $build_errors_file

xcodebuild -workspace 'project.xcworkspace' -scheme 'Anywhere' -configuration Release -sdk iphonesimulator ONLY_ACTIVE_ARCH=NO OBJROOT=$outputdir EXCLUDED_ARCHS="arm64" SYMROOT=$outputdir 2>&1 | tee -a $build_errors_file

mv $outputdir/Release-iphonesimulator/$2.a $outputdir/$2i386.a
mv $outputdir/Release-iphoneos/$2.a $outputdir/$2ARM64.a

lipo $outputdir/$2i386.a $outputdir/$2ARM64.a -output $outputdir/$2.a -create 

errors=`grep -wc "The following build commands failed" $build_errors_file`

echo "Errors: "$errors
if [ "$errors" != "0" ]
then
    echo "BUILD FAILED."
    rm $build_errors_file
    exit 1
fi

cd ..
