#!/usr/bin/env bash

exitStatus () {
    if [ $1 -ne 0 ]; then
        exit $1 
    fi
}

echo "systemctl stop tomcat"
systemctl stop tomcat
exitStatus $?

echo "rm -rf /opt/tomcat/webapps/ROOT"
rm -rf /opt/tomcat/webapps/ROOT
exitStatus $?

echo "rm -f /opt/tomcat/webapps/ROOT.war"
rm -f /opt/tomcat/webapps/ROOT.war
exitStatus $?

echo "cp $1/TreePLE-Spring/build/libs/treeple*.war /opt/tomcat/webapps/ROOT.war"
cp $1/TreePLE-Spring/build/libs/treeple*.war /opt/tomcat/webapps/ROOT.war
exitStatus $?

echo "systemctl start tomcat"
systemctl start tomcat
exitStatus $?

