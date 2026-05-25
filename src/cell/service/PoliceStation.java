package cell.service;

import enums.ServiceType;

public class PoliceStation extends ServiceProvider {
    public PoliceStation(int row, int col) {
        super(row, col, 5);
    }

    @Override
    public char getSymbol() {
        return 'F';
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.SECURITY;
    }
}
