package cs213.chess;

import cs213.util.Utils;

import java.util.HashMap;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;


/**
 * Class for board
 * @author Nicholas Fong
 * @author Jeffrey Kang
 */
public class Board {
	
	public HashMap <String,Piece> map = null;
	public boolean enpassant = false;
	
	/**
	 * The constructor for board. Initializes a 
	 * chess board with default pieces and default 
	 * locations
	 */
	public Board(){
		
		map = new HashMap<String,Piece>();
			
		map.put("a8", new Rook("black" ,"a8"));		
		map.put("b8", new Knight("black" ,"b8"));
		map.put("c8", new Bishop("black" ,"c8"));
		map.put("d8", new Queen("black" ,"d8"));
		map.put("e8", new King("black" ,"e8"));
		map.put("f8", new Bishop("black" ,"f8"));
		map.put("g8", new Knight("black" ,"g8"));
		map.put("h8", new Rook("black" ,"h8"));

		map.put("a7", new Pawn("black", "a7"));
		map.put("b7", new Pawn("black", "b7"));
		map.put("c7", new Pawn("black", "c7"));
		map.put("d7", new Pawn("black", "d7"));
		map.put("e7", new Pawn("black", "e7"));
		map.put("f7", new Pawn("black", "f7"));
		map.put("g7", new Pawn("black", "g7"));
		map.put("h7", new Pawn("black", "h7"));
		
		map.put("a2", new Pawn("white", "a2"));
		map.put("b2", new Pawn("white", "b2"));
		map.put("c2", new Pawn("white", "c2"));
		map.put("d2", new Pawn("white", "d2"));
		map.put("e2", new Pawn("white", "e2"));
		map.put("f2", new Pawn("white", "f2"));
		map.put("g2", new Pawn("white", "g2"));
		map.put("h2", new Pawn("white", "h2"));
		
		map.put("a1", new Rook("white" ,"a1"));		
		map.put("b1", new Knight("white" ,"b1"));
		map.put("c1", new Bishop("white" ,"c1"));
		map.put("d1", new Queen("white" ,"d1"));
		map.put("e1", new King("white" ,"e1"));
		map.put("f1", new Bishop("white" ,"f1"));
		map.put("g1", new Knight("white" ,"g1"));
		map.put("h1", new Rook("white" ,"h1"));
	}
	
	/**
	 * Creates a deep copy of the board
	 * @param board - 
	 */
	public Board (Board board){
		this.enpassant = board.enpassant;
		map = new HashMap<String,Piece>();
		// check all x y pairs 
		for (int x = 1; x < 9; x++) {
			for (int y = 1; y < 9; y++){
				String coord = Utils.generateCoord(x, y);
				Piece piece = board.get(coord);
				
				// no piece at this coord, skip
				if (piece == null){
					continue;
				} else { // put piece into hashmap
					switch(piece.getClass().getName()){
					case "pieces.Rook": map.put(coord, new Rook(piece)); break; 
					case "pieces.Knight": map.put(coord, new Knight(piece)); break;
					case "pieces.Bishop": map.put(coord, new Bishop(piece)); break;
					case "pieces.Queen": map.put(coord, new Queen(piece)); break;
					case "pieces.King": map.put(coord, new King(piece)); break;
					case "pieces.Pawn": map.put(coord, new Pawn(piece)); break;
					}
				}
			}
		} 
	}
	/**
	 * Prints the board that the game is played on as well 
	 * as filling in empty spots with either blank or black spaces.
	 */
	public void printBoard(){
		//System.out.println((map.get("a8")).name);
		
		
		int tempNum = 0;
		for(int i = 0 ; i < 9 ; i++){
			tempNum = 8 - i;
			
			String tempLetter = "";
			String tempName = "";
			String temp = "";
			for(int j=0; j < 8;j++){
				
				tempLetter = Utils.numberToLetter(j+1);
				tempName = tempLetter + tempNum;
				
				if(map.get(tempName) == null){	//check for null spots 
					int numCheck = Utils.letterToCoord(tempLetter);
					if ((numCheck % 2 == 0 && tempNum % 2 == 0) || (numCheck % 2 != 0 && tempNum % 2 != 0) )//checks if you are suppose to put in a space or ##
					temp = temp + "## ";
					else{
						temp = temp + "   ";
					}
				}
				else{
				temp = temp + map.get(tempName).name + " ";
				}
			}			
			
			if (tempNum !=0 ){	//prints the first 8 lines
			temp = temp + Integer.toString(tempNum);
			System.out.println(temp);
			}
			else{	//prints the last line
				System.out.println(" a  b  c  d  e  f  g  h");
			}
		}
		
		
	}
	
		
	public Piece get(String coordinate){
		return map.get(coordinate);
	}
	
	public Piece put(String coordinate, Piece piece){
		return map.put(coordinate, piece);
	}
	
	public Piece remove(String coordinate){
		return map.remove(coordinate);
	}
	
}
