package de.hsrm.demo.coded;

/* Zweite Klasse bei der wir dierkt sagen,
 * dass die Message ist eine Login Anfrage
 * sein soll.
*/
public class LoginMessage extends Message {
    private final String username;
    private final String kontoPin;

    public LoginMessage(String username, String kontoPin) {
        super(MessageType.LOGIN);
        this.username = username;
        this.kontoPin = kontoPin;
    }

    public String getUsername() {
        return username;
    }

    public String getKontoPin() {
        return kontoPin;
    }

    @Override
    public String serialize() {
        return getType() + "|" + username + "|" + kontoPin;
    }

    public static LoginMessage deserialize(String data) {
        String[] parts = data.split("\\|", 3);
        return new LoginMessage(
                parts.length > 1 ? parts[1] : "",
                parts.length > 2 ? parts[2] : ""
        );
    }
}