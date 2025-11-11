package de.hsrm.demo.coded;

/* Vierte Klasse dazu da, dass wir 
 * das "Abheben" simulieren können.
*/
public class AbhebenMessage extends Message {
    private final String kontonummer;
    private final double abhebeBetrag;

    public AbhebenMessage(String kontonummer, double abhebeBetrag) {
        super(MessageType.ABHEBEN);
        this.kontonummer = kontonummer;
        this.abhebeBetrag = abhebeBetrag;
    }

    public String getKontonummer() {
        return kontonummer;
    }

    public double getAbhebeBetrag() {
        return abhebeBetrag;
    }

    @Override
    public String serialize() {
        return getType() + "|" + kontonummer + "|" + abhebeBetrag;
    }

    public static AbhebenMessage deserialize(String data) {
        String[] parts = data.split("\\|", 3);
        String konto = parts.length > 1 ? parts[1] : "";
        double betrag = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;
        return new AbhebenMessage(konto, betrag);
    }
}