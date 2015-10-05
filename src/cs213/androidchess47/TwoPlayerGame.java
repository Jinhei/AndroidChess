package cs213.androidchess47;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import pieces.Piece;
import cs213.androidchess47.R;
import cs213.chess.Board;
import cs213.util.Utils;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class TwoPlayerGame extends Activity {
	
	Board BOARD = null;
	String LAST_MOVE = "";
	Board LAST_BOARD = null;
	String TEMP_REPLAY = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.two_player_game);
		this.BOARD = new Board();
		this.LAST_BOARD = new Board();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.two_player_game, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void undoButtonClick(View view){
		if(LAST_MOVE.equals("")){
			Toast.makeText(getApplicationContext(),"No Undos are available", Toast.LENGTH_SHORT).show();
		}
		else{
			String start = LAST_MOVE.substring(0, 2);	//first part of string
			String end = LAST_MOVE.substring(3,5);		//second part of string
			
			if (LAST_MOVE.contains("castle")){
				if(start.substring(0, 1).compareTo(end.substring(0,1)) > 0){ // left
					String rookStart = "a" + start.substring(1,2);
					String rookEnd = "d" + end.substring(1,2);
					Piece piece = BOARD.get(rookEnd);
					guiMovePiece(rookEnd, rookStart, piece.name);
				} else { // right
					String rookStart = "h" + start.substring(1,2);
					String rookEnd = "f" + end.substring(1,2);
					Piece piece = BOARD.get(rookEnd);
					guiMovePiece(rookEnd, rookStart, piece.name);
				}
			}
			
			Piece p = BOARD.get(end);
			guiMovePiece(end, start, p.name);
			
			BOARD = new Board(LAST_BOARD);
			
			p = BOARD.get(end);
			if(p != null){
				Resources r = getResources();
				String name = getPackageName();
				
				ImageButton endButtonID =  (ImageButton) findViewById(r.getIdentifier("button" + end, "id" ,name));
				endButtonID.setImageResource(r.getIdentifier(p.name.toLowerCase(), "drawable", name));
			}
			
			LAST_MOVE = "";
			TextView textTurn = (TextView) findViewById(R.id.PlayerTurn);
			
			if(textTurn.getText().toString().contains("Black")){
					textTurn.setText("Turn:   White");
				}
			else{
					textTurn.setText("Turn:   Black");
			}
			TextView textMove = (TextView) findViewById(R.id.PlayerMove);
			textMove.setText("Move");
			
			
		}
	}
	
	public void aiButtonClick(View view){
		
		TextView textTurn = (TextView) findViewById(R.id.PlayerTurn);
		LAST_BOARD = new Board(BOARD);
		if(textTurn.getText().toString().contains("Black")){
			String AIMove = Utils.generateAIMove("black", BOARD);
			if(AIMove == null){
				return;
			}
			BOARD = parseInputs(AIMove, BOARD, "black");
			textTurn.setText("Turn:   White");
			
			if(Utils.checkForPromotion(BOARD)){
				promoteDialog();
			} if(Utils.isKingInCheck("white", BOARD)){
				Toast.makeText(getApplicationContext(), "Check!", Toast.LENGTH_SHORT).show();
			} 
		}
		else{
			String AIMove = Utils.generateAIMove("white", BOARD);
			if(AIMove == null){
				return;
			}
			BOARD = parseInputs(AIMove, BOARD, "white");
			textTurn.setText("Turn:   Black");
			if(Utils.checkForPromotion(BOARD)){
				promoteDialog();
			} if(Utils.isKingInCheck("black", BOARD)){
				Toast.makeText(getApplicationContext(), "Check!", Toast.LENGTH_SHORT).show();
			} 
		}
	}
	public void resignClick(View view){
		
		buttonsToUnclickable();
		TextView textTurn = (TextView) findViewById(R.id.PlayerTurn);
		if(textTurn.getText().toString().contains("Black")){
			textTurn.setText("White Wins!");
		}
		else{
			textTurn.setText("Black Wins!");
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	     builder.setMessage("Victory!");
	     builder.setPositiveButton(R.string.okay_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
           	 TextView textTurn = (TextView) findViewById(R.id.PlayerTurn);
           	 textTurn.setText(textTurn.getText());
           	 buttonsToUnclickable();
            }
        });
        builder.show();
		
		promptSaveReplay();
		
		LAST_MOVE = "";
		BOARD = null;
			
		
	}
	
	public void drawClick(View view){
								
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
	     builder.setMessage("End in draw?");
	     builder.setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
            	 TextView textTurn = (TextView) findViewById(R.id.PlayerTurn);
            	 textTurn.setText("Draw");
            	 buttonsToUnclickable();
            	 promptSaveReplay();
             }
         });
         builder.setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
             }
         });
  // Create the AlertDialog object and return it
  builder.show();
  
		
	}
	public void buttonClick(View view){
		String ID = (String) view.getTag();
		//Toast.makeText(getApplicationContext(), ID, Toast.LENGTH_SHORT).show();		
		
		TextView textMove = (TextView) findViewById(R.id.PlayerMove);
		TextView textTurn = (TextView) findViewById(R.id.PlayerTurn);
		
		if(textMove.getText().toString().length() == 4){
			textMove.setText(textMove.getText().toString() + " " +  ID);
		}
		else if(textMove.getText().toString().length() > 4){
			
			
			
			if(textTurn.getText().toString().contains("Black")){
				Board b = parseInputs((textMove.getText().toString() + " " + ID).substring(5),BOARD, "black");
				
				if (b == null){
					//TODO 	
				}
				else{
					LAST_BOARD = new Board(BOARD);
					BOARD = b;
					textTurn.setText("Turn:   White");
					if(Utils.checkForPromotion(BOARD)){
						promoteDialog();
					} if(Utils.isKingInCheck("white", BOARD)){
						Toast.makeText(getApplicationContext(), "Check!", Toast.LENGTH_SHORT).show();
					} 
				}
				
				
				}
			else{
				Board b = parseInputs((textMove.getText().toString() + " " + ID).substring(5),BOARD, "white");
				if (b == null){
					//TODO 
				}
				else{
					LAST_BOARD = new Board(BOARD);
					BOARD = b;
					textTurn.setText("Turn:   Black");
					if(Utils.checkForPromotion(BOARD)){
						promoteDialog();
					} if(Utils.isKingInCheck("black", BOARD)){
						Toast.makeText(getApplicationContext(), "Check!", Toast.LENGTH_SHORT).show();
					}
					
				}
				
			}
			textMove.setText("Move");
			
		}
		
				return;
	}
	
	public void buttonsToUnclickable(){
		
		Resources r = getResources();
		String name = getPackageName();
		
		for(char letter = 'a' ; letter <= 'h' ; letter++){
		
			for(int i = 1; i < 9  ; i++){
				
				ImageButton buttonID = (ImageButton) findViewById(r.getIdentifier("button" + letter + i, "id" ,name));
				buttonID.setClickable(false);
				
			}
		
		}
	}
	
	/**
	 * Parses the players input. 
	 * @param input - String that holds the players input
	 * @param board - The board that the game is played on 
	 * @param color - The player's color
	 * @return
	 */
	public Board parseInputs(String input,Board b, String color){
		
		Board board = new Board(b);
		//Toast.makeText(getApplicationContext(), "TEST" + input,Toast.LENGTH_SHORT).show();
		String start = input.substring(0, 2);	//first part of string
		String end = input.substring(3,5);		//second part of string
		Piece piece = board.get(start);
		
		
		if (piece == null){
			Toast.makeText(getApplicationContext(), "No piece exists.",Toast.LENGTH_SHORT).show();
			return null;						
		} else if (!piece.color.equals(color)){
			Toast.makeText(getApplicationContext(), "It's not your turn!",Toast.LENGTH_SHORT).show();
			return null;
		}
		
		int moveCheck = piece.move(end,board);
		
		//Toast.makeText(getApplicationContext(), String.valueOf(moveCheck),Toast.LENGTH_SHORT).show();//TODO
		
		if(moveCheck == -1){
			Toast.makeText(getApplicationContext(), "Invalid move.",Toast.LENGTH_SHORT).show();
			return null;
		}
		else if(moveCheck == 1 ||moveCheck ==3 || moveCheck == 4){
			LAST_MOVE = input;
			guiMovePiece(start,end,piece.name);
		} else if (moveCheck == 0){
			LAST_MOVE = input+" castle";
			guiMovePiece(start, end, piece.name);
			if(start.substring(0, 1).compareTo(end.substring(0,1)) > 0){ // left
				String rookStart = "a" + start.substring(1,2);
				String rookEnd = "d" + end.substring(1,2);
				
				piece = BOARD.get(rookStart);
				guiMovePiece(rookStart, rookEnd, piece.name);
			} else { // right
				String rookStart = "h" + start.substring(1,2);
				String rookEnd = "f" + end.substring(1,2);
				
				piece = BOARD.get(rookStart);
				guiMovePiece(rookStart, rookEnd, piece.name);
			}
		}
		return board;
	}
	
	public void guiMovePiece(String start, String end, String type){
		Resources r = getResources();
		String name = getPackageName();
		
		ImageButton startButtonID = (ImageButton) findViewById(r.getIdentifier("button" + start, "id" ,name));
		startButtonID.setImageResource(0);
		
		ImageButton endButtonID =  (ImageButton) findViewById(r.getIdentifier("button" + end, "id" ,name));
		endButtonID.setImageResource(r.getIdentifier(type.toLowerCase(), "drawable", name));
		
		writeToTempReplay(start+" "+end);
	}
	
	/**
	 * Writes a move to the temp replay file
	 * @param move - move to be written to file 
	 * @return true on success, false otherwise
	 */
	public boolean writeToTempReplay(String move){
		if(move.isEmpty())
			return false;
		
		TEMP_REPLAY += move+"\n";
		
		return true;
	}
	
	/**
	 * Saves the temp replay to a new file 
	 * @param title - new name of replay
	 * @return true on success, false otherwise
	 */
	public boolean saveReplay(String title) {
		
		try {
			File file = getFileStreamPath(title);
			if (!file.exists()){
				FileOutputStream fos = openFileOutput(title, Context.MODE_PRIVATE);
	
				fos.write(TEMP_REPLAY.getBytes());
				
				fos.close();
			} else {
				Toast.makeText(getApplicationContext(), "Replay with that title already exists.", Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error copying replay: "+e.toString(), Toast.LENGTH_LONG).show();
			return false;
		} 
		
		
		return true;
	}
	
	public void promoteDialog(){
		 final CharSequence[] types = {
	                "Queen", "Rook", "Bishop", "Knight", "Pawn"
	        };

	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Make your selection");
	        builder.setItems(types, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	            	Resources r = getResources();
	        		String name = getPackageName();
	        		
	                String coord = Utils.promotion(types[item].toString(), BOARD);
	                String type = BOARD.get(coord).name;
	                
	                ImageButton endButtonID =  (ImageButton) findViewById(r.getIdentifier("button" + coord, "id" ,name));
	        		endButtonID.setImageResource(r.getIdentifier(type.toLowerCase(), "drawable", name));
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();
	}
	
	public void promptSaveReplay(){
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        final EditText input = new EditText(getApplicationContext());
	        input.setHint("Replay Title");
	        input.setHintTextColor(Color.LTGRAY);
	        input.setTextColor(Color.BLACK);
	        builder.setMessage("Save Replay?")	
	        	   .setView(input)
	               .setPositiveButton(R.string.save_replay_dialog_yes, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   String title = input.getText().toString();
	                       if(!saveReplay(title)){
	                    	   promptSaveReplay();
	                       }
	                       
	                   }
	               })
	               .setNegativeButton(R.string.save_replay_dialog_no, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                   }
	               });
	        // Create the AlertDialog object and return it
	        builder.show();
	}
}
