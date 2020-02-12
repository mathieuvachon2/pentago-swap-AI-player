package student_player;

import boardgame.BoardState;
import boardgame.Move;
import boardgame.Player;
import pentago_swap.*;

import java.sql.Time;
import java.util.ArrayList;

public class MyTools {

    public static ArrayList<PentagoCoord> initialCentreMoves = new ArrayList<>();
    static final int ID = 260745604;
    static final int BOARD_SIZE = 6;

    // Returns a random move from one of the quadrant centres
    public static Move initialMovesWhite(PentagoBoardState boardState) {

        ArrayList<PentagoCoord> centreMoves = new ArrayList<>();

        PentagoCoord[] centreCoordinates = {new PentagoCoord(1, 1), new PentagoCoord(4, 1), new PentagoCoord(1, 4), new PentagoCoord(4, 4)};

        // Find coordinates that are not busy and add them to centre moves
        for(PentagoCoord coord : centreCoordinates){
            if(boardState.isPlaceLegal(coord)) centreMoves.add(coord);
        }

        PentagoCoord chosenCoord = centreMoves.get((int)Math.random()*centreMoves.size()); // Get random coordinate
        initialCentreMoves.add(chosenCoord);

        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        return new PentagoMove(chosenCoord, quadrantA, quadrantB, 260745604);

    }

    // Returns a random swap
    public static PentagoBoardState.Quadrant[] randomSwap(){
        PentagoBoardState.Quadrant[] quadrants = new PentagoBoardState.Quadrant[2];

        // Options of Quadrants
        PentagoBoardState.Quadrant[] options = {PentagoBoardState.Quadrant.BL, PentagoBoardState.Quadrant.BR, PentagoBoardState.Quadrant.TL, PentagoBoardState.Quadrant.TR};

        int[] indices = new int[2];
        // Quadrant a
        quadrants[0] = options[(int)(Math.random()*4)];

        // Find quadrant b
        while(true){
            quadrants[1] = options[(int)(Math.random()*4)];
            if(!quadrants[0].equals(quadrants[1])) break; // break if 2 quadrants are different
        }

        return quadrants;
    }

    // This function returns the number of pieces on a row in a quadrant
    public static int getNbrUsefulPiecesHorizontal(PentagoBoardState.Quadrant quadrant, int row, PentagoBoardState.Piece piece, PentagoBoardState.Piece opponent, PentagoBoardState state){
        if((quadrant == PentagoBoardState.Quadrant.BR) || quadrant == PentagoBoardState.Quadrant.BL ) row = row + 3;
        int count = 0;
        System.out.println("This is quad: " + quadrant);
        switch(quadrant){
            case TL:
            case BL:
                // If middle other player's piece, return 0
                if((state.getPieceAt(row, 1 ) == opponent))
                    return 0;

                for(int col = 0; col < 3; col++){
                    if(piece == state.getPieceAt(row, col )) count++;
                }

                return count;

            case TR:
            case BR:

                // If middle other player's piece, return 0
                if((state.getPieceAt(row, 4 ) == opponent))
                    return 0;

                for(int col = 3; col < 6; col++){
                    if(piece == state.getPieceAt(row, col )) count++;
                }
                return count;

                default:
                    return count;
        }
    }

    // This function returns the number of pieces on a row in a quadrant
    public static int getNbrUsefulPiecesVertical(PentagoBoardState.Quadrant quadrant, int col, PentagoBoardState.Piece piece, PentagoBoardState.Piece opponent ,PentagoBoardState state){
        if((quadrant == PentagoBoardState.Quadrant.BR) || quadrant == PentagoBoardState.Quadrant.TR ) col = col + 3;

        int count = 0;
        switch(quadrant){
            case TL:
            case TR:
                // If middle other player's piece, return 0
                if((state.getPieceAt(1, col ) == opponent))
                    return 0;

                for(int row = 0; row < 3; row++){
                    if(piece == state.getPieceAt(row, col )) count++;
                }

                return count;

            case BL:
            case BR:

                // If middle other player's piece, return 0
                if((state.getPieceAt(4, col ) == opponent))
                    return 0;

                for(int row = 3; row < 6; row++){
                    if(piece == state.getPieceAt(row, col )) count++;
                }
                return count;

            default:
                return count;
        }
    }

    // Method that returns the Quadrant from the coordinates
    public static PentagoBoardState.Quadrant getCoordQuadrant(int row, int col){
        if(col < 3 && row < 3) return PentagoBoardState.Quadrant.TL;
        if(col < 3 && row >= 3) return PentagoBoardState.Quadrant.BL;
        if(col >= 3 && row < 3) return PentagoBoardState.Quadrant.TR;
        return PentagoBoardState.Quadrant.BR;
    }

