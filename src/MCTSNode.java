import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MCTSNode {
    private State state;
    private MCTSNode parent;
    private List<MCTSNode> children;
    private int[] move; // the move that led to this node: int[]{smallBoardIndex, cellIndex}
    private int wins;
    private int visits;
    private List<int[]> untriedMoves; // moves not yet expanded from this node

    public MCTSNode(State state, MCTSNode parent, int[] move) {
        this.state = state;
        this.parent = parent;
        this.move = move;
        this.children = new ArrayList<>();
        this.wins = 0;
        this.visits = 0;
        // populate untried moves lazily on first visit
        this.untriedMoves = new ArrayList<>(state.getLegalMoves());
        Collections.shuffle(untriedMoves); //shuffle so that the expansion order is random
    }

    // UCB1 score this will be called on a child node to decide which child to select
    public double ucb1(double c) {
        if (visits == 0) return Double.MAX_VALUE; // unvisited nodes are always preferred within the selection
        return (double) wins / visits + c * Math.sqrt(Math.log(parent.visits) / visits);
    }

    // this will pick the child with the highest UCB1 score as part of the selection phase
    public MCTSNode selectChild(double c) {
        return Collections.max(children, (a, b) -> Double.compare(a.ucb1(c), b.ucb1(c)));
    }

    // this iwll expand one untried move and creates a child node and returns it for the expansion phase
    public MCTSNode expand() {
        int[] move = untriedMoves.remove(untriedMoves.size() - 1); // take from end (already shuffled)
        State childState = state.copy();
        childState.applyMove(move[0], move[1]);
        MCTSNode child = new MCTSNode(childState, this, move);
        children.add(child);
        return child;
    }

    // walk back up the tree updating wins and visits as part of the backpropogation phase
    // winner is X O or DRAW
    public void backpropagate(String winner, Cell rootPlayer) {
        visits++;
        if (winner.equals(rootPlayer.toString())) {
            wins++;
        }
        if (parent != null) {
            parent.backpropagate(winner, rootPlayer);
        }
    }

    public boolean isFullyExpanded() {
        return untriedMoves.isEmpty();
    }

    public boolean isTerminal() {
        return state.isGameOver();
    }

    // atter MCTS finishes, pick the child with the most visits
    public MCTSNode bestChild() {
        return Collections.max(children, (a, b) -> Integer.compare(a.visits, b.visits));
    }

    public State getState()            { return state; }
    public MCTSNode getParent()        { return parent; }
    public List<MCTSNode> getChildren(){ return children; }
    public int[] getMove()             { return move; }
    public int getWins()               { return wins; }
    public int getVisits()             { return visits; }
    public List<int[]> getUntriedMoves(){ return untriedMoves; }
}