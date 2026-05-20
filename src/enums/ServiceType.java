package enums;

public enum ServiceType {
    SECURITY("security"),
    HEALTH("health"),
    EDUCATION("education");

    private final String label;

    ServiceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
