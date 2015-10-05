package cs213.androidchess47;

import cs213.androidchess47.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainMenu extends Activity {
	
	public static final int PLAY_GAME_CODE = 1; 
	public static final int VIEW_REPLAYS_CODE = 2;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
    
    public void play(View view){
    	startActivityForResult(new Intent("cs213.androidchess47.TwoPlayerGame"), PLAY_GAME_CODE);
    }
    
    public void viewReplays(View view){
    	startActivityForResult(new Intent("cs213.androidchess47.ViewReplays"), VIEW_REPLAYS_CODE);
    }
    
}
