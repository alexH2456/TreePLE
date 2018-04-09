#!/usr/bin/env bash

systemctl stop tomcat
rm -rf /opt/tomcat/webapps/ROOT
rm -f /opt/tomcat/webapps/ROOT.war
cp $WORKSPACE/build/libs/treeple*.war /opt/tomcat/webapps/ROOT.war
systemctl start tomcat

