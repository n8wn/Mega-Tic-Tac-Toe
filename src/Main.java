import java.util.Objects;
import java.util.Scanner;
import java.util.Random;

public class Main {


    public static void main(String[] args) {
        Board board = new Board();
        Player playerX = new HumanPlayer();
        //Player playerO = new AIPlayer();
        Player playerO = new HumanPlayer();
        //Player playerX = new AIPlayer();
        Cell currentPlayer = (new Random().nextInt(2) == 0) ? Cell.X : Cell.O;

        printBoard(board, currentPlayer);

        while (checkForWin(board).isEmpty()) {

            if (playerX instanceof AIPlayer && playerO instanceof AIPlayer) {
                try {
                    Thread.sleep(500); // half second delay between AI moves
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            Player current = (currentPlayer == Cell.X) ? playerX : playerO;
            Move move = current.getMove(board);

            if (!checkValidMove(move, board)) {
                System.out.println("Invalid move, try again.");
                continue;
            }


            move.getsmallBoardMove().setCell(move.getCellIndex(), currentPlayer);

            printBoard(board, currentPlayer);

            currentPlayer = (currentPlayer == Cell.X) ? Cell.O : Cell.X;
        }

        System.out.println("Player " + checkForWin(board) + " wins!");
    }

    static boolean checkValidMove(Move move, Board board) {
        return move.isValidMove(move.getCellMove());
    }

    static String checkForWin(Board board) {
        int[][] lines = {
                {0,1,2},{3,4,5},{6,7,8}, // rows
                {0,3,6},{1,4,7},{2,5,8}, // columns
                {0,4,8},{2,4,6}          // diagonals
        };

        //
        String[] results = new String[9];
        for (int i = 0; i < 9; i++) {
            results[i] = checkForSmallBoardWin(board.getSmallBoard(i));
        }

        for (int[] line : lines) {
            String first = results[line[0]];
            if (!first.isEmpty() &&
                    first.equals(results[line[1]]) &&
                    first.equals(results[line[2]])) {
                return first;
            }
        }
        return "";
    }

    static String checkForSmallBoardWin(SmallBoard smallBoard) {

        int[][] lines = {
                {0,1,2},{3,4,5},{6,7,8}, // rows
                {0,3,6},{1,4,7},{2,5,8}, // columns
                {0,4,8},{2,4,6}          // diagonals
        };

        for (int[] line : lines) {
            if (smallBoard.getCell(line[0]) != Cell.EMPTY && smallBoard.getCell(line[0]) == smallBoard.getCell(line[1]) && smallBoard.getCell(line[0]) == smallBoard.getCell(line[2])) {
                return smallBoard.getCell(line[0]).toString();
            }
        }
        return "";
    }

    static void printBoard(Board board, Cell current) {
        System.out.println("Current Player is " + current+"'s.");

        // iterate over the 3 rows of large board
        for (int bigRow = 0; bigRow < 3; bigRow++) {

            // iterate over the 3 rows within each small board
            for (int smallRow = 0; smallRow < 3; smallRow++) {

                // iterate over the 3 small boards in this big row
                for (int bigCol = 0; bigCol < 3; bigCol++) {
                    int smallBoardIndex = bigRow * 3 + bigCol;
                    SmallBoard smallBoard = board.getSmallBoard(smallBoardIndex);

                    // print the 3 cells in this small row
                    for (int smallCol = 0; smallCol < 3; smallCol++) {
                        int cellIndex = smallRow * 3 + smallCol;
                        Cell cell = smallBoard.getCell(cellIndex);

                        char symbol = switch (cell) {
                            case X -> 'X';
                            case O -> 'O';
                            case EMPTY -> '.';
                        };
                        System.out.print(symbol);
                        if (smallCol < 2) System.out.print(" ");
                    }

                    // separate small boards horizontally with a divider
                    if (bigCol < 2) System.out.print(" | ");
                }
                System.out.println();
            }

            // separate small board rows vertically with a divider
            if (bigRow < 2) System.out.println("------+-------+------");
        }
    }
}