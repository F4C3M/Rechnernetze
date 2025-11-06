package de.hsrm.demo.client;

import de.hsrm.demo.coded.MessageCodec;

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
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            String msg = "Hallo Server, hier ist der Client!";
            String encoded = MessageCodec.encode(msg);

            System.out.println("Sende kodiert: " + encoded);
            out.println(encoded);

            String reply = in.readLine();
            System.out.println("Roh empfangen: " + reply);

            String decodedReply = MessageCodec.decode(reply);
            System.out.println("Dekodierte Antwort: " + decodedReply);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
