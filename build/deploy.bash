#!/bin/bash

#Remove old releases
rm -R TarsosLSH-*

#Build the new release
ant release

#Find the current version
filename=$(basename TarsosLSH-*-bin.jar)
version=${filename:10:3}

deploy_dir="/var/www/be.0110/current/public/releases/TarsosLSH"
deploy_location=$deploy_dir"/TarsosLSH-$version"

#Build the readme file
textile2html ../README.textile TarsosLSH-$version-Readme.html
echo "<br><span>Version info: TarsosLSH-$version</span>" >> TarsosLSH-$version-Readme.html

#Remove old version from the server:
ssh joren@0110.be rm -R $deploy_location

ssh joren@0110.be mkdir -p $deploy_location
#Deploy to the server 
scp -r TarsosLSH-* joren@0110.be:$deploy_location

ssh joren@0110.be rm -R $deploy_dir/TarsosLSH-latest
ssh joren@0110.be mkdir $deploy_dir/TarsosLSH-latest
ssh joren@0110.be ln -s -f $deploy_location/TarsosLSH-$version.jar $deploy_dir/TarsosLSH-latest/TarsosLSH-latest.jar
ssh joren@0110.be ln -s -f $deploy_location/TarsosLSH-$version-bin.jar $deploy_dir/TarsosLSH-latest/TarsosLSH-latest-bin.jar
ssh joren@0110.be ln -s -f $deploy_location/TarsosLSH-$version-Documentation $deploy_dir/TarsosLSH-latest/TarsosLSH-latest-Documentation
ssh joren@0110.be ln -s -f $deploy_location/TarsosLSH-$version-Readme.html $deploy_dir/TarsosLSH-latest/TarsosLSH-latest-Readme.html

