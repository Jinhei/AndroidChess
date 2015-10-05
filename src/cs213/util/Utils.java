package cs213.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import pieces.*;
import cs213.chess.Board;

/**
 * Utility class of helper functions and logging
 * 
 * @author Nicholas Fong
 * @author Jeffrey Kang
 */
public class Utils {
	
	public final static String REPLAY_PATH = "./data/";
	
	public final static String TEMP_FILE = "temp";
	
	/**
	 * Gets the number value of the letter
	 * @param letter
	 * @return number value of the letter; -1 if 
	 * letter is < -1;
	 */
	public static int letterToCoord(String letter){
		switch(letter){
		case "a": return 1;
		case "b": return 2;
		case "c": return 3;
		case "d": return 4;
		case "e": return 5;
		case "f": return 6;
		case "g": return 7;
		case "h": return 8;
		default: return -1;
		}
	}
	
	/**
	 * Gets the letter value of the number
	 * @param number
	 * @return a-h for 1-8, z otherwise
	 */
	public static String numberToLetter(int number){
		switch(number){
		case 1: return "a";
		case 2: return "b";
		case 3: return "c";
		case 4: return "d";
		case 5: return "e";
		case 6: return "f";
		case 7: return "g";
		case 8: return "h";
		default: return "z";
		}
	}
	
	/**
	 * Generates a coordinate string based on x and y
	 * @param x
	 * @param y
	 * @return a 2 character string with a letter and 
	 * number (e.g. a6)
	 */
	public static String generateCoord(int x, int y){
		String coord = numberToLetter(x);
		coord += y;
		
		//Utils.info(" generateCoord() generated "+coord);
		return coord;
	}
	
	/**
	 * Checks if there is a king of the given color in check on the given board
	 * @param color
	 * @param board
	 * @return true if there is a king of the given color in check, else returns 
	 * false
	 */
	public static boolean isKingInCheck(String color, Board board){
		// name of king 
		String pName = "";
		if (color.equals("black")){
			pName = "bK";
		} else {
			pName = "wK";
		}
		//HashMap<String,Piece> map = new HashMap<String,Piece>(board.map);
		
		String kingc = "";
		
		for (int i = 1; i < 9; i++){
			for (int j = 1; j < 9; j++){
				String c = Utils.generateCoord(i, j);
				Piece p = board.get(c);
				if (p == null){
					continue;
				} else if (p.name.equals(pName)){
					kingc = c;
				}
			}
		}
		
		if (kingc.isEmpty()){
			return true;
		}
		
		// check all x y pairs 
		for (int x = 1; x < 9; x++) {
			for (int y = 1; y < 9; y++){
				String coord = Utils.generateCoord(x, y);
				Piece piece = board.get(coord);
				
				// no piece at this coord, skip
				if (piece == null){
					continue;
				} else if (piece.isDone){
					continue;
				} else if (piece.color.equals(color)) { // check opposite color
					continue;
				} else if (!piece.isPossibleMove(kingc, board)){
					continue;
				}
				int valid = piece.isValidMove(kingc, board);
				if (valid != -1 && valid != 0){
					Utils.info("isKingInCheck() returning true for "+color+"; piece "+piece.name+" checking "+pName);
					return true;
				}
			}
		}
		
		//Utils.debug("isKingInCheck returning false");
		return false;
	}
	
	/**
	 * Checks if king of the given color is in checkmate
	 * @param board
	 * @param color
	 * @return true if checkmate, else false
	 */
	public static boolean canMakeValidMove(String color, Board board){
		for(int x = 1; x < 9; x++){
			for (int y = 1; y < 9; y++){
				String coord = Utils.generateCoord(x, y);
				Piece piece = board.get(coord);
				
				// no piece at this coord, skip
				if (piece == null){
					continue;
				} else if (!piece.color.equals(color)) { // skip opposite color pieces
					continue;
				} else { // check all moves for this piece
					for (int i = 1; i < 9; i++) {
						for (int j = 1; j < 9; j++){
							String c = Utils.generateCoord(i, j);
							// piece can move to given location
							if (!piece.isPossibleMove(c, board)){
								continue;
							} if (c.equals(Utils.generateCoord(piece.x, piece.y))){
								continue;
							}
							Utils.info("canMakeValidMove() checking "+piece.name+" at "+coord+" to "+c);
							if (piece.isValidMove(c, board) != -1){
								Utils.debug("canMakeValidMove() returning true for "+color+"; "+piece.name+" can make valid move to "+c);
								return true;
							}
						}
					}
				}
			}
		}
		
		Utils.debug("canMakeValidMove() returning false");
		return false;
	}
	
