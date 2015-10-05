package cs213.androidchess47;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import pieces.Piece;
import cs213.chess.Board;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class PlayReplay extends Activity {
	
	BufferedReader replay = null;
	Board BOARD = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_replay);
		buttonsToUnclickable();
		
		Bundle b = 	getIntent().getExtras();
		String title = b.getString("title");
		File file = (File) b.getSerializable("file");
		
		setTitle(title);
		this.BOARD = new Board();
		
		try {
			replay = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			Toast.makeText(getApplicationContext(), "Replay could not be found.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_replay, menu);
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
	
	public void guiMovePiece(String start, String end, String type){
		Resources r = getResources();
		String name = getPackageName();
		
		ImageButton startButtonID = (ImageButton) findViewById(r.getIdentifier("button" + start, "id" ,name));
		startButtonID.setImageResource(0);
		
		ImageButton endButtonID =  (ImageButton) findViewById(r.getIdentifier("button" + end, "id" ,name));
		endButtonID.setImageResource(r.getIdentifier(type.toLowerCase(), "drawable", name));
	}
	
	public void nextMove(View view) {
		try {
			String move = replay.readLine(); // e.g. a2 a3
			
			if(move == null){
				Toast.makeText(getApplicationContext(), "Reached end of replay.", Toast.LENGTH_LONG).show();
				return;
			}
			
			String start = move.substring(0, 2);	// e.g. a2
			String end = move.substring(3,5);		// e.g. a3
			Piece p = BOARD.get(start);
			p.forceMove(end, BOARD);
			
			guiMovePiece(start, end, p.name);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Error getting next move: "+e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}
