package cell.zone;

import cell.Cell;
import enums.ResourceType;
import enums.ServiceType;
import enums.UtilityType;
import interfaces.Producible;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Zone extends Cell implements Producible {
    private int level;
    private int demand;
    private final EnumMap<UtilityType, Integer> utilitiesReceived;
    private final EnumSet<ServiceType> servicesReceived;
    private final EnumMap<ResourceType, Integer> resourcesReceived;
    private final EnumMap<ResourceType, Integer> lastOutput;

    protected Zone(int row, int col) {
        super(row, col);
        this.level = 0;
        this.demand = 1;
        this.utilitiesReceived = new EnumMap<>(UtilityType.class);
        this.servicesReceived = EnumSet.noneOf(ServiceType.class);
        this.resourcesReceived = new EnumMap<>(ResourceType.class);
        this.lastOutput = new EnumMap<>(ResourceType.class);
        resetMaps();
    }

    private void resetMaps() {
        for (UtilityType type : UtilityType.values()) {
            utilitiesReceived.put(type, 0);
        }
        for (ResourceType type : ResourceType.values()) {
            resourcesReceived.put(type, 0);
            lastOutput.put(type, 0);
        }
    }

    public int getLevel() {
        return level;
    }

    protected void setLevel(int level) {
        this.level = Math.max(0, Math.min(3, level));
    }

    public int getDemand() {
        return Math.max(1, demand);
    }

    public void resetTickData() {
        for (UtilityType type : UtilityType.values()) {
            utilitiesReceived.put(type, 0);
        }
        servicesReceived.clear();
        for (ResourceType type : ResourceType.values()) {
            resourcesReceived.put(type, 0);
        }
    }

    public void receiveUtility(UtilityType type, int amount) {
        if (amount > 0) {
            utilitiesReceived.put(type, utilitiesReceived.get(type) + amount);
        }
    }

    public void receiveService(ServiceType type) {
        servicesReceived.add(type);
    }

    public void receiveResource(ResourceType type, int amount) {
        if (amount > 0) {
            resourcesReceived.put(type, resourcesReceived.get(type) + amount);
        }
    }

    public int getUtilityAmount(UtilityType type) {
        return utilitiesReceived.get(type);
    }

    public int getResourceAmount(ResourceType type) {
        return resourcesReceived.get(type);
    }

    public boolean hasService(ServiceType type) {
        return servicesReceived.contains(type);
    }

    public boolean hasAllServices() {
        return servicesReceived.contains(ServiceType.SECURITY)
                && servicesReceived.contains(ServiceType.HEALTH)
                && servicesReceived.contains(ServiceType.EDUCATION);
    }

    public int getMinimumRequiredUtility() {
        int minimum = Integer.MAX_VALUE;
        for (UtilityType type : getRequiredUtilities()) {
            minimum = Math.min(minimum, getUtilityAmount(type));
        }
        return minimum == Integer.MAX_VALUE ? 0 : minimum;
    }

    public boolean hasRequiredUtilities() {
        for (UtilityType type : getRequiredUtilities()) {
            if (getUtilityAmount(type) <= 0) {
                return false;
            }
        }
        return true;
    }

    public void update() {
        if (!hasRequiredUtilities()) {
            setLevel(0);
        } else {
            int target = calculateTargetLevel();
            if (target > level) {
                setLevel(level + 1);
            } else if (target < level) {
                setLevel(level - 1);
            }
        }
        Map<ResourceType, Integer> produced = produce();
        for (ResourceType type : ResourceType.values()) {
            lastOutput.put(type, produced.getOrDefault(type, 0));
        }
        int totalOutput = produced.values().stream().mapToInt(Integer::intValue).sum();
        demand = Math.max(1, totalOutput);
    }

    protected EnumMap<ResourceType, Integer> emptyProduction() {
        EnumMap<ResourceType, Integer> output = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            output.put(type, 0);
        }
        return output;
    }

    protected int baseOutput() {
        int m = getMinimumRequiredUtility();
        if (getLevel() <= 0 || m <= 0) {
            return 0;
        }
        if (getLevel() == 1) {
            return m;
        }
        return 2 * m;
    }

    @Override
    public int getOutput(ResourceType type) {
        return lastOutput.get(type);
    }

    public Map<ResourceType, Integer> getLastOutput() {
        return Map.copyOf(lastOutput);
    }

    public String servicesText() {
        if (servicesReceived.isEmpty()) {
            return "none";
        }
        return servicesReceived.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    public String resourcesText() {
        return "population=" + getResourceAmount(ResourceType.POPULATION)
                + ", goods=" + getResourceAmount(ResourceType.GOODS)
                + ", lifestyle=" + getResourceAmount(ResourceType.LIFESTYLE);
    }

    public String utilitiesText() {
        return "electricity=" + getUtilityAmount(UtilityType.ELECTRICITY)
                + ", water=" + getUtilityAmount(UtilityType.WATER)
                + ", internet=" + getUtilityAmount(UtilityType.INTERNET);
    }

    @Override
    public boolean isConnectable() {
        return true;
    }

    @Override
    public String getDisplaySymbol() {
        return getSymbol() + Integer.toString(level);
    }

    public abstract Set<UtilityType> getRequiredUtilities();
    public abstract boolean consumesResource(ResourceType type);
    protected abstract int calculateTargetLevel();
}
