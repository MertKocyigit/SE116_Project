package cell.service;

import enums.ServiceType;

public class Hospital extends ServiceProvider {
    public Hospital(int row, int col) {
        super(row, col, 3);
    }

    @Override
    public char getSymbol() {
        return 'D';
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.HEALTH;
    }
}
