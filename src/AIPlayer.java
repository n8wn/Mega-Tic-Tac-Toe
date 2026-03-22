import java.util.List;
import java.util.Random;

public class AIPlayer implements Player {
    private Cell cell;
    private double c = Math.sqrt(2);
    private int numOfIterations;
    private Random random = new Random();

    public AIPlayer(Cell cell, int numOfIterations) {
        this.cell = cell;
        this.numOfIterations = numOfIterations;
    }

    @Override
    public Move getMove(Board board, int activeBoard) {
        State state = new State(board, activeBoard, cell);
        MCTSNode root = new MCTSNode(state, null, null);

        for (int i = 0; i < numOfIterations; i++) {
            MCTSNode node = root;
            while (node.isFullyExpanded() && !node.isTerminal()) {
                node = node.selectChild(c);
            }

            if (!node.isTerminal()) {
                node = node.expand();
            }

            String result = rollout(node.getState());

            node.backpropagate(result, cell);
        }

        Move move = new Move(board, root.bestChild().getMove()[0], root.bestChild().getMove()[1]);
        return move;
    }

    private String rollout(State state) {
        State copy = state.copy();
        while (!copy.isGameOver()) {
            List<int[]> moves = copy.getLegalMoves();
            int[] move = moves.get(random.nextInt(moves.size()));
            copy.applyMove(move[0], move[1]);
        }
        return copy.getGameResult();
    }
}