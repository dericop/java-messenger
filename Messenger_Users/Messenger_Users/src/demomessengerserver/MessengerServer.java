package demomessengerserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dericop
 */
public class MessengerServer {

    private int theSessionID = 1;
    private String theName = "";
    private int thePort = 0;
    final private LinkedList<MessengerClient> clients = new LinkedList();
    private ServerSocket serverSocket = null;
    final private LinkedList<String> messagesList = new LinkedList();

    public MessengerServer(int aPort, String aName) {
        thePort = aPort;
        theName = aName;
    }
    public void sendMessageToAll(String em, String message) {
        for (int i = 0; i < clients.size(); ++i) {
            clients.get(i).writeMessage(em+": "+message);
        }
        messagesList.add(message); //Almacenar el mensaje que se ha enviado
    }
    public void sendMessageToUser(String em ,String usr, String message) {
        System.out.println(usr);
        for (MessengerClient client : clients) {
            System.out.println(usr);
            if (client.getTheName().equals(usr)) {
                client.addMessage(message);
                client.writeMessage("PRIVATE:\n" + em+": "+message);
                return;
            }
        }
    }

    public void deleteUsr(MessengerClient usr) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getTheName().equals(usr.getTheName())) {
                sendMessageToAll(this.theName, "Se ha desconectado el usuario: "+clients.get(i).getTheName());
                clients.remove(i);
                sendMessageToAll(this.theName, showUserList());
                return;
            }
        }
    }
    public String getLastMessages() {
        String lastMessages;
        if (messagesList.isEmpty()) {
            lastMessages = "Registro de mensajes vacío, ¡ ESCRIBE UNO !";
        } else {
            lastMessages = "MESSAGES:\n";
            for (String message : messagesList) {
                lastMessages += message + " \n";
            }
        }
        return lastMessages;
    }
    public String getLastMessage() {
        String lastMessage = "MESSAGE:\n";
        if (messagesList.isEmpty()) {
            lastMessage = "Registro de mensajes vacío, ¡ ESCRIBE UNO !";
        } else {
            lastMessage += messagesList.get(messagesList.size() - 1) + "\n";
        }
        return lastMessage;
    }

    public String showUserList() {
        String userList;

        if (clients.isEmpty()) {
            userList = "no hay usuarios registrados, ¡ REGISTRATE !";
        } else {
            userList = "USERLIST:\n";
            for (MessengerClient client : clients) {
                if (!client.getTheName().equals("")) {
                    userList += client.getTheName();
                } else {
                    userList += "Anónimo " + client.getTheID();
                }

                userList += "\n";
            }
        }
        return userList;
    }

    public boolean usrExist(MessengerClient client) {
        for (MessengerClient c : clients) {
            if (!client.getTheName().equals("") && client.getTheName().equals(c.getTheName())) {
                return true;
            }
        }
        return false;
    }

    public boolean connectUsr(String usr) {
        try {
            Socket socket = serverSocket.accept();
            for (MessengerClient client : clients) {
                if (client.getTheName().equals(usr)) {
                    client.setTheSocket(socket);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public void registerClient(String usrName, MessengerClient client) {
        boolean usrAvailable = true;
        for (MessengerClient c : clients) {
            if (c.getTheName().equals(usrName)) {
                usrAvailable = false;
            }
        }
        if (usrAvailable) {
            client.setTheName(usrName);
            for (MessengerClient c : clients) {
                c.writeMessage(showUserList());
            }
        }else{
            client.writeMessage("El usuario que ingresó ya ha sido asignado");
        }
    }

    public void run() {
        Socket socket;
        MessengerClient client;
        boolean quit = false;
        try {
            serverSocket = new ServerSocket(thePort);
            while (!quit) {
                try {
                    socket = serverSocket.accept();
                    client = new MessengerClient(socket, theSessionID, this);
                    clients.add(client);
                    client.writeMessage(getLastMessages());
                    theSessionID++;
                } catch (IOException ex) {
                    Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(MessengerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
