package cell.zone;

import enums.ResourceType;
import enums.ServiceType;
import enums.UtilityType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class Industrial extends Zone {
    public Industrial(int row, int col) {
        super(row, col);
    }

    @Override
    public char getSymbol() {
        return 'I';
    }

    @Override
    public Set<UtilityType> getRequiredUtilities() {
        return EnumSet.of(UtilityType.ELECTRICITY, UtilityType.WATER);
    }

    @Override
    public boolean consumesResource(ResourceType type) {
        return type == ResourceType.POPULATION;
    }

    @Override
    protected int calculateTargetLevel() {
        int target = 1;
        if (hasService(ServiceType.SECURITY)) {
            target = 2;
        }
        if (target >= 2 && getResourceAmount(ResourceType.POPULATION) > 0) {
            target = 3;
        }
        return target;
    }

    @Override
    public Map<ResourceType, Integer> produce() {
        var output = emptyProduction();
        int amount = baseOutput();
        if (getLevel() == 3) {
            amount += getResourceAmount(ResourceType.POPULATION);
        }
        output.put(ResourceType.GOODS, amount);
        return output;
    }
}
