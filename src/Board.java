// Board.java
import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<SmallBoard> board;

    public Board() {
        this.board = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            board.add(new SmallBoard());
        }
    }

    public List<SmallBoard> getBoard() {
        return board;
    }

    public SmallBoard getSmallBoard(int index) {
        return board.get(index);
    }
}