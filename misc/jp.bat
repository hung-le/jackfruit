RMDIR /S /Q in
RMDIR /S /Q out

mkdir in
mkdir out

copy ..\target\jackfruit-0.0.1-SNAPSHOT.jar in

jpackage --dest ./out --verbose -i ./in --name jackfruit --main-jar jackfruit-0.0.1-SNAPSHOT.jar --add-modules java.base,java.logging,java.xml,java.sql,java.prefs,java.desktop,java.management,java.naming --app-version 0.1 --vendor "Hung Le" --win-shortcut --type msi