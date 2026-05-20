package cell;

public class Road extends Cell {
    public Road(int row, int col) {
        super(row, col);
    }

    @Override
    public char getSymbol() {
        return 'R';
    }

    @Override
    public boolean isConnectable() {
        return true;
    }
}
