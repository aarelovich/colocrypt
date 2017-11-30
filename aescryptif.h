#ifndef AESCRYPTIF_H
#define AESCRYPTIF_H

#include <QString>
#include <QtAndroidExtras/QAndroidJniObject>
#include <QtAndroid>
#include <QFile>
#include <QTextStream>


class AESCryptIF
{
public:
    AESCryptIF();

    // Login.
    static QString login(const QString &password_string, bool *loggedIn, bool *firstTime);

    // Show a toast with a message
    enum Duration {
        SHORT = 0,
        LONG = 1
    };

    static void showToast(const QString &message, Duration duration = LONG);

    static QString encryptData(const QString &data, const QString &password = "");

};

#endif // AESCRYPTIF_H
