package pieces;

import cs213.chess.Board;
import cs213.util.Utils;

/**
 * 
 * @author Nicholas Fong
 * @author Jeffrey Kang
 */
public class Knight extends Piece {

	public Knight (String color, String location){
		super(color, location);
		if (color.equals("black"))
			this.name = "bN";
		else this.name = "wN";
	}
	
	public Knight (Piece p){
		super(p);
		if (color.equals("black"))
			this.name = "bN";
		else this.name = "wN";
	}
	
	@Override
	public boolean isPossibleMove(String location, Board board) {
		int newX = Utils.letterToCoord(location.substring(0, 1));
		int newY = Integer.parseInt(location.substring(1));
		Utils.info(name+" current location: "+x+", "+y);
		Utils.info(name+" input location: "+newX+", "+newY);
		
			// up/down 2, left/right 1
		if (((newX == x+1 || newX == x-1) && (newY == y+2 || newY == y-2)) ||
			// right/left 2, up/down 1
			((newX == x+2 || newX == x-2) && (newY == y+1 || newY == y-1))){
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
		Utils.info(name+" current location: "+x+", "+y);
		Utils.info(name+" input location: "+newX+", "+newY);
		
			// up/down 2, left/right 1
		if (((newX == x+1 || newX == x-1) && (newY == y+2 || newY == y-2)) ||
			// right/left 2, up/down 1
			((newX == x+2 || newX == x-2) && (newY == y+1 || newY == y-1))){
			Utils.info(name+" location ok");
			// check for self-check
			Board b = new Board(board);
			b.remove(Utils.generateCoord(this.x, this.y));
			Knight k = new Knight(this);
			String newC = Utils.generateCoord(newX, newY);
			k.setLocation(newC);
			k.isDone = true;
			b.put(newC, k);
			if(Utils.isKingInCheck(color, b)){
				Utils.info(name+" returning -1: cannot self-check");
				return -1;
			} 
			
			Utils.info(name+" returning 1: success");
			return 1;
		}
		
		Utils.info(name+" returning -1: invalid move.");
		return -1;
	}

}
