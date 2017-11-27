#include "passwordgenerator.h"

PassWordGenerator::PassWordGenerator()
{
    uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    letters = "abcdefghijklmnopqrstuvwxyz";
    numbers = "0123456789";
    symbols = "#!&%?-";
}

QString PassWordGenerator::generatePassword(int size, bool useSymbols){

    int what;
    QString password = "";
    bool hasSymbol = false;
    bool hasUppercse = false;
    bool hasNumber = false;
    bool hasLetter = false;

    while (true){

        password = "";

        for (int  i = 0; i < size; i++){

            what = qrand() % 4;

            if (!useSymbols) what = what+1;

            switch (what){
            case 0:
                password = password + symbols.at(qrand() % symbols.size());
                hasSymbol = true;
                break;
            case 1:
                password = password + uppercase.at(qrand() % uppercase.size());
                hasUppercse = true;
                break;
            case 2:
                password = password + numbers.at(qrand() % numbers.size());
                hasNumber = true;
                break;
            default:
                hasLetter = true;
                password = password + letters.at(qrand() % letters.size());
                break;
            }

        }

        if (hasNumber && hasUppercse && hasLetter){
            if (useSymbols && hasSymbol){
                break;
            }
            else{
                break;
            }
        }
    }

    return password;

}
