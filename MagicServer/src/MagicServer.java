import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MagicServer implements Runnable{
    private static final int Port = 1604;
    private boolean running = false;

    // main entry point for the application
    public static void main(String args[])
    {
        try
        {
            // startup
            MagicServer server = new MagicServer();
            Thread serverThread = new Thread(server);
            serverThread.start();

            // wait until finished
            System.out.println("Math server running, press enter to quit...");
            Console cons = System.console();
            String enterString = cons.readLine();

            // shutdown gracefully
            server.running = false;
            serverThread.interrupt();
        }
        catch (Exception e)
        {
            System.err.println("Math server error: " + e.toString());
        }
    }

    // constructor
    public MagicServer()
    {
        this.running = true;
    }

    // this is the server thread
    public void run()
    {

        ServerSocket openingSocket = null;
        //open Scanner
        Scanner input = new Scanner(System.in);

        // create the TCP server datagram socket
        try
        {
            openingSocket = new ServerSocket(Port);
        }
        catch (Exception e)
        {
            System.err.println("Couldn't create datagram socket: " + e.toString());
        }

        while(this.running == true)
        {
            //Help with TCP stuff comes from:
            //https://systembash.com/a-simple-java-tcp-server-and-tcp-client/
            try
            {
                // DONE: create buffer and packet
                if (openingSocket != null) {

                    // done: receive packet via the info socket
                    Socket connection = openingSocket.accept();
                    System.out.println("connection made");

                    // done: convert the buffer in the packet into string variable message
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());

                    String message = inFromClient.readLine();
                    System.out.println(message);

                    // answer the math problem and print answer
                    int mathAnswer = this.handleMathMessage(message);
                    System.out.println("Received via TCP: " + message);
                    System.out.println("The math answer to the message was : " + Integer.toString(mathAnswer));

                    //take user input
                    System.out.print("Write a message back to the client: ");
                    String messageBack = input.nextLine();

                    //send back to client
                    outToClient.writeBytes("The answer sent from server was: " + Integer.toString(mathAnswer) +
                            " bonus message from user: " + messageBack + "\n");


                    Thread.yield();
                } else {
                    System.err.println("Couldn't create datagram socket and the problem compounded");
                }
            }
            catch (Exception e)
            {
                System.err.println("Failed to receive UDP packet, general exception: " + e.toString());
                this.running = false;
                break;
            }
        }
        input.close();
    }


    //reads the BufferedReader of the stream sent by the client and returns it as a string.
    private String readInput(BufferedReader reader) throws IOException {
        String wholeMessage = "";
        String holder = reader.readLine();
        while (holder != null) {
            wholeMessage = wholeMessage + " " + holder;
            holder = reader.readLine();
        }

        return wholeMessage;
    }

    private int handleMathMessage(String message)
    {
        Scanner scanner = new Scanner(message);

        String operation = scanner.next();
        int a = scanner.nextInt();
        int b = scanner.nextInt();

        int answer = 0;

        if (operation.equals("add")) {
            answer = add(a, b);
        } else if (operation.equals("subtract")) {
            answer = subtract(a, b);
        } else if (operation.equals("multiply")) {
            answer = multiply(a, b);
        } else if (operation.equals("divide")) {
            answer = divide(a, b);
        } else {
            System.err.println("command not recognized");
        }

        /*
         * done: handle your math application layer protocol message
         * this protocol must handle: add, subtract, multiply, and divide
         * each of these functions should take 2 integers
         *
         * You must parse the string, perform the math, and then return
         * the integer value of the answer
         *
         * You must handle negative numbers gracefully
         *
         */

        return answer;
    }

    private int add (int a, int b) {
        return a + b;
    }

    private int subtract (int a, int b) {
        return a - b;
    }

    private int multiply (int a, int b) {
        return a * b;
    }

    private int divide (int a, int b) {
        return a / b;
    }
}
