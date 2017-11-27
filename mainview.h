#ifndef MAINVIEW_H
#define MAINVIEW_H

#include <QMainWindow>
#include <QDebug>
#include <QClipboard>
#include <QMessageBox>

#include "aescryptif.h"
#include "passdata.h"
#include "passwordgenerator.h"

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

    void on_lwEntries_itemClicked(QListWidgetItem *item);

    void on_lwEntries_itemDoubleClicked(QListWidgetItem *item);

    void on_pbCopyUser_clicked();

    void on_pbCopyPass_clicked();

    void on_pbSearch_clicked();

    void on_pbClearInputFields_clicked();

    void on_pbChangePass_clicked();

    void on_pbGoBack_clicked();

    void on_pbAddEntry_clicked();

    void on_pbGeneratePassword_clicked();

    void on_pbVerifyChangePasswd_clicked();

private:
    Ui::MainView *ui;
    PassData passData;
    PassWordGenerator pgen;
    QString newPassword;
};

#endif // MAINVIEW_H
