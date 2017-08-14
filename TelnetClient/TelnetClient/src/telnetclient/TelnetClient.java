/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telnetclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Camilo
 */
public class TelnetClient implements Runnable {

    private JTextArea textArea = null;
    private JTextArea textArea1 = null;

    /**
     * @return the textArea
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /**
     * @param aTextArea the textArea to set
     */
    public void setTextArea(JTextArea aTextArea) {
        textArea = aTextArea;
    }
    private int port;
    private String hostname;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread thread;
    private String code;

    public TelnetClient(String hostname, int port, String code, JTextArea textArea,JTextArea textArea1) {
        this.port = port;
        this.hostname = hostname;
        this.code = code;
        this.textArea = textArea;
        this.textArea1 = textArea1;
        this.open();
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void open() {
        try {
            this.setSocket(new Socket(this.getHostname(), this.getPort()));
            this.setOut(new PrintWriter(this.getSocket().getOutputStream(), true));
            this.setIn(new BufferedReader(new InputStreamReader(getSocket().getInputStream(), this.getCode())));
        } catch (IOException ex) {
            Logger.getLogger(TelnetClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            this.getSocket().close();
            this.getOut().close();
            this.getIn().close();
        } catch (IOException ex) {
            Logger.getLogger(TelnetClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeMessage(String message) {
        this.getOut().println(message);
        
    }

    public void receiveMessage(String message) {
        this.textArea.append(message + "\n");
    }

    @Override
    public void run() {
        String readLine = "";
        boolean quit = false;
        while (!quit) {
            try {
                readLine = this.getIn().readLine();
//                writeMessage(readLine);
                this.receiveMessage(readLine);
            } catch (IOException ex) {
                Logger.getLogger(TelnetClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        close();
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * @return the out
     */
    public PrintWriter getOut() {
        return out;
    }

    /**
     * @param out the out to set
     */
    public void setOut(PrintWriter out) {
        this.out = out;
    }

    /**
     * @return the in
     */
    public BufferedReader getIn() {
        return in;
    }

    /**
     * @param in the in to set
     */
    public void setIn(BufferedReader in) {
        this.in = in;
    }

    /**
     * @return the thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * @param thread the thread to set
     */
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

}
