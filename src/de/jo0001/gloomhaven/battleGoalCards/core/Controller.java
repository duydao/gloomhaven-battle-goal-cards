package de.jo0001.gloomhaven.battleGoalCards.core;

import de.jo0001.gloomhaven.battleGoalCards.network.Client;
import de.jo0001.gloomhaven.battleGoalCards.network.Server;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Label title1;
    @FXML
    public Label title2;
    @FXML
    public Label desc1;
    @FXML
    public Label desc2;
    @FXML
    public Label checks1;
    @FXML
    public Label checks2;
    @FXML
    public Label id1;
    @FXML
    public Label id2;
    @FXML
    MenuItem manageServerBtn;
    @FXML
    MenuItem connect;
    @FXML
    MenuItem resetbtn;
    @FXML
    AnchorPane card1;
    @FXML
    AnchorPane card2;
    @FXML
    public Button btn;

    private boolean locked = false;
    public boolean connected = false;
    private Client client;
    private int port = 58889;
    private String host = null;

    public Controller() {
        //@FXML-Fields are not initialized
        System.out.println("Initiated Controller instance");
    }

    public void onBtnPress(ActionEvent buttonEvent) {
        if (!connected) {
            prompt(buttonEvent);
        } else {
            resetCard(1);
            resetCard(2);
            //showSide(1);
            client.getInfo();
            client.getInfo();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetCard(1);
        resetCard(2);
        btn.setText("Connect to server");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                onBtnPress(actionEvent);
            }
        });
        connect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                prompt(actionEvent);
            }
        });

        resetbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                reset(actionEvent);
            }
        });
        manageServerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mangeServer(actionEvent);
            }
        });
        card1.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> cardClicked(card1));
        card2.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> cardClicked(card2));

        desc1.setWrapText(true);
        desc2.setWrapText(true);
    }

    private void mangeServer(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("58889");
        dialog.setTitle("Start a server");
        dialog.setHeaderText(null);
        dialog.setContentText("Port");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            int tmpPort;
            try {
                tmpPort = Integer.parseInt(result.get());
                startServer(tmpPort);
            } catch (NumberFormatException e) {
                alert("Invalid port", "Port must contain only numbers. For example 58889", Alert.AlertType.ERROR);
            }
        }
    }

    private void startServer(int tmpPort) {
        Server s = new Server(tmpPort);
        s.start();
    }

    private void reset(ActionEvent actionEvent) {
        resetCard(1);
        resetCard(2);
        btn.setDisable(false);
        card1.getStyleClass().clear();
        card1.getStyleClass().add("side1");
        card2.getStyleClass().clear();
        card2.getStyleClass().add("side1");
        locked = false;
        setUpClient(host, port);
    }

    private void cardClicked(AnchorPane card) {
        if (!locked) {
            if (card == card1) {
                card2.getStyleClass().clear();
                card2.getStyleClass().add("side1");
                resetCard(2);
            } else {
                card1.getStyleClass().clear();
                card1.getStyleClass().add("side1");
                resetCard(1);
            }
            locked = true;
        }
    }

    public void show() {
        card1.getStyleClass().clear();
        card1.getStyleClass().add("side2");
        card2.getStyleClass().clear();
        card2.getStyleClass().add("side2");
    }

    private void resetCard(int card) {
        if (card == 1) {
            title1.setText(null);
            desc1.setText(null);
            checks1.setText(null);
            id1.setText(null);
        } else {
            title2.setText(null);
            desc2.setText(null);
            checks2.setText(null);
            id2.setText(null);
        }
    }

    private void prompt(ActionEvent buttonEvent) {
        TextInputDialog dialog = new TextInputDialog("localhost:58889");
        dialog.setTitle("Settings");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter ip & port");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String[] res = result.get().split(":");

            try {
                port = Integer.parseInt(res[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                alert("Invalid port", "Port must contain only numbers. Using default port " + port, Alert.AlertType.WARNING);
            }
            host = res[0];
            setUpClient(host, port);
            btn.setText("Für alle Anzeigen");
            connected = true;

        }
    }

    private void setUpClient(String host, int port) {
        client = new Client(host, port, this);
        client.start();
    }


    private void alert(String title, String mes, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(mes);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
        alert.showAndWait();
    }
}