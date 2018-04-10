#!/usr/bin/env bash

exitStatus () {
    if [ $1 -ne 0 ]; then
        exit $1
    fi
}

echo "systemctl stop httpd"
systemctl stop httpd
exitStatus $?

echo "npm --prefix $1/TreePLE-Web install --unsafe-perm"
npm --prefix $1/TreePLE-Web install --unsafe-perm
exitStatus $?

echo "npm run --prefix $1/TreePLE-Web build"
npm run --prefix $1/TreePLE-Web build
exitStatus $?

echo "cp -f $1/TreePLE-Web/dist/bundle.js /var/www/html/"
cp -f $1/TreePLE-Web/dist/bundle.js /var/www/html/
exitStatus $?

echo "cp -f $1/TreePLE-Web/dist/bundle.js.map /var/www/html/"
cp -f $1/TreePLE-Web/dist/bundle.js.map /var/www/html/
exitStatus $?

echo "cp -f $1/TreePLE-Web/index.html /var/www/html/"
cp -f $1/TreePLE-Web/index.html /var/www/html/
exitStatus $?

echo "systemctl start httpd"
systemctl start httpd
exitStatus $?

