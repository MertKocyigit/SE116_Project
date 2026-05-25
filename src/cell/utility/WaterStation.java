package cell.utility;

import enums.UtilityType;

public class WaterStation extends UtilityProvider {
    public WaterStation(int row, int col) {
        super(row, col);
    }

    @Override
    public char getSymbol() {
        return 'W';
    }

    @Override
    public UtilityType getUtilityType() {
        return UtilityType.WATER;
    }
}
