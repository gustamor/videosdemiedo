package net.laenredadera.full.peliculas.gratis.de.terror.Models;

public class UpdateTextEvent {
    private final String sampleTextValue;

    public UpdateTextEvent(String textValue) {
        this.sampleTextValue = textValue;
    }
    public UpdateTextEvent(String textValue, String textCode) {
        this.sampleTextValue = textValue;
    }
    public String getTextValue() {
        return sampleTextValue;
    }
    // --Commented out by Inspection (25/10/21 2:35):public String getCodeValue() { return codeTextValue; }
}
