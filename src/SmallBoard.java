import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmallBoard {
    private List<Cell> cells;

    public SmallBoard() {
        this.cells = new ArrayList<>(Collections.nCopies(9, Cell.EMPTY));
    }

    public List<Cell> getCells() {
        return cells;
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }

    public void setCell(int index, Cell value) {
        cells.set(index, value);
    }

    public boolean isFull() {
        return cells.stream().noneMatch(c -> c == Cell.EMPTY);
    }
}