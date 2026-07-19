#!/bin/bash

BUILD_DIR="out"
LIB_DIR="lib"


rm -rf $BUILD_DIR
mkdir $BUILD_DIR
echo "Compilation des sources Java..."
# Classpath uniquement avec servlet-api et les librairies de l'application
find -name "*.java" > sources.txt
javac -cp "./$LIB_DIR/servlet-api.jar" -d $BUILD_DIR @sources.txt

if [ $? -ne 0 ]; then
    echo "ERREUR: La compilation a échoué."
    exit 1
fi

jar cvf /home/itu/L2/app/src/main/webapp/WEB-INF/lib/framework.jar -C $BUILD_DIR .
