import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
    private Cell[][] cells; // cells[smallBoardIndex][cellIndex]
    private Map<Integer, String> boardStatus;  // cached per-board result: "X", "O", "DRAW", or ""
    private int activeBoard;  // -1 = free choice, 0-8 = forced board
    private Cell currentPlayer;
    private boolean isGameOver;
    private String gameResult; // "X", "O", "DRAW", or "" if still in play

    private static final int[][] LINES = {
            {0,1,2}, {3,4,5}, {6,7,8},
            {0,3,6}, {1,4,7}, {2,5,8},
            {0,4,8}, {2,4,6}
    };

    // construct from a live Board
    public State(Board board, int activeBoard, Cell currentPlayer) {
        this.cells = new Cell[9][9];
        this.boardStatus = new HashMap<>();
        this.activeBoard = activeBoard;
        this.currentPlayer = currentPlayer;
        this.isGameOver = false;
        this.gameResult = "";

        for (int i = 0; i < 9; i++) {
            SmallBoard sb = board.getSmallBoard(i);
            for (int j = 0; j < 9; j++) {
                cells[i][j] = sb.getCell(j);
            }
            boardStatus.put(i, computeResult(i)); // board status is a hash map of each board number and the computed restult of X O DRAW or still in play from that result
        }
        gameResult = computeGlobalResult();
        isGameOver = !gameResult.isEmpty();
    }

    // private constructor used by copy()
    private State() {}

    // deep copy used by MCTS to simulate without mutating the real state
    public State copy() {
        State s = new State();
        s.cells = new Cell[9][9];
        for (int i = 0; i < 9; i++) {
            s.cells[i] = this.cells[i].clone();
        }
        s.boardStatus = new HashMap<>(this.boardStatus);
        s.activeBoard = this.activeBoard;
        s.currentPlayer = this.currentPlayer;
        s.isGameOver = this.isGameOver;
        s.gameResult = this.gameResult;
        return s;
    }

    public void applyMove(int smallBoardIndex, int cellIndex) {
        cells[smallBoardIndex][cellIndex] = currentPlayer;

        // recompute only the affected board
        boardStatus.put(smallBoardIndex, computeResult(smallBoardIndex));

        // the cell just played determines which board the opponent is sent to
        String targetStatus = boardStatus.getOrDefault(cellIndex, "");
        activeBoard = targetStatus.isEmpty() ? cellIndex : -1; // active board is either the cell index or -1 depening on if it's empty or not

        // check if the global game is now over
        gameResult = computeGlobalResult();
        isGameOver = !gameResult.isEmpty();

        currentPlayer = (currentPlayer == Cell.X) ? Cell.O : Cell.X;
    }

    // returns all legal moves as int[]{smallBoardIndex, cellIndex}
    public List<int[]> getLegalMoves() {
        List<int[]> moves = new ArrayList<>();

        if (isGameOver) return moves;

        // which small boards are candidates?
        List<Integer> candidates = new ArrayList<>();
        if (activeBoard != -1) {
            candidates.add(activeBoard);
        } else {
            for (int i = 0; i < 9; i++) {
                if (boardStatus.getOrDefault(i, "").isEmpty()) {
                    candidates.add(i);
                }
            }
        }

        for (int sb : candidates) {
            for (int cell = 0; cell < 9; cell++) {
                if (cells[sb][cell] == Cell.EMPTY) {
                    moves.add(new int[]{sb, cell});
                }
            }
        }

        return moves;
    }

        //compute the result for one small board from the current cell array
    private String computeResult(int sbIndex) {
        Cell[] c = cells[sbIndex];

        for (int[] line : LINES) {
            Cell first = c[line[0]];
            if (first != Cell.EMPTY && first == c[line[1]] && first == c[line[2]]) {
                return first.toString();
            }
        }

        for (Cell cell : c) {
            if (cell == Cell.EMPTY) return "";
        }

        return "DRAW";
    }

    // calculates the global result from the 9 cached board statuses
    private String computeGlobalResult() {
        for (int[] line : LINES) {
            String first = boardStatus.getOrDefault(line[0], "");
            if (!first.isEmpty() && !first.equals("DRAW") &&
                    first.equals(boardStatus.getOrDefault(line[1], "")) &&
                    first.equals(boardStatus.getOrDefault(line[2], ""))) {
                return first;
            }
        }

        // draw if all 9 boards are decided
        for (int i = 0; i < 9; i++) {
            if (boardStatus.getOrDefault(i, "").isEmpty()) return "";
        }

        return "DRAW";
    }

    public Cell getCurrentPlayer()       { return currentPlayer; }
    public int getActiveBoard()          { return activeBoard; }
    public boolean isGameOver()          { return isGameOver; }
    public String getGameResult()        { return gameResult; }
    public String getBoardStatus(int i)  { return boardStatus.getOrDefault(i, ""); }
    public Cell getCell(int sb, int c)   { return cells[sb][c]; }
}