package cell;

import interfaces.Connectable;

public abstract class Cell implements Connectable {
    private final int row;
    private final int col;

    protected Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public abstract char getSymbol();

    @Override
    public boolean isConnectable() {
        return false;
    }

    public String getDisplaySymbol() {
        return Character.toString(getSymbol());
    }
}
