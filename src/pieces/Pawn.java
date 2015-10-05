package pieces;

import cs213.util.Utils;
import cs213.chess.Board;

/**
 * 
 * @author Nicholas Fong
 * @author Jeffrey Kang
 */
public class Pawn extends Piece{
	
	public Pawn (String color, String location){
		super(color, location);
		if (color.equals("black"))
			this.name = "bP";
		else this.name = "wP";
		hasMoved = 0;
		isChanged = false;
	}
	
	public Pawn (Piece p){
		super(p);
		isChanged = p.isChanged;
		hasMoved = p.hasMoved;
	}
	
	@Override
	public boolean isPossibleMove(String location, Board board) {
		
		int newX = Utils.letterToCoord(location.substring(0, 1));
		int newY = Integer.parseInt(location.substring(1));
		Utils.info(name+" input location: "+newX+", "+newY);
		
		if ((this.color.equals("white") && newY == y+1 && (newX == x+1 || newX == x-1)) || 
				(this.color.equals("black") && newY == y-1 && (newX == x+1 || newX == x-1))){
			return true;
		}
		if (((this.color.equals("white") && newY == y+1) || 
				(this.color.equals("black") && newY == y-1)) && newX == x){
			return true;
		}
		if (((this.color.equals("white") && newY == y+2) || 
				(this.color.equals("black") && newY == y-2)) && newX == x){
			return true;
		}
		
		return false;
	}

	@Override
	public int isValidMove(String location, Board board) {
		if (Utils.isOutOfBounds(location)){
			Utils.info(name+" returning -1: location out of bounds.");
			return -1;
		}
		Piece cp = board.get(location);
		if (cp != null){
			if (cp.color.equals(this.color)){
				Utils.info(name+" returning -1: friendly piece already at location");
				return -1;
			}
		}
		
		int newX = Utils.letterToCoord(location.substring(0, 1));
		int newY = Integer.parseInt(location.substring(1));
		Utils.info(name+" input location: "+newX+", "+newY);
		
		// normal movement
		if (((this.color.equals("white") && newY == y+1) || 
				(this.color.equals("black") && newY == y-1)) && newX == x){
			Utils.info(name+" normal movement.");
			Piece p = board.get(location);
			if (p != null){ // pawn can't take going forward
				Utils.info(name+" returning -1: piece already there");
				return -1;
			} else {
				
				// check for self-check
				Board b = new Board(board);
				b.remove(Utils.generateCoord(this.x, this.y));
				Pawn k = new Pawn(this);
				String newC = Utils.generateCoord(newX, newY);
				k.setLocation(newC);
				k.isDone = true;
				b.put(newC, k);
				if(Utils.isKingInCheck(this.color, b)){
					Utils.info(name+" returning -1: cannot self-check");
					return -1;
				} 
				
				Utils.info(name+" returning 4: normal movement success");
				return 4;
			}
		}
		
		// diagonal take
		if ((this.color.equals("white") && newY == y+1 && (newX == x+1 || newX == x-1)) || 
				(this.color.equals("black") && newY == y-1 && (newX == x+1 || newX == x-1))){
			Utils.info(name+" diagonal take");
			Piece p = board.get(location);
			if (p == null){
				//wait for en-passant
			} else {
				// check for self-check
				Board b = new Board(board);
				b.remove(Utils.generateCoord(this.x, this.y));
				Pawn k = new Pawn(this);
				String newC = Utils.generateCoord(newX, newY);
				k.setLocation(newC);
				k.isDone = true;
				b.put(newC, k);
				if(Utils.isKingInCheck(this.color, b)){
					Utils.info(name+" returning -1: cannot self-check");
					return -1;
				} 
				
				Utils.info(name+" returning 1: diagonal take success");
				return 1;
			}
		}
		
		// starting moves
		if (this.hasMoved == 0){
			Utils.info(name+" starting move");
			
			// move 2
			if (((this.color.equals("white") && newY == y+2) || 
					(this.color.equals("black") && newY == y-2)) && newX == x){
				Utils.info(name+" moving 2 spaces");
				// check for pieces in the way
				if(this.color.equals("white")){
					Piece p = board.get(Utils.generateCoord(x, y+1));
					if (p != null){
						Utils.info(name+" returning -1: piece in the way");
						return -1;
					}
					p = board.get(Utils.generateCoord(x, y+2));
					if (p != null){
						Utils.info(name+" returning -1: piece in the way");
						return -1;
					}
				} else { //checks for black side
					Piece p = board.get(Utils.generateCoord(x, y-1));
					if (p != null) {
						Utils.info(name+" returning -1: piece in the way");
						return -1;
					}
					p = board.get(Utils.generateCoord(x, y-2));
					if (p != null){
						Utils.info(name+" returning -1: piece in the way");
						return -1;
					}
				}
				
				// check for self-check
				Board b = new Board(board);
				b.remove(Utils.generateCoord(this.x, this.y));
				Pawn k = new Pawn(this);
				String newC = Utils.generateCoord(newX, newY);
				k.setLocation(newC);
				k.isDone = true;
				b.put(newC, k);
				if(Utils.isKingInCheck(this.color, b)){
					Utils.info(name+" returning -1: cannot self-check");
					return -1;
				} 
				
				Utils.info(name+" returning 3: successful 2 space starting move");
				return 3;
			}
		}
			
		// en passant
		if (board.enpassant){
			Utils.info(name+" in enpassant");
			if (((this.color.equals("white") && newY == y+1) || 
				(this.color.equals("black") && newY == y-1)) && (newX == x+1 || newX == x-1)) {
				String coord = Utils.generateCoord(newX, y);				
				Piece piece = board.get(coord);
				if (piece != null){
					if(!piece.color.equals(this.color) && piece.getClass().getName().equals("pieces.Pawn")){
						
						// check for self-check
						Board b = new Board(board);
						b.remove(Utils.generateCoord(this.x, this.y));
						Pawn k = new Pawn(this);
						String newC = Utils.generateCoord(newX, newY);
						k.setLocation(newC);
						k.isDone = true;
						b.put(newC, k);
						if(Utils.isKingInCheck(this.color, b)){
							Utils.info(name+" returning -1: cannot self-check");
							return -1;
						} 
						
						// takes piece
						board.remove(coord);
						Utils.info(name+" returning 4: successful en passant");
						return 4;
					}
				}
			}
			
		}
		
		
		Utils.info(name+" returning -1: invalid move");
		return -1;
	}
	
}
