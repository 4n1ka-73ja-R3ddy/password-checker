#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper for Unix / Linux / macOS
# ----------------------------------------------------------------------------

set -e

if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
fi

BASE_DIR=$(dirname "$0")
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
    exec java -jar "$WRAPPER_JAR" "$@"
else
    echo "[ERROR] Maven command 'mvn' not found in PATH and wrapper jar not found." >&2
    echo "Please install Maven or ensure maven-wrapper.jar is present in .mvn/wrapper/" >&2
    exit 1
fi

