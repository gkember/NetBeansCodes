import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * A generic server program which can spwan multiple 
 * threaded worker server 
 * socket to receive data from multiple clients. 
 * The data is received line by line. So each chunk on the client side must be 
 * terminated by a new line.  
 * Although it is a generic server it is to be used to receive Accelerometer 
 * readings from an accelerometer attached to an Aruino Yun.
 * 
 * By Samer Mansour.
 */

public class ServerSocket_Ex1 {

    public static void main(String[] args) throws Exception {
        //Here force binding to the wifi IP address to make sure you 
        //receive from the Arduino Yun. Both must be connected to the same 
        //network. It must be changed based on your IP.
        byte bytesAdd [] = {(byte) 192, (byte) 168, (byte) 240, (byte) 150};         
        InetAddress addr = InetAddress.getByAddress(bytesAdd );//getLocalHost();
        int port = 5555;
        //SocketAddress sockaddr = new InetSocketAddress(addr, port);
        
        int clientNumber = 0;
        //ServerSocket listener = new ServerSocket(5555);
        ServerSocket listener = new ServerSocket(port, 10, addr);
        System.out.println("The server is running." + listener);
        try {
            while (true) {
                
                new ClientHandler(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A thread in which to run a worker server socket
     */
    private static class ClientHandler extends Thread {
        private Socket socket;
        private int clientNumber;

        public ClientHandler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            consoleLog("New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * The thread's run method.
         */
        public void run() {
            try {

                //A buffered reader to read from the socket stream.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());
                
                /*Later if we need to reply back to the client*/
                //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // Send a welcome message to the client.
                //out.println("Hello, you are client #" + clientNumber + ".");
              
                // Get messages from the client, line by line; 
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        break;
                    }
                    //Log the received measurments to a console. 
                    //Later log to a file or to a database with timestamps.
                    consoleLog("Received:" + input);
                }
            } catch (IOException e) {
                consoleLog("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    consoleLog("Couldn't close a socket:" + socket + ". Error :" + e);
                }
                consoleLog("Connection with client# " + clientNumber + " closed");
            }
        }

        /**
         * Logs a simple message to the console. 
         */
        private void consoleLog(String message) {
            System.out.println(message);
        }
    }
}