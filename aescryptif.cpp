#include "aescryptif.h"

AESCryptIF::AESCryptIF()
{

}


QString AESCryptIF::login(const QString &password_string, bool *loggedIn){

    *loggedIn = false;

    QAndroidJniObject mediaDir = QAndroidJniObject::callStaticObjectMethod("android/os/Environment", "getExternalStorageDirectory", "()Ljava/io/File;");
    QAndroidJniObject mediaPath = mediaDir.callObjectMethod( "getAbsolutePath", "()Ljava/lang/String;" );
    QString path = mediaPath.toString() + "/ccrypt/data.aux";
    QAndroidJniObject datafile = QAndroidJniObject::fromString(path);
    QAndroidJniObject password = QAndroidJniObject::fromString(password_string);

    // Calling the Init and decrypt fucntions.
    QAndroidJniObject ans =
            QAndroidJniObject::callStaticObjectMethod("org/qcolocrypt/AESCrypt",
                                                      "AESCryptInit",
                                                      "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                                                      password.object<jstring>(),
                                                      datafile.object<jstring>());


    QAndroidJniObject decrypted_data = QAndroidJniObject::callStaticObjectMethod("org/qcolocrypt/AESCrypt",
                                                                                 "decrypt",
                                                                                 "()Ljava/lang/String;");


    QAndroidJniObject status = QAndroidJniObject::callStaticObjectMethod("org/qcolocrypt/AESCrypt",
                                                                         "getStatus",
                                                                         "()Ljava/lang/String;");

    *loggedIn = status.toString().isEmpty();
    return decrypted_data.toString();

}


void AESCryptIF::showToast(const QString &message, Duration duration) {
    // all the magic must happen on Android UI thread
    QtAndroid::runOnAndroidThread([message, duration] {
        QAndroidJniObject javaString = QAndroidJniObject::fromString(message);
        QAndroidJniObject toast = QAndroidJniObject::callStaticObjectMethod("android/widget/Toast", "makeText",
                                                                            "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;",
                                                                            QtAndroid::androidActivity().object(),
                                                                            javaString.object(),
                                                                            jint(duration));
        toast.callMethod<void>("show");
    });
}
