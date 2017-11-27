#include "qsearchedit.h"

QSearchEdit::QSearchEdit(QWidget *parent):QLineEdit(parent){

}

void QSearchEdit::keyPressEvent(QKeyEvent *e){
    QLineEdit::keyPressEvent(e);
    if (this->text().size() > 3){
        emit(sendFilter(this->text()));
    }
}
