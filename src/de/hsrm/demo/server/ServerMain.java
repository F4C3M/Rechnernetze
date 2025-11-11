package de.hsrm.demo.server;
import de.hsrm.demo.coded.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Einfacher TCP-Server:
 *  - lauscht auf Port 5000
 *  - akzeptiert genau einen Client
 *  - liest eine Zeile, dekodiert sie, gibt sie auf der Konsole aus
 *  - sendet eine Antwort zurück
 */
public class ServerMain {
    public static final int PORT = 5001;

    private static final Map<String, Double> kontoMap = new HashMap<>();
    static {
        kontoMap.put("Bob der Knechter", 5200.00);
        kontoMap.put("Marvin der Große", 1200.50);
        kontoMap.put("Toma die arme Wurst", 12.35);
        kontoMap.put("Denys der heimlichreiche", 70000.00);
        kontoMap.put("Marcelino der Stadionist", 3333.33);
        kontoMap.put("Melisana die Blume", 777.77);
    }


    public static void main(String[] args) {
        System.out.println("Server startet auf Port " + PORT + " ...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Warte auf Verbindung...");

            while(true) {
                try (Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Client verbunden: " + clientSocket.getRemoteSocketAddress());
                    String line;

                    while((line = in.readLine()) != null) {
                        Message msg = MessageCodec.decode(line);
                        
                        if (msg == null) {
                            out.println(MessageCodec.encode(new ErrorMessage("Ungültige Nachricht.")));
                            continue;
                        }

                        switch (msg.getType()) {
                            case TEXTITEXT:
                                TextMessage textMsg = (TextMessage) msg;
                                System.out.println("Text empfangen: " + textMsg.getContent());
                                out.println(MessageCodec.encode(new TextMessage("Server hat empfangen: " + textMsg.getContent())));
                                break;

                            case LOGIN:
                                LoginMessage logMsg = (LoginMessage) msg;
                                System.out.println("Login-Versuch von " + logMsg.getUsername());
                                if ("alice".equalsIgnoreCase(logMsg.getUsername()) && "1234".equals(logMsg.getKontoPin())) {
                                    out.println(MessageCodec.encode(new TextMessage("Login erfolgreich, Willkommen " + logMsg.getUsername() + "!")));
                                } else {
                                    out.println(MessageCodec.encode(new ErrorMessage("Login fehlgeschlagen!")));
                                }
                                break;

                            case KONTOSTAND:
                                KontostandMessage kontoMsg = (KontostandMessage) msg;
                                Double kontostand = kontoMap.get(kontoMsg.getKontonummer());
                                if (kontostand != null) {
                                    out.println(MessageCodec.encode(new KontostandMessage(kontoMsg.getKontonummer(), kontostand)));
                                } else {
                                    out.println(MessageCodec.encode(new ErrorMessage("Konto nicht gefunden.")));
                                }
                                break;

                            case ABHEBEN:
                                AbhebenMessage abhebenMsg = (AbhebenMessage) msg;
                                Double betrag = kontoMap.get(abhebenMsg.getKontonummer());
                                if (betrag == null) {
                                    out.println(MessageCodec.encode(new ErrorMessage("Konto existiert nicht.")));
                                } else if (betrag >= abhebenMsg.getAbhebeBetrag()) {
                                    kontoMap.put(abhebenMsg.getKontonummer(), betrag - abhebenMsg.getAbhebeBetrag());
                                    out.println(MessageCodec.encode(new TextMessage("Abhebung erfolgreich. Neuer Kontostand: " + kontoMap.get(abhebenMsg.getKontonummer()))));
                                } else {
                                    out.println(MessageCodec.encode(new ErrorMessage("Nicht genug Guthaben.")));
                                }
                                break;

                            case ERRORS:
                                ErrorMessage errMsg = (ErrorMessage) msg;
                                System.err.println("Fehlermeldung vom Client: " + errMsg.getFehlertext());
                                break;

                            default:
                                out.println(MessageCodec.encode(new ErrorMessage("Unbekannter Nachrichtentyp!")));
                        }
                    }   
                } catch (IOException e) {
                    System.out.println("Fehler mit dem Client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}