public class Move {
    private Board bigBoard;
    private SmallBoard smallBoard;
    private Cell cell;
    private int smallBoardIndex;
    private int cellIndex;

    Move(Board bigBoard, int smallBoardIndex, int cellIndex) {
        this.bigBoard = bigBoard;
        this.smallBoardIndex = smallBoardIndex;
        this.cellIndex = cellIndex;
        this.smallBoard = bigBoard.getSmallBoard(smallBoardIndex);
        this.cell = this.smallBoard.getCell(cellIndex);
    }

    public SmallBoard getsmallBoardMove() {
        return smallBoard;
    }

    public Cell getCellMove() {
        return cell;
    }

    public boolean isValidMove(Cell cell) {
        return cell == Cell.EMPTY;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public int getSmallBoardIndex() {
        return smallBoardIndex;
    }
}