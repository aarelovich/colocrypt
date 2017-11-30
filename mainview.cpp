#include "mainview.h"
#include "ui_mainview.h"

MainView::MainView(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainView)
{
    ui->setupUi(this);
    ui->gbAddEntryView->setVisible(false);
    ui->gbListView->setVisible(false);
    ui->gbChangePassView->setVisible(false);

    // Making it a password field
    ui->leLogin->setEchoMode(QLineEdit::Password);
    ui->leChangePass->setEchoMode(QLineEdit::Password);

    // Search when the input box changes.
    connect(ui->leSearchBox,&QSearchEdit::sendFilter,this,&MainView::on_pbSearch_clicked);

    // Loading font
    int id = QFontDatabase::addApplicationFont(":/fonts/sansation.ttf");
    theFont = QFontDatabase::applicationFontFamilies(id).at(0);
    passData.setFontName(theFont);

    setButtonStyleSheet(ui->pbLogin);
    setButtonStyleSheet(ui->pbAddEntry);
    setButtonStyleSheet(ui->pbCancelEntry);
    setButtonStyleSheet(ui->pbChangePass);
    setButtonStyleSheet(ui->pbClearInputFields);
    setButtonStyleSheet(ui->pbCopyPass);
    setButtonStyleSheet(ui->pbCopyUser);
    setButtonStyleSheet(ui->pbCreateEntry);
    setButtonStyleSheet(ui->pbGeneratePassword);
    setButtonStyleSheet(ui->pbGoBack);
    setButtonStyleSheet(ui->pbLogOut);
    setButtonStyleSheet(ui->pbSearch);
    setButtonStyleSheet(ui->pbVerifyChangePasswd);
    setButtonStyleSheet(ui->pbDeleteEntry);

    setEditFieldStyle(ui->leChangePass);
    setEditFieldStyle(ui->leInputEntry);
    setEditFieldStyle(ui->leInputPasswd);
    setEditFieldStyle(ui->leInputUser);
    setEditFieldStyle(ui->leLogin);
    setEditFieldStyle(ui->lePasswd);
    setEditFieldStyle(ui->leSearchBox);
    setEditFieldStyle(ui->leUser);

    setLabelStyle(ui->label);
    setLabelStyle(ui->label_2);
    setLabelStyle(ui->label_3);
    setLabelStyle(ui->label_4);

    ui->comboBox->setStyleSheet("font: 20pt \"Monospace\";\ncolor: rgb(0, 170, 127);");
    ui->cboxUseSymbols->setStyleSheet("font: 20pt \"Monospace\";\ncolor: rgb(255, 255, 255);\nborder-color: 3px solid rgb(0, 170, 255);");

    this->setStyleSheet("background-color: rgb(0, 0, 0);");

    ui->lbTitle->setFont(QFont(theFont,18,QFont::Bold,false));
}

MainView::~MainView()
{
    delete ui;
}


// ---------------------------------- VIEW SWITCHING ---------------------------------------------------
void MainView::on_pbLogin_clicked()
{

    bool loggedIn = false;
    bool firstTime = false;
    QString data = AESCryptIF::login(ui->leLogin->text(),&loggedIn,&firstTime);

    if (firstTime){
        ui->gbLoginView->setVisible(false);
        ui->pbGoBack->setEnabled(false);
        on_pbChangePass_clicked();
        return;
    }

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
    passData.clear();
    ui->lwEntries->clear();
    ui->lePasswd->clear();
    ui->leSearchBox->clear();
    ui->leInputUser->clear();
    on_pbClearInputFields_clicked();
    ui->leLogin->clear();
    ui->gbListView->setVisible(false);
    ui->gbLoginView->setVisible(true);
    ui->gbChangePassView->setVisible(false);
}

void MainView::on_pbCreateEntry_clicked()
{
    ui->gbListView->setVisible(false);
    ui->gbAddEntryView->setVisible(true);
}

void MainView::on_pbCancelEntry_clicked()
{
    on_pbClearInputFields_clicked();
    ui->gbAddEntryView->setVisible(false);
    ui->gbListView->setVisible(true);
}

void MainView::on_lwEntries_itemClicked(QListWidgetItem *item)
{
    PassData::UserPass up = passData.getUserPass(item->text());
    ui->leUser->setText(up.user);
    ui->lePasswd->setText(up.password);
}

void MainView::on_lwEntries_itemDoubleClicked(QListWidgetItem *item)
{
    PassData::UserPass up = passData.getUserPass(item->text());

    // Switching the view
    on_pbCreateEntry_clicked();

    // Loading the data
    ui->leInputEntry->setText(item->text());
    ui->leInputPasswd->setText(up.password);
    ui->leInputUser->setText(up.user);
}

void MainView::on_pbCopyUser_clicked()
{
    QClipboard *clipboard = QApplication::clipboard();
    clipboard->setText(ui->leUser->text());
}

void MainView::on_pbCopyPass_clicked()
{
    QClipboard *clipboard = QApplication::clipboard();
    clipboard->setText(ui->lePasswd->text());

}

