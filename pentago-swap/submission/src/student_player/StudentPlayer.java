package student_player;

import boardgame.Board;
import boardgame.Move;

import pentago_swap.PentagoMove;
import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    static int pieceType;
    static PentagoBoardState.Piece pieceColor;
    static PentagoBoardState.Piece opponentColor;
    static boolean firstOpponentMoveDone = false;
    static Node root;
    static long startTime;
    static Move lostBlock = null;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260745604");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        long startTime = System.currentTimeMillis();

        Move myMove = null;

        // Do one strategy for first four moves for white and first four for black 
        Boolean isWhite = (boardState.getOpponent() == PentagoBoardState.BLACK);
        if(isWhite){
            pieceType = PentagoBoardState.WHITE;
            pieceColor = PentagoBoardState.Piece.WHITE;
            opponentColor = PentagoBoardState.Piece.BLACK;

        }
        else{
            pieceType = PentagoBoardState.BLACK;
            pieceColor = PentagoBoardState.Piece.BLACK;
            opponentColor = PentagoBoardState.Piece.WHITE;

        }

        if(isWhite) {
            // Only go here if first 2 turns
            if (boardState.getTurnNumber() < 2) {
                myMove = MyTools.initialMovesWhite(boardState);
            }

            if(boardState.getTurnNumber() == 2){

                myMove = MyTools.thirdMoveWhite(boardState);
            }

            if(boardState.getTurnNumber() == 3){
                myMove = MyTools.fourthMoveWhite(boardState);
            }
        }
        
        else{ // Strategy for black
            if(boardState.getTurnNumber() == 0){
                myMove = MyTools.initialMovesWhite(boardState);
            }
            
            if(boardState.getTurnNumber() == 1){
                myMove = MyTools.secondMoveBlack(boardState);
            }

            if(boardState.getTurnNumber() == 2 || boardState.getTurnNumber() == 3){
                myMove = MyTools.thirdAndFourthMoveBlack(boardState);
            }
        }

        // Use monte carlo from here
        if(boardState.getTurnNumber() > 3 && boardState.getTurnNumber() <=11) {
            myMove = pickNextMove(boardState, 1);
        }
        if(boardState.getTurnNumber() > 11) myMove = pickNextMove(boardState, 2);

        // Return your move to be processed by the server.
        if (myMove == null) {
            //throw new IllegalArgumentException("Move is null dummy");
            myMove = boardState.getRandomMove();
        }
        System.out.println("time: " + (System.currentTimeMillis() - startTime));
        return myMove;
    }

    public int getID(){return this.player_id;}

    // Function that picks next best move (uses alphabeta function)
    public static PentagoMove pickNextMove(PentagoBoardState state, int depth){
        Map<Integer, PentagoMove> movescores = new HashMap<>();
        ArrayList<Integer> bestValues = new ArrayList<>();
        PentagoMove bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        boolean checkOnce = false;
        for(PentagoMove possibleMove: state.getAllLegalMoves()) {
            PentagoBoardState clone = (PentagoBoardState) state.clone();
            clone.processMove(possibleMove);
            if (clone.gameOver()) {
                if (clone.getWinner() == pieceType) return possibleMove;
                if (clone.getWinner() == 1 - pieceType) continue;
            }
        }

        for(PentagoMove possibleMove: state.getAllLegalMoves()) {
            PentagoBoardState clone = (PentagoBoardState) state.clone();
            clone.processMove(possibleMove);

            if(!checkOnce) {
                PentagoMove defendMove = defendCheck(state);
                if (defendMove != null){

                    return defendMove;
                }
                checkOnce = true;
            }

            int alphabetaResult = alphabeta(possibleMove, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, (PentagoBoardState) state.clone());

            if(alphabetaResult > bestScore){
                bestScore = alphabetaResult;
                bestMove = possibleMove;
            }
        }

           /* if(bestValues.size() < 5) {
                bestValues.add(alphabetaResult);
                bestValues.sort(null);
                movescores.put(alphabetaResult, possibleMove);
            }

            else{
                // Only keep 5 best in array list
                if(alphabetaResult > bestValues.get(0)){
                    movescores.remove(bestValues.get(0));
                    bestValues.remove(0);
                    bestValues.add(0, alphabetaResult);
                    bestValues.sort(null);
                    movescores.put(alphabetaResult, possibleMove);
                }
            }

        }

        // Now run a little monte Carlo simulation with the 5 best moves
        int bestScore = Integer.MIN_VALUE;

        for(PentagoMove testMove: movescores.values()){
            int numWins = monteCarloWinsReturn(testMove, (PentagoBoardState) state.clone());
            if(numWins > bestScore){
                bestScore = numWins;
                bestMove = testMove;
            }
        } */
        return bestMove;
    }

    // Alpha beta algorithm taken from the wikipedia pseudocode
    public static int alphabeta(Move node, int depth, int alpha, int beta, boolean maxPlayer, PentagoBoardState clone){
        PentagoBoardState.Piece currentPiece, opposingPiece;
        if(maxPlayer){
            currentPiece = pieceColor;
            opposingPiece = opponentColor;
        }
        else{
            currentPiece = opponentColor;
            opposingPiece = pieceColor;
        }

        clone.processMove((PentagoMove) node);
        if(depth == 0 || clone.gameOver()) return heuristic(clone, currentPiece, opposingPiece); // Get heuristic if child or max depth

        if(maxPlayer){
            int value = Integer.MIN_VALUE;

            // Find all children
            for(Move child : clone.getAllLegalMoves()){
                value = Math.max(value, alphabeta(child, depth - 1 , alpha , beta , false , (PentagoBoardState) clone.clone()));
                alpha =  Math.max(alpha, value );
                if(alpha >= beta) break; // Prune in this case
            }
            return value;
        }
        else{
            int value = Integer.MAX_VALUE;
            // Find all children
            for(Move child : clone.getAllLegalMoves()){
                value = Math.min(value, alphabeta(child,depth - 1 , alpha , beta , true , (PentagoBoardState) clone.clone()) );
                beta = Math.min(beta, value );
                if(alpha >= beta) break; // Prune
            }

            return value;
        }

    }

    // Monte Carlo tree search algorithm
    public static PentagoMove monteCarlo(PentagoBoardState state) throws CloneNotSupportedException {
        firstOpponentMoveDone = false;
        startTime = System.currentTimeMillis(); // Start
        Node node = new Node(state, null);  // Create a node
        root = node;

        // Generate all children's
        Move first = node.generateChildrenFirst();
        if(first != null) return (PentagoMove) first;

        monteCarloSelection(state, 0, node);

        System.out.println("Time right nooooow: " + (System.currentTimeMillis() - startTime));
        Node chosenNode = null;
        double max = Double.MIN_VALUE;
        System.out.println("This is the number of children " + node.children.size() );
        for(Node n: node.children){
            //System.out.println("I have seen this much bro: " + n.timesVisited);
            //System.out.println("I have won this much bro: " + n.timesWon);

            if(n.timesVisited >= 20) {
                double result = n.timesWon / n.timesVisited;
                if (result >= max) {
                    max = result;
                    chosenNode = n;
                }
            }
        }

        // Find best child node, only do this loop if no nodes found with over 20 simulations
        if(chosenNode == null) {
            for (Node n : node.children) {
                //System.out.println("I have seen this much bro: " + n.timesVisited);
                //System.out.println("I have won this much bro: " + n.timesWon);

                double result = n.timesWon / n.timesVisited;
                //System.out.println("Yoo: " + n.timesWon + " " + n.timesVisited);
                if (result >= max) {
                    max = result;
                    chosenNode = n;
                }
            }
        }

        System.out.println("Time after nooooow: " + (System.currentTimeMillis() - startTime));


        if(chosenNode != null) System.out.println("Chosen node won: " + chosenNode.timesWon + " played " + chosenNode.timesVisited);
        else{
            for (Node n : node.children) {
                //System.out.println("I have seen this much bro: " + n.timesVisited);
                //System.out.println("I have won this much bro: " + n.timesWon);

                double result = n.timesVisited;
                if (result >= max) {
                    max = result;
                    chosenNode = n;
                }
            }
        }

        // Return the move
        //System.out.println("Node seen this much: " + chosenNode.timesVisited);
        //System.out.println("Node won this much: " + chosenNode.timesWon);

        return (PentagoMove) node.boardmap.get(chosenNode);
    }

    // Actual Monte Carlo descent
    public static void monteCarloSelection(PentagoBoardState state, int depth, Node parent) throws CloneNotSupportedException {

        while (System.currentTimeMillis() - startTime < 800) { // Get out when you get close to 2 seconds

            if(parent == null){
                System.out.println("Im here");
                return;
            }
            if (parent.allChildrenVisited) { // Do selection
                double max = Double.MIN_VALUE;

                for (Node node : parent.children) {
                    double result = (node.timesWon / node.timesVisited) + (Math.sqrt(2* Math.log(parent.timesVisited) / node.timesVisited));
                    if(result >= max) {
                        max = result;
                        parent = node;
                    }

                }

                depth++;


            } else {  // expand next child
                boolean flag = false;
                for (Node node : parent.children) {
                    node.generateChildren(depth);
                    if(node.parentDeleted){
                        parent = root;
                        node.trimNodes();
                        flag = true;
                        node.parentDeleted = false;
                        break;
                    }
                    if(node.parentDeletedWin){
                        parent = root;
                        node.trimNodesWin();
                        flag = true;
                        node.parentDeletedWin = false;
                        break;
                    }
                    if(node.draw){
                        parent = root;
                        flag = true;
                        node.draw = false;
                        break;
                    }
                    double simValue = monteCarloSimulation((PentagoBoardState) state.clone());

                    if(simValue != 2.0) rollBack(node, simValue);
                }
                if(!flag) parent.allChildrenVisited = true;
                depth = 0;
            }
        }
    }

    // Random simulation of Monte Carlo until win, loss or draw
    public static double monteCarloSimulation(PentagoBoardState state){

        while (!state.gameOver()) {

            state.processMove((PentagoMove) state.getRandomMove()); // Do the move
        }
            if(state.getWinner() == pieceType){ // You won
                return 1;
            }

            if(state.getWinner() == 1 - pieceType){ // You lost
                return 0;
            }

            if(state.getWinner() == Board.DRAW){ // Draw
                return 0.5;
            }

            return 0.0; // SHOULD NOT GET HERE
    }

    // Update all node stats until you hit the root
    public static void rollBack(Node lastNode, double value){
        while (lastNode.parent != null){

            lastNode.timesVisited++;
            lastNode.timesWon = lastNode.timesWon + value;
            lastNode = lastNode.parent;
        }
            lastNode.timesVisited++;
            lastNode.timesWon = lastNode.timesWon + value;
    }

    // My custom heuristic for best board states
   /* public static double heuristic(PentagoBoardState state){

        Boolean isWhite = (state.getOpponent() == PentagoBoardState.BLACK);

        double heuristic = 0;
        // Check 3 rows in quadrant
        if(isWhite){
            // Top rows
            heuristic = heuristic + MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.TR,5 , PentagoBoardState.Piece.WHITE, state)
                    + MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.TL,5 , PentagoBoardState.Piece.WHITE, state) +
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.BR,2 , PentagoBoardState.Piece.WHITE, state) +
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.BL,2 , PentagoBoardState.Piece.WHITE, state) -
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.TR,5 , PentagoBoardState.Piece.BLACK, state)
                    - MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.TL,5 , PentagoBoardState.Piece.BLACK, state) -
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.BR,2 , PentagoBoardState.Piece.BLACK, state) -
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.BL,2 , PentagoBoardState.Piece.BLACK, state);

            // Middle rows
            heuristic = heuristic + MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.TR,5 , PentagoBoardState.Piece.WHITE, state)
                    + MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.TL,5 , PentagoBoardState.Piece.WHITE, state) +
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.BR,2 , PentagoBoardState.Piece.WHITE, state) +
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.BL,2 , PentagoBoardState.Piece.WHITE, state) -
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.TR,5 , PentagoBoardState.Piece.BLACK, state)
                    - MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.TL,5 , PentagoBoardState.Piece.BLACK, state) -
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.BR,2 , PentagoBoardState.Piece.BLACK, state) -
                    MyTools.getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant.BL,2 , PentagoBoardState.Piece.BLACK, state);

            // In progress for alphabeta ...
        }


        return 0.0;
    } */

   // My heuristic which calls all my heuristic calculations in MyTools
   public static int heuristic(PentagoBoardState state, PentagoBoardState.Piece currentPlayer, PentagoBoardState.Piece opponent){
       int heuristic = 0;

       // First do all checks for the horizontals
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);

       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);

       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupHorizontal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 2, currentPlayer, opponent, state);

       // Checks for the verticals
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);

       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);

       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 2, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupVertical(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 2, currentPlayer, opponent, state);

       // Checks for the diagonals
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 0, currentPlayer, opponent, state);

       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);
       heuristic = heuristic + heuristicSetupDiagonal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 1, currentPlayer, opponent, state);

       if(currentPlayer == opponentColor) heuristic = heuristic * -1; // Negate if it is the opponent's turn

       return heuristic;

   }

   // Playing with the heuristic
   public static int heuristicSetupHorizontal(PentagoBoardState.Quadrant quad, PentagoBoardState.Quadrant quad2, int row, PentagoBoardState.Piece currentPlayer, PentagoBoardState.Piece opponent,PentagoBoardState state){
       return MyTools.HeuristicHorizontal(quad, quad2 , row, currentPlayer, opponent, state, true) - MyTools.HeuristicHorizontal(quad, quad2 , row, opponent, currentPlayer, state, false);
   }

    public static int heuristicSetupVertical(PentagoBoardState.Quadrant quad, PentagoBoardState.Quadrant quad2, int row, PentagoBoardState.Piece currentPlayer, PentagoBoardState.Piece opponent,PentagoBoardState state){
        return MyTools.HeuristicVertical(quad, quad2 , row, currentPlayer, opponent, state, true) - MyTools.HeuristicVertical(quad, quad2 , row, opponent, currentPlayer, state, false);
    }

    public static int heuristicSetupDiagonal(PentagoBoardState.Quadrant quad, PentagoBoardState.Quadrant quad2, int row, PentagoBoardState.Piece currentPlayer, PentagoBoardState.Piece opponent,PentagoBoardState state){
        return MyTools.HeuristicDiagonal(quad, quad2 , row, currentPlayer, opponent, state, true) - MyTools.HeuristicDiagonal(quad, quad2 , row, opponent, currentPlayer, state, false);
    }

    // Function that checks if my ai should just defend (do this when facing very bad board states)
    public static PentagoMove defendCheck(PentagoBoardState state){
       PentagoMove defense = null;
        // First do all checks for the horizontals
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;

        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;

        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveHorizontal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;

        // Now for vertical
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;

        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;

        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveVertical(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 2, pieceColor, opponentColor, state);
        if(defense != null) return defense;


        // Now for diagonal
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 0, pieceColor, opponentColor, state);
        if(defense != null) return defense;

        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BL, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BL, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.TR, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;
        defense = MyTools.checkIfForceMoveDiagonal(PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, 1, pieceColor, opponentColor, state);
        if(defense != null) return defense;

        return defense;
    }

    // Function that simulates random runs with the top 5 moves
    /*public static int monteCarloWinsReturn(PentagoMove move, PentagoBoardState state){
       state.processMove(move);
       int count = 0;
       for(int i = 0; i < 25; i++) {
           while (!state.gameOver()) {

               state.processMove((PentagoMove) state.getRandomMove()); // Do the move
           }
           if (state.getWinner() == pieceType) { // You won
               count++;
           }
       }

        return count;
    }*/


}