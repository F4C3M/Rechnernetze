package de.hsrm.demo.coded;

/* Fünfte Klasse bei der wir die
 * Error Messages "erstellen".
*/
public class ErrorMessage extends Message {
    private final String fehlertext;

    public ErrorMessage(String fehlertext) {
        super(MessageType.ERRORS);
        this.fehlertext = fehlertext;
    }

    public String getFehlertext() {
        return fehlertext;
    }

    @Override
    public String serialize() {
        return getType() + "|" + fehlertext;
    }

    public static ErrorMessage deserialize(String data) {
        String[] parts = data.split("\\|", 2);
        return new ErrorMessage(parts.length > 1 ? parts[1] : "");
    }
}