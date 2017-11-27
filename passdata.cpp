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
            qDebug() << "COLOCRYPT: Entry with wrong format: " + entry;
            continue;
        }
        //qDebug() << APP << "Adding " << parts.at(0);
        addEntry(parts.at(0),parts.at(1),parts.at(2));
    }


    // Add entries to the list.
    dbentries.clear();
    dbentries = passData.keys();
    dbentries.sort(Qt::CaseInsensitive);
    filterEntries("",lv);

}

void PassData::filterEntries(const QString &search, QListWidget *lv){

    lv->clear();

    //qDebug() << APP << "filterEntries: Dic size" << dbentries.size() << "passData size " << passData.size();

    QFont font(fontName,20);

    for (int i = 0; i < dbentries.size(); i++){
        QString entry = dbentries.at(i);
        if (entry.contains(search,Qt::CaseInsensitive) || search.isEmpty()){
            //qDebug() << APP << "filterEntries: Adding entry " << entry;
            QListWidgetItem *item = new QListWidgetItem(dbentries.at(i));
            item->setFont(font);
            item->setTextAlignment(Qt::AlignCenter);
            item->setTextColor(QColor::fromRgb(0, 170, 127));
            lv->addItem(item);
        }
    }

}

PassData::UserPass PassData::getUserPass(const QString &entry) const{

    UserPass up;
    up.password = "";
    up.user = "";

    if (passData.contains(entry)){
        return passData.value(entry);
    }
    else return up;

}

bool PassData::validString(const QString &s){

    if (s.contains('<') || s.contains('>') || s.contains('|')) return false;
    else return true;

}

void PassData::addEntry(const QString &name, const QString &user, const QString &password, QListWidget *lv, const bool &allowRepeat){

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

    // If allow repeat is false then there is a forced overwrite of the entry.
    passData[entryname] = up;

    // Not allow repeat means that just onen entry has been added.
    if (lv != nullptr){
        dbentries.clear();
        dbentries = passData.keys();
        dbentries.sort(Qt::CaseInsensitive);
        filterEntries("",lv);
    }

}

void PassData::clear(){
    passData.clear();
    dbentries.clear();
}

QString PassData::getData() const{

    QStringList data;
    for (int i = 0; i < dbentries.size(); i++){
        UserPass up = passData.value(dbentries.at(i));
        QString datum = dbentries.at(i) + "|" + up.user + "|" + up.password;
        data << datum;
    }
    return data.join("<>");

}
