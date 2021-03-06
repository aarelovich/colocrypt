#include "aescryptif.h"

AESCryptIF::AESCryptIF()
{

}


QString AESCryptIF::login(const QString &password_string, bool *loggedIn, bool *firstTime){

    *loggedIn = false;

    QAndroidJniObject mediaDir = QAndroidJniObject::callStaticObjectMethod("android/os/Environment", "getExternalStorageDirectory", "()Ljava/io/File;");
    QAndroidJniObject mediaPath = mediaDir.callObjectMethod( "getAbsolutePath", "()Ljava/lang/String;" );
    QString path = mediaPath.toString() + "/ccrypt/data.aux";
    QAndroidJniObject datafile = QAndroidJniObject::fromString(path);
    QAndroidJniObject passobj = QAndroidJniObject::fromString(password_string);

    // Making sure the file exists.
    QFile dataFile(path);
    if (!dataFile.exists()){
        *firstTime = true;
        return "";
    }
    *firstTime = false;


    // Calling the Init and decrypt fucntions.
    QAndroidJniObject ans =
            QAndroidJniObject::callStaticObjectMethod("org/qcolocrypt/AESCrypt",
                                                      "AESCryptInit",
                                                      "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                                                      passobj.object<jstring>(),
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

QString AESCryptIF::encryptData(const QString &data, const QString &password) {

    QAndroidJniObject thedata = QAndroidJniObject::fromString(data);

    if (!password.isEmpty()){

        // Initializing the AES Engine again.
        QAndroidJniObject mediaDir = QAndroidJniObject::callStaticObjectMethod("android/os/Environment", "getExternalStorageDirectory", "()Ljava/io/File;");
        QAndroidJniObject mediaPath = mediaDir.callObjectMethod( "getAbsolutePath", "()Ljava/lang/String;" );
        QString path = mediaPath.toString() + "/ccrypt/data.aux";
        QAndroidJniObject datafile = QAndroidJniObject::fromString(path);
        QAndroidJniObject passobj = QAndroidJniObject::fromString(password);


        // Calling the Init and encrypt functions.
        QAndroidJniObject ans =
                QAndroidJniObject::callStaticObjectMethod("org/qcolocrypt/AESCrypt",
                                                          "AESCryptInit",
                                                          "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                                                          passobj.object<jstring>(),
                                                          datafile.object<jstring>());
    }

    QAndroidJniObject ans = QAndroidJniObject::callStaticObjectMethod("org/qcolocrypt/AESCrypt",
                                                    "encrypt",
                                                    "(Ljava/lang/String;)Ljava/lang/String;",
                                                     thedata.object<jstring>());

    return ans.toString();

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
