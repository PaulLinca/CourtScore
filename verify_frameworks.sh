#!/bin/bash

echo "Checking Debug Framework:"
echo "========================="
ls -la shared/build/XCFrameworks/debug/shared.xcframework/
echo ""
echo "Debug Simulator Framework:"
lipo -info shared/build/XCFrameworks/debug/shared.xcframework/watchos-arm64_x86_64-simulator/shared.framework/shared 2>&1
echo ""
echo "Debug Device Framework:"
lipo -info shared/build/XCFrameworks/debug/shared.xcframework/watchos-arm64_arm64_32/shared.framework/shared 2>&1
echo ""
echo "Checking Release Framework:"
echo "==========================="
ls -la shared/build/XCFrameworks/release/shared.xcframework/
echo ""
echo "Release Simulator Framework:"
lipo -info shared/build/XCFrameworks/release/shared.xcframework/watchos-arm64_x86_64-simulator/shared.framework/shared 2>&1
echo ""
echo "Release Device Framework:"
lipo -info shared/build/XCFrameworks/release/shared.xcframework/watchos-arm64_arm64_32/shared.framework/shared 2>&1

