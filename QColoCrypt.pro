#-------------------------------------------------
#
# Project created by QtCreator 2017-11-23T06:31:24
#
#-------------------------------------------------

QT       += core gui androidextras

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = QColoCrypt
TEMPLATE = app

ANDROID_PACKAGE_SOURCE_DIR = $$PWD/java

SOURCES += main.cpp\
        mainview.cpp \
    aescryptif.cpp \
    passdata.cpp \
    qsearchedit.cpp \
    passwordgenerator.cpp

HEADERS  += mainview.h \
    aescryptif.h \
    passdata.h \
    qsearchedit.h \
    passwordgenerator.h

FORMS    += mainview.ui

MOC_DIR = MOCS
OBJECTS_DIR = OBJS

CONFIG += mobility
MOBILITY = 

OTHER_FILES += \
    java/AndroidManifest.xml \
    java/src/org/qcolocrypt/AESCrypt.java

RESOURCES += \
    icons.qrc

