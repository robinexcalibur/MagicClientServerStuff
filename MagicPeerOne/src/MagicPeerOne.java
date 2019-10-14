import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class MagicPeerOne implements Runnable {
        private static final int PORT = 1604;
        private boolean running = false;

        // main entry point for the application
        public static void main(String args[])
        {
            try
            {
                // startup
                MagicPeerOne server = new MagicPeerOne();
                Thread serverThread = new Thread(server);
                serverThread.start();

                // wait until finished
                System.out.println("Server running, press enter to exit.");
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

            try
            {
                boolean shouldQuit = false;
                System.out.println("We are now testing the Magic Client!");
                while(!shouldQuit)
                {

                    //math variables
                    String command = "";
                    int a = 0;
                    int b = 0;
                    String mathProtocol = "";

                    Scanner input = new Scanner(System.in);
                    String[] recognizedCommands = {"add", "subtract", "multiply", "divide"};

                    while (!shouldQuit) {
                        System.out.println("Commands include: add, subtract, multiply, divide, or quit.");
                        System.out.print("Please enter a command: ");
                        command = input.next().toLowerCase();

                        if (command.equals("quit")) {
                            shouldQuit = true;
                            break;
                        } else if (Arrays.asList(recognizedCommands).contains(command)) {
                            System.out.print("Please enter a first number: ");
                            a = input.nextInt();

                            System.out.print("Please enter a second number: ");
                            b = input.nextInt();
                            mathProtocol = command + " " + a + " " + b;

                            try
                            {

                                // done: create datagram socket

                                //note: my server has my sources
                                InetAddress localIpAddress = InetAddress.getLocalHost();
                                Socket socket = new Socket("localhost", PORT);

                                // Build input and outputs
                                DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
                                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                                //write message out to sever
                                outToServer.writeBytes(mathProtocol + '\n');

                                socket.setSoTimeout(30000);

                                String returnMessage = inFromServer.readLine();
                                System.out.println(returnMessage);


                            }
                            catch (Exception e)
                            {
                                System.err.println("Failed to create socket and send packet: " + e.toString());
                                shouldQuit = true;
                                break;
                            }

                        } else {
                            System.out.println("Command not found, please try again.");
                        }

                    }

                    input.close();

                }
            }
            catch (Exception e)
            {
                System.err.println("Math client error: " + e.toString());
            }
        }


        public MagicPeerOne()
        {
            this.running = true;
        }

        @Override
        public void run() {

        }
    }
