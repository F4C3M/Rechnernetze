package de.hsrm.demo.server;

import de.hsrm.demo.coded.MessageCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Einfacher TCP-Server:
 *  - lauscht auf Port 5000
 *  - akzeptiert genau einen Client
 *  - liest eine Zeile, dekodiert sie, gibt sie auf der Konsole aus
 *  - sendet eine Antwort zurück
 */
public class ServerMain {

    public static final int PORT = 5001;

    public static void main(String[] args) {
        System.out.println("Server startet auf Port " + PORT + " ...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Warte auf Verbindung...");
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                System.out.println("Client verbunden: " + clientSocket.getRemoteSocketAddress());

                String line = in.readLine(); // eine Zeile empfangen
                System.out.println("Roh empfangen: " + line);

                String decoded = MessageCodec.decode(line);
                System.out.println("Dekodiert: " + decoded);

                String response = "Hallo Client, ich habe deine Nachricht erhalten: " + decoded;
                String encodedResponse = MessageCodec.encode(response);
                out.println(encodedResponse);
                System.out.println("Antwort gesendet: " + encodedResponse);

            }
            System.out.println("Server beendet Verbindung und fährt runter.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
