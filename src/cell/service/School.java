package cell.service;

import enums.ServiceType;

public class School extends ServiceProvider {
    public School(int row, int col) {
        super(row, col, 4);
    }

    @Override
    public char getSymbol() {
        return 'S';
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.EDUCATION;
    }
}
