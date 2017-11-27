#ifndef PASSWORDGENERATOR_H
#define PASSWORDGENERATOR_H

#include <QList>
#include <QString>

class PassWordGenerator
{
public:
    PassWordGenerator();

    QString generatePassword(int size, bool useSymbols);

private:

    QString letters;
    QString uppercase;
    QString numbers;
    QString symbols;

};

#endif // PASSWORDGENERATOR_H
