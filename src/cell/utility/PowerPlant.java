package cell.utility;

import enums.UtilityType;

public class PowerPlant extends UtilityProvider {
    public PowerPlant(int row, int col) {
        super(row, col);
    }

    @Override
    public char getSymbol() {
        return 'P';
    }

    @Override
    public UtilityType getUtilityType() {
        return UtilityType.ELECTRICITY;
    }
}
