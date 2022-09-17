import glob
import os
import sys


if len(sys.argv) != 2:
    raise ValueError('Path to xEdit installation is missing')

for file in glob.iglob("REQ_*"):
    os.link(file, os.path.join(sys.argv[1], "Edit Scripts", file))
