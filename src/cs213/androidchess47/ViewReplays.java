package cs213.androidchess47;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import cs213.androidchess47.R;
import cs213.chess.Board;
import cs213.util.Replay;
import cs213.util.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewReplays extends Activity {
	List<Replay> replayList = null;
	ReplayAdapter adapter = null;
	
	public final int PLAY_REPLAY_CODE = 21;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_replays);
		ListView replaylist = (ListView) findViewById(R.id.replays_list);
		replayList = generateReplayList();
		adapter = new ReplayAdapter(replayList, this);
		replaylist.setAdapter(adapter);
		replaylist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent("cs213.androidchess47.PlayReplay");
				Bundle b = new Bundle();
				Replay r = replayList.get(position);
				b.putString("title", r.title);
				b.putSerializable("file", r.file);
				intent.putExtras(b);
				startActivityForResult(intent, PLAY_REPLAY_CODE);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_replays, menu);
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
	
	public void sortByDate(View view){
		Utils.sortReplaysByDate(replayList);
		adapter.update(replayList);
	}
	
	public void sortByTitle(View view){
		Utils.sortReplaysByTitle(replayList);
		adapter.update(replayList);
	}
	
	
	
	/**
	 * Generates a list of replays from the REPLAY_PATH folder
	 * @return the generated list of replays
	 */
	public List<Replay> generateReplayList() {
		List<Replay> replayList = new ArrayList<Replay>();
		
		File replayPath = getFilesDir();
		File[] replays = replayPath.listFiles(); 
		
		if (replays != null){
			for (int i = 0; i < replays.length; i++){
				if(replays[i].isFile()){
					Replay r = new Replay(replays[i]);
					replayList.add(r);
				} 
			}
		}
		
		
		return replayList;
	}
	
	class ReplayAdapter extends BaseAdapter {

		List <Replay> replayList = null;
		LayoutInflater inflater = null;
		
		public ReplayAdapter(List<Replay> replays, Context context){
			replayList = replays;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return replayList.size();
		}

		@Override
		public Object getItem(int position) {
			return replayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = inflater.inflate(R.layout.row_replay, parent, false);
			}
			
			TextView title = (TextView) convertView.findViewById(R.id.replay_title);
			TextView date = (TextView) convertView.findViewById(R.id.replay_date);
			/*mViewHolder.tvTitle = detail(convertView, R.id.tvTitle, myList.get(position).getTitle());
            mViewHolder.tvDesc  = detail(convertView, R.id.tvDesc,  myList.get(position).getDescription());*/
			
			title.setText(replayList.get(position).title);
			date.setText(replayList.get(position).lastModifiedString);
			
			return convertView;
		}
		
		public void update(List<Replay> replays){
			replayList = replays;
			notifyDataSetChanged();
		}
		
	}
}
