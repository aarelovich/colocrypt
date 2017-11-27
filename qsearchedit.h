#ifndef QSEARCHEDIT_H
#define QSEARCHEDIT_H

#include <QLineEdit>
#include <QKeyEvent>
#include <QDebug>

class QSearchEdit : public QLineEdit
{
    Q_OBJECT
public:
    QSearchEdit(QWidget *parent = 0);

signals:
    void sendFilter(QString filter);

protected:
    void keyPressEvent(QKeyEvent *e);

};

#endif // QSEARCHEDIT_H
