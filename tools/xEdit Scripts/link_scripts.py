import filecmp
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
        if not os.path.exists(dst):
            os.link(src, dst)
        elif not os.path.samefile(src, dst):
            # If src and dst have the same file content, recreate the link on
            # the assumption that it was previously established and
            # subsequently destroyed by switching branches in git
            if filecmp.cmp(src, dst):
                os.remove(dst)
                os.link(src, dst)
            else:
                answer = input(f'{dst} already exists. '
                               f'Do you want to overwrite this file? (y/n)\n')
                if answer == "y":
                    os.remove(dst)
                    os.link(src, dst)
except Exception as e:
    input(e)
