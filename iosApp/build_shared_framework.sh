#!/bin/bash

set -e

cd "$SRCROOT/.."
./gradlew :shared:assembleSharedDebugXCFramework

