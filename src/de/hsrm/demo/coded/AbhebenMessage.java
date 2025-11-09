package de.hsrm.demo.coded;

/* Vierte Klasse ... für die Message
 * zum Abheben.
*/
public class AbhebenMessage extends Message {
    private final String kontonummer;
    private final double betrag;

    public AbhebenMessage(String kontonummer, double betrag) {
        super(MessageType.ABHEBEN);
        this.kontonummer = kontonummer;
        this.betrag = betrag;
    }

    public String getKontonummer() {
        return kontonummer;
    }

    public double getBetrag() {
        return betrag;
    }

    @Override
    public String serialize() {
        return getType() + "|" + kontonummer + "|" + betrag;
    }

    public static AbhebenMessage deserialize(String data) {
        String[] parts = data.split("\\|", 3);
        String konto = parts.length > 1 ? parts[1] : "";
        double betrag = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;
        return new AbhebenMessage(konto, betrag);
    }
}