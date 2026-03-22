import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Board board = new Board();
        //Player playerX = new HumanPlayer();
        Player playerO = new HumanPlayer();
        Player playerX = new AIPlayer(Cell.X, 1000);
        Cell currentPlayer = (new Random().nextInt(2) == 0) ? Cell.X : Cell.O;

        // -1 means free choice and 0-8 means the player is forced
        int activeBoard = -1;

        printBoard(board, currentPlayer, activeBoard);

        String result;
        while ((result = checkForWin(board)).isEmpty()) {

            if (playerX instanceof AIPlayer && playerO instanceof AIPlayer) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            Player current = (currentPlayer == Cell.X) ? playerX : playerO;

            Move move;
            try {
                move = current.getMove(board, activeBoard);
            } catch (Exception e) {
                System.out.println("Invalid input, try again.");
                continue;
            }

            if (!checkValidMove(move, board, activeBoard)) {
                System.out.println("Invalid move, try again.");
                continue;
            }

            board.getSmallBoard(move.getSmallBoardIndex()).setCell(move.getCellIndex(), currentPlayer);

            // the cell index just played chooses which board the opponent is sent to
            int nextBoard = move.getCellIndex();
            String targetResult = getSmallBoardResult(board.getSmallBoard(nextBoard));
            // if that board is already won or ful the other guy gets free choice
            activeBoard = targetResult.isEmpty() ? nextBoard : -1;

            currentPlayer = (currentPlayer == Cell.X) ? Cell.O : Cell.X;
            printBoard(board, currentPlayer, activeBoard);
        }

        if (result.equals("DRAW")) {
            System.out.println("It's a draw!");
        } else {
            System.out.println("Player " + result + " wins!");
        }
    }

    static boolean checkValidMove(Move move, Board board, int activeBoard) {
        int sbIndex = move.getSmallBoardIndex();
        int cellIndex = move.getCellIndex();

        // check the bounds of the input
        if (sbIndex < 0 || sbIndex > 8 || cellIndex < 0 || cellIndex > 8) return false;

        // must play in the forced board if one is set
        if (activeBoard != -1 && sbIndex != activeBoard) return false;

        // can't play in a board thats already been won or drawn.
        if (!getSmallBoardResult(board.getSmallBoard(sbIndex)).isEmpty()) return false;

        // the cell must be empty
        return move.getCellMove() == Cell.EMPTY;
    }

    // this should return X O DRAW, or "" for still in play
    static String checkForWin(Board board) {
        int[][] lines = {
                {0,1,2}, {3,4,5}, {6,7,8},
                {0,3,6}, {1,4,7}, {2,5,8},
                {0,4,8}, {2,4,6}
        };

        String[] results = new String[9];
        boolean allDecided = true;
        for (int i = 0; i < 9; i++) {
            results[i] = getSmallBoardResult(board.getSmallBoard(i));
            if (results[i].isEmpty()) allDecided = false;
        }

        for (int[] line : lines) {
            String first = results[line[0]];
            if (!first.isEmpty() && !first.equals("DRAW") &&
                    first.equals(results[line[1]]) &&
                    first.equals(results[line[2]])) {
                return first;
            }
        }

        if (allDecided) return "DRAW";
        return "";
    }

    // this should return X O DRAW, or "" for still in play
    static String getSmallBoardResult(SmallBoard smallBoard) {
        int[][] lines = {
                {0,1,2}, {3,4,5}, {6,7,8},
                {0,3,6}, {1,4,7}, {2,5,8},
                {0,4,8}, {2,4,6}
        };

        for (int[] line : lines) {
            Cell first = smallBoard.getCell(line[0]);
            if (first != Cell.EMPTY &&
                    first == smallBoard.getCell(line[1]) &&
                    first == smallBoard.getCell(line[2])) {
                return first.toString();
            }
        }

        if (smallBoard.isFull()) return "DRAW";
        return "";
    }

    static void printBoard(Board board, Cell current, int activeBoard) {
        System.out.println("\nCurrent player: " + current);
        if (activeBoard == -1) {
            System.out.println("Free choice — play in any open board.");
        } else {
            int row = activeBoard / 3 + 1;
            int col = activeBoard % 3 + 1;
            System.out.println("Must play in board (" + row + ", " + col + ").");
        }
        System.out.println();

        for (int bigRow = 0; bigRow < 3; bigRow++) {
            for (int smallRow = 0; smallRow < 3; smallRow++) {
                for (int bigCol = 0; bigCol < 3; bigCol++) {
                    int smallBoardIndex = bigRow * 3 + bigCol;
                    SmallBoard smallBoard = board.getSmallBoard(smallBoardIndex);
                    String boardResult = getSmallBoardResult(smallBoard);

                    for (int smallCol = 0; smallCol < 3; smallCol++) {
                        int cellIndex = smallRow * 3 + smallCol;
                        char symbol;

                        if (!boardResult.isEmpty() && !boardResult.equals("DRAW")) {
                            // Won board: fill every cell with the winner's symbol
                            symbol = boardResult.charAt(0);
                        } else {
                            Cell cell = smallBoard.getCell(cellIndex);
                            symbol = switch (cell) {
                                case X -> 'X';
                                case O -> 'O';
                                // full boards show '#' for empty cells so they look visually closed
                                case EMPTY -> boardResult.equals("DRAW") ? '#' : '.';
                            };
                        }

                        System.out.print(symbol);
                        if (smallCol < 2) System.out.print(" ");
                    }

                    if (bigCol < 2) System.out.print(" | ");
                }
                System.out.println();
            }

            if (bigRow < 2) System.out.println("------+-------+------");
        }
    }
}