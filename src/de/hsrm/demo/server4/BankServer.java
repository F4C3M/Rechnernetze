package de.hsrm.demo.server4;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import de.hsrm.demo.coded.*;


public class BankServer {
    private final int port;

    public BankServer(int port) {
        this.port = port;
    }
    
    static class UserAccount {
        String username;
        String kontoPin;
        String kontonummer;
        double kontostand;
        UserStatus status;

        UserAccount(String username, String kontoPin, String kontonummer, double kontostand) {
            this.username = username;
            this.kontoPin = kontoPin;
            this.kontonummer = kontonummer;
            this.kontostand = kontostand;
            this.status = UserStatus.LOGGED_OUT;
        }
    }


    private final Map<String, UserAccount> users = new HashMap<>();

    private void initUsers() {
        users.put("Dennel", new UserAccount("Dennel", "1277", "722_586", 218.0));
        users.put("Jamamoto", new UserAccount("Jamamoto", "1706", "234_886", 25370.43));
    }
    /* Normalerweise sollten die Keys einer HashMap eindeutig sein, so wie "user1", aber das ist nur zum testen jetzt so */

    public void start() throws IOException {
        initUsers();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Der Bankserver läuft auf dem Port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Klient wird verbunden: " + clientSocket.getInetAddress());

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    UserAccount currentUser = null;

                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Empfangen vom Client: " + line);    
                        Message msg = MessageCodec.decode(line);

                        switch (msg.getType()) {
                            case LOGIN:
                                LoginMessage loginMsg = (LoginMessage) msg;
                                currentUser = users.get(loginMsg.getUsername());

                                if (currentUser == null || !currentUser.kontoPin.equals(loginMsg.getKontoPin())) {
                                    out.write(new ErrorMessage("Login ist fehlgeschlagen. PIN falsch").serialize() + "\n");
                                    out.flush();
                                    currentUser = null;
                                } else if (currentUser.status == UserStatus.BLOCKED) {
                                    out.write(new ErrorMessage("Account ist gesperrt").serialize() + "\n");
                                    out.flush();
                                    currentUser = null;
                                } else {
                                    currentUser.status = UserStatus.LOGGED_IN;
                                    out.write(new TextMessage("OK").serialize() + "\n");
                                    out.flush();
                                }
                                break;

                            case KONTOSTAND:
                                if (currentUser == null || currentUser.status != UserStatus.LOGGED_IN) {
                                    out.write(new ErrorMessage("Sie müssen sich einloggen.").serialize() + "\n");
                                    out.flush();
                                    break;
                                }
                                KontostandMessage kontoMsg = (KontostandMessage) msg;
                                if (!currentUser.kontonummer.equals(kontoMsg.getKontonummer())) {
                                    out.write(new ErrorMessage("Falsche Kontonummer für diesen Kontoinhaber.").serialize() + "\n");
                                    out.flush();
                                    break;
                                }
                                out.write(new KontostandMessage(kontoMsg.getKontonummer(), currentUser.kontostand).serialize() + "\n");
                                out.flush();
                                break;

                            case ABHEBEN:
                                if (currentUser == null || currentUser.status != UserStatus.LOGGED_IN) {
                                    out.write(new ErrorMessage("Sie müssen sich einloggen.").serialize() + "\n");
                                    out.flush();
                                    break;
                                }
                                AbhebenMessage abhebenMsg = (AbhebenMessage) msg;
                                 if (!currentUser.kontonummer.equals(abhebenMsg.getKontonummer())) {
                                    out.write(new ErrorMessage("Falsche Kontonummer für diesen Kontoinhaber.").serialize() + "\n");
                                    out.flush();
                                    break;
                                }
                                double betrag = abhebenMsg.getAbhebeBetrag();
                                if (betrag > currentUser.kontostand) {
                                    out.write(new ErrorMessage("Sie haben zu wenig Guthaben").serialize() + "\n");
                                    out.flush();
                                } else {
                                    currentUser.kontostand -= betrag;
                                    out.write(new TextMessage("Bitte entnehmen Sie Ihr Geld.").serialize() + "\n");
                                    out.flush();
                                    out.write(new TextMessage("Abhebung: " + betrag).serialize() + "\n");
                                    out.flush();
                                }
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