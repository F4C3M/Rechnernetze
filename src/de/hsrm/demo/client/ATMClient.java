package de.hsrm.demo.client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import de.hsrm.demo.coded.*;


public class ATMClient {
    private final String host;
    private final int port;
    private UserStatus status = UserStatus.LOGGED_OUT;

    public ATMClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private Socket verbindungsVersuche() {
        for (int i = 1; i <= 3; i++) {
            try {
                System.out.println(i + "er Verbindungsversuch zu: " + host + "auf dem Port: " + port);
                return new Socket(host, port);
            } catch (IOException e) {
                System.out.println("Verbindungsversuch ist fehlgeschlagen!");
                System.out.println("Neuer Versuch startet in 20 Sekunden.");
                try { Thread.sleep(20_000); } catch (InterruptedException ignored) {}
            }
        }
        System.out.println("Keine Verbindung zum Host möglich, Klient wird jetzt beendet.");
        return null;
    }

    public void run() throws IOException {
        Socket socket = verbindungsVersuche();
        if (socket == null) {
            return;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner scanner = new Scanner(System.in)) {

            System.out.println("ATM Kommandos:");
            System.out.println("'login'      -  Einloggen");
            System.out.println("'kontostand' -  Kontostand anfragen");
            System.out.println("'abheben'    -  Geld abheben");
            System.out.println("'text'       -  Textnachricht senden");
            System.out.println("'exit'       -  Beenden");

            while (true) {
                String kommando = scanner.nextLine().trim().toLowerCase();
                if (kommando.isEmpty()) {
                    System.out.println("Bitte geben Sie ein Kommando ein!");
                    continue;
                }

                switch (kommando) {
                    case "login":
                        System.out.print("Bitte geben Sie den Benutzernamen ein: ");
                        String username = scanner.nextLine().trim();
                        while (username.isEmpty()) {
                            username = scanner.nextLine().trim();
                            if (username.isEmpty()) {
                                System.out.println("Benutzername darf nicht leer sein!");
                            }
                        }

                        System.out.print("Bitte geben Sie Ihren PIN ein: ");
                        String kontoPin = scanner.nextLine().trim();
                        while (kontoPin.isEmpty()) {
                            kontoPin = scanner.nextLine().trim();
                            if (kontoPin.isEmpty()) {
                                System.out.println("PIN darf nicht leer sein!");
                            }
                        }

                        LoginMessage logMsg = new LoginMessage(username, kontoPin);
                        out.write(logMsg.serialize() + "\n");
                        out.flush();

                        /* Serverantwort mit Verarbeitung */
                        Message logAnt = MessageCodec.decode(in.readLine());
                        
                        if (logAnt instanceof TextMessage) {
                            status = UserStatus.LOGGED_IN;
                            System.out.println("Sie haben sich erfolgreich eingeloggt!");
                        } else if (logAnt instanceof ErrorMessage) {
                            status = UserStatus.LOGGED_OUT;
                            System.out.println("Login ist fehlgeschlagen: " +
                                ((ErrorMessage) logAnt).getFehlertext());
                        } else {
                            System.out.println("Unerwartete Antowort! Error!");
                        }
                        break;


                    case "kontostand":
                        if (status != UserStatus.LOGGED_IN) {
                            System.out.println("Sie müssen sich zuerst einloggen!");
                            break;
                        }

                        /* Kontonummereingabe mit Fehlerabfangen */
                        System.out.print("Bitte geben Sie Ihre Kontonummer ein: ");
                        String kontonummer = scanner.nextLine().trim();
                        while (kontonummer.isEmpty()) {
                            kontonummer = scanner.nextLine().trim();
                            if (kontonummer.isEmpty()) {
                                System.out.println("Sie müssen eine Kontonummer eingeben");
                            }
                        }

                        KontostandMessage kontoMsg = new KontostandMessage(kontonummer, -1.0);
                        out.write(kontoMsg.serialize() + "\n");
                        out.flush();

                        /* Antwort vom Server mit Verarbeitung der Fälle */
                        Message kontoStandAnt = MessageCodec.decode(in.readLine());
                        
                        if (kontoStandAnt instanceof KontostandMessage) {
                            KontostandMessage info = (KontostandMessage) kontoStandAnt;
                            System.out.println("Kontostand: " + info.getKontostandBetrag() + " €");
                        } else if (kontoStandAnt instanceof ErrorMessage) {
                            System.out.println("Fehler: " +
                                ((ErrorMessage) kontoStandAnt).getFehlertext());
                        }
                        break;


                    case "abheben":
                        if (status != UserStatus.LOGGED_IN) {
                            System.out.println("Sie müssen sich zuerst einloggen!");
                            break;
                        }

                        /* Kontonumer eingabe mit Prüfung, ob der Wert falsch ist */
                        System.out.print("Bitte geben Sie Ihre Kontonummer ein: ");
                        String kontonumer = scanner.nextLine().trim();
                        while (kontonumer.isEmpty()) {
                            kontonumer = scanner.nextLine().trim();
                            if (kontonumer.isEmpty()) {
                                System.out.println("Sie müssen eine Kontonummer angeben!");
                            }
                        }

                        /* Betrags Eingabe + Prüfen ob der Betrag <= 0 ist oder überhaupt eine Zahl */
                        System.out.print("Wie viel wollen Sie abheben? Betrag: ");
                        double abhebeBetrag = -1;
                        while (abhebeBetrag <= 0) {
                            try {
                                abhebeBetrag = Double.parseDouble(scanner.nextLine());
                                if (abhebeBetrag <= 0) {
                                    System.out.println("Sie können nichts unter 0€ abheben!");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Bitte geben Sie einen Zahlenwert ein.");
                            }
                        }

                        AbhebenMessage abhebenMsg = new AbhebenMessage(kontonumer, abhebeBetrag);
                        out.write(abhebenMsg.serialize() + "\n");
                        out.flush();

                        /* Antwort vom Server + Verarbeitung der Fälle */
                        Message abhebenAnt1 = MessageCodec.decode(in.readLine());
                        
                        if (abhebenAnt1 instanceof ErrorMessage) {
                            System.out.println("Fehler: " +
                                ((ErrorMessage) abhebenAnt1).getFehlertext());
                            break;
                        }
                        if (abhebenAnt1 instanceof TextMessage) {
                            System.out.println(((TextMessage) abhebenAnt1).getContent());
                        }
                        if (in.ready()) {
                            Message abhebenAnt2 = MessageCodec.decode(in.readLine());
                            if (abhebenAnt2 instanceof TextMessage) {
                                System.out.println(((TextMessage) abhebenAnt2).getContent());
                            }
                        }
                        break;


                    case "text":
                        if (status != UserStatus.LOGGED_IN) {
                            System.out.println("Sie müssen sich zuerst einloggen!");
                            break;
                        }

                        System.out.print("Bitte geben Sie eine Nachricht ein: ");
                        String text = scanner.nextLine().trim();
                        while (text.isEmpty()) {
                            text = scanner.nextLine().trim();
                            if (text.isEmpty()) {
                                System.out.println("Bitte geben Sie etwas ein.");
                            }
                        }

                        TextMessage textMsg = new TextMessage(text);
                        out.write(textMsg.serialize() + "\n");
                        out.flush();

                        /* Antwort vom Server mit Verarbeitung */
                        Message textAnt = MessageCodec.decode(in.readLine());
                        
                        if (textAnt instanceof TextMessage) {
                            System.out.println("Server: " +
                                ((TextMessage) textAnt).getContent());
                        }
                        break;


                    case "exit":
                        System.out.println("Klient wird jetzt beendet.");
                        return;


                    default:
                        System.out.println("Nicht bekanntes Kommando.");
                    
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            new ATMClient("localhost", 5001).run();
        } catch (IOException e) {
            System.out.println("Fehler beim verbinden zum Server: " + e.getMessage());
        }
    }
}