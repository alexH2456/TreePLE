#!/usr/bin/env bash

systemctl stop httpd
#npm --prefix $WORKSPACE/TreePLE-Web install
#npm run --prefix $WORKSPACE/TreePLE-Web build
#cp -f $WORKSPACE/TreePLE-Web/dist/bundle.js /var/www/html/
#cp -f $WORKSPACE/TreePLE-Web/index.html /var/www/html/
npm --prefix $1/TreePLE-Web install --unsafe-perm
npm run --prefix $1/TreePLE-Web build
cp -f $1/TreePLE-Web/dist/bundle.js /var/www/html/
cp -f $1/TreePLE-Web/index.html /var/www/html/
systemctl start httpd

