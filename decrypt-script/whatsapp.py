#!/usr/bin/env python
 
import sys
from Crypto.Cipher import AES
 
try:
    wafile=sys.argv[1]
except:
    print "Usage: %s <msgstore.db.crypt>" % __file__
    sys.exit(1)
 
key = "346a23652a46392b4d73257c67317e352e3372482177652c".decode('hex')
cipher = AES.new(key,1)
open('msgstore.db',"wb").write(cipher.decrypt(open(wafile,"rb").read()))
