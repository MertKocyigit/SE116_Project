package cell.service;

import cell.Cell;
import enums.ServiceType;

public abstract class ServiceProvider extends Cell {
    private final int radius;

    protected ServiceProvider(int row, int col, int radius) {
        super(row, col);
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public abstract ServiceType getServiceType();
}
