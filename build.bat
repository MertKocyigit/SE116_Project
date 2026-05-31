@echo off
if exist out rmdir /s /q out
mkdir out
for /R src %%f in (*.java) do echo %%f >> sources.txt
javac -d out @sources.txt
jar cfm ObjectVilleGame.jar manifest.mf -C out .
del sources.txt
echo Build successful: ObjectVilleGame.jar
