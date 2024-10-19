package org.example.javafxdb_sql_shellcode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.javafxdb_sql_shellcode.db.ConnDbOps;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;




public class DB_GUI_Controller implements Initializable {
    private static ConnDbOps cdbop;
    private final ObservableList<Person> data = FXCollections.observableArrayList();


    @FXML
    TextField full_name, email_adr, phone_number, home_adr, password;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, String> tv_name, tv_email, tv_pn, tv_adr;
    @FXML
    private MenuItem toggleDarkMode, loginButton;
    @FXML
    ImageView img_view;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cdbop = new ConnDbOps();
        tv_name.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tv_email.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        tv_pn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        tv_adr.setCellValueFactory(new PropertyValueFactory<>("houseAddress"));

        //Using getPeopleFromDatabase to put them on the tv
        ObservableList<Person> personList = cdbop.getUserFromDatabase();
        data.addAll(personList);
        tv.setItems(data);


    }

//Adds new user to database

    @FXML
    protected void addNewRecord() {
        cdbop = new ConnDbOps();

        data.add(new Person(
                full_name.getText(),
                email_adr.getText(),
                phone_number.getText(),
                home_adr.getText(),
                password.getText()
        ));
        cdbop.insertUser(full_name.getText(), email_adr.getText(), phone_number.getText(), home_adr.getText(), password.getText());

        tv.refresh();
    }

    //Clears textfields

    @FXML
    protected void clearForm() {
        full_name.clear();
        email_adr.setText("");
        phone_number.setText("");
        home_adr.setText("");
        password.setText("");
        tv.refresh();
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    //Creates second person which will replace the current person (which p1 is assigned to), data is removed then added in the selected spot

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int c = data.indexOf(p);
        Person p2 = new Person();

        p2.setFullName(full_name.getText());
        p2.setEmailAddress(email_adr.getText());
        p2.setPhoneNumber(phone_number.getText());
        p2.setHouseAddress(home_adr.getText());
        p2.setPassword(password.getText());
        cdbop.updateUser(full_name.getText(), email_adr.getText(), phone_number.getText(), home_adr.getText(), password.getText(), p.getEmailAddress());
        data.remove(c);
        data.add(c, p2);
        tv.getSelectionModel().select(c);
        tv.refresh();
    }


    //Deletes user from database

    @FXML
    protected void deleteRecord() {
        cdbop = new ConnDbOps();
        //get user from list via selection
        Person selectedUser = tv.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            //remove from the list
            data.remove(selectedUser);

            //Delete user using email
            cdbop.deleteUserFromDatabase(selectedUser.getEmailAddress());
            tv.refresh();
        } else {
            System.out.println("No user selected to delete.");
        }
    }


    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));

        }
    }


    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        full_name.setText(p.getFullName());
        email_adr.setText(p.getEmailAddress());
        phone_number.setText(p.getPhoneNumber());
        home_adr.setText(p.getHouseAddress());

    }


    //Login screen that opens when you go into File->Login.

    @FXML
    protected void login() {
        Stage stage = new Stage();
        stage.setTitle("Login to your account");
        TextField username = new TextField();
        TextField password = new TextField();
        username.setPromptText("Username");
        password.setPromptText("Password");
        Button login = new Button("Login");
        Button exit = new Button("Exit");
        exit.setOnAction(e -> stage.close());

        VBox vbox = new VBox();
        vbox.getChildren().addAll(username, password, login, exit);
        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        stage.show();


    }

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
    }
}