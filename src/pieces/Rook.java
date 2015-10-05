package pieces;

import cs213.chess.Board;
import cs213.util.Utils;

/**
 * 
 * @author Nicholas Fong
 * @author Jeffrey Kang
 */
public class Rook extends Piece {

	public Rook (String color, String location){
		super(color, location);
		if (color.equals("black"))
			this.name = "bR";
		else this.name = "wR";
		hasMoved = 0;
	}
	
	public Rook (Piece p){
		super(p);
		hasMoved = p.hasMoved;
		if (color.equals("black"))
			this.name = "bR";
		else this.name = "wR";
	}
	
	@Override
	public boolean isPossibleMove(String location, Board board) {
		int newX = Utils.letterToCoord(location.substring(0, 1));
		int newY = Integer.parseInt(location.substring(1));
		Utils.info(name+" input location: "+newX+", "+newY);
		
		if ((x == newX || y == newY) && !(newX == x && newY == y)){
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
		
		if ((x == newX || y == newY) && !(newX == x && newY == y)){
			Utils.info(name+" location ok");
			
			// check if any pieces in the way
			if (x != newX){
				for (int i = Math.min(x, newX)+1; i < Math.max(x, newX); i++){
					if (board.get(Utils.generateCoord(i, y)) != null){
						Utils.info(name+" returning -1: piece in the way.");
						return -1;
					}
				}
			} else if (y != newY){
				for (int i = Math.min(y, newY)+1; i < Math.max(y, newY); i++){
					if (board.get(Utils.generateCoord(x, i)) != null){
						Utils.info(name+" returning -1: piece in the way.");
						return -1;
					}
				}
			}
			
			// check for self-check
			Board b = new Board(board);
			b.remove(Utils.generateCoord(this.x, this.y));
			Rook k = new Rook(this);
			String newC = Utils.generateCoord(newX, newY);
			k.setLocation(newC);
			k.isDone = true;
			b.put(newC, k);
			if(Utils.isKingInCheck(this.color, b)){
				Utils.info(name+" returning -1: cannot self-check");
				return -1;
			} 
			
			Utils.info(name+" returning 1: success.");
			return 1;
		}
		
		Utils.info(name+" returning -1: invalid move.");
		return -1;
	}

}
