import java.util.Scanner;

public class HumanPlayer implements Player {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public Move getMove(Board board) {
        System.out.println("\nEnter your move using row and column numbers (1-3):");

        System.out.print("Small board row (1-3): ");
        int bigRow = scanner.nextInt() - 1;
        System.out.print("Small board column (1-3): ");
        int bigCol = scanner.nextInt() - 1;
        int smallBoardIndex = bigRow * 3 + bigCol;

        System.out.print("Cell row (1-3): ");
        int smallRow = scanner.nextInt() - 1;
        System.out.print("Cell column (1-3): ");
        int smallCol = scanner.nextInt() - 1;
        int cellIndex = smallRow * 3 + smallCol;

        return new Move(board, smallBoardIndex, cellIndex);
    }
}