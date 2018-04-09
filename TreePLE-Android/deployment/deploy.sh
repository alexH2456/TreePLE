#!/usr/bin/env bash

rm -f /opt/tomcat/TreePLE-Android/*-release-signed.apk
cp -f $WORKSPACE/TreePLE-Android/app/build/outputs/apk/release/*-release-signed.apk /opt/tomcat/TreePLE-Android/TreePLE.apk
chown tomcat -R /opt/tomcat/TreePLE-Android/
