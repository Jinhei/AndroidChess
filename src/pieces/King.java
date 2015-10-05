package pieces;

import cs213.chess.Board;
import cs213.util.Utils;

/**
 * 
 * @author Nicholas Fong
 * @author Jeffrey Kang
 */
public class King extends Piece {

	public King (String color, String location){
		super(color, location);
		if (color.equals("black"))
			this.name = "bK";
		else this.name = "wK";
		hasMoved = 0;
	}
	
	public King (Piece p){
		super(p);
		hasMoved = p.hasMoved;

		if (color.equals("black"))
			this.name = "bK";
		else this.name = "wK";
	}
	
	@Override
	public boolean isPossibleMove(String location, Board board) {
		int newX = Utils.letterToCoord(location.substring(0, 1));
		int newY = Integer.parseInt(location.substring(1));
		Utils.info(name+" input location: "+newX+", "+newY);
		
		if ((newX == x+1 || newX == x-1 || newX == x) && (newY == y+1 || newY == y-1 || newY == y) && !(newX == x && newY == y)){
			return true;
		}
		return false;
	}
	
	@Override
	public int isValidMove(String location, Board board) {
		if (Utils.isOutOfBounds(location)){
			Utils.info(name+" returning -1: location out of bounds");
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
		
		// if x +/-1 OR y +/- 1
		if ((newX == x+1 || newX == x-1) || (newY == y+1 || newY == y-1)){
			// check for self-check
			Board b = new Board(board);
			b.remove(Utils.generateCoord(this.x, this.y));
			King k = new King(this);
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
		// castle
		else if ((newX == x+2 || newX == x-2) && newY == y){
			Utils.debug(name+" castling");
			if (this.hasMoved != 0){
				Utils.debug(name+" returning -1: cannot castle - king has moved");
				return -1;
			} else if (Utils.isKingInCheck(this.color, board)){
				Utils.debug(name+" returning -1: cannot castle - king is in check");
			} else {
				Piece p = null;
				String side = "";
				// right side castle
				if (newX == x+2){
					Utils.debug(name+" right side castle");
					p = board.get(Utils.generateCoord(newX+1, newY));
					side = "right";
				} else if (newX == x-2){ // left side castle
					Utils.debug(name+" left side castle");
					p = board.get(Utils.generateCoord(newX-2, newY));
					side = "left";
				}
				// no rook to castle 
				if (p == null){
					Utils.debug(name+" returning -1: no piece found for castle");
					return -1;
				} else { // check rook
					String pName = "";
					if (this.color.equals("black"))
						pName = "bR";
					else pName = "wR";
					
					if(p.name != pName){
						Utils.debug(name+" returning -1: no rook for castle");
						return -1;
					} else if (p.hasMoved != 0) {
						Utils.debug(name+" returning -1: cannot castle - rook has moved");
					} else { // valid castle
						if (side.equals("right")){
							// check for pieces
							for (int i = x+1; i < x+3; i++){
								if (board.get(Utils.generateCoord(i, y)) != null){
									Utils.debug(name+" returning -1: cannot right castle - piece in the way.");
									return -1;
								}
							}
							
							// check for self-check
							Board b = new Board(board);
							b.remove(Utils.generateCoord(this.x, this.y));
							King k = new King(this);
							String newC = Utils.generateCoord(newX, newY);
							k.setLocation(newC);
							k.isDone = true;
							b.put(newC, k);
							if(Utils.isKingInCheck(this.color, b)){
								Utils.info(name+" returning -1: cannot self-check");
								return -1;
							} 
							
							board.remove(Utils.generateCoord(x+3, y));
							String rookC = Utils.generateCoord(x+1, newY);
							board.put(rookC, p);
							p.setLocation(rookC);
							Utils.debug(name+" returning 0: right-side castle success");
							return 0;
						} else if(side.equals("left")){
							// check for pieces
							for (int i = x-3; i < x; i++){
								if (board.get(Utils.generateCoord(i, y)) != null){
									Utils.debug(name+" returning -1: cannot left castle - piece in the way.");
									return -1;
								}
							}
							
							// check for self-check
							Board b = new Board(board);
							b.remove(Utils.generateCoord(this.x, this.y));
							King k = new King(this);
							String newC = Utils.generateCoord(newX, newY);
							k.setLocation(newC);
							k.isDone = true;
							b.put(newC, k);
							if(Utils.isKingInCheck(this.color, b)){
								Utils.info(name+" returning -1: cannot self-check");
								return -1;
							} 
							
							board.remove(Utils.generateCoord(x-4, newY));
							String rookC = Utils.generateCoord(x-1, newY);
							p.setLocation(rookC);
							board.put(rookC, p);
							Utils.debug(name+" returning 0: left-side castle success");
							return 0;
						}
					}
				}
			
			}
		}
		Utils.info(name+"returning -1: invalid move");
		return -1;
	}

}
