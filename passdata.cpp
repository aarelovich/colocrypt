#include "passdata.h"

PassData::PassData()
{

}

void PassData::parseDataFile(const QString &data, QListWidget *lv){

    QStringList entries = data.split("<>",QString::SkipEmptyParts);
    for (qint32 i = 0; i < entries.size(); i++){
        QString entry = entries.at(i);
        QStringList parts = entry.split("|",QString::SkipEmptyParts);
        if (parts.size() != 3) {
            qDebug() << "QCOLOCRYPT: Entry with wrong format: " + entry;
            continue;
        }
        addEntry(parts.at(0),parts.at(1),parts.at(2));
    }


    // Add entries to the list.
    QStringList keys = passData.keys();
    for (int i = 0; i < keys.size(); i++){
        QListWidgetItem *item = new QListWidgetItem(keys.at(i));
        lv->addItem(item);
    }

}

bool PassData::addEntry(const QString &name, const QString &user, const QString &password, const bool &allowRepeat){

    UserPass up;
    up.password = password;
    up.user = user;

    QString entryname = name;

    if (allowRepeat){
        int counter = 0;
        QString original_name = name;
        while (passData.contains(name)){
            entryname = original_name + QString::number(counter);
            counter++;
        }
    }
    else{
        if (passData.contains(name)) return false;
    }

    passData[entryname] = up;
    return true;
}
