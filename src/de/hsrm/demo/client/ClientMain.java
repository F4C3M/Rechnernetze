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
                Message login = new LoginMessage("alice", "1234");
                out.println(MessageCodec.encode(login));
                System.out.println("Antwort: " + MessageCodec.decode(in.readLine()));

                // Test Kontostandabfrage
                Message konto = new KontostandMessage("alice", -1.0);
                out.println(MessageCodec.encode(konto));
                System.out.println("Antwort: " + MessageCodec.decode(in.readLine()));

                // Testitest zum Abheben
                Message abheben = new AbhebenMessage("alice", 200.0);
                out.println(MessageCodec.encode(abheben));
                System.out.println("Antwort: " + MessageCodec.decode(in.readLine()));

                // Normaler Text Test
                Message text = new TextMessage("Hallo, ich bin Alice!");
                out.println(MessageCodec.encode(text));
                System.out.println("Antwort: " + MessageCodec.decode(in.readLine()));


                
                /* erstellen der Message */
                // Message msg = new TextMessage("Hallo Server, hier ist der Client!");
                /* codieren der Message */
                // String encoded = MessageCodec.encode(msg);
                // System.out.println("Sende kodiert: " + encoded);
                /* senden der Message */
                // out.println(encoded);
                /* Antwort empfangen*/
                // String reply = in.readLine();
                // System.out.println("Roh empfangen: " + reply);
                /* decodieren der Message */
                // Message decodedReply = MessageCodec.decode(reply);
                // if (decodedReply instanceof TextMessage textReply) {
                //    System.out.println("Dekodierte Antwort: " + textReply.getContent());
                // } else {
                //    System.out.println("Antwort ist keine TextMessage!");
                // }
            } catch (IOException e) {
            e.printStackTrace();
        }
    }
}