	/**
	 * Checks the board for any pawns to promote
	 * @param board
	 * @return true if promotion is possible, false otherwise
	 */
	public static boolean checkForPromotion(Board board){
		Piece p = null;
		for (int x = 1; x < 9; x++){
			for (int y = 1; y < 9; y++){
				if (y == 1 || y == 8){
					String coord = Utils.generateCoord(x, y);
					p = board.get(coord);
					if (p == null){
						continue;
					} else if (!p.getClass().getName().equals("pieces.Pawn")){
						continue;
					} else {
						Utils.debug("promotion() found pawn");
						if(!p.isChanged)
							return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Default promotes to queen
	 * @param board
	 * @return 
	 */
	public static String promotion(Board board){
		return Utils.promotion("Q", board);
	}
	
	/**
	 * Promotes a pawn to the given type
	 * @param type - new type (e.g. "Q")
	 * @param board
	 * @return coordinate of promoted piece
	 */
	public static String promotion(String type, Board board){
		Piece p = null;

		boolean flag = false;
		for (int x = 1; x < 9; x++){
			for (int y = 1; y < 9; y++){
				if (y == 1 || y == 8){
					String coord = Utils.generateCoord(x, y);
					p = board.get(coord);
					if (p == null){
						continue;
					} else if (!p.getClass().getName().equals("pieces.Pawn")){
						continue;
					} else {
						Utils.debug("promotion() found pawn");
						flag = true;
						break;
					}
				}
			}
			if (flag){
				break;
			}
		}
		
		//Utils.debug("promotion() pawnFlag = "+flag);
		
		if (!flag){
			return "";
		}
		Utils.debug("promotion() p.isChanged = "+((Pawn) p).isChanged);
		if (((Pawn) p).isChanged){
			return "";
		}
		
		String coord = Utils.generateCoord(p.x, p.y);
		switch(type){
		case "Rook": board.put(coord, new Rook(p)); break;
		case "Knight": board.put(coord, new Knight(p)); break;
		case "Bishop": board.put(coord, new Bishop(p)); break;
		case "Queen": board.put(coord, new Queen(p)); break;
		case "Pawn": p.isChanged = true; break;
		default: Utils.debug("Invalid type"); return "";
		}
		return coord;
	}
	
	/**
	 * Checks if game is in stalemate
	 * @return true if the game is in stalemate, false otherwise
	 */
	public static boolean isStalemate(String color, Board board){
		if(!Utils.isKingInCheck(color, board)){
			if(!Utils.canMakeValidMove(color, board)){
				Utils.debug("isStalemate() returning true: "+color+" cannot make any moves");
				return true;
			}
		}

		for (int x = 1; x < 9; x++){
			for (int y = 1; y < 9; y++){
				String coord = Utils.generateCoord(x, y);
				Piece piece = board.get(coord);
				if (piece == null) {
					continue;
				}
				if (!piece.getClass().getName().equals("pieces.King")){
					Utils.info("isStalemate() returning false: non-king piece found");
					return false;
				} 
			}
		}
		Utils.debug("isStalemate() returning true: only kings left");
		return true;
	}
	
	/**
	 * Checks if the location is out of board boundaries
	 * @param location 
	 * @return true if out of bounds, else false
	 */
	public static boolean isOutOfBounds(String location) {
		int x = Utils.letterToCoord(location.substring(0, 1));
		int y = Integer.parseInt(location.substring(1));
		if (x < 1 || x > 8 || y < 1 || y > 8)
			return true;
		else return false;
	}
	
	/**
	 * Logs the message at INFO level if INFO == true
	 * @param message
	 */
	public static void info(String message) {
		boolean INFO = false;
		
		if (INFO)
			System.out.println("     INFO: "+message);
	}
	
	/**
	 * Logs the message at DEBUG level if DEBUG == true
	 * @param message
	 */
	public static void debug(String message) {
		boolean DEBUG = false;
		
		if (DEBUG)
			System.out.println("     DEBUG: "+message);
	}

	/**
	 * Sorts a list of replays by date, newest first
	 * @param replayList - list of replays to sort
	 */
	public static void sortReplaysByDate(List<Replay> replayList) {
		int replays = replayList.size();
		if(replays <= 1){
			return;
		}
		
		for(int i = 0; i < replays; i++){	
			for(int j = 1; j < (replays - i); j++){
				Replay cur = replayList.get(j-1);
				Replay next = replayList.get(j);
				if (cur.lastModified.compareTo(next.lastModified) < 0){
					replayList.remove(j-1);
					replayList.add(j, cur);
				}
			}
		}
	}
	
	/**
	 * Sorts a list of replays alphabetically by title
	 * @param replayList - list of replays to sort
	 */
	public static void sortReplaysByTitle(List<Replay> replayList) {
		int replays = replayList.size();
		if(replays <= 1){
			return;
		}
		
		for(int i = 0; i < replays; i++){	
			for(int j = 1; j < (replays - i); j++){
				Replay cur = replayList.get(j-1);
				Replay next = replayList.get(j);
				if (cur.title.compareToIgnoreCase(next.title) > 0){
					replayList.remove(j-1);
					replayList.add(j, cur);
				}
			}
		}
	}
	
	/**
	 * Generates a valid move for ai to make
	 * @param board - given board
	 * @param color - color to move
	 * @return null if no moves to make, move in format "a2 a3" that ai chooses
	 */
	public static String generateAIMove(String color, Board board){
		List<String> possibleMoves = new ArrayList<String>();
		
		for(int x = 1; x < 9; x++){
			for (int y = 1; y < 9; y++){
				String coord = Utils.generateCoord(x, y);
				Piece piece = board.get(coord);
				
				// no piece at this coord, skip
				if (piece == null){
					continue;
				} else if (!piece.color.equals(color)) { // skip opposite color pieces
					continue;
				} else { // check all moves for this piece
					for (int i = 1; i < 9; i++) {
						for (int j = 1; j < 9; j++){
							String c = Utils.generateCoord(i, j);
							// piece can move to given location
							if (!piece.isPossibleMove(c, board)){
								continue;
							} if (c.equals(Utils.generateCoord(piece.x, piece.y))){
								continue;
							}
							Utils.info("canMakeValidMove() checking "+piece.name+" at "+coord+" to "+c);
							if (piece.isValidMove(c, board) != -1){
								Utils.debug("canMakeValidMove() returning true for "+color+"; "+piece.name+" can make valid move to "+c);
								String move = coord+" "+c;
								possibleMoves.add(move);
							}
						}
					}
				}
			}
		}
		
		if (possibleMoves.isEmpty())
			return null;
		
		Random rand = new Random();
		int random = rand.nextInt(possibleMoves.size());
		return possibleMoves.get(random);
	}
}
