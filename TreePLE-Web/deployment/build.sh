#!/usr/bin/env bash

exitStatus () {
    if [ $1 -ne 0 ]; then
        exit $1
    fi
}

echo "npm --prefix $1/TreePLE-Web install --unsafe-perm"
npm --prefix $1/TreePLE-Web install --unsafe-perm
exitStatus $?

echo "npm run --prefix $1/TreePLE-Web build"
npm run --prefix $1/TreePLE-Web build
exitStatus $?
