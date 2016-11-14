import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashSet;

/**
 * Class for the TeaParty chat server!
 * Will create a new thread for each host connection
 * @author Oliver
 *
 */
public class ChatServer {

	protected final int PORT = 25410; //Connection port to server
	protected HashSet<String> clientNames = new HashSet<String>(); //All of the connected user names (duplicates not allowed)
	protected HashSet<PrintWriter> clientWriters = new HashSet<PrintWriter>(); //All the writer objects to each client used to distribute messages
	
	public static void main(String[] args) throws Exception {
		
        ChatServer server = new ChatServer(); //Create a new chat server
		System.out.println("TEA PARTY SERVER ACTIVATED (PORT:" + server.PORT + ")");
		
		//Create a new socket connection listener on the default port for this server
        ServerSocket listener = new ServerSocket(server.PORT);
        try {
            while (true) {
                new Handler(listener.accept(),server).start();
            }
        } finally {
        	//Will also occur if an interrupt occurs to end execution
            listener.close(); //Prevent memory leaks!
        }
        
    }
}
