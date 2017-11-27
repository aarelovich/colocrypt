#ifndef PASSDATA_H
#define PASSDATA_H

#include <QHash>
#include <QString>
#include <QListWidget>
#include <QListWidgetItem>
#include <QDebug>
#include <QStringList>

#define APP "COLOCRYPT:"

class PassData
{
public:
    PassData();

    struct UserPass{
        QString user;
        QString password;
    };

    // Add all decrypted entries
    void parseDataFile(const QString &data, QListWidget *lv);

    // Filter entries
    void filterEntries(const QString &search, QListWidget *lv);

    // Add single entry.
    void addEntry(const QString &name, const QString &user, const QString &password, QListWidget *lv = nullptr, const bool &allowRepeat = true);

    bool entryExists(const QString &name) {return passData.contains(name);}

    QStringList listEntries() const {return passData.keys();}

    UserPass getUserPass(const QString &entry) const;

    static bool validString(const QString &s);

    QString getData() const;

    void clear();

    void setFontName(const QString &fn){ fontName = fn; }

    void deleteEntry(const QString &entry) { passData.remove(entry); dbentries = passData.keys(); dbentries.sort(); }

private:

    // The data.
    QHash<QString,UserPass> passData;

    // The sorted keys.
    QStringList dbentries;

    QString fontName;

};

#endif // PASSDATA_H
