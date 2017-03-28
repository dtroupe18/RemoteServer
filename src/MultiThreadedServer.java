/**
 * Created by Dave on 3/8/17.
 */

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class MultiThreadedServer extends Application {
    // Text area to display content
    private TextArea textArea = new TextArea();

    // number the clients
    private int clientNo = 0;
    private String toRotate;
    private String rotated;
    private Object fromClient;
    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;
    private boolean clientConnected = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 15, 5));
        paneForTextField.setCenter(textArea);
        ScrollPane scrollPane = new ScrollPane(textArea);
        Button quit = new Button("Quit");
        paneForTextField.setCenter(scrollPane);
        paneForTextField.setBottom(quit);
        Scene scene = new Scene(paneForTextField,  450, 450);


        primaryStage.setTitle("MultiThreaded Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        quit.setOnAction(e -> {
            boolean toQuit = PopUpWindows.quit();
            if (toQuit) {
                Platform.exit();
                System.exit(0);
            }
        });

        // MULTI-THREADING
        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8675);
                textArea.appendText("Multi-Threaded Server Started at " + new Date() + "\n");

                // FOREVER!! Listen for connections
                while (true) {
                    Socket socket = serverSocket.accept();
                    clientNo++; // we added a client
                    Platform.runLater(() -> {
                        // Display the number of clients
                        textArea.appendText("Starting thread for client: " + clientNo +
                                " at " + new Date() + "\n");
                        // Find the client's host name, and IP address
                        InetAddress inetAddress = socket.getInetAddress();
                        textArea.appendText("Client " + clientNo +"'s host name is " +
                                inetAddress.getHostName() +"\n");
                        textArea.appendText("Client " + clientNo +"'s IP Address is " +
                                inetAddress.getHostAddress() + "\n");
                    });

                    // Create a new thread for each connection
                    new Thread(new HandleAClient(socket)).start();
                }
            }
            catch (IOException exception) {
                System.err.println(exception);
            }
        }).start();
    }

    // Thread class for handling new client connections
    class HandleAClient implements Runnable {
        private Socket socket;

        // construct a thread
        public HandleAClient(Socket socket) {
            this.socket = socket;
        }

        // run a thread
        public  void run() {
            try {
                // create input and output data streams
                inputFromClient = new ObjectInputStream(socket.getInputStream());
                outputToClient = new ObjectOutputStream(socket.getOutputStream());


                // FOREVER serve the client
                while (true) {
                    // receive radius from client
                    readFromClient();

                    if (clientConnected) {
                        if (toRotate.charAt(0) == '#') {
                            rotated = rotateStringR(toRotate.substring(1));

                        }
                        else {
                            rotated = rotateString(toRotate);
                        }


                        // send area back to the client
                        writeObjectToClient(rotated);
                    }

//                    Platform.runLater(() -> {
//                        textArea.appendText("String received from client: " +
//                                toRotate + "\n");
//
//                        textArea.appendText("Rotated String: " + rotated + "\n");
//                    });
                }

            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }

    private void readFromClient() {
        try {
             fromClient = inputFromClient.readObject();

            if (fromClient instanceof String) {
                toRotate = (String) fromClient;
                clientConnected = true;
            }

        } catch (ClassNotFoundException cnf) {
            textArea.appendText(cnf.getLocalizedMessage());
        } catch (IOException ioe2) {
            System.out.println("Client Quit");
            Thread.currentThread().stop();
        }
    }

    private void writeObjectToClient(String s) {
        try {
            outputToClient.writeObject(s);
        }
        catch (IOException ioe) {
            textArea.appendText(ioe.getLocalizedMessage());
        }
    }

    private String rotateString(String s) {
        char tmp = s.charAt(0);
        s = s.substring(1, s.length());
        s += tmp;

        return s;
    }

    private String rotateStringR(String s) {
        String tmp = s.substring(s.length() - 1);
        s = s.substring(0, s.length() - 1);
        tmp += s;

        return tmp;
    }
}
