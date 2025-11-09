package de.hsrm.demo.coded;

/* Dritte Klasse bei der wir sagen,
 * Message ist für den Kontostand.
*/
public class KontostandMessage extends Message {
    private final String kontonummer;
    private final double betrag; // -1.0 = Anfrage, >= 0 = Antwort

    public KontostandMessage(String kontonummer, double betrag) {
        super(MessageType.KONTOSTAND);
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

    public static KontostandMessage deserialize(String data) {
        String[] parts = data.split("\\|", 3);
        String konto = parts.length > 1 ? parts[1] : "";
        double betrag = parts.length > 2 ? Double.parseDouble(parts[2]) : -1.0;
        return new KontostandMessage(konto, betrag);
    }
}