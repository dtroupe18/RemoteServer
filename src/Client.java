/*
 * Created by Dave on 3/12/17.
 */

import java.io.*;
import java.net.*;


public class Client {

    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static Object response;
    private static Socket socket;
    private static Boolean connected = false;



    public static String doStuff(String toRotate) {
        if (!connected) {
            connected = connectToServer();
        }

        if (!toRotate.equals("")) {
            writeToServer(toRotate);
            response = readFromServer();


            if (response instanceof String) {
                System.out.println(response);
                return (String) response;
            }
            else {
                return  "Invalid Server Response" ;
            }
        }
        return "Failure";
    }





    private static void writeToServer(String s) {
        try {
            oos.writeObject(s);
        }
        catch (IOException ioe) {
            System.out.println(ioe.getLocalizedMessage() + "\n");
        }
    }


    private static Object readFromServer() {
        try {
            Object response = ois.readObject();
            return response;
        } catch (ClassNotFoundException cnf) {
            System.out.println(cnf.getLocalizedMessage());
        } catch (IOException ioe2) {
            System.out.println(ioe2.getMessage());
        }
        return "Failed to read from server\n";
    }

    private static boolean connectToServer() {
        try {
            socket = new Socket("localhost", 8675);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to Server");
            return true;
        }
        catch (IOException ex) {
            System.out.println("Failed to connect to server");
            return false;
        }
    }
}

