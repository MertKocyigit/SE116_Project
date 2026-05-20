package enums;

public enum ResourceType {
    POPULATION("population"),
    GOODS("goods"),
    LIFESTYLE("lifestyle");

    private final String label;

    ResourceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
