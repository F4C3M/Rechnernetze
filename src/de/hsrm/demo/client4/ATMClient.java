package de.hsrm.demo.client4;

import java.io.*;
import java.net.*;
import de.hsrm.demo.coded.*;


public class ATMClient {
    private final String host;
    private final int port;

    public ATMClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        Socket socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        /* Actor will sich anmelden */
        LoginMessage logMsg = new LoginMessage("user1", "geheim");
        out.write(logMsg.serialize() + "\n");
        out.flush();
        System.out.println("Server: " + in.readLine());

        /* Actor fragt Kontostnad ab */
        KontostandMessage kontoMsg = new KontostandMessage("123456", -1.0);
        out.write(kontoMsg.serialize() + "\n");
        out.flush();
        System.out.println("Server: " + in.readLine());

        /* Actor will Geld abheben */
        AbhebenMessage abhebenMsg = new AbhebenMessage("123456", 100.0);
        out.write(abhebenMsg.serialize() + "\n");
        out.flush();
        System.out.println("Server: " + in.readLine()); // Hinweis
        System.out.println("Server: " + in.readLine()); // Erfolg

        /* Actor sendet dem Server einen Text */
        TextMessage textMsg = new TextMessage("Danke für Ihre Hilfe!");
        out.write(textMsg.serialize() + "\n");
        out.flush();
        System.out.println("Server: " + in.readLine());

        socket.close();
    }

    public static void main(String[] args) throws IOException {
        new ATMClient("localhost", 5001).run();
    }
}