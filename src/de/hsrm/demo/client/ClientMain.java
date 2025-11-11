package de.hsrm.demo.client;
import de.hsrm.demo.coded.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Einfacher TCP-Client:
 *  - verbindet sich mit localhost:5000
 *  - sendet eine kodierte Nachricht
 *  - empfängt die Antwort und dekodiert sie
 */
public class ClientMain {
    
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5001;

        System.out.println("Verbinde zu " + host + ":" + port + " ...");

        try (Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                System.out.println("Verbunden mit " + host + ":" + port);

                // Login Test
                Message logMsg = new LoginMessage("alice", "1234");
                out.println(MessageCodec.encode(logMsg));
                System.out.println("Antwort: " + MessageCodec.decode(in.readLine()));

                // Test Kontostandabfrage
                Message kontoMsg = new KontostandMessage("alice", -1.0);
                out.println(MessageCodec.encode(kontoMsg));
                System.out.println("Antwort: " + MessageCodec.decode(in.readLine()));

                // Testitest zum Abheben
                Message abhebenMsg = new AbhebenMessage("alice", 200.0);
                out.println(MessageCodec.encode(abhebenMsg));
                System.out.println("Antwort: " + MessageCodec.decode(in.readLine()));

                // Normaler Text Test
                Message textMsg = new TextMessage("Hallo, ich bin Alice!");
                out.println(MessageCodec.encode(textMsg));
                System.out.println("Antwort: " + MessageCodec.decode(in.readLine()));

            } catch (IOException e) {
            e.printStackTrace();
        }
    }
}