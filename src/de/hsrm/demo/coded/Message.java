package de.hsrm.demo.coded;
import java.io.Serializable;

/* Die Message Klasse mit der NUR definieren WIE unsere
 * Nachrichten / Messages aufgebaut sein sollen.
*/
public abstract class Message implements Serializable{
    private final MessageType type;

    protected Message(MessageType type) {
        this.type = type;
    }

    /* Getter für unseren EnumType*/
    public MessageType getType() {
        return type;
    }

    /* Methode damti zum selbst defini. der Umwandlung*/
    public abstract String serialize();
}