void MainView::on_pbSearch_clicked()
{
    if ((ui->leSearchBox->text().size() >= 3) || (ui->leSearchBox->text().isEmpty()))
        passData.filterEntries(ui->leSearchBox->text(),ui->lwEntries);
}

void MainView::on_pbClearInputFields_clicked()
{
    ui->leInputEntry->setText("");
    ui->leInputPasswd->setText("");
    ui->leInputUser->setText("");
}

void MainView::on_pbChangePass_clicked()
{
    ui->gbChangePassView->setVisible(true);
    ui->gbAddEntryView->setVisible(false);
    newPassword = "";
    ui->leChangePass->setText("");
    ui->pbVerifyChangePasswd->setText("ENTER NEW PASSWORD");
}

void MainView::on_pbGoBack_clicked()
{
    ui->gbChangePassView->setVisible(false);
    ui->gbAddEntryView->setVisible(true);

}

void MainView::on_pbAddEntry_clicked()
{
    QString entry = ui->leInputEntry->text();
    if (entry.isEmpty()) return;

    QString passwd = ui->leInputPasswd->text();
    QString user = ui->leInputUser->text();

    // Checking for data validity
    QString msg = "cannot contain <, > or |";
    if (!PassData::validString(entry)){
        AESCryptIF::showToast("Entry name " + msg);
        return;
    }

    if (!PassData::validString(passwd)){
        AESCryptIF::showToast("Password " + msg);
        return;
    }

    if (!PassData::validString(user)){
       AESCryptIF::showToast("User name " + msg);
       return;
    }

    if (passData.entryExists(entry)){
        int ans = QMessageBox::question(this,"Modify Entry","Data exists for entry " + entry + ".\nDo you wish to overwrite it",QMessageBox::Yes|QMessageBox::No,QMessageBox::No);
        if (ans == QMessageBox::No){
            return;
        }
    }

    passData.addEntry(entry,user,passwd,ui->lwEntries,false);
    passData.filterEntries("",ui->lwEntries);

    QString error = AESCryptIF::encryptData(passData.getData());

    if (!error.isEmpty()){
        AESCryptIF::showToast("ERROR: During encryption " + error);
    }

    on_pbCancelEntry_clicked();

}

void MainView::on_pbGeneratePassword_clicked()
{
    QString passwd = pgen.generatePassword(ui->comboBox->currentIndex() + 4, ui->cboxUseSymbols->isChecked());
    ui->leInputPasswd->setText(passwd);
}

void MainView::on_pbVerifyChangePasswd_clicked()
{
    if (newPassword.isEmpty()){
        newPassword = ui->leChangePass->text();
        ui->leChangePass->setText("");
        ui->pbVerifyChangePasswd->setText("VERIFY PASSWORD");
    }
    else{
        if (newPassword == ui->leChangePass->text()){

            // Re encrypting the data.
            QString error = AESCryptIF::encryptData(passData.getData(),newPassword);
            if (!error.isEmpty()){
                AESCryptIF::showToast("ERROR: During encryption " + error);
            }
            // Logging out.
            on_pbLogOut_clicked();

            //Making sure the go back button is reneabled.
            ui->pbGoBack->setEnabled(true);

        }
        else{
            newPassword = "";
            ui->leChangePass->setText("");
            ui->pbVerifyChangePasswd->setText("ENTER NEW PASSWORD");
            AESCryptIF::showToast("Passwords do not match. Please try again");
        }
    }
}

void MainView::setButtonStyleSheet(QPushButton *pb){
    QString styleSheet = "background-color: qlineargradient(spread:pad, x1:0.502, y1:1, x2:0.498, y2:0, stop:0 rgba(0, 128, 128, 255), stop:1 rgba(30, 30, 30, 255));\nfont: 63 20pt \""
            + theFont + "\";\ncolor: rgb(255, 255, 255);";
    pb->setStyleSheet(styleSheet);
}

void MainView::setEditFieldStyle(QLineEdit *le){
    QString style = "background-color: rgb(100,100,100);\nfont: 20pt \"Monospace\";\ncolor: rgb(0, 170, 127);";
    le->setStyleSheet(style);
}

void MainView::setLabelStyle(QLabel *l){
    l->setStyleSheet("font: 20pt \"Monospace\";\ncolor: rgb(255, 255, 255);");
}

void MainView::on_pbDeleteEntry_clicked()
{
    QString entry = ui->leInputEntry->text();
    if (entry.isEmpty()) return;

    if (passData.entryExists(entry)){
        int ans = QMessageBox::question(this,"Delete Entry","Are you sure you want to delete \n" + entry + "?",QMessageBox::Yes|QMessageBox::No,QMessageBox::No);
        if (ans == QMessageBox::No){
            return;
        }
    }

    passData.deleteEntry(entry);
    passData.filterEntries("",ui->lwEntries);

    QString error = AESCryptIF::encryptData(passData.getData());

    if (!error.isEmpty()){
        AESCryptIF::showToast("ERROR: During encryption " + error);
    }

    on_pbCancelEntry_clicked();

}
