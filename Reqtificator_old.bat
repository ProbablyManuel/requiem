@echo off
set DIR="%~dp0"
set JAVA_EXEC="%DIR:"=%\SkyProc Patchers\Requiem\app\bin\java"
cd "SkyProc Patchers\Requiem"
%JAVA_EXEC% -Xmx1024m -m skyrim.requiem/skyrim.requiem.MainKt
exit