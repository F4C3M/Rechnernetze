package de.hsrm.demo.coded;

/* Dritte Klasse bei der wir sagen,
 * Message ist für den Kontostand.
*/
public class KontostandMessage extends Message {
    private final String kontonummer;
    private final double kontostandBetrag;

    public KontostandMessage(String kontonummer, double kontostandBetrag) {
        super(MessageType.KONTOSTAND);
        this.kontonummer = kontonummer;
        this.kontostandBetrag = kontostandBetrag;
    }

    public String getKontonummer() {
        return kontonummer;
    }

    public double getKontostandBetrag() {
        return kontostandBetrag;
    }

    @Override
    public String serialize() {
        return getType() + "|" + kontonummer + "|" + kontostandBetrag;
    }

    public static KontostandMessage deserialize(String data) {
        String[] parts = data.split("\\|", 3);
        String konto = parts.length > 1 ? parts[1] : "";
        double betrag = parts.length > 2 ? Double.parseDouble(parts[2]) : -1.0;
        return new KontostandMessage(konto, betrag);
    }
}