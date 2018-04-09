#!/usr/bin/env bash

systemctl stop httpd
#npm --prefix $WORKSPACE/TreePLE-Web install
npm --prefix /run/jenkins/workspace/TreePLE2/TreePLE-Web install
#npm run --prefix $WORKSPACE/TreePLE-Web build
npm run --prefix /run/jenkins/workspace/TreePLE2/TreePLE-Web build
cp -f $WORKSPACE/TreePLE-Web/dist/bundle.js /var/www/html/
cp -f $WORKSPACE/TreePLE-Web/index.html /var/www/html/
systemctl start httpd

