package enums;

public enum UtilityType {
    ELECTRICITY("electricity"),
    WATER("water"),
    INTERNET("internet");

    private final String label;

    UtilityType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
