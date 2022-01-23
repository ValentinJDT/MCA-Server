package fr.mrcoq;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Server {

    private int i = 0;
    private boolean stop = false;
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {

        try(ServerSocket serverSocket = new ServerSocket(this.port)) {
            while(!stop) {

                System.out.println("Waiting a new client (" + i + ") ...");
                Socket socket = serverSocket.accept();
                i++;

                System.out.println("Client found on " + socket.getLocalAddress().getHostName());

                boolean canConnect = true;

                while(canConnect || !socket.isConnected()) {
                    canConnect = false;

                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    Scanner reader = new Scanner(inputStream, StandardCharsets.UTF_8);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));

                    Timer timer = new Timer();

                    timer.schedule(new TimerTask() {
                        public void run() {

                            if(reader.hasNext()) {
                                String line = reader.nextLine();

                                if(line.trim().equals("client-disconnect")) {
                                    System.out.printf("Client %s disconnected\n", socket.getLocalAddress().getHostName());
                                    i--;
                                    this.cancel();
                                } else {
                                    System.out.printf("From %s : %s%n", socket.getLocalAddress().getHostName(), line);
                                }

                            }

                        }
                    }, 0L, 50L);
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

    }

}
