#!/bin/bash

# DeepLinkDispatch Maven Repository Publisher
# This script builds the project and creates a local Maven repository structure

set -e

# Configuration
GROUP_ID="com.airbnb"
VERSION="6.3.0-SNAPSHOT"
REPO_DIR="maven-repo"

echo "=== DeepLinkDispatch Maven Repository Publisher ==="
echo "Version: $VERSION"
echo "Repository: $REPO_DIR"
echo

# Clean and build
echo "1. Cleaning project..."
./gradlew clean

echo "2. Building project (excluding samples)..."
./gradlew assemble -x :sample:assemble -x :sample-benchmark:assemble

# Create repository structure
echo "3. Creating Maven repository structure..."
rm -rf $REPO_DIR
mkdir -p $REPO_DIR

# Function to publish an artifact
publish_artifact() {
    local artifact_id=$1
    local source_file=$2
    local packaging=$3
    local artifact_dir="$REPO_DIR/$GROUP_ID/$artifact_id/$VERSION"
    
    echo "Publishing $artifact_id..."
    mkdir -p "$artifact_dir"
    
    # Copy artifact
    local target_file="$artifact_dir/${artifact_id}-${VERSION}.${packaging}"
    cp "$source_file" "$target_file"
    
    # Generate POM
    local pom_file="$artifact_dir/${artifact_id}-${VERSION}.pom"
    generate_pom "$artifact_id" "$packaging" > "$pom_file"
    
    # Generate checksums
    for file in "$target_file" "$pom_file"; do
        md5sum "$file" | cut -d' ' -f1 > "$file.md5"
        sha1sum "$file" | cut -d' ' -f1 > "$file.sha1"
    done
}

# Function to generate POM
generate_pom() {
    local artifact_id=$1
    local packaging=$2
    
    cat << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>$GROUP_ID</groupId>
  <artifactId>$artifact_id</artifactId>
  <version>$VERSION</version>
  <packaging>$packaging</packaging>
  <name>DeepLinkDispatch - $artifact_id</name>
  <description>Library designed to handle deep linking in an Android application.</description>
  <url>https://github.com/airbnb/deeplinkdispatch</url>
  <licenses>
    <license>
      <name>The Apache Software License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <developers>
    <developer>
      <id>airbnb</id>
      <name>Airbnb</name>
      <email>android@airbnb.com</email>
    </developer>
  </developers>
  <scm>
    <connection>scm:https://github.com/airbnb/DeepLinkDispatch.git</connection>
    <developerConnection>scm:git@github.com:airbnb/DeepLinkDispatch.git</developerConnection>
    <url>https://github.com/airbnb/deeplinkdispatch</url>
  </scm>
EOF

    # Add dependencies based on artifact
    case $artifact_id in
        "deeplinkdispatch-base")
            cat << EOF
  <dependencies>
    <dependency>
      <groupId>androidx.room</groupId>
      <artifactId>room-compiler-processing</artifactId>
      <version>2.5.0</version>
    </dependency>
    <dependency>
      <groupId>org.jetbrains.kotlin</groupId>
      <artifactId>kotlin-stdlib</artifactId>
      <version>2.0.21</version>
    </dependency>
  </dependencies>
EOF
            ;;
        "deeplinkdispatch-processor")
            cat << EOF
  <dependencies>
    <dependency>
      <groupId>$GROUP_ID</groupId>
      <artifactId>deeplinkdispatch-base</artifactId>
      <version>$VERSION</version>
    </dependency>
    <dependency>
      <groupId>com.google.devtools.ksp</groupId>
      <artifactId>symbol-processing-api</artifactId>
      <version>2.0.21-1.0.27</version>
    </dependency>
    <dependency>
      <groupId>androidx.room</groupId>
      <artifactId>room-compiler-processing</artifactId>
      <version>2.5.0</version>
    </dependency>
    <dependency>
      <groupId>com.squareup</groupId>
      <artifactId>kotlinpoet</artifactId>
      <version>1.11.0</version>
    </dependency>
    <dependency>
      <groupId>com.squareup</groupId>
      <artifactId>javapoet</artifactId>
      <version>1.13.0</version>
    </dependency>
    <dependency>
      <groupId>org.jetbrains.kotlin</groupId>
      <artifactId>kotlin-stdlib</artifactId>
      <version>2.0.21</version>
    </dependency>
  </dependencies>
EOF
            ;;
        "deeplinkdispatch")
            cat << EOF
  <dependencies>
    <dependency>
      <groupId>$GROUP_ID</groupId>
      <artifactId>deeplinkdispatch-base</artifactId>
      <version>$VERSION</version>
    </dependency>
    <dependency>
      <groupId>androidx.core</groupId>
      <artifactId>core</artifactId>
      <version>1.8.0</version>
    </dependency>
    <dependency>
      <groupId>androidx.appcompat</groupId>
      <artifactId>appcompat</artifactId>
      <version>1.4.2</version>
    </dependency>
    <dependency>
      <groupId>org.jetbrains.kotlin</groupId>
      <artifactId>kotlin-stdlib</artifactId>
      <version>2.0.21</version>
    </dependency>
  </dependencies>
EOF
            ;;
    esac
    
    echo "</project>"
}

# Publish artifacts
echo "4. Publishing artifacts..."
publish_artifact "deeplinkdispatch-base" "deeplinkdispatch-base/build/libs/deeplinkdispatch-base-${VERSION}.jar" "jar"
publish_artifact "deeplinkdispatch-processor" "deeplinkdispatch-processor/build/libs/deeplinkdispatch-processor-${VERSION}.jar" "jar"
publish_artifact "deeplinkdispatch" "deeplinkdispatch/build/outputs/aar/deeplinkdispatch-release.aar" "aar"

# Generate repository metadata
echo "5. Generating repository metadata..."
for module in deeplinkdispatch-base deeplinkdispatch-processor deeplinkdispatch; do
    metadata_dir="$REPO_DIR/$GROUP_ID/$module"
    metadata_file="$metadata_dir/maven-metadata.xml"
    
    cat > "$metadata_file" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<metadata>
  <groupId>$GROUP_ID</groupId>
  <artifactId>$module</artifactId>
  <versioning>
    <latest>$VERSION</latest>
    <release>$VERSION</release>
    <versions>
      <version>$VERSION</version>
    </versions>
    <lastUpdated>$(date +%Y%m%d%H%M%S)</lastUpdated>
  </versioning>
</metadata>
EOF
    
    # Generate checksums for metadata
    md5sum "$metadata_file" | cut -d' ' -f1 > "$metadata_file.md5"
    sha1sum "$metadata_file" | cut -d' ' -f1 > "$metadata_file.sha1"
done

echo
echo "=== Publishing Complete ==="
echo "Repository created at: $REPO_DIR"
echo
echo "To use this repository in your project, add the following to your build.gradle:"
echo
echo "repositories {"
echo "    maven { url 'file://$(pwd)/$REPO_DIR' }"
echo "}"
echo
echo "Then add dependencies:"
echo "implementation '$GROUP_ID:deeplinkdispatch:$VERSION'"
echo "ksp '$GROUP_ID:deeplinkdispatch-processor:$VERSION'"
echo
echo "To host on GitHub Pages:"
echo "1. Copy the $REPO_DIR directory to your GitHub Pages repository"
echo "2. Commit and push the changes"
echo "3. Use the GitHub Pages URL as your Maven repository URL"