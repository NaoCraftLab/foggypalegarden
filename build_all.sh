#!/bin/bash
set -e

MOD_ID=$(grep "^modId=" gradle.properties | cut -d'=' -f2)
MOD_VERSION=$(grep "^modVersion=" gradle.properties | cut -d'=' -f2)
TARGET_DIR=distrs

if [ -d "${TARGET_DIR}" ]; then
    echo "Deleting folder '${TARGET_DIR}'"
    rm -rf "${TARGET_DIR}"
fi

echo "Creating folder '${TARGET_DIR}'"
mkdir -p "${TARGET_DIR}"

echo "Building ${MOD_ID} ${MOD_VERSION} for all available Minecraft versions"

for file in mcVersions/*.properties; do
    BUILD_PROPS_VERSION=$(basename "$file" .properties)
    MIN_MC_VERSION=$(grep "^minecraftVersion=" "$file" | cut -d'=' -f2)
    echo "Building by properties $BUILD_PROPS_VERSION for Minecraft $MIN_MC_VERSION"

    export mcVersion="$BUILD_PROPS_VERSION"

    ./gradlew clean build
    
    for platform in fabric forge neoforge; do
        JAR_PATTERN="${MOD_ID}-${MOD_VERSION}+${MIN_MC_VERSION}-${platform}.jar"
        JAR_FILE=$(find "$platform/build/libs" -maxdepth 1 -type f -name "${JAR_PATTERN}" | head -n 1)
        if [ -f "$JAR_FILE" ]; then
            mv "$JAR_FILE" "${TARGET_DIR}/"
            echo "Moved file for platform '${platform}': $JAR_FILE"
        else
            echo "File '${JAR_PATTERN}' not found in directory '${platform}/build/libs'"
        fi
    done
done