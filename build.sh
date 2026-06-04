#!/usr/bin/env bash
set -e

rm -rf out
mkdir -p out

find src -name "*.java" -print0 | xargs -0 javac -d out

jar cfm ObjectVilleGame.jar manifest.mf -C out .

echo "Build successful: ObjectVilleGame.jar"
