package cell.zone;

import enums.ResourceType;
import enums.UtilityType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class Housing extends Zone {
    public Housing(int row, int col) {
        super(row, col);
    }

    @Override
    public char getSymbol() {
        return 'H';
    }

    @Override
    public Set<UtilityType> getRequiredUtilities() {
        return EnumSet.of(UtilityType.ELECTRICITY, UtilityType.WATER, UtilityType.INTERNET);
    }

    @Override
    public boolean consumesResource(ResourceType type) {
        return type == ResourceType.LIFESTYLE;
    }

    @Override
    protected int calculateTargetLevel() {
        int target = 1;
        if (hasAllServices()) {
            target = 2;
        }
        if (target >= 2 && getResourceAmount(ResourceType.LIFESTYLE) > 0) {
            target = 3;
        }
        return target;
    }

    @Override
    public Map<ResourceType, Integer> produce() {
        var output = emptyProduction();
        int amount = baseOutput();
        if (getLevel() == 3) {
            amount += getResourceAmount(ResourceType.LIFESTYLE);
        }
        output.put(ResourceType.POPULATION, amount);
        return output;
    }
}
