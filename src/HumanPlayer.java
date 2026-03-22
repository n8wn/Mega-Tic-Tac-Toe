import java.util.Scanner;

public class HumanPlayer implements Player {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public Move getMove(Board board, int activeBoard) {
        int smallBoardIndex;

        if (activeBoard != -1) {
            // Player is forced into a specific board — no need to ask
            smallBoardIndex = activeBoard;
            int row = activeBoard / 3 + 1;
            int col = activeBoard % 3 + 1;
            System.out.println("You must play in board (" + row + ", " + col + ").");
        } else {
            System.out.println("Free choice — pick any open board.");
            System.out.print("Small board row (1-3): ");
            int bigRow = scanner.nextInt() - 1;
            System.out.print("Small board column (1-3): ");
            int bigCol = scanner.nextInt() - 1;
            smallBoardIndex = bigRow * 3 + bigCol;
        }

        System.out.print("Cell row (1-3): ");
        int smallRow = scanner.nextInt() - 1;
        System.out.print("Cell column (1-3): ");
        int smallCol = scanner.nextInt() - 1;
        int cellIndex = smallRow * 3 + smallCol;

        return new Move(board, smallBoardIndex, cellIndex);
    }
}