# 163Music

[![Build Status](https://ci.appveyor.com/api/projects/status/32r7s2skrgm9ubva?svg=true&retina=true)](https://ci.appveyor.com/project/CRonYii/163musicdownloader)
[![Build Status](https://travis-ci.org/CRonYii/163MusicDownloader.svg?branch=master)](https://travis-ci.org/CRonYii/163MusicDownloader)

A web spider music download application build on JavaFX.

# Data Source
* Music Information fetched from http://music.163.com
* Music are downloaded from https://ouo.us/fm/163/

# Development

163Music is developed using various libraries
* [Maven](http://maven.apache.org/) is used to resolve dependencies and to build 163music.
* [JSoup](https://jsoup.org/download) is a Web Crawler Library used to fetched information for website
* [mp3agic](https://github.com/mpatric/mp3agic) is used for mp3 tags edition
* [JFoenix](https://github.com/jfoenixadmin/JFoenix) is a JavaFX Material Design Library

## Building

To build 163Music, you will need:

* [JDK 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html) -Java Development Kit (version 8 or up)
* [maven](http://maven.apache.org/) - Version 3 recommended

### To build .jar executable
```
$ mvn clean jfx:jar
```

Please note that, for JDK 9 or up, You must uses JFoenix v9.0.4

Change the jfoenix dependency in pox.xml to
```xml
<dependency>
    <groupId>com.jfoenix</groupId>
    <artifactId>jfoenix</artifactId>
    <version>9.0.4</version>
</dependency>
```

### To build native .exe
```
$ mvn clean jfx:native
```
Which will build .exe Application based on your OS.

For more Infomation, see [JavaFX Maven Plugin](https://github.com/javafx-maven-plugin/javafx-maven-plugin)
# Release

To uses 163Music without building, please download the jar executable
* [163Music Release](https://github.com/CRonYii/163MusicDownloader/releases)
