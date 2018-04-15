#!/usr/bin/env bash

#Install the cert utils
sudo apt install libnss3-tools
# Trust the certificate for SSL
pk12util -d sql:$HOME/.pki/nssdb -i treeple.pfx
# Trust a self-signed server certificate
certutil -d sql:$HOME/.pki/nssdb -A -t "P,," -n 'dev cert' -i treeple.crt
