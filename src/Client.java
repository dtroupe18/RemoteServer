/*
 * Created by Dave on 3/12/17.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Client extends Application {
    private String command; // last command sent to the server
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private TextArea textArea;
    private Object response;
    private Socket socket;
    private Boolean connected;



    @Override
    public void start(Stage primaryStage) throws Exception {

        // GUI
        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 5, 5));
        paneForTextField.setStyle("-fx-border-color: red");
        paneForTextField.setLeft(new Label("Send String To Server: "));
        Button submit = new Button("Submit");
        Button reconnect = new Button("Reconnect");
        paneForTextField.setBottom(reconnect);
        paneForTextField.setRight(submit);


        Button quit = new Button("QUIT");
        quit.setStyle("-fx-text-fill: red");

        TextField textField = new TextField();
        textField.setAlignment(Pos.BOTTOM_RIGHT);
        paneForTextField.setCenter(textField);

        //create pane for buttons
        GridPane gridPane = new GridPane();
        // add horizontal spacing between buttons in grid pane
        gridPane.setHgap(8);

        // add button to grid pane
        gridPane.add(quit, 0, 0);

        BorderPane mainPane = new BorderPane();
        // display contents
        textArea = new TextArea();
        //set the size of the text area
        textArea.setPrefColumnCount(50);
        textArea.setPrefRowCount(30);
        mainPane.setCenter(new ScrollPane(textArea));
        mainPane.setTop(paneForTextField);
        mainPane.setBottom(gridPane);


        // create scene and place it on the stage
        Scene scene = new Scene(mainPane, 700, 700);
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        primaryStage.show();
        // END OF GUI

        connected = connectToServer();

        submit.setOnAction( e -> {
            if (connected) {
                command = textField.getText();

                if (!command.equals("")) {
                    writeToServer();
                    response = readFromServer();


                    if (response instanceof String) {
                        textArea.appendText("Response: " + response + "\n");
                    }
                    else {
                        textArea.appendText("Invalid Server Response\n");
                    }
                }
                else {
                    textArea.appendText("Enter a command:\n");
                }
            }
            // not connected
            else  {
                textArea.appendText("Please connect to the server:\n");
            }
        });

        reconnect.setOnAction(e -> {
            connected = connectToServer();
        });

        quit.setOnAction(e -> {
            boolean userQuit = PopUpWindows.quit();
            if (userQuit && connected) {
                try {
                    command = "Quit";
                    writeToServer();
                    socket.close();
                }
                catch (IOException ioe) {
                    textArea.appendText(ioe.getMessage());
                }
                primaryStage.close();
            }
            else {
                primaryStage.close();
            }
        });
    }


    private void writeToServer() {
        try {
            oos.writeObject(command);
        }
        catch (IOException ioe) {
            textArea.appendText(ioe.getLocalizedMessage() + "\n");
        }
    }


    private Object readFromServer() {
        try {
            Object response = ois.readObject();
            return response;
        } catch (ClassNotFoundException cnf) {
            textArea.appendText(cnf.getLocalizedMessage());
        } catch (IOException ioe2) {
            textArea.appendText(ioe2.getMessage());
        }
        return "Failed to read from server\n";
    }

    private boolean connectToServer() {
        try {
            socket = new Socket("localhost", 8675);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            textArea.appendText("Connected to Server\n");
            return true;
        }
        catch (IOException ex) {
            textArea.appendText("Failed to connect to server\n");
            return false;
        }
    }
}

