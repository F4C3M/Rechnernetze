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
        try (Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            LoginMessage logMsg = new LoginMessage("Dennel", "1277");
            out.write(logMsg.serialize() + "\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            KontostandMessage kontoMsg = new KontostandMessage("722_586", -1.0);
            out.write(kontoMsg.serialize() + "\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            AbhebenMessage abhebenMsg = new AbhebenMessage("722_586", 10000.0);
            out.write(abhebenMsg.serialize() + "\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            AbhebenMessage abhebenMsg2 = new AbhebenMessage("722_586", 10.0);
            out.write(abhebenMsg2.serialize() + "\n");
            out.flush();
            System.out.println("Server: " + in.readLine());
            System.out.println("Server: " + in.readLine());

            TextMessage textMsg = new TextMessage("Danke für Ihre Hilfe!");
            out.write(textMsg.serialize() + "\n");
            out.flush();
            System.out.println("Server: " + in.readLine());
        }
    }

    public static void main(String[] args) throws IOException {
        new ATMClient("localhost", 5001).run();
    }
}