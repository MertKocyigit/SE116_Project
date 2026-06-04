@echo off
if exist out rmdir /s /q out
mkdir out
if exist sources.txt del sources.txt
for /R "src" %%f in (*.java) do echo "%%f" >> sources.txt
javac -d out @sources.txt
if errorlevel 1 (
    echo Build failed.
    exit /b 1
)
jar cfm ObjectVilleGame.jar manifest.mf -C out .
if errorlevel 1 (
    echo Jar creation failed.
    exit /b 1
)
del sources.txt
echo Build successful: ObjectVilleGame.jar
