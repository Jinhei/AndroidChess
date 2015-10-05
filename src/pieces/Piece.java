package pieces;

import cs213.chess.Board;
import cs213.util.Utils;

/**
 * Abstract class for pieces
 * @author Nicholas Fong
 * @author Jeffrey Kang
 */
public abstract class Piece {
	public String name;
	public String color;
	//public String location;
	public int x;
	public int y;
	public int hasMoved;
	public boolean isDone;
	public boolean isChanged;
	
	public Piece(String color, String location){
		this.hasMoved = 0;
		this.color = color;
		this.isDone = false;
		this.isChanged = true;
		setLocation(new String(location));
	}
	
	/**
	 * Constructor used for promotion
	 * @param piece
	 */
	public Piece(Piece piece){
		this.name = new String(piece.name);
		this.color = new String(piece.color);
		this.x = piece.x;
		this.y = piece.y;
		this.hasMoved = piece.hasMoved;
		this.isDone = piece.isDone;
	}
	
	/**
	 * Checks if the move is possible for the piece type
	 * @param location
	 * @param board
	 * @return true if possible, else false
	 */
	public abstract boolean isPossibleMove(String location, Board board);
	
	/**
	 * Checks if the move is valid for the piece
	 * @param location
	 * @return 1 if the move is valid, -1 if invalid,
	 * 0 if special move completed, 2 if promotion
	 */
	public abstract int isValidMove(String location, Board board);
	
	
	/*
	 * Checks if the piece can directly take the location
	 * @param location
	 * @param board
	 * @return true if the piece can directly take the piece, else
	 * false
	 */
	/*public abstract boolean canPieceTake(String location, Board board);*/
	
	/**
	 * Checks if location is a valid move, then 
	 * sets the new location of the piece
	 * @param location - new position for piece
	 * @return 1 if piece was moved, 0 if castle
	 * was completed, -1 if invalid move, 2 
	 * if promotion was completed, 3 for first 
	 * pawn move, 4 for non-taking pawn move
	 */
	public int move(String location, Board board){
		int valid = isValidMove(location, board);
		Utils.debug(name+" returning "+valid+" for move()");
		if (valid == 3) {
			board.enpassant = true;
			this.hasMoved++;
			board.remove(Utils.generateCoord(x, y));
			setLocation(location);
			board.put(location, this);
			return valid;
		} else if (valid != -1) {
			//board.enpassant = false;
		} if (valid == 1 || valid == 4 || valid == 0){
			board.enpassant = false;
			this.hasMoved++;
			board.remove(Utils.generateCoord(x, y));
			setLocation(location);
			board.put(location, this);
			return valid;
		} else return valid;
	}
	
	/**
	 * Moves the piece to the given location without checking
	 * for validity.
	 * @param location - coord (e.g. "a2");
	 * @param board 
	 */
	public void forceMove(String location, Board board){
		board.remove(Utils.generateCoord(x, y));
		setLocation(location);
		board.put(location, this);
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	/**
	 * Helper function for setting x and y location
	 * @param location
	 */
	protected void setLocation(String location){
		//this.location = location;
		this.x = Utils.letterToCoord(location.substring(0, 1));
		//System.out.println(location.substring(1));
		this.y = Integer.parseInt(location.substring(1));
	}
}
