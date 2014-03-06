#!/usr/bin/python
# -*- coding: utf-8 -*-

import commands
import os,sys
from subprocess import check_call

masu = "/home/s-kimura/Dropbox/devel/IntelliJ/MASU/newMasu/out/artifacts/newMasu_jar/newMasu.jar"

def GetFileNum(unfRepoDir, output, dataFld):
	with open(os.devnull, "w") as nn, open(output, "a") as f:

		list = os.listdir(unfRepoDir)
		for file in list:
			cmdArray = ["java", "-jar", 
				masu, 
				"-d", os.path.abspath(unfRepoDir + "/" + file), 
				"-o", dataFld,
				"-c"]
			print cmdArray
			comId = file.split(' ')[0]
			#print comId
			check_call( cmdArray, stdout=nn, stderr=nn )

			if os.path.exists(dataFld + "/inOut.csv"):
				for line in open(dataFld + "/inOut.csv", 'r'):
					line = line.rstrip()

					print >> f, comId+" "+line

			print
			print


if __name__=="__main__":
    dataFld = "/dev/shm"
    if len(sys.argv) == 4:
        dataFld = sys.argv[3]
    GetFileNum(sys.argv[1], sys.argv[2], dataFld)
