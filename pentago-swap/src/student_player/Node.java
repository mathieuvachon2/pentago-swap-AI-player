package student_player;

import boardgame.Board;
import boardgame.Move;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * My tree class
 */
public class Node {
    PentagoBoardState state;
    Node parent;
    List<Node> children;
    Map<Node, Move> boardmap;
    double timesWon;
    double timesVisited;
    boolean allChildrenVisited;
    boolean parentDeleted = false;
    boolean parentDeletedWin = false;
    boolean draw = false;

    public Node(PentagoBoardState state, Node parent) {
        this.state = state;
        this.parent = parent;
        children = new ArrayList<>();
        boardmap = new HashMap<>();
        timesWon = 0;
        timesVisited = 0;
        allChildrenVisited = false;
    }

    // Function to generate all the legal moves. Associate parents and children
    public void generateChildren(int depth) throws CloneNotSupportedException {
        ArrayList<PentagoMove> childMoves = new ArrayList<>();

        // At depth higher than 1, only check for random swaps
        if(depth <= 1) childMoves = state.getAllLegalMoves();
        else childMoves = MyTools.getLegalCoordinates(state);
        for(PentagoMove move: childMoves){

            PentagoBoardState clonedState = (PentagoBoardState) state.clone();
            clonedState.processMove(move);
            if(clonedState.gameOver()){
                if(clonedState.getWinner() == StudentPlayer.pieceType){
                    parentDeletedWin = true;
                    StudentPlayer.rollBack(this, 1);
                }
                if(clonedState.getWinner() == 1 - StudentPlayer.pieceType){
                        parentDeleted = true;
                        StudentPlayer.rollBack(this, 0);
                }
                if(clonedState.getWinner() == Board.DRAW){
                    draw = true;
                    StudentPlayer.rollBack(this, 0.5);
                }
            }
            else {
                Node node = new Node(clonedState, this);
                if(heuristicTrimCheck(node)){
                    parentDeleted = true;
                }
                else {
                    boardmap.put(node, move);
                    children.add(node);
                }
            }
        }
    }

    // Function to generate all the legal moves. Associate parents and children
    public Move generateChildrenFirst() throws CloneNotSupportedException {
        ArrayList<PentagoMove> childMoves = state.getAllLegalMoves();
        for(PentagoMove move: childMoves){

            PentagoBoardState clonedState = (PentagoBoardState) state.clone();
            clonedState.processMove(move);
            if(clonedState.gameOver()){
                if(clonedState.getWinner() == StudentPlayer.pieceType){
                    System.out.println("Return here");
                    return move;
                    //StudentPlayer.rollBack(this, 1);
                }
                if(clonedState.getWinner() == 1 - StudentPlayer.pieceType){
                    StudentPlayer.rollBack(this, 0);
                }
                if(clonedState.getWinner() == Board.DRAW){
                    StudentPlayer.rollBack(this, 0.5);
                }
            }
            else {
                Node node = new Node(clonedState, this);
                boardmap.put(node, move);
                children.add(node);
            }
        }
        return null;
    }

    // Method to trim nodes when you see an opponent could win
    public void trimNodes() throws CloneNotSupportedException {

        StudentPlayer.rollBack(this, 0);
        this.parent.children.clear();
        this.parent.children.add(this); // cut off links
    }

    // Method to trim nodes when you win
    public void trimNodesWin(){
        StudentPlayer.rollBack(this, 1);
        this.parent.children.clear();
        this.parent.children.add(this); // cut off links
    }

    // Clone method for Node
    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    // Condition check to see if node and parent should be trimmed out
    public boolean heuristicTrimCheck(Node child){
        if(child.state.getTurnPlayer() == StudentPlayer.pieceType){ // my turn, first check if you have good state
            if((MyTools.badStateHorizontal(child.state, StudentPlayer.pieceColor, StudentPlayer.opponentColor)) || (MyTools.badStateVertical(child.state, StudentPlayer.pieceColor, StudentPlayer.opponentColor)))
                return false;
        }

        // Now check from opponent point of view
        if((MyTools.badStateHorizontal(child.state, StudentPlayer.opponentColor, StudentPlayer.pieceColor)) || (MyTools.badStateVertical(child.state, StudentPlayer.opponentColor, StudentPlayer.pieceColor)))
            return true;

        return false;
    }

}


