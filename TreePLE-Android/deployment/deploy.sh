#!/usr/bin/env bash

exitStatus () {
    if [ $1 -ne 0 ]; then
        exit $1 
    fi
}

echo "rm -f /opt/tomcat/TreePLE-Android/*-release-signed.apk"
rm -f /opt/tomcat/TreePLE-Android/*-release-signed.apk
exitStatus $?

echo "cp -f $1/TreePLE-Android/app/build/outputs/apk/release/*-release-signed.apk /opt/tomcat/TreePLE-Android/TreePLE.apk"
cp -f $1/TreePLE-Android/app/build/outputs/apk/release/*-release-signed.apk /opt/tomcat/TreePLE-Android/TreePLE.apk
exitStatus $?

echo "chown tomcat -R /opt/tomcat/TreePLE-Android/"
chown tomcat -R /opt/tomcat/TreePLE-Android/
exitStatus $?

