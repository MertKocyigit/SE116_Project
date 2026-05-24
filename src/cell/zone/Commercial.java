package cell.zone;

import enums.ResourceType;
import enums.ServiceType;
import enums.UtilityType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class Commercial extends Zone {
    public Commercial(int row, int col) {
        super(row, col);
    }

    @Override
    public char getSymbol() {
        return 'C';
    }

    @Override
    public Set<UtilityType> getRequiredUtilities() {
        return EnumSet.of(UtilityType.ELECTRICITY, UtilityType.WATER, UtilityType.INTERNET);
    }

    @Override
    public boolean consumesResource(ResourceType type) {
        return type == ResourceType.POPULATION || type == ResourceType.GOODS;
    }

    @Override
    protected int calculateTargetLevel() {
        int target = 1;
        if (hasService(ServiceType.SECURITY)) {
            target = 2;
        }
        if (target >= 2
                && getResourceAmount(ResourceType.POPULATION) > 0
                && getResourceAmount(ResourceType.GOODS) > 0) {
            target = 3;
        }
        return target;
    }

    @Override
    public Map<ResourceType, Integer> produce() {
        var output = emptyProduction();
        int amount = baseOutput();
        if (getLevel() == 3) {
            amount += Math.min(getResourceAmount(ResourceType.POPULATION), getResourceAmount(ResourceType.GOODS));
        }
        output.put(ResourceType.LIFESTYLE, amount);
        return output;
    }
}
