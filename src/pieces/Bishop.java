package pieces;

import cs213.chess.Board;
import cs213.util.Utils;

/**
 * 
 * @author Nicholas Fong
 * @author Jeffrey Kang
 */
public class Bishop extends Piece {

	public Bishop (String color, String location){
		super(color, location);
		if (color.equals("black"))
			this.name = "bB";
		else this.name = "wB";
	}
	
	public Bishop (Piece p){
		super(p);
		if (color.equals("black"))
			this.name = "bB";
		else this.name = "wB";
	}
	
	@Override
	public boolean isPossibleMove(String location, Board board) {
		int newX = Utils.letterToCoord(location.substring(0, 1));
		int newY = Integer.parseInt(location.substring(1));
		Utils.info(name+" input location: "+newX+", "+newY);
		
		if((Math.abs(x-newX) == Math.abs(y-newY)) && !(newX == x && newY == y)){
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
		
		if((Math.abs(x-newX) == Math.abs(y-newY)) && !(newX == x && newY == y)){
			Utils.info(name+" location ok");
			
			
			int diffX = newX - x;
			int diffY = newY - y;
			
			int i = 1*Integer.signum(diffX);
			while(Math.abs(i) < Math.abs(diffX)){
				int px = x + i;
				int j = Math.abs(i)*Integer.signum(diffY);
				int py = y + j; 
				String c = Utils.generateCoord(px, py);
				Utils.info(name+" checking "+c);
				Piece p = board.get(c);
				if (p != null){
					Utils.info(name+" returning -1: piece in the way at "+c);
					return -1;
				} 
				
				i += 1*Integer.signum(diffX);
			}
			
			// check for self-check
			Board b = new Board(board);
			b.remove(Utils.generateCoord(this.x, this.y));
			Bishop k = new Bishop(this);
			String newC = Utils.generateCoord(newX, newY);
			k.setLocation(newC);
			k.isDone = true;
			b.put(newC, k);
			if(Utils.isKingInCheck(this.color, b)){
				Utils.info(name+" returning -1: cannot self-check");
				return -1;
			} 
			
			Utils.info(name+" returning 1: success");
			return 1;
		}
		
		Utils.info(name+" returning -1: invalid move");
		return -1;
	}

}
