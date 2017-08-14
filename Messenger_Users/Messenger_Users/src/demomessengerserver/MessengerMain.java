
package demomessengerserver;

/**
 *
 * @author Daniel Rico
 */
public class MessengerMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MessengerServer server = new MessengerServer(5521,"Dericop - Server");
        server.run();
    }
    
}
