package cell.utility;

import enums.UtilityType;

public class InternetHub extends UtilityProvider {
    public InternetHub(int row, int col) {
        super(row, col);
    }

    @Override
    public char getSymbol() {
        return 'T';
    }

    @Override
    public UtilityType getUtilityType() {
        return UtilityType.INTERNET;
    }
}
