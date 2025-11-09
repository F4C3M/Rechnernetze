package de.hsrm.demo.coded;

/* Erste Klasse bei der wir dierkt sagen,
 * die Message ist ein einfacher Text.
*/
public class TextMessage extends Message {
    private final String content;

    public TextMessage(String content) {
        super(MessageType.TEXTITEXT);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String serialize() {
        return getType() + "|" + content;
    }

    public static TextMessage deserialize(String data) {
        String[] parts = data.split("\\|", 2);
        return new TextMessage(parts.length > 1 ? parts[1] : "");
    }
}