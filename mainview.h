#ifndef MAINVIEW_H
#define MAINVIEW_H

#include <QMainWindow>
#include <QDebug>

#include "aescryptif.h"
#include "passdata.h"

namespace Ui {
class MainView;
}

class MainView : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainView(QWidget *parent = 0);
    ~MainView();

private slots:
    void on_pbLogin_clicked();

    void on_pbLogOut_clicked();

    void on_pbCreateEntry_clicked();

    void on_pbCancelEntry_clicked();

private:
    Ui::MainView *ui;

    PassData passData;
};

#endif // MAINVIEW_H
