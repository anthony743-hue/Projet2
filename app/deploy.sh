#!/bin/bash

source .env

APP_NAME="test"
SRC_DIR="src/main/java"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR_IN="src/main/webapp/WEB-INF/lib"

if [ -z "$TOMCAT_WEBAPPS" ]; then 
    echo "ERREUR: TOMCAT_WEBAPPS non définie dans .env"
    exit 1
fi

# --- 2. NETTOYAGE ET PRÉPARATION ---
echo "Nettoyage du répertoire de construction..."
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/WEB-INF/classes
mkdir -p $BUILD_DIR/WEB-INF/lib

# --- 3. COMPILATION ---
echo "Compilation des sources Java..."
# Classpath uniquement avec servlet-api et les librairies de l'application
find $SRC_DIR -name "*.java" > sources.txt
javac -cp "$LIB_DIR_IN/framework.jar" -d $BUILD_DIR/WEB-INF/classes --add-opens java.base/java.time=ALL-UNNAMED @sources.txt

if [ $? -ne 0 ]; then
    echo "ERREUR: La compilation a échoué."
    exit 1
fi

# --- 4. ASSEMBLAGE DE L'APPLICATION WEB ---
echo "Assemblage du WAR..."

# Copier les fichiers web (JSP, HTML, CSS, etc.)
cp $LIB_DIR_IN/*.jar $BUILD_DIR/WEB-INF/lib/
cp -r $WEB_DIR/* $BUILD_DIR/

# Copier les librairies de l'application dans WEB-INF/lib (hors driver JDBC)

echo "Copie des $(ls $BUILD_DIR/WEB-INF/lib | wc -l) JARs dans WEB-INF/lib."

# --- 5. CRÉATION DU FICHIER .WAR ---
cd $BUILD_DIR || exit
jar -cvf $APP_NAME.war .
cd ..

# --- 6. DÉPLOIEMENT ---
echo "Déploiement de $APP_NAME.war dans Tomcat..."
cp -f $BUILD_DIR/$APP_NAME.war $TOMCAT_WEBAPPS/

echo "--------------------------------------------------------"
echo "Déploiement terminé. Contexte d'application: /$APP_NAME"
echo "--------------------------------------------------------"
