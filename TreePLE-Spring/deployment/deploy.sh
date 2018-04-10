#!/usr/bin/env bash

exitStatus () {
    if [ $1 -ne 0 ]; then
        exit $1 
    fi
}

systemctl stop tomcat
exitStatus $?

rm -rf /opt/tomcat/webapps/ROOT
exitStatus $?

rm -f /opt/tomcat/webapps/ROOT.war
exitStatus $?

cp $1/TreePLE-Spring/build/libs/treeple*.war /opt/tomcat/webapps/ROOT.war
exitStatus $?

systemctl start tomcat
exitStatus $?

