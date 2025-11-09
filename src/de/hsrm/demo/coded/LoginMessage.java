package de.hsrm.demo.coded;

/* Zweite Klasse bei der wir dierkt sagen,
 * dass die Message ist eine Login Anfrage
 * sein soll.
*/
public class LoginMessage extends Message {
    private final String username;
    private final String password;

    public LoginMessage(String username, String password) {
        super(MessageType.LOGIN);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String serialize() {
        return getType() + "|" + username + "|" + password;
    }

    public static LoginMessage deserialize(String data) {
        String[] parts = data.split("\\|", 3);
        return new LoginMessage(
                parts.length > 1 ? parts[1] : "",
                parts.length > 2 ? parts[2] : ""
        );
    }
}