    // Method that returns best second coord for black player (either in middle of a quadrant or defensive)
    public static Move secondMoveBlack(PentagoBoardState state){

        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        // First find white's first piece
        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 6; col++){
                if(state.getPieceAt(row, col) == PentagoBoardState.Piece.WHITE){ // check if there exists adjacent white piece in same quadrant and block it if possible
                    if((col+2 < 6)  && (state.getPieceAt(row,col+1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row,col+2))) && (getCoordQuadrant(row,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row, col+2, quadrantA, quadrantB, 260745604 );
                    if((col-2 >= 0) && (row+2 < 6) && (state.getPieceAt(row+1,col-1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row+2,col-2))) && (getCoordQuadrant(row+2,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2,col-2, quadrantA, quadrantB, 260745604);
                    if((row+2 < 6) && (state.getPieceAt(row+1,col ) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row+2, col))) && (getCoordQuadrant(row+2,col).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2, col, quadrantA, quadrantB, 260745604);
                    if((col+2 < 6) && (row+2 < 6) && (state.getPieceAt(row+1,col+1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row+2,col+2))) && (getCoordQuadrant(row+2,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2,col+2, quadrantA, quadrantB, 260745604);
                    if((col-2 >= 0)  && (state.getPieceAt(row,col-1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row,col-2))) && (getCoordQuadrant(row,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row, col-2, quadrantA, quadrantB, 260745604 );
                    if((row-2 >= 0) && (state.getPieceAt(row-1,col ) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row-2, col))) && (getCoordQuadrant(row-2,col).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2, col, quadrantA, quadrantB, 260745604);
                    if((col-2 >= 0) && (row-2 >= 0) && (state.getPieceAt(row-1,col-1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row-2,col-2))) && (getCoordQuadrant(row-2,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2,col-2, quadrantA, quadrantB, 260745604);
                    if((col+2 < 6) && (row-2 >= 0) && (state.getPieceAt(row-1,col+1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row-2,col+2))) && (getCoordQuadrant(row-2,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2,col+2, quadrantA, quadrantB, 260745604);

                }
            }
        }

        // Otherwise return random middle move
        return initialMovesWhite(state);

    }

    // Method that takes care of third and fourth move logic for black
    public static Move thirdAndFourthMoveBlack(PentagoBoardState state){
        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];
        Move threeDiagMove;

        // First find white's first piece
        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 6; col++){
                if(state.getPieceAt(row, col) == PentagoBoardState.Piece.WHITE){ // check if there exists adjacent white piece in same quadrant and block it if possible
                    if((col+2 < 6)  && (state.getPieceAt(row,col+1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row,col+2))) && (getCoordQuadrant(row,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row, col+2, quadrantA, quadrantB, 260745604 );
                    if((col-2 >= 0) && (row+2 < 6) && (state.getPieceAt(row+1,col-1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row+2,col-2))) && (getCoordQuadrant(row+2,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2,col-2, quadrantA, quadrantB, 260745604);
                    if((row+2 < 6) && (state.getPieceAt(row+1,col ) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row+2, col))) && (getCoordQuadrant(row+2,col).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2, col, quadrantA, quadrantB, 260745604);
                    if((col+2 < 6) && (row+2 < 6) && (state.getPieceAt(row+1,col+1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row+2,col+2))) && (getCoordQuadrant(row+2,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2,col+2, quadrantA, quadrantB, 260745604);
                    if((col-2 >= 0)  && (state.getPieceAt(row,col-1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row,col-2))) && (getCoordQuadrant(row,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row, col-2, quadrantA, quadrantB, 260745604 );
                    if((row-2 >= 0) && (state.getPieceAt(row-1,col ) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row-2, col))) && (getCoordQuadrant(row-2,col).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2, col, quadrantA, quadrantB, 260745604);
                    if((col-2 >= 0) && (row-2 >= 0) && (state.getPieceAt(row-1,col-1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row-2,col-2))) && (getCoordQuadrant(row-2,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2,col-2, quadrantA, quadrantB, 260745604);
                    if((col+2 < 6) && (row-2 >= 0) && (state.getPieceAt(row-1,col+1) == PentagoBoardState.Piece.WHITE) &&  (state.isPlaceLegal(new PentagoCoord(row-2,col+2))) && (getCoordQuadrant(row-2,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2,col+2, quadrantA, quadrantB, 260745604);

                }
            }
        }

        // Now check for sneaky 3 quadrant wins by diagonal
        if((state.getPieceAt(0,1 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(1,0 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 1, PentagoBoardState.Quadrant.TL, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(0,4 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(1,3 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 1, PentagoBoardState.Quadrant.TR, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,0 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(3,1 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 1, PentagoBoardState.Quadrant.BL, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,3 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(3,4 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 1, PentagoBoardState.Quadrant.BR, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(1,2 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(2,1 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 2, PentagoBoardState.Quadrant.TL, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(2,4 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(1,5 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 2, PentagoBoardState.Quadrant.TR, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }

        if((state.getPieceAt(5,1 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(4,2 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 2, PentagoBoardState.Quadrant.BL, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,5 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(5,4 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 2, PentagoBoardState.Quadrant.BR, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(0,1 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(1,2 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 3, PentagoBoardState.Quadrant.TL, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(0,4 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(1,5 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 3, PentagoBoardState.Quadrant.TR, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(3,1 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(4,2 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 3, PentagoBoardState.Quadrant.BL, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(3,4 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(4,5 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 3, PentagoBoardState.Quadrant.BR, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(1,0 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(2,1 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 4, PentagoBoardState.Quadrant.TL, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(1,3 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(2,4 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 4, PentagoBoardState.Quadrant.TR, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,0 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(5,1 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 4, PentagoBoardState.Quadrant.BL, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,3 ) == PentagoBoardState.Piece.WHITE) && (state.getPieceAt(5,4 ) == PentagoBoardState.Piece.WHITE)){
            threeDiagMove = defendThreeQuadrantDiag(state, 4, PentagoBoardState.Quadrant.BR, PentagoBoardState.Piece.WHITE);
            if(threeDiagMove != null) return threeDiagMove;
        }

        return blackAttack(state);
    }

    // Method that takes care of the third move logic for white
    public static Move thirdMoveWhite(PentagoBoardState state){
        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        // First find black's first piece
        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 6; col++){
                if(state.getPieceAt(row, col) == PentagoBoardState.Piece.BLACK){ // check if there exists adjacent white piece in same quadrant and block it if possible
                    if((col+2 < 6)  && (state.getPieceAt(row,col+1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row,col+2))) && (getCoordQuadrant(row,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row, col+2, quadrantA, quadrantB, 260745604 );
                    if((col-2 >= 0) && (row+2 < 6) && (state.getPieceAt(row+1,col-1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row+2,col-2))) && (getCoordQuadrant(row+2,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2,col-2, quadrantA, quadrantB, 260745604);
                    if((row+2 < 6) && (state.getPieceAt(row+1,col ) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row+2, col))) && (getCoordQuadrant(row+2,col).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2, col, quadrantA, quadrantB, 260745604);
                    if((col+2 < 6) && (row+2 < 6) && (state.getPieceAt(row+1,col+1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row+2,col+2))) && (getCoordQuadrant(row+2,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2,col+2, quadrantA, quadrantB, 260745604);
                    if((col-2 >= 0)  && (state.getPieceAt(row,col-1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row,col-2))) && (getCoordQuadrant(row,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row, col-2, quadrantA, quadrantB, 260745604 );
                    if((row-2 >= 0) && (state.getPieceAt(row-1,col ) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row-2, col))) && (getCoordQuadrant(row-2,col).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2, col, quadrantA, quadrantB, 260745604);
                    if((col-2 >= 0) && (row-2 >= 0) && (state.getPieceAt(row-1,col-1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row-2,col-2))) && (getCoordQuadrant(row-2,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2,col-2, quadrantA, quadrantB, 260745604);
                    if((col+2 < 6) && (row-2 >= 0) && (state.getPieceAt(row-1,col+1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row-2,col+2))) && (getCoordQuadrant(row-2,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2,col+2, quadrantA, quadrantB, 260745604);

                }
            }
        }

        // Go into attack mode
        return whiteAttack(state);

    }

    // Method that takes care of the fourth move logic for white
    public static Move fourthMoveWhite(PentagoBoardState state){
        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];
        Move threeDiagMove;

        // First find black's first piece
        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 6; col++){
                if(state.getPieceAt(row, col) == PentagoBoardState.Piece.BLACK){ // check if there exists adjacent white piece in same quadrant and block it if possible
                    if((col+2 < 6)  && (state.getPieceAt(row,col+1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row,col+2))) && (getCoordQuadrant(row,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row, col+2, quadrantA, quadrantB, 260745604 );
                    if((col-2 >= 0) && (row+2 < 6) && (state.getPieceAt(row+1,col-1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row+2,col-2))) && (getCoordQuadrant(row+2,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2,col-2, quadrantA, quadrantB, 260745604);
                    if((row+2 < 6) && (state.getPieceAt(row+1,col ) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row+2, col))) && (getCoordQuadrant(row+2,col).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2, col, quadrantA, quadrantB, 260745604);
                    if((col+2 < 6) && (row+2 < 6) && (state.getPieceAt(row+1,col+1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row+2,col+2))) && (getCoordQuadrant(row+2,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row+2,col+2, quadrantA, quadrantB, 260745604);
                    if((col-2 >= 6)  && (state.getPieceAt(row,col-1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row,col-2))) && (getCoordQuadrant(row,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row, col-2, quadrantA, quadrantB, 260745604 );
                    if((row-2 >= 0) && (state.getPieceAt(row-1,col ) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row-2, col))) && (getCoordQuadrant(row-2,col).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2, col, quadrantA, quadrantB, 260745604);
                    if((col-2 >= 0) && (row-2 >= 0) && (state.getPieceAt(row-1,col-1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row-2,col-2))) && (getCoordQuadrant(row-2,col-2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2,col-2, quadrantA, quadrantB, 260745604);
                    if((col+2 < 6) && (row-2 >= 0) && (state.getPieceAt(row-1,col+1) == PentagoBoardState.Piece.BLACK) &&  (state.isPlaceLegal(new PentagoCoord(row-2,col+2))) && (getCoordQuadrant(row-2,col+2).equals(getCoordQuadrant(row, col))))
                        return new PentagoMove(row-2,col+2, quadrantA, quadrantB, 260745604);

                }
            }
        }

        // Now check for sneaky 3 quadrant wins by diagonal
        if((state.getPieceAt(0,1 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(1,0 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 1, PentagoBoardState.Quadrant.TL, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(0,4 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(1,3 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 1, PentagoBoardState.Quadrant.TR, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,0 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(3,1 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 1, PentagoBoardState.Quadrant.BL, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,3 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(3,4 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 1, PentagoBoardState.Quadrant.BR, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(1,2 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(2,1 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 2, PentagoBoardState.Quadrant.TL, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(2,4 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(1,5 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 2, PentagoBoardState.Quadrant.TR, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }

        if((state.getPieceAt(5,1 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(4,2 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 2, PentagoBoardState.Quadrant.BL, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,5 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(5,4 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 2, PentagoBoardState.Quadrant.BR, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(0,1 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(1,2 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 3, PentagoBoardState.Quadrant.TL, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(0,4 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(1,5 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 3, PentagoBoardState.Quadrant.TR, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(3,1 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(4,2 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 3, PentagoBoardState.Quadrant.BL, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(3,4 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(4,5 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 3, PentagoBoardState.Quadrant.BR, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(1,0 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(2,1 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 4, PentagoBoardState.Quadrant.TL, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(1,3 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(2,4 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 4, PentagoBoardState.Quadrant.TR, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,0 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(5,1 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 4, PentagoBoardState.Quadrant.BL, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }
        if((state.getPieceAt(4,3 ) == PentagoBoardState.Piece.BLACK) && (state.getPieceAt(5,4 ) == PentagoBoardState.Piece.BLACK)){
            threeDiagMove = defendThreeQuadrantDiag(state, 4, PentagoBoardState.Quadrant.BR, PentagoBoardState.Piece.BLACK);
            if(threeDiagMove != null) return threeDiagMove;
        }

        return whiteAttack(state);
    }

    // Function intented to defend a win using 3 quadrants
    public static Move defendThreeQuadrantDiag(PentagoBoardState state, int type, PentagoBoardState.Quadrant quad, PentagoBoardState.Piece opponent){
        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        // Now do a switch depending on the orientation and level of the diagonal
        switch (type){
            case 1: // ascending from left -> right and the top diagonal
                switch (quad){
                    case TL:
                        if((state.getPieceAt(1,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,4))))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,3))))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,0))))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,1))))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,4))))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,3))))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, 260745604);

                        break;

                    case TR:
                        if((state.getPieceAt(1,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,1))))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,0))))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,0))))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,1))))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,4))))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,3))))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, 260745604);

                        break;

                    case BL:
                        if((state.getPieceAt(1,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,4))))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,3))))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,0))))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(1,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,1))))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,4))))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,3))))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, 260745604);

                        break;

                    case BR:
                        if((state.getPieceAt(1,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,4))))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,3))))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,0))))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,1))))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(1,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,1))))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,0))))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, 260745604);

                        break;
                }

            case 2: // ascending left -> right, bottom level
                switch (quad){
                    case TL:
                        if((state.getPieceAt(1,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,4))))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,5))))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,2))))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,1))))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,4))))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,5))))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, 260745604);

                        break;

                    case TR:
                        if((state.getPieceAt(1,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,1))))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,2))))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,2))))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,1))))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,4))))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,5))))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, 260745604);

                        break;

                    case BL:
                        if((state.getPieceAt(1,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,4))))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,5))))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,2))))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(1,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,1))))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,4))))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,5))))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, 260745604);

                        break;

                    case BR:
                        if((state.getPieceAt(1,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,4))))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,5))))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,2))))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,1))))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(1,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,1))))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,2))))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, 260745604);

                        break;
                }

            case 3: // Descending left -> right, top level
                switch (quad){
                    case TL:
                        if((state.getPieceAt(1,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,4))))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,5))))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,2))))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,1))))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,4))))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,5))))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, 260745604);

                        break;

                    case TR:
                        if((state.getPieceAt(1,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,1))))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,2))))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,2))))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,1))))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,4))))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,5))))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, 260745604);

                        break;

                    case BL:
                        if((state.getPieceAt(1,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,4))))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,5))))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,2))))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(1,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,1))))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,4))))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,5))))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, 260745604);

                        break;

                    case BR:
                        if((state.getPieceAt(1,5 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,4))))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,5))))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(3,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,2))))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(3,1))))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(0,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,2))))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(1,2 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(0,1))))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, 260745604);

                        break;
                }

            case 4: // descending left -> right: bottom level
                switch (quad){
                    case TL:
                        if((state.getPieceAt(1,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,4))))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,3))))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,0))))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,1))))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,4))))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,3))))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, 260745604);

                        break;

                    case TR:
                        if((state.getPieceAt(1,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,1))))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,0))))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,0))))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,1))))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,4))))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,3))))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, 260745604);

                        break;

                    case BL:
                        if((state.getPieceAt(1,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,4))))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,3))))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,0))))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(1,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,1))))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,4))))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,3))))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, 260745604);

                        break;

                    case BR:
                        if((state.getPieceAt(1,3 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,4))))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,4 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,3))))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(5,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(4,0))))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(4,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(5,1))))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(1,0 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(2,1))))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, 260745604);
                        if((state.getPieceAt(2,1 ) == opponent) && (state.isPlaceLegal(new PentagoCoord(1,0))))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, 260745604);
                        break;
                }
        }

        // If it gets here return null
        return null;
    }

    // Function that attacks for black in its 3rd or 4th move
    public static Move blackAttack(PentagoBoardState state){
        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        PentagoCoord firstMove = initialCentreMoves.get(0);
        PentagoCoord secondMove;
        if(initialCentreMoves.size() > 1) secondMove = initialCentreMoves.get(1);

        // Set quads
        int count = 0;
        PentagoBoardState.Quadrant quad1 = null;
        PentagoBoardState.Quadrant quad2 = null;
        while(quad2 == null) {
            if (state.getPieceAt(1, 1) == PentagoBoardState.Piece.BLACK) {
                if (count == 0) {
                    quad1 = PentagoBoardState.Quadrant.TL;
                    firstMove = new PentagoCoord(1, 1);
                } else {
                    quad2 = PentagoBoardState.Quadrant.TL;
                    secondMove = new PentagoCoord(1, 1);
                }
                count++;
            }
            if (state.getPieceAt(1, 4) == PentagoBoardState.Piece.BLACK) {
                if (count == 0) {
                    quad1 = PentagoBoardState.Quadrant.TR;
                    firstMove = new PentagoCoord(1, 4);

                } else {
                    quad2 = PentagoBoardState.Quadrant.TR;
                    secondMove = new PentagoCoord(1, 4);

                }
                count++;
            }

            if (state.getPieceAt(4, 1) == PentagoBoardState.Piece.BLACK) {
                if (count == 0) {
                    quad1 = PentagoBoardState.Quadrant.BL;
                    firstMove = new PentagoCoord(4, 1);

                } else {
                    quad2 = PentagoBoardState.Quadrant.BL;
                    secondMove = new PentagoCoord(4, 1);

                }
                count++;
            }

            if (state.getPieceAt(4, 4) == PentagoBoardState.Piece.BLACK) {
                if (count == 0) {
                    quad1 = PentagoBoardState.Quadrant.BR;
                    firstMove = new PentagoCoord(4, 4);
                } else {
                    quad2 = PentagoBoardState.Quadrant.BR;
                    secondMove = new PentagoCoord(4, 4);

                }
                count++;
            }
        }

        System.out.println("In black attack: quad1: " + quad1 + " , quad2: " + quad2);
        int nbrWhite1 = getNbrUsefulPiecesHorizontal(quad1, 1, PentagoBoardState.Piece.WHITE, PentagoBoardState.Piece.BLACK, state);
        int nbrBlack1 = getNbrUsefulPiecesHorizontal(quad1, 1 , PentagoBoardState.Piece.BLACK, PentagoBoardState.Piece.WHITE, state);
        int nbrWhite2 = getNbrUsefulPiecesHorizontal(quad2, 1, PentagoBoardState.Piece.WHITE, PentagoBoardState.Piece.BLACK, state);
        int nbrBlack2 = getNbrUsefulPiecesHorizontal(quad2, 1 , PentagoBoardState.Piece.BLACK, PentagoBoardState.Piece.WHITE, state);
        int nbrWhiteVertical1 = getNbrUsefulPiecesVertical(quad1, 1 , PentagoBoardState.Piece.WHITE , PentagoBoardState.Piece.BLACK , state );
        int nbrWhiteVertical2 = getNbrUsefulPiecesVertical(quad2, 1 , PentagoBoardState.Piece.WHITE , PentagoBoardState.Piece.BLACK , state );
        int nbrBlackVertical1 = getNbrUsefulPiecesVertical(quad1, 1 , PentagoBoardState.Piece.BLACK , PentagoBoardState.Piece.WHITE , state );
        int nbrBlackVertical2 = getNbrUsefulPiecesVertical(quad2, 1 , PentagoBoardState.Piece.BLACK , PentagoBoardState.Piece.WHITE , state );

        // For horizontal
        if((nbrWhite1 == 0) && (nbrWhite2 == 0)){
            if(nbrBlack1 == 2){
                switch (quad1){
                    case TL:
                        if(state.isPlaceLegal(new PentagoCoord(1, 0)))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(1, 2)))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, ID);

                        break;

                    case TR:
                        if(state.isPlaceLegal(new PentagoCoord(1, 3)))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(1, 5)))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, ID);

                        break;

                    case BL:
                        if(state.isPlaceLegal(new PentagoCoord(4, 0)))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(4, 2)))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, ID);

                        break;
                    case BR:
                        if(state.isPlaceLegal(new PentagoCoord(4, 3)))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(4, 5)))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, ID);

                        break;
                }
            }

            if(nbrBlack2 == 2){
                switch (quad2){
                    case TL:
                        if(state.isPlaceLegal(new PentagoCoord(1, 0)))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(1, 2)))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, ID);

                        break;

                    case TR:
                        if(state.isPlaceLegal(new PentagoCoord(1, 3)))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(1, 5)))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, ID);

                        break;

                    case BL:
                        if(state.isPlaceLegal(new PentagoCoord(4, 0)))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(4, 2)))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, ID);

                        break;
                    case BR:
                        if(state.isPlaceLegal(new PentagoCoord(4, 3)))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(4, 5)))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, ID);

                        break;
                }
            }

            switch (quad1){
                case TL:
                    if(state.isPlaceLegal(new PentagoCoord(1, 0)))
                        return new PentagoMove(1, 0, quadrantA, quadrantB, ID);

                    break;

                case TR:
                    if(state.isPlaceLegal(new PentagoCoord(1, 3)))
                        return new PentagoMove(1, 3, quadrantA, quadrantB, ID);
                    break;

                case BL:
                    if(state.isPlaceLegal(new PentagoCoord(4, 0)))
                        return new PentagoMove(4, 0, quadrantA, quadrantB, ID);

                    break;
                case BR:
                    if(state.isPlaceLegal(new PentagoCoord(4, 3)))
                        return new PentagoMove(4, 3, quadrantA, quadrantB, ID);


                    break;
            }
        }

        // For vertical
        if((nbrWhiteVertical1 == 0) && (nbrWhiteVertical2 == 0)){
            if(nbrBlackVertical1 == 2){
                switch (quad1){
                    case TL:
                        if(state.isPlaceLegal(new PentagoCoord(0, 1)))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(2, 1)))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, ID);

                        break;

                    case TR:
                        if(state.isPlaceLegal(new PentagoCoord(0, 4)))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(2, 4)))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, ID);

                        break;

                    case BL:
                        if(state.isPlaceLegal(new PentagoCoord(3, 1)))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(5, 1)))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, ID);

                        break;
                    case BR:
                        if(state.isPlaceLegal(new PentagoCoord(3, 4)))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(5, 4)))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, ID);

                        break;
                }
            }

            if(nbrBlackVertical2 == 2){
                switch (quad2){
                    case TL:
                        if(state.isPlaceLegal(new PentagoCoord(0, 1)))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(2, 1)))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, ID);

                        break;

                    case TR:
                        if(state.isPlaceLegal(new PentagoCoord(0, 4)))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(2, 4)))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, ID);

                        break;

                    case BL:
                        if(state.isPlaceLegal(new PentagoCoord(3, 1)))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(5, 1)))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, ID);

                        break;
                    case BR:
                        if(state.isPlaceLegal(new PentagoCoord(3, 4)))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(5, 4)))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, ID);

                        break;
                }
            }

            switch (quad1){
                case TL:
                    if(state.isPlaceLegal(new PentagoCoord(0, 1)))
                        return new PentagoMove(0, 1, quadrantA, quadrantB, ID);

                    break;

                case TR:
                    if(state.isPlaceLegal(new PentagoCoord(0, 4)))
                        return new PentagoMove(0, 4, quadrantA, quadrantB, ID);

                    break;

                case BL:
                    if(state.isPlaceLegal(new PentagoCoord(3, 1)))
                        return new PentagoMove(3, 1, quadrantA, quadrantB, ID);

                    break;
                case BR:
                    if(state.isPlaceLegal(new PentagoCoord(3, 4)))
                        return new PentagoMove(3, 4, quadrantA, quadrantB, ID);

                    break;
            }
        }

        // Just fit a diagonal
        if(state.isPlaceLegal(new PentagoCoord(firstMove.getX() - 1, firstMove.getY() - 1)))
            return new PentagoMove(firstMove.getX() - 1, firstMove.getY() - 1, quadrantA, quadrantB, ID);
        if(state.isPlaceLegal(new PentagoCoord(firstMove.getX() - 1, firstMove.getY() + 1)))
            return new PentagoMove(firstMove.getX() - 1, firstMove.getY() + 1, quadrantA, quadrantB, ID);
        if(state.isPlaceLegal(new PentagoCoord(firstMove.getX() + 1, firstMove.getY() - 1)))
            return new PentagoMove(firstMove.getX() + 1, firstMove.getY() - 1, quadrantA, quadrantB, ID);
        if(state.isPlaceLegal(new PentagoCoord(firstMove.getX() + 1, firstMove.getY() + 1)))
            return new PentagoMove(firstMove.getX() + 1, firstMove.getY() + 1, quadrantA, quadrantB, ID);

        // Should not get here
        return null;
    }

    // Function that attacks for white in its 3rd or 4th move
    public static Move whiteAttack(PentagoBoardState state){

        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        PentagoCoord firstMove = initialCentreMoves.get(0);
        PentagoCoord secondMove = initialCentreMoves.get(1);

        // Set quads
        int count = 0;
        PentagoBoardState.Quadrant quad1 = null, quad2 = null;
        while(quad2 == null) {
            if (state.getPieceAt(1, 1) == PentagoBoardState.Piece.WHITE) {
                if (count == 0) {
                    quad1 = PentagoBoardState.Quadrant.TL;
                    firstMove = new PentagoCoord(1, 1);
                } else {
                    quad2 = PentagoBoardState.Quadrant.TL;
                    secondMove = new PentagoCoord(1, 1);
                }
                count++;
            }
            if (state.getPieceAt(1, 4) == PentagoBoardState.Piece.WHITE) {
                if (count == 0) {
                    quad1 = PentagoBoardState.Quadrant.TR;
                    firstMove = new PentagoCoord(1, 4);

                } else {
                    quad2 = PentagoBoardState.Quadrant.TR;
                    secondMove = new PentagoCoord(1, 4);

                }
                count++;
            }

            if (state.getPieceAt(4, 1) == PentagoBoardState.Piece.WHITE) {
                if (count == 0) {
                    quad1 = PentagoBoardState.Quadrant.BL;
                    firstMove = new PentagoCoord(4, 1);

                } else {
                    quad2 = PentagoBoardState.Quadrant.BL;
                    secondMove = new PentagoCoord(4, 1);

                }
                count++;
            }

            if (state.getPieceAt(4, 4) == PentagoBoardState.Piece.WHITE) {
                if (count == 0) {
                    quad1 = PentagoBoardState.Quadrant.BR;
                    firstMove = new PentagoCoord(4, 4);
                } else {
                    quad2 = PentagoBoardState.Quadrant.BR;
                    secondMove = new PentagoCoord(4, 4);

                }
                count++;
            }
        }

        int nbrWhite1 = getNbrUsefulPiecesHorizontal(quad1, 1, PentagoBoardState.Piece.WHITE, PentagoBoardState.Piece.BLACK, state);
        int nbrBlack1 = getNbrUsefulPiecesHorizontal(quad1, 1 , PentagoBoardState.Piece.BLACK, PentagoBoardState.Piece.WHITE, state);
        int nbrWhite2 = getNbrUsefulPiecesHorizontal(quad2, 1, PentagoBoardState.Piece.WHITE, PentagoBoardState.Piece.BLACK, state);
        int nbrBlack2 = getNbrUsefulPiecesHorizontal(quad2, 1 , PentagoBoardState.Piece.BLACK, PentagoBoardState.Piece.WHITE, state);
        int nbrWhiteVertical1 = getNbrUsefulPiecesVertical(quad1, 1 , PentagoBoardState.Piece.WHITE , PentagoBoardState.Piece.BLACK , state );
        int nbrWhiteVertical2 = getNbrUsefulPiecesVertical(quad2, 1 , PentagoBoardState.Piece.WHITE , PentagoBoardState.Piece.BLACK , state );
        int nbrBlackVertical1 = getNbrUsefulPiecesVertical(quad1, 1 , PentagoBoardState.Piece.BLACK , PentagoBoardState.Piece.WHITE , state );
        int nbrBlackVertical2 = getNbrUsefulPiecesVertical(quad2, 1 , PentagoBoardState.Piece.BLACK , PentagoBoardState.Piece.WHITE , state );

        // For horizontal
        if((nbrBlack1 == 0) && (nbrBlack2 == 0)){
            if(nbrWhite1 == 2){
                switch (quad1){
                    case TL:
                        if(state.isPlaceLegal(new PentagoCoord(1, 0)))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(1, 2)))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, ID);

                        break;

                    case TR:
                        if(state.isPlaceLegal(new PentagoCoord(1, 3)))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(1, 5)))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, ID);

                        break;

                    case BL:
                        if(state.isPlaceLegal(new PentagoCoord(4, 0)))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(4, 2)))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, ID);

                        break;
                    case BR:
                        if(state.isPlaceLegal(new PentagoCoord(4, 3)))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(4, 5)))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, ID);

                        break;
                }
            }

            if(nbrWhite2 == 2){
                switch (quad2){
                    case TL:
                        if(state.isPlaceLegal(new PentagoCoord(1, 0)))
                            return new PentagoMove(1, 0, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(1, 2)))
                            return new PentagoMove(1, 2, quadrantA, quadrantB, ID);

                        break;

                    case TR:
                        if(state.isPlaceLegal(new PentagoCoord(1, 3)))
                            return new PentagoMove(1, 3, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(1, 5)))
                            return new PentagoMove(1, 5, quadrantA, quadrantB, ID);

                        break;

                    case BL:
                        if(state.isPlaceLegal(new PentagoCoord(4, 0)))
                            return new PentagoMove(4, 0, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(4, 2)))
                            return new PentagoMove(4, 2, quadrantA, quadrantB, ID);

                        break;
                    case BR:
                        if(state.isPlaceLegal(new PentagoCoord(4, 3)))
                            return new PentagoMove(4, 3, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(4, 5)))
                            return new PentagoMove(4, 5, quadrantA, quadrantB, ID);

                        break;
                }
            }

            switch (quad1){
                case TL:
                    if(state.isPlaceLegal(new PentagoCoord(1, 0)))
                        return new PentagoMove(1, 0, quadrantA, quadrantB, ID);

                    break;

                case TR:
                    if(state.isPlaceLegal(new PentagoCoord(1, 3)))
                        return new PentagoMove(1, 3, quadrantA, quadrantB, ID);
                    break;

                case BL:
                    if(state.isPlaceLegal(new PentagoCoord(4, 0)))
                        return new PentagoMove(4, 0, quadrantA, quadrantB, ID);

                    break;
                case BR:
                    if(state.isPlaceLegal(new PentagoCoord(4, 3)))
                        return new PentagoMove(4, 3, quadrantA, quadrantB, ID);


                    break;
            }
        }

        // For vertical
        if((nbrBlackVertical1 == 0) && (nbrBlackVertical2 == 0)){
            if(nbrWhiteVertical1 == 2){
                switch (quad1){
                    case TL:
                        if(state.isPlaceLegal(new PentagoCoord(0, 1)))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(2, 1)))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, ID);

                        break;

                    case TR:
                        if(state.isPlaceLegal(new PentagoCoord(0, 4)))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(2, 4)))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, ID);

                        break;

                    case BL:
                        if(state.isPlaceLegal(new PentagoCoord(3, 1)))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(5, 1)))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, ID);

                        break;
                    case BR:
                        if(state.isPlaceLegal(new PentagoCoord(3, 4)))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(5, 4)))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, ID);

                        break;
                }
            }

            if(nbrWhiteVertical2 == 2){
                switch (quad2){
                    case TL:
                        if(state.isPlaceLegal(new PentagoCoord(0, 1)))
                            return new PentagoMove(0, 1, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(2, 1)))
                            return new PentagoMove(2, 1, quadrantA, quadrantB, ID);

                        break;

                    case TR:
                        if(state.isPlaceLegal(new PentagoCoord(0, 4)))
                            return new PentagoMove(0, 4, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(2, 4)))
                            return new PentagoMove(2, 4, quadrantA, quadrantB, ID);

                        break;

                    case BL:
                        if(state.isPlaceLegal(new PentagoCoord(3, 1)))
                            return new PentagoMove(3, 1, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(5, 1)))
                            return new PentagoMove(5, 1, quadrantA, quadrantB, ID);

                        break;
                    case BR:
                        if(state.isPlaceLegal(new PentagoCoord(3, 4)))
                            return new PentagoMove(3, 4, quadrantA, quadrantB, ID);
                        if(state.isPlaceLegal(new PentagoCoord(5, 4)))
                            return new PentagoMove(5, 4, quadrantA, quadrantB, ID);

                        break;
                }
            }

            switch (quad1){
                case TL:
                    if(state.isPlaceLegal(new PentagoCoord(0, 1)))
                        return new PentagoMove(0, 1, quadrantA, quadrantB, ID);

                    break;

                case TR:
                    if(state.isPlaceLegal(new PentagoCoord(0, 4)))
                        return new PentagoMove(0, 4, quadrantA, quadrantB, ID);

                    break;

                case BL:
                    if(state.isPlaceLegal(new PentagoCoord(3, 1)))
                        return new PentagoMove(3, 1, quadrantA, quadrantB, ID);

                    break;
                case BR:
                    if(state.isPlaceLegal(new PentagoCoord(3, 4)))
                        return new PentagoMove(3, 4, quadrantA, quadrantB, ID);

                    break;
            }
        }

        // Just fit a diagonal
        if(state.isPlaceLegal(new PentagoCoord(firstMove.getX() - 1, firstMove.getY() - 1)))
            return new PentagoMove(firstMove.getX() - 1, firstMove.getY() - 1, quadrantA, quadrantB, ID);
        if(state.isPlaceLegal(new PentagoCoord(firstMove.getX() - 1, firstMove.getY() + 1)))
            return new PentagoMove(firstMove.getX() - 1, firstMove.getY() + 1, quadrantA, quadrantB, ID);
        if(state.isPlaceLegal(new PentagoCoord(firstMove.getX() + 1, firstMove.getY() - 1)))
            return new PentagoMove(firstMove.getX() + 1, firstMove.getY() - 1, quadrantA, quadrantB, ID);
        if(state.isPlaceLegal(new PentagoCoord(firstMove.getX() + 1, firstMove.getY() + 1)))
            return new PentagoMove(firstMove.getX() + 1, firstMove.getY() + 1, quadrantA, quadrantB, ID);

        // Should not get here
        return null;
    }

    // Function that returns all legal coordinates (with random swaps)
    public static ArrayList<PentagoMove> getLegalCoordinates(PentagoBoardState state){
        ArrayList<PentagoMove> legalMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) { //Iterate through positions on board
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (state.getPieceAt(i, j) == PentagoBoardState.Piece.EMPTY) {
                    // Finding both quadrants
                    PentagoBoardState.Quadrant[] quadrants = randomSwap();
                    PentagoBoardState.Quadrant quadrantA = quadrants[0];
                    PentagoBoardState.Quadrant quadrantB = quadrants[1];
                            legalMoves.add(new PentagoMove(i, j, quadrantA, quadrantB, state.getTurnPlayer()));
                }
            }
        }
        return legalMoves;
    }

    // Check if you have bad state on horizontals, if yes do not go there
    public static boolean badStateHorizontal(PentagoBoardState state, PentagoBoardState.Piece movePlayer, PentagoBoardState.Piece opponent){
        for(int row = 0; row < BOARD_SIZE; row++){
            // First check for state where you have 4 in a row on horizontal and no opponents defending
            if((state.getPieceAt(row, 1) == movePlayer) && (state.getPieceAt(row, 2) == movePlayer) && (state.getPieceAt(row, 3) == movePlayer) && (state.getPieceAt(row, 4) == movePlayer) && (state.getPieceAt(row, 0) != opponent) && (state.getPieceAt(row, 5) != opponent))
                return true;

            // Also check if you have 3 in 1 quadrant and 1 in the middle in the other
            if((state.getPieceAt(row, 0) == movePlayer) && (state.getPieceAt(row, 1) == movePlayer) && (state.getPieceAt(row, 2) == movePlayer) && (state.getPieceAt(row, 4) == movePlayer) && (state.getPieceAt(row, 3) != opponent) && (state.getPieceAt(row, 5) != opponent))
                return true;
            if((state.getPieceAt(row, 3) == movePlayer) && (state.getPieceAt(row, 4) == movePlayer) && (state.getPieceAt(row, 5) == movePlayer) && (state.getPieceAt(row, 1) == movePlayer) && (state.getPieceAt(row, 0) != opponent) && (state.getPieceAt(row, 2) != opponent))
                return true;
        }

        return false;
    }

    // Check if you have bad state on verticals, if yes do not go there
    public static boolean badStateVertical(PentagoBoardState state, PentagoBoardState.Piece movePlayer, PentagoBoardState.Piece opponent){
        for(int col = 0; col < BOARD_SIZE; col++){
            // First check for state where you have 4 in a row on vertical and no opponents defending
            if((state.getPieceAt( 1,col) == movePlayer) && (state.getPieceAt(2,col) == movePlayer) && (state.getPieceAt(3,col) == movePlayer) && (state.getPieceAt(4,col) == movePlayer) && (state.getPieceAt(0,col) != opponent) && (state.getPieceAt(5,col) != opponent))
                return true;

            // Also check if you have 3 in 1 quadrant and 1 in the middle in the other
            if((state.getPieceAt(0,col) == movePlayer) && (state.getPieceAt(1,col) == movePlayer) && (state.getPieceAt(2,col) == movePlayer) && (state.getPieceAt(4,col) == movePlayer) && (state.getPieceAt(3,col) != opponent) && (state.getPieceAt(5,col) != opponent))
                return true;
            if((state.getPieceAt(3,col) == movePlayer) && (state.getPieceAt(4,col) == movePlayer) && (state.getPieceAt(5,col) == movePlayer) && (state.getPieceAt(1,col) == movePlayer) && (state.getPieceAt(0,col) != opponent) && (state.getPieceAt(2,col) != opponent))
                return true;
        }

        return false;
    }

    // Heuristic for 1 row, takes in 2 quadrants
    public static int HeuristicHorizontal(PentagoBoardState.Quadrant quadrant1, PentagoBoardState.Quadrant quadrant2, int row, PentagoBoardState.Piece piece, PentagoBoardState.Piece opponent, PentagoBoardState state, boolean myTurn){

        int player = 0;
        if(piece == PentagoBoardState.Piece.WHITE)
            player = PentagoBoardState.WHITE;
        if(piece == PentagoBoardState.Piece.BLACK)
            player = PentagoBoardState.BLACK;

        int row1 = row;
        int heuristic = 0;
        int row2 = row;
        int col1 = 0;
        int col2 = 0;
        int playerCount = 0;
        int opponentCount = 0;
        if((quadrant1 == PentagoBoardState.Quadrant.BR) || quadrant1 == PentagoBoardState.Quadrant.BL ) row1 = row + 3;
        if((quadrant2 == PentagoBoardState.Quadrant.BR) || quadrant2 == PentagoBoardState.Quadrant.BL ) row2 = row + 3;
        if((quadrant1 == PentagoBoardState.Quadrant.TR) || quadrant1 == PentagoBoardState.Quadrant.BR ) col1 = col1 + 3;
        if((quadrant2 == PentagoBoardState.Quadrant.TR) || quadrant2 == PentagoBoardState.Quadrant.BR ) col2 = col2 + 3;

        ArrayList<PentagoBoardState.Piece> piecesArray = new ArrayList<>();
        // Do counts of pieces
        PentagoBoardState.Piece piece1Left = state.getPieceAt(row1,col1 );
        piecesArray.add(piece1Left);
        PentagoBoardState.Piece piece1Middle = state.getPieceAt(row1,col1+1 );
        piecesArray.add(piece1Middle);
        PentagoBoardState.Piece piece1Right = state.getPieceAt(row1,col1+2 );
        piecesArray.add(piece1Right);
        PentagoBoardState.Piece piece2Left = state.getPieceAt(row2,col2 );
        piecesArray.add(piece2Left);
        PentagoBoardState.Piece piece2Middle = state.getPieceAt(row2,col2+1 );
        piecesArray.add(piece2Middle);
        PentagoBoardState.Piece piece2Right = state.getPieceAt(row2,col2+2 );
        piecesArray.add(piece2Right);
        for(int i = 0; i < 6; i++){
            if(piecesArray.get(i) == piece) playerCount++;
            if(piecesArray.get(i) == opponent) opponentCount++;
        }
        // If row is blocked, return 0

        if(state.gameOver() && (state.getWinner() == player)) return 200;

        if((((piece1Middle == opponent) || (piece2Middle == opponent)) && (playerCount >= 2)) || (((piece1Middle == piece) || (piece2Middle == piece)) && (opponentCount >= 2)) )
            return 0;
        if((opponentCount >= 2) && (playerCount > 2))
            return 0;
        if(playerCount == 5 && opponentCount == 1) heuristic = heuristic + 10;
        if(playerCount == 4 && opponentCount == 1) heuristic = heuristic + 5;
        if(playerCount == 4 && opponentCount == 0) heuristic = heuristic + 30;
        if((playerCount == 3 && opponentCount == 0) && ((piece1Left == piece && piece1Middle == piece && piece1Right == piece) || (piece2Left == piece && piece2Middle == piece && piece2Right == piece) ))
            heuristic = heuristic + 15;

        if((playerCount == 3 && opponentCount == 0) && (piece1Middle != piece && piece2Middle != piece)) // No middle
            heuristic = heuristic + 5;
        else{
            if((playerCount == 3 && opponentCount == 0) && (piece1Middle == piece || piece2Middle != piece))
                heuristic = heuristic + 10;
        }
        if((playerCount == 3 && opponentCount == 1) && (piece1Middle != piece && piece2Middle != piece)) // No middle
            heuristic = heuristic + 3;
        else{
            if((playerCount == 3 && opponentCount == 1) && (piece1Middle == piece || piece2Middle == piece)) // No middle
                heuristic = heuristic + 7;
        }
        if(playerCount == 2 && opponentCount == 1) heuristic = heuristic + 2;
        if(playerCount == 2 && opponentCount == 0) heuristic = heuristic + 4;
        if(playerCount == 1 && opponentCount == 0) heuristic = heuristic + 2;

        // Now defense
        if(myTurn) {
            if ((playerCount == 1 && opponentCount == 3) && (((piece1Left == opponent && piece1Middle == opponent && piece1Right == opponent) || (piece2Left == opponent && piece2Middle == opponent && piece2Right == opponent))) && (piece1Middle == piece || piece2Middle == piece))
                heuristic = heuristic + 20;
            else {
                if (playerCount == 1 && opponentCount == 3)
                    heuristic = heuristic + 15;
            }
            if ((playerCount == 4 && opponentCount == 1) && (piece1Middle == piece || piece2Middle == piece))
                heuristic = heuristic + 35;
            if (playerCount == 2 && opponentCount == 4)
                heuristic = heuristic + 35;
        }

        return heuristic;


    }

    // Heuristic for 1 col, takes in 2 quadrants
    public static int HeuristicVertical(PentagoBoardState.Quadrant quadrant1, PentagoBoardState.Quadrant quadrant2, int col, PentagoBoardState.Piece piece, PentagoBoardState.Piece opponent, PentagoBoardState state, boolean myTurn){

        int player = 0;
        if(piece == PentagoBoardState.Piece.WHITE)
            player = PentagoBoardState.WHITE;
        if(piece == PentagoBoardState.Piece.BLACK)
            player = PentagoBoardState.BLACK;

        int row1 = 0;
        int heuristic = 0;
        int row2 = 0;
        int col1 = col;
        int col2 = col;
        int playerCount = 0;
        int opponentCount = 0;
        if((quadrant1 == PentagoBoardState.Quadrant.BR) || quadrant1 == PentagoBoardState.Quadrant.BL ) row1 = row1 + 3;
        if((quadrant2 == PentagoBoardState.Quadrant.BR) || quadrant2 == PentagoBoardState.Quadrant.BL ) row2 = row2 + 3;
        if((quadrant1 == PentagoBoardState.Quadrant.TR) || quadrant1 == PentagoBoardState.Quadrant.BR ) col1 = col + 3;
        if((quadrant2 == PentagoBoardState.Quadrant.TR) || quadrant2 == PentagoBoardState.Quadrant.BR ) col2 = col + 3;

        if(state.gameOver() && (state.getWinner() == player)) return 200;


        ArrayList<PentagoBoardState.Piece> piecesArray = new ArrayList<>();
        // Do counts of pieces
        PentagoBoardState.Piece piece1Top = state.getPieceAt(row1,col1 );
        piecesArray.add(piece1Top);
        PentagoBoardState.Piece piece1Middle = state.getPieceAt(row1+1,col1 );
        piecesArray.add(piece1Middle);
        PentagoBoardState.Piece piece1Down = state.getPieceAt(row1+2,col1 );
        piecesArray.add(piece1Down);
        PentagoBoardState.Piece piece2Top = state.getPieceAt(row2,col2 );
        piecesArray.add(piece2Top);
        PentagoBoardState.Piece piece2Middle = state.getPieceAt(row2+1,col2 );
        piecesArray.add(piece2Middle);
        PentagoBoardState.Piece piece2Down = state.getPieceAt(row2+2,col2 );
        piecesArray.add(piece2Down);
        for(int i = 0; i < 6; i++){
            if(piecesArray.get(i) == piece) playerCount++;
            if(piecesArray.get(i) == opponent) opponentCount++;
        }
        // If row is blocked, return 0
        if((((piece1Middle == opponent) || (piece2Middle == opponent)) && (playerCount >= 2)) || (((piece1Middle == piece) || (piece2Middle == piece)) && (opponentCount >= 2)) )
            return 0;
        if((opponentCount >= 2) && (playerCount > 2))
            return 0;
        if(playerCount == 5 && opponentCount == 1) heuristic = heuristic + 10;
        if(playerCount == 4 && opponentCount == 1) heuristic = heuristic + 5;
        if(playerCount == 4 && opponentCount == 0) heuristic = heuristic + 30;
        if((playerCount == 3 && opponentCount == 0) && ((piece1Top == piece && piece1Middle == piece && piece1Down == piece) || (piece2Top == piece && piece2Middle == piece && piece2Down == piece) ))
            heuristic = heuristic + 15;

        if((playerCount == 3 && opponentCount == 0) && (piece1Middle != piece && piece2Middle != piece)) // No middle
            heuristic = heuristic + 5;
        else{
            if((playerCount == 3 && opponentCount == 0) && (piece1Middle == piece || piece2Middle != piece))
                heuristic = heuristic + 10;
        }
        if((playerCount == 3 && opponentCount == 1) && (piece1Middle != piece && piece2Middle != piece)) // No middle
            heuristic = heuristic + 3;
        else{
            if((playerCount == 3 && opponentCount == 1) && (piece1Middle == piece || piece2Middle == piece)) // No middle
                heuristic = heuristic + 7;
        }
        if(playerCount == 2 && opponentCount == 1) heuristic = heuristic + 2;
        if(playerCount == 2 && opponentCount == 0) heuristic = heuristic + 4;
        if(playerCount == 1 && opponentCount == 0) heuristic = heuristic + 2;

        // Now defense
        if(myTurn) {
            if ((playerCount == 1 && opponentCount == 3) && (((piece1Top == opponent && piece1Middle == opponent && piece1Down == opponent) || (piece2Top == opponent && piece2Middle == opponent && piece2Down == opponent))) && (piece1Middle == piece || piece2Middle == piece))
                heuristic = heuristic + 20;
            else {
                if (playerCount == 1 && opponentCount == 3)
                    heuristic = heuristic + 15;
            }
            if ((playerCount == 4 && opponentCount == 1) && (piece1Middle == piece || piece2Middle == piece))
                heuristic = heuristic + 35;
            if (playerCount == 2 && opponentCount == 4)
                heuristic = heuristic + 35;
        }

        return heuristic;


    }

    // Heuristic for 1 diagonal, takes in 2 quadrants
    public static int HeuristicDiagonal(PentagoBoardState.Quadrant quadrant1, PentagoBoardState.Quadrant quadrant2, int diagType, PentagoBoardState.Piece piece, PentagoBoardState.Piece opponent, PentagoBoardState state, boolean myTurn){

        int player = 0;
        if(piece == PentagoBoardState.Piece.WHITE)
            player = PentagoBoardState.WHITE;
        if(piece == PentagoBoardState.Piece.BLACK)
            player = PentagoBoardState.BLACK;

        if(state.gameOver() && (state.getWinner() == player)) return 200;


        int row1 = 2;
        boolean ascending = false;
        int heuristic = 0;
        int row2 = 2;
        int col1 = 0;
        int col2 = 0;
        int playerCount = 0;
        int opponentCount = 0;
        ascending = (diagType == 1) ? true : false;
        if(ascending) {
            if ((quadrant1 == PentagoBoardState.Quadrant.BR) || quadrant1 == PentagoBoardState.Quadrant.BL)
                row1 = 5;
            if ((quadrant2 == PentagoBoardState.Quadrant.BR) || quadrant2 == PentagoBoardState.Quadrant.BL)
                row2 = 5;
            if ((quadrant1 == PentagoBoardState.Quadrant.TR) || quadrant1 == PentagoBoardState.Quadrant.BR)
                col1 = 3;
            if ((quadrant2 == PentagoBoardState.Quadrant.TR) || quadrant2 == PentagoBoardState.Quadrant.BR)
                col2 = 3;
        }
        else{ // descending
            if ((quadrant1 == PentagoBoardState.Quadrant.BR) || quadrant1 == PentagoBoardState.Quadrant.BL)
                row1 = 3;
            else row1 = 0;
            if ((quadrant2 == PentagoBoardState.Quadrant.BR) || quadrant2 == PentagoBoardState.Quadrant.BL)
                row2 = 3;
            else row2 = 0;
            if ((quadrant1 == PentagoBoardState.Quadrant.TR) || quadrant1 == PentagoBoardState.Quadrant.BR)
                col1 = 3;
            if ((quadrant2 == PentagoBoardState.Quadrant.TR) || quadrant2 == PentagoBoardState.Quadrant.BR)
                col2 = 3;
        }

        ArrayList<PentagoBoardState.Piece> piecesArray = new ArrayList<>();
        // Do counts of pieces
        PentagoBoardState.Piece piece1Left, piece1Middle, piece1Right, piece2Left, piece2Middle, piece2Right;
        if(ascending) {
            piece1Left = state.getPieceAt(row1, col1);
            piecesArray.add(piece1Left);
            piece1Middle = state.getPieceAt(row1-1, col1 + 1);
            piecesArray.add(piece1Middle);
            piece1Right = state.getPieceAt(row1-2, col1 + 2);
            piecesArray.add(piece1Right);
            piece2Left = state.getPieceAt(row2, col2);
            piecesArray.add(piece2Left);
            piece2Middle = state.getPieceAt(row2-1, col2 + 1);
            piecesArray.add(piece2Middle);
            piece2Right = state.getPieceAt(row2-2, col2 + 2);
            piecesArray.add(piece2Right);
        }
        else{
            piece1Left = state.getPieceAt(row1, col1);
            piecesArray.add(piece1Left);
            piece1Middle = state.getPieceAt(row1+1, col1 + 1);
            piecesArray.add(piece1Middle);
            piece1Right = state.getPieceAt(row1+2, col1 + 2);
            piecesArray.add(piece1Right);
            piece2Left = state.getPieceAt(row2, col2);
            piecesArray.add(piece2Left);
            piece2Middle = state.getPieceAt(row2+1, col2 + 1);
            piecesArray.add(piece2Middle);
            piece2Right = state.getPieceAt(row2+2, col2 + 2);
            piecesArray.add(piece2Right);
        }
        for(int i = 0; i < 6; i++){
            if(piecesArray.get(i) == piece) playerCount++;
            if(piecesArray.get(i) == opponent) opponentCount++;
        }
        // If row is blocked, return 0
        if((((piece1Middle == opponent) || (piece2Middle == opponent)) && (playerCount >= 2)) || (((piece1Middle == piece) || (piece2Middle == piece)) && (opponentCount >= 2)) )
            return 0;
        if((opponentCount >= 2) && (playerCount > 2))
            return 0;
        if(playerCount == 5 && opponentCount == 1) heuristic = heuristic + 10;
        if(playerCount == 4 && opponentCount == 1) heuristic = heuristic + 5;
        if(playerCount == 4 && opponentCount == 0) heuristic = heuristic + 30;
        if((playerCount == 3 && opponentCount == 0) && ((piece1Left == piece && piece1Middle == piece && piece1Right == piece) || (piece2Left == piece && piece2Middle == piece && piece2Right == piece) ))
            heuristic = heuristic + 15;

        if((playerCount == 3 && opponentCount == 0) && (piece1Middle != piece && piece2Middle != piece)) // No middle
            heuristic = heuristic + 5;
        else{
            if((playerCount == 3 && opponentCount == 0) && (piece1Middle == piece || piece2Middle != piece))
                heuristic = heuristic + 10;
        }
        if((playerCount == 3 && opponentCount == 1) && (piece1Middle != piece && piece2Middle != piece)) // No middle
            heuristic = heuristic + 3;
        else{
            if((playerCount == 3 && opponentCount == 1) && (piece1Middle == piece || piece2Middle == piece)) // No middle
                heuristic = heuristic + 7;
        }
        if(playerCount == 2 && opponentCount == 1) heuristic = heuristic + 2;
        if(playerCount == 2 && opponentCount == 0) heuristic = heuristic + 4;
        if(playerCount == 1 && opponentCount == 0) heuristic = heuristic + 2;

        // Now defense
        if(myTurn) {
            if ((playerCount == 1 && opponentCount == 3) && (((piece1Left == opponent && piece1Middle == opponent && piece1Right == opponent) || (piece2Left == opponent && piece2Middle == opponent && piece2Right == opponent))) && (piece1Middle == piece || piece2Middle == piece))
                heuristic = heuristic + 20;
            else {
                if (playerCount == 1 && opponentCount == 3)
                    heuristic = heuristic + 15;
            }
            if ((playerCount == 4 && opponentCount == 1) && (piece1Middle == piece || piece2Middle == piece))
                heuristic = heuristic + 35;
            if (playerCount == 2 && opponentCount == 4)
                heuristic = heuristic + 35;

        }
        return heuristic;


    }

    // If you face a very bad state, force a defend move
    public static PentagoMove checkIfForceMoveHorizontal(PentagoBoardState.Quadrant quadrant1, PentagoBoardState.Quadrant quadrant2, int row,PentagoBoardState.Piece piece, PentagoBoardState.Piece opponent, PentagoBoardState state){

        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        int row1 = row;
        int heuristic = 0;
        int row2 = row;
        int col1 = 0;
        int col2 = 0;
        int playerCount = 0;
        int opponentCount = 0;
        if((quadrant1 == PentagoBoardState.Quadrant.BR) || quadrant1 == PentagoBoardState.Quadrant.BL ) row1 = row + 3;
        if((quadrant2 == PentagoBoardState.Quadrant.BR) || quadrant2 == PentagoBoardState.Quadrant.BL ) row2 = row + 3;
        if((quadrant1 == PentagoBoardState.Quadrant.TR) || quadrant1 == PentagoBoardState.Quadrant.BR ) col1 = col1 + 3;
        if((quadrant2 == PentagoBoardState.Quadrant.TR) || quadrant2 == PentagoBoardState.Quadrant.BR ) col2 = col2 + 3;

        ArrayList<PentagoBoardState.Piece> piecesArray = new ArrayList<>();
        ArrayList<PentagoBoardState.Piece> emptyPieces = new ArrayList<>();
        // Do counts of pieces
        PentagoBoardState.Piece piece1Left = state.getPieceAt(row1,col1 );
        piecesArray.add(piece1Left);
        PentagoBoardState.Piece piece1Middle = state.getPieceAt(row1,col1+1 );
        piecesArray.add(piece1Middle);
        PentagoBoardState.Piece piece1Right = state.getPieceAt(row1,col1+2 );
        piecesArray.add(piece1Right);
        PentagoBoardState.Piece piece2Left = state.getPieceAt(row2,col2 );
        piecesArray.add(piece2Left);
        PentagoBoardState.Piece piece2Middle = state.getPieceAt(row2,col2+1 );
        piecesArray.add(piece2Middle);
        PentagoBoardState.Piece piece2Right = state.getPieceAt(row2,col2+2 );
        piecesArray.add(piece2Right);
        for(int i = 0; i < 6; i++){
            if(piecesArray.get(i) == PentagoBoardState.Piece.EMPTY) emptyPieces.add(piecesArray.get(i));
            if(piecesArray.get(i) == piece) playerCount++;
            if(piecesArray.get(i) == opponent) opponentCount++;
        }
        if((playerCount == 0 || playerCount == 1) && opponentCount == 4 && (piece1Middle != piece && piece2Middle != piece)){
            if(piece1Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1+1, quadrantA, quadrantB, ID);
            if(piece2Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2+1, quadrantA, quadrantB, ID);
            if(piece1Left == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1, quadrantA, quadrantB, ID);
            if(piece2Left == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2, quadrantA, quadrantB, ID);
            if(piece1Right == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1+2, quadrantA, quadrantB, ID);
            if(piece2Right == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2+2, quadrantA, quadrantB, ID);
        }
        if ((playerCount == 0 && opponentCount == 3) && (((piece1Left == opponent && piece1Middle == opponent && piece1Right == opponent) || (piece2Left == opponent && piece2Middle == opponent && piece2Right == opponent)))){
            if(piece1Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1+1, quadrantA, quadrantB, ID);
            if(piece2Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2+1, quadrantA, quadrantB, ID);
        }
        if(playerCount == 0 && opponentCount == 3){
            if(piece1Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1+1, quadrantA, quadrantB, ID);
            if(piece2Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2+1, quadrantA, quadrantB, ID);
            if(piece1Left == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1, quadrantA, quadrantB, ID);
            if(piece2Left == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2, quadrantA, quadrantB, ID);
            if(piece1Right == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1+2, quadrantA, quadrantB, ID);
            if(piece2Right == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2+2, quadrantA, quadrantB, ID);
        }
        return null;
    }

    public static PentagoMove checkIfForceMoveVertical(PentagoBoardState.Quadrant quadrant1, PentagoBoardState.Quadrant quadrant2, int col,PentagoBoardState.Piece piece, PentagoBoardState.Piece opponent, PentagoBoardState state){

        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        int row1 = 0;
        int heuristic = 0;
        int row2 = 0;
        int col1 = col;
        int col2 = col;
        int playerCount = 0;
        int opponentCount = 0;
        if((quadrant1 == PentagoBoardState.Quadrant.BR) || quadrant1 == PentagoBoardState.Quadrant.BL ) row1 = row1 + 3;
        if((quadrant2 == PentagoBoardState.Quadrant.BR) || quadrant2 == PentagoBoardState.Quadrant.BL ) row2 = row2 + 3;
        if((quadrant1 == PentagoBoardState.Quadrant.TR) || quadrant1 == PentagoBoardState.Quadrant.BR ) col1 = col + 3;
        if((quadrant2 == PentagoBoardState.Quadrant.TR) || quadrant2 == PentagoBoardState.Quadrant.BR ) col2 = col + 3;

        ArrayList<PentagoBoardState.Piece> piecesArray = new ArrayList<>();
        ArrayList<PentagoBoardState.Piece> emptyPieces = new ArrayList<>();
        // Do counts of pieces
        PentagoBoardState.Piece piece1Top = state.getPieceAt(row1,col1 );
        piecesArray.add(piece1Top);
        PentagoBoardState.Piece piece1Middle = state.getPieceAt(row1+1,col1 );
        piecesArray.add(piece1Middle);
        PentagoBoardState.Piece piece1Bottom = state.getPieceAt(row1+2,col1 );
        piecesArray.add(piece1Bottom);
        PentagoBoardState.Piece piece2Top = state.getPieceAt(row2,col2 );
        piecesArray.add(piece2Top);
        PentagoBoardState.Piece piece2Middle = state.getPieceAt(row2+1,col2 );
        piecesArray.add(piece2Middle);
        PentagoBoardState.Piece piece2Bottom = state.getPieceAt(row2+2,col2 );
        piecesArray.add(piece2Bottom);
        for(int i = 0; i < 6; i++){
            if(piecesArray.get(i) == PentagoBoardState.Piece.EMPTY) emptyPieces.add(piecesArray.get(i));
            if(piecesArray.get(i) == piece) playerCount++;
            if(piecesArray.get(i) == opponent) opponentCount++;
        }
        if((playerCount == 0 || playerCount == 1) && opponentCount == 4 && (piece1Middle != piece && piece2Middle != piece)){
            if(piece1Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1+1, col1, quadrantA, quadrantB, ID);
            if(piece2Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2+1, col2, quadrantA, quadrantB, ID);
            if(piece1Top == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1, quadrantA, quadrantB, ID);
            if(piece2Top == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2, quadrantA, quadrantB, ID);
            if(piece1Bottom == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1+2, col1, quadrantA, quadrantB, ID);
            if(piece2Bottom == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2+2, col2, quadrantA, quadrantB, ID);
        }
        if ((playerCount == 0 && opponentCount == 3) && (((piece1Top == opponent && piece1Middle == opponent && piece1Bottom == opponent) || (piece2Top == opponent && piece2Middle == opponent && piece2Bottom == opponent)))){
            if(piece1Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1+1, col1, quadrantA, quadrantB, ID);
            if(piece2Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2+1, col2, quadrantA, quadrantB, ID);
        }
        if(playerCount == 0 && opponentCount == 3){
            if(piece1Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1+1, col1, quadrantA, quadrantB, ID);
            if(piece2Middle == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2+1, col2, quadrantA, quadrantB, ID);
            if(piece1Top == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1, col1, quadrantA, quadrantB, ID);
            if(piece2Top == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2, col2, quadrantA, quadrantB, ID);
            if(piece1Bottom == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row1+2, col1, quadrantA, quadrantB, ID);
            if(piece2Bottom == PentagoBoardState.Piece.EMPTY) return new PentagoMove(row2+2, col2, quadrantA, quadrantB, ID);
        }
        return null;
    }

    public static PentagoMove checkIfForceMoveDiagonal(PentagoBoardState.Quadrant quadrant1, PentagoBoardState.Quadrant quadrant2, int diagType,PentagoBoardState.Piece piece, PentagoBoardState.Piece opponent, PentagoBoardState state){

        // Finding both quadrants
        PentagoBoardState.Quadrant[] quadrants = randomSwap();
        PentagoBoardState.Quadrant quadrantA = quadrants[0];
        PentagoBoardState.Quadrant quadrantB = quadrants[1];

        int row1 = 2;
        boolean ascending = false;
        int heuristic = 0;
        int row2 = 2;
        int col1 = 0;
        int col2 = 0;
        int playerCount = 0;
        int opponentCount = 0;
        ascending = (diagType == 1) ? true : false;
        if(ascending) {
            if ((quadrant1 == PentagoBoardState.Quadrant.BR) || quadrant1 == PentagoBoardState.Quadrant.BL)
                row1 = 5;
            if ((quadrant2 == PentagoBoardState.Quadrant.BR) || quadrant2 == PentagoBoardState.Quadrant.BL)
                row2 = 5;
            if ((quadrant1 == PentagoBoardState.Quadrant.TR) || quadrant1 == PentagoBoardState.Quadrant.BR)
                col1 = 3;
            if ((quadrant2 == PentagoBoardState.Quadrant.TR) || quadrant2 == PentagoBoardState.Quadrant.BR)
                col2 = 3;
        }
        else{ // descending
            if ((quadrant1 == PentagoBoardState.Quadrant.BR) || quadrant1 == PentagoBoardState.Quadrant.BL)
                row1 = 3;
            else row1 = 0;
            if ((quadrant2 == PentagoBoardState.Quadrant.BR) || quadrant2 == PentagoBoardState.Quadrant.BL)
                row2 = 3;
            else row2 = 0;
            if ((quadrant1 == PentagoBoardState.Quadrant.TR) || quadrant1 == PentagoBoardState.Quadrant.BR)
                col1 = 3;
            if ((quadrant2 == PentagoBoardState.Quadrant.TR) || quadrant2 == PentagoBoardState.Quadrant.BR)
                col2 = 3;
        }

        ArrayList<PentagoBoardState.Piece> piecesArray = new ArrayList<>();
        ArrayList<PentagoBoardState.Piece> emptyPieces = new ArrayList<>();

        // Do counts of pieces
        PentagoBoardState.Piece piece1Left, piece1Middle, piece1Right, piece2Left, piece2Middle, piece2Right;
        if(ascending) {
            piece1Left = state.getPieceAt(row1, col1);
            piecesArray.add(piece1Left);
            piece1Middle = state.getPieceAt(row1-1, col1 + 1);
            piecesArray.add(piece1Middle);
            piece1Right = state.getPieceAt(row1-2, col1 + 2);
            piecesArray.add(piece1Right);
            piece2Left = state.getPieceAt(row2, col2);
            piecesArray.add(piece2Left);
            piece2Middle = state.getPieceAt(row2-1, col2 + 1);
            piecesArray.add(piece2Middle);
            piece2Right = state.getPieceAt(row2-2, col2 + 2);
            piecesArray.add(piece2Right);
        }
        else{
            piece1Left = state.getPieceAt(row1, col1);
            piecesArray.add(piece1Left);
            piece1Middle = state.getPieceAt(row1+1, col1 + 1);
            piecesArray.add(piece1Middle);
            piece1Right = state.getPieceAt(row1+2, col1 + 2);
            piecesArray.add(piece1Right);
            piece2Left = state.getPieceAt(row2, col2);
            piecesArray.add(piece2Left);
            piece2Middle = state.getPieceAt(row2+1, col2 + 1);
            piecesArray.add(piece2Middle);
            piece2Right = state.getPieceAt(row2+2, col2 + 2);
            piecesArray.add(piece2Right);
        }
        for(int i = 0; i < 6; i++){
            if(piecesArray.get(i) == piece) playerCount++;
            if(piecesArray.get(i) == opponent) opponentCount++;
            if(piecesArray.get(i) == PentagoBoardState.Piece.EMPTY) emptyPieces.add(piecesArray.get(i));

        }
        if(ascending) {
            if ((playerCount == 0 || playerCount == 1) && opponentCount == 4 && (piece1Middle != piece && piece2Middle != piece)) {
                if (piece1Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1-1, col1 + 1, quadrantA, quadrantB, ID);
                if (piece2Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2-1, col2 + 1, quadrantA, quadrantB, ID);
                if (piece1Left == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1, col1, quadrantA, quadrantB, ID);
                if (piece2Left == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2, col2, quadrantA, quadrantB, ID);
                if (piece1Right == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1-2, col1 + 2, quadrantA, quadrantB, ID);
                if (piece2Right == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2-2, col2 + 2, quadrantA, quadrantB, ID);
            }
            if ((playerCount == 0 && opponentCount == 3) && (((piece1Left == opponent && piece1Middle == opponent && piece1Right == opponent) || (piece2Left == opponent && piece2Middle == opponent && piece2Right == opponent)))) {
                if (piece1Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1-1, col1 + 1, quadrantA, quadrantB, ID);
                if (piece2Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2-1, col2 + 1, quadrantA, quadrantB, ID);
            }
            if(playerCount == 0 && opponentCount == 3){
                if (piece1Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1-1, col1 + 1, quadrantA, quadrantB, ID);
                if (piece2Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2-1, col2 + 1, quadrantA, quadrantB, ID);
                if (piece1Left == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1, col1, quadrantA, quadrantB, ID);
                if (piece2Left == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2, col2, quadrantA, quadrantB, ID);
                if (piece1Right == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1-2, col1 + 2, quadrantA, quadrantB, ID);
                if (piece2Right == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2-2, col2 + 2, quadrantA, quadrantB, ID);
            }
        }
        else {
            if ((playerCount == 0 || playerCount == 1) && opponentCount == 4 && (piece1Middle != piece && piece2Middle != piece)) {
                if (piece1Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1+1, col1 + 1, quadrantA, quadrantB, ID);
                if (piece2Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2+1, col2 + 1, quadrantA, quadrantB, ID);
                if (piece1Left == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1, col1, quadrantA, quadrantB, ID);
                if (piece2Left == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2, col2, quadrantA, quadrantB, ID);
                if (piece1Right == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1+2, col1 + 2, quadrantA, quadrantB, ID);
                if (piece2Right == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2+2, col2 + 2, quadrantA, quadrantB, ID);
            }
            if ((playerCount == 0 && opponentCount == 3) && (((piece1Left == opponent && piece1Middle == opponent && piece1Right == opponent) || (piece2Left == opponent && piece2Middle == opponent && piece2Right == opponent)))) {
                if (piece1Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1+1, col1 + 1, quadrantA, quadrantB, ID);
                if (piece2Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2+1, col2 + 1, quadrantA, quadrantB, ID);
            }
            if(playerCount == 0 && opponentCount == 3){
                if (piece1Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1+1, col1 + 1, quadrantA, quadrantB, ID);
                if (piece2Middle == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2+1, col2 + 1, quadrantA, quadrantB, ID);
                if (piece1Left == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1, col1, quadrantA, quadrantB, ID);
                if (piece2Left == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2, col2, quadrantA, quadrantB, ID);
                if (piece1Right == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row1+2, col1 + 2, quadrantA, quadrantB, ID);
                if (piece2Right == PentagoBoardState.Piece.EMPTY)
                    return new PentagoMove(row2+2, col2 + 2, quadrantA, quadrantB, ID);
            }
        }
        return null;
    }

}