/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demomessengerserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dericop
 */
final public class MessengerClient implements Runnable {

    private MessengerServer theServer = null;
    private int theID = 0;
    private String theName = "";
    private Thread theThread = null;
    private Socket theSocket = null;
    private PrintWriter theOut = null;
    private BufferedReader theIn = null;
    final private LinkedList<String> privateMessages = new LinkedList();
    String readLine = "";

    public MessengerClient(String aName, int anID, MessengerServer aServer) {
        theServer = aServer;
        theID = anID;
        theName = aName;
    }
    public MessengerClient(Socket aSocket, int anID, MessengerServer aServer) {

        theServer = aServer;
        theID = anID;
        this.setTheSocket(aSocket);
    }
    public void close() {
        try {
            theOut.close();
            theIn.close();
            theSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(MessengerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeMessage(String message) {
        theOut.println(message);
    }
    
    public void addMessage(String m) {
        this.privateMessages.push(m);
    }
    public String getTheName() {
        return theName;
    }

    public void setTheName(String usr) {
        this.theName = usr;
    }

    public void setTheSocket(Socket theSocket) {
        this.theSocket = theSocket;

        try {
            theOut = new PrintWriter(theSocket.getOutputStream(), true);
            theIn = new BufferedReader(new InputStreamReader(theSocket.getInputStream(), "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(MessengerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        theThread = new Thread(this);
        theThread.start();
    }

    public int getTheID() {
        return theID;
    }

    @Override
    public void run() {
        writeMessage(theName);
        writeMessage("Session ID = " + theID);

        while (!readLine.trim().equalsIgnoreCase("QUIT")) {
            try {
                if (theIn.ready()) {
                    readLine = theIn.readLine();
                     System.out.println("Petición: "+readLine);
                    if (readLine != null) {
                        readLine = readLine.trim().toUpperCase();

                        if (readLine.startsWith("SEND ALL ")) {
                            //Enviar un mensaje a todos los usuarios registrados
                            if (theServer.usrExist(this)) {
                                readLine = readLine.substring(9);
                                theServer.sendMessageToAll(this.theName, readLine);
                            } else {
                                writeMessage("El usuario debe estar registrado.");
                            }

                        } else if (readLine.startsWith("REGISTER ")) {
                            //Registrar un nuevo usuario y comprobar que ya no exista
                            String usr = readLine.substring(9);
                            theServer.registerClient(usr, this);

                        } else if (readLine.startsWith("SEND USER ")) {
                            //Enviar un mensaje a un único usuario
                            if (theServer.usrExist(this)) {
                                String[] splitedMessage = readLine.split(" ");
                                String usr = splitedMessage[2];
                                
                                String message = "";
                                for (int i = 3; i < splitedMessage.length; i++) {
                                     message += " "+splitedMessage[i];
                                }

                                theServer.sendMessageToUser(this.theName, usr, message);
                            } else {
                                writeMessage("El usuario debe estar registrado.");
                            }

                        } else if (readLine.startsWith("USERS")) {
                            //Listar todos los usuarios que están registrados
                            String usrList = theServer.showUserList();
                            writeMessage(usrList);

                        } else if (readLine.startsWith("LAST MESSAGES")) {
                            //Lanzar los últimos 10 mensajes escritos
                            String lastMessages = theServer.getLastMessages();
                            writeMessage(lastMessages);

                        } else if (readLine.startsWith("LAST MESSAGE")) {
                            //Lanzar el último mensaje que se ha escrito
                            String lastMessage = theServer.getLastMessage();
                            writeMessage(lastMessage);

                        } else if (!readLine.equals("QUIT")) {
                            writeMessage("Error: Invalid command");
                        }

                    } else {
                        readLine = "";
                    }
                }

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }
        theServer.deleteUsr(this);
        close();
    }

    

}
