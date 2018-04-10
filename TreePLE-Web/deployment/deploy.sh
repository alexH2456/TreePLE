#!/usr/bin/env bash

exitStatus () {
    if [ $1 -ne 0 ]; then
        exit $1
    fi
}

systemctl stop httpd
exitStatus $?

npm --prefix $1/TreePLE-Web install --unsafe-perm
exitStatus $?

npm run --prefix $1/TreePLE-Web build
exitStatus $?

cp -f $1/TreePLE-Web/dist/bundle.js /var/www/html/
exitStatus $?

cp -f $1/TreePLE-Web/dist/bundle.js.map /var/www/html/
exitStatus $?

cp -f $1/TreePLE-Web/index.html /var/www/html/
exitStatus $?

systemctl start httpd
exitStatus $?

