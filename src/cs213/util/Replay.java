package cs213.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Replay {
	public String title = "";
	
	public Calendar lastModified = null;
	
	public String lastModifiedString = "";
	
	public File file = null;
	
	public Replay (File r) {
		file = r;
		title = r.getName();
		
		lastModified = Calendar.getInstance(); 
		lastModified.setTimeInMillis(r.lastModified());
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
		lastModifiedString = sdf.format(lastModified.getTime());
	}
}
