import java.util.Scanner;

public class GameTester {

    private int iterations;
    private Scanner scanner = new Scanner(System.in);

    public GameTester(int iterations) {
        this.iterations = iterations;
    }

    // ---------------------------------------------------------------
    // Entry point — shows a menu of test modes
    // ---------------------------------------------------------------
    public void menu() {
        System.out.println("=== Ultimate TTT Tester ===");
        System.out.println("Iterations per move: " + iterations);
        System.out.println();
        System.out.println("1. Watch a single AI vs AI game");
        System.out.println("2. Run a batch of AI vs AI games and see results");
        System.out.println("3. Compare two AIs with different iteration counts");
        System.out.print("\nChoice: ");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> watchGame();
            case 2 -> {
                System.out.print("How many games? ");
                int n = scanner.nextInt();
                runBatch(n, iterations, iterations);
            }
            case 3 -> {
                System.out.print("Iterations for X: ");
                int x = scanner.nextInt();
                System.out.print("Iterations for O: ");
                int o = scanner.nextInt();
                System.out.print("How many games? ");
                int n = scanner.nextInt();
                runBatch(n, x, o);
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    // ---------------------------------------------------------------
    // Watch a single AI vs AI game, move by move
    // ---------------------------------------------------------------
    public void watchGame() {
        System.out.println("\n--- Watching AI vs AI game (" + iterations + " iterations) ---\n");

        Board board = new Board();
        AIPlayer playerX = new AIPlayer(Cell.X, iterations);
        AIPlayer playerO = new AIPlayer(Cell.O, iterations);
        int activeBoard = -1;
        Cell currentPlayer = Cell.X;
        int moveNumber = 0;

        Main.printBoard(board, currentPlayer, activeBoard);

        while (true) {
            AIPlayer current = (currentPlayer == Cell.X) ? playerX : playerO;

            long start = System.currentTimeMillis();
            Move move = current.getMove(board, activeBoard);
            long elapsed = System.currentTimeMillis() - start;

            moveNumber++;
            int sbRow = move.getSmallBoardIndex() / 3 + 1;
            int sbCol = move.getSmallBoardIndex() % 3 + 1;
            int cRow  = move.getCellIndex() / 3 + 1;
            int cCol  = move.getCellIndex() % 3 + 1;

            System.out.println("\nMove " + moveNumber + " — Player " + currentPlayer
                    + " plays board (" + sbRow + "," + sbCol + ") cell (" + cRow + "," + cCol + ")"
                    + "  [" + elapsed + "ms]");

            board.getSmallBoard(move.getSmallBoardIndex()).setCell(move.getCellIndex(), currentPlayer);

            String targetStatus = Main.getSmallBoardResult(board.getSmallBoard(move.getCellIndex()));
            activeBoard = targetStatus.isEmpty() ? move.getCellIndex() : -1;

            currentPlayer = (currentPlayer == Cell.X) ? Cell.O : Cell.X;
            Main.printBoard(board, currentPlayer, activeBoard);

            String result = Main.checkForWin(board);
            if (!result.isEmpty()) {
                printGameOver(result);
                return;
            }

            // Pause between moves so the output is readable
            System.out.println("Press Enter for next move...");
            scanner.nextLine();
            scanner.nextLine();
        }
    }

    // ---------------------------------------------------------------
    // Run a silent batch of games and print a results summary
    // ---------------------------------------------------------------
    public void runBatch(int numGames, int iterationsX, int iterationsO) {
        int xWins = 0, oWins = 0, draws = 0;
        long totalTime = 0;

        System.out.println("\n--- Batch: " + numGames + " games  |  X=" + iterationsX
                + " itr  O=" + iterationsO + " itr ---\n");

        for (int i = 0; i < numGames; i++) {
            long start = System.currentTimeMillis();
            String result = playSilentGame(iterationsX, iterationsO);
            long elapsed = System.currentTimeMillis() - start;
            totalTime += elapsed;

            switch (result) {
                case "X"    -> xWins++;
                case "O"    -> oWins++;
                default     -> draws++;
            }

            System.out.println("Game " + pad(i + 1, numGames) + ": " + result
                    + "  (" + elapsed + "ms)");
        }

        System.out.println();
        System.out.println("--- Summary ---");
        System.out.println("X wins : " + xWins + " (" + pct(xWins, numGames) + "%)");
        System.out.println("O wins : " + oWins + " (" + pct(oWins, numGames) + "%)");
        System.out.println("Draws  : " + draws  + " (" + pct(draws,  numGames) + "%)");
        System.out.println("Avg game time: " + (totalTime / numGames) + "ms");
    }

    // ---------------------------------------------------------------
    // Internal: play a game silently and return the result
    // ---------------------------------------------------------------
    private String playSilentGame(int iterationsX, int iterationsO) {
        Board board = new Board();
        AIPlayer playerX = new AIPlayer(Cell.X, iterationsX);
        AIPlayer playerO = new AIPlayer(Cell.O, iterationsO);
        int activeBoard = -1;
        Cell currentPlayer = Cell.X;

        while (true) {
            AIPlayer current = (currentPlayer == Cell.X) ? playerX : playerO;
            Move move = current.getMove(board, activeBoard);

            board.getSmallBoard(move.getSmallBoardIndex()).setCell(move.getCellIndex(), currentPlayer);

            String targetStatus = Main.getSmallBoardResult(board.getSmallBoard(move.getCellIndex()));
            activeBoard = targetStatus.isEmpty() ? move.getCellIndex() : -1;

            String result = Main.checkForWin(board);
            if (!result.isEmpty()) return result;

            currentPlayer = (currentPlayer == Cell.X) ? Cell.O : Cell.X;
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------
    private void printGameOver(String result) {
        System.out.println();
        if (result.equals("DRAW")) {
            System.out.println("=== DRAW ===");
        } else {
            System.out.println("=== Player " + result + " wins! ===");
        }
    }

    private String pct(int count, int total) {
        return String.format("%.1f", (double) count / total * 100);
    }

    // Pads the game number so columns line up, e.g. " 1" vs "10"
    private String pad(int n, int total) {
        int width = String.valueOf(total).length();
        return String.format("%" + width + "d", n);
    }

    // ---------------------------------------------------------------
    // Run directly
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        new GameTester(10000).menu();
    }
}