package de.hsrm.demo.server4;

import java.io.*;
import java.net.*;
import de.hsrm.demo.coded.*;


public class BankServer {
    private final int port;

    public BankServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Der Bankserver läuft auf dem Port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Ein neuer Klient wird verbunden: " + clientSocket.getInetAddress());

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    String line;
                    while ((line = in.readLine()) != null) {
                        Message msg = MessageCodec.decode(line);

                        switch (msg.getType()) {
                            case LOGIN:
                                LoginMessage logMsg = (LoginMessage) msg;
                                System.out.println("Login-Anfrage: " + logMsg.getUsername());
                                out.write(new TextMessage("OK").serialize() + "\n");
                                out.flush();
                                break;
                            
                            case KONTOSTAND:
                                KontostandMessage kontoMsg = (KontostandMessage) msg;
                                System.out.println("Kontostand-Anfrage für: " + kontoMsg.getKontonummer());
                                out.write(new KontostandMessage(kontoMsg.getKontonummer(), 1234.56).serialize() + "\n");
                                out.flush();
                                break;
                            
                            case ABHEBEN:
                                AbhebenMessage abhebenMsg = (AbhebenMessage) msg;
                                System.out.println("Abhebungsanfrage: " + abhebenMsg.getBetrag() + " von " + abhebenMsg.getKontonummer());
                                out.write(new TextMessage("Bitte entnehmen Sie Ihr Geld").serialize() + "\n");
                                out.flush();
                                out.write(new TextMessage("Abhebung erfolgreich").serialize() + "\n");
                                out.flush();
                                break;
                            
                            case TEXTITEXT:
                                TextMessage textMsg = (TextMessage) msg;
                                System.out.println("Textnachricht vom Client: " + textMsg.getContent());
                                out.write(new TextMessage("Server hat die Nachricht erhalten").serialize() + "\n");
                                out.flush();
                                break;
                            
                            default:
                                out.write(new ErrorMessage("Unbekannter Nachrichttyp").serialize() + "\n");
                                out.flush();
                                break;
                        }
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        new BankServer(5001).start();
    }
}