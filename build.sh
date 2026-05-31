#!/usr/bin/env bash
set -e
rm -rf out
mkdir -p out
javac -d out $(find src -name "*.java")
jar cfm ObjectVilleGame.jar manifest.mf -C out .
echo "Build successful: ObjectVilleGame.jar"
