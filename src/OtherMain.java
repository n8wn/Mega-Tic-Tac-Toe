import java.util.Scanner;

public class OtherMain {
    static final String[] KEYS = {"q","w","e","a","s","d","z","x","c"};

    public static void main(String[] args) {
        String[] board = {"-","-","-","-","-","-","-","-","-"};
        boolean player1Turn = true;
        int turnCount = 0;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Tic Tac Toe:");
        System.out.println("Controls: q w e / a s d / z x c");
        printBoard(board);

        while (true) {
            System.out.println("Player " + (player1Turn ? "1 (X)" : "2 (O)") + "'s turn:");
            String input = scanner.nextLine().trim();
            int index = getIndex(input);

            if (index == -1) {
                System.out.println("Invalid input, try again.");
                continue;
            }
            if (!board[index].equals("-")) {
                System.out.println("Cell already taken, try again.");
                continue;
            }

            board[index] = player1Turn ? "X" : "O";
            player1Turn = !player1Turn;
            turnCount++;
            printBoard(board);

            String winner = checkForWin(board);
            if (!winner.isEmpty()) {
                System.out.println(winner + "'s Win!");
                break;
            }
            if (turnCount == 9) {
                System.out.println("Tie.");
                break;
            }
        }

        scanner.close();
    }

    static int getIndex(String input) {
        for (int i = 0; i < KEYS.length; i++) {
            if (KEYS[i].equals(input)) return i;
        }
        return -1;
    }

    static void printBoard(String[] board) {
        for (int i = 0; i < 3; i++) {
            System.out.println(board[i*3] + " | " + board[i*3+1] + " | " + board[i*3+2]);
        }
        System.out.println();
    }

    static String checkForWin(String[] b) {
        int[][] lines = {
                {0,1,2},{3,4,5},{6,7,8}, // rows
                {0,3,6},{1,4,7},{2,5,8}, // columns
                {0,4,8},{2,4,6}          // diagonals
        };
        for (int[] line : lines) {
            String s = b[line[0]];
            if (!s.equals("-") && s.equals(b[line[1]]) && s.equals(b[line[2]])) {
                return s;
            }
        }
        return "";
    }
}