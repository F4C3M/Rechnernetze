package de.hsrm.demo.coded;
public class MessageCodec {

    /* Methode zum "encoden" einer Message */
    public static String encode(Message msg) {
        if (msg == null) {
            return "[ENC][/ENC]";
        }
        return "[ENC]" + msg.serialize() + "[/ENC]";
    }


    /* Methode zum "decoden" einer Message */
    public static Message decode(String encoded) {
        if (encoded == null || !encoded.startsWith("[ENC]") || !encoded.endsWith("[/ENC]")) {
            return null;
        }

        String content = encoded.substring(5, encoded.length() - 6);
        String[] parts = content.split("\\|", 2);
        MessageType type = MessageType.valueOf(parts[0]);

        switch (type) {
            case TEXTITEXT:
                return TextMessage.deserialize(content);
            case LOGIN:
                return LoginMessage.deserialize(content);
            case KONTOSTAND:
                return KontostandMessage.deserialize(content);
            case ABHEBEN:
                return AbhebenMessage.deserialize(content);
            case ERRORS:
                return ErrorMessage.deserialize(content);
            default:
                throw new IllegalArgumentException("Unbekannter Nachrichtentyp: " + type);
        }
    }
}