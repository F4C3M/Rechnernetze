package de.hsrm.demo.coded;

public class MessageCodec {

    public static String encode(String msg) {
        if (msg == null) {
            return "[ENC][/ENC]";
        }
        return "[ENC]" + msg + "[/ENC]";
    }

    public static String decode(String encoded) {
        if (encoded == null) {
            return null;
        }
        if (encoded.startsWith("[ENC]") && encoded.endsWith("[/ENC]")) {
            return encoded.substring(5, encoded.length() - 6);
        }
        return encoded;
    }
}

// Ich schreib das nur um was zu testen ihr Stinka