mkdir "components/plugins/temp"
copy /y %1 "components/plugins/temp/"%1
Spriggit.CLI sort-randomized-fields --InputPath components/plugins/temp/%1 --OutputPath components/plugins/temp/%1 --GameRelease SkyrimSE
Spriggit.CLI convert-from-plugin --InputPath components/plugins/temp/%1 --OutputPath components/plugins/%1
rmdir /S /Q "components/plugins/temp"
