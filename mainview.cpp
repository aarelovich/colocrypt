#include "mainview.h"
#include "ui_mainview.h"

MainView::MainView(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainView)
{
    ui->setupUi(this);
    ui->gbAddEntryView->setVisible(false);
    ui->gbListView->setVisible(false);

    // Making it a password field
    ui->leLogin->setEchoMode(QLineEdit::Password);

    ui->leLogin->setText("bellagorda551");
}

MainView::~MainView()
{
    delete ui;
}


// ---------------------------------- VIEW SWITCHING ---------------------------------------------------
void MainView::on_pbLogin_clicked()
{

    bool loggedIn = false;
    QString data = AESCryptIF::login(ui->leLogin->text(),&loggedIn);

    if (loggedIn){
        ui->gbLoginView->setVisible(false);
        ui->gbListView->setVisible(true);

        passData.parseDataFile(data,ui->lwEntries);

    }
    else{
        AESCryptIF::showToast("Could not decrypt. Please verify your password");
    }
}

void MainView::on_pbLogOut_clicked()
{
    ui->gbListView->setVisible(false);
    ui->gbLoginView->setVisible(true);
}

void MainView::on_pbCreateEntry_clicked()
{
    ui->gbListView->setVisible(false);
    ui->gbAddEntryView->setVisible(true);
}

void MainView::on_pbCancelEntry_clicked()
{
    ui->gbAddEntryView->setVisible(false);
    ui->gbListView->setVisible(true);
}
