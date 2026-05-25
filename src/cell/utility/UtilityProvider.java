package cell.utility;

import cell.Cell;
import enums.UtilityType;

public abstract class UtilityProvider extends Cell {
    private static final int CAPACITY = 100;

    protected UtilityProvider(int row, int col) {
        super(row, col);
    }

    public int getCapacity() {
        return CAPACITY;
    }

    public abstract UtilityType getUtilityType();

    @Override
    public boolean isConnectable() {
        return false;
    }
}
