import glob
import os
import sys


try:
    try:
        xEdit = sys.argv[1]
    except IndexError:
        xEdit = "C:\\Skyrim Tools\\SSEEdit"

    for f in glob.iglob("REQ_*"):
        src = f
        dst = os.path.join(xEdit, "Edit Scripts", f)
        if not os.path.exists(dst) or not os.path.samefile(src, dst):
            os.link(src, dst)
except Exception as e:
    input(e)
