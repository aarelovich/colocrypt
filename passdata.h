#ifndef PASSDATA_H
#define PASSDATA_H

#include <QHash>
#include <QString>
#include <QListWidget>
#include <QListWidgetItem>
#include <QDebug>

class PassData
{
public:
    PassData();

    // Add all decrypted entries
    void parseDataFile(const QString &data, QListWidget *lv);

    // Add single entry.
    bool addEntry(const QString &name, const QString &user, const QString &password, const bool &allowRepeat = true);

    QStringList listEntries() const {return passData.keys();}

    void clear() {passData.clear();}

private:

    struct UserPass{
        QString user;
        QString password;
    };

    // The data.
    QHash<QString,UserPass> passData;

};

#endif // PASSDATA_H
