package cavd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FilenameUtils;

/*
 * Used to append video title info to caption file. Takes .srt file as input.
 * Extract caption files and place in source folder. For videos without captions, create empty srt files. 
 * Name of file should be video ID of the YouTube video. Source folder is held by mainDir variable.
 * Running CollateInfo will append video title, description, YouTube category and channel name to the srt.
 */

public class CollateInfo {

private static String videoId;
	
	private static File videoInfoLoc;
	
	public static void main(String[] args) {
		//change source folder here
		File mainDir = new File("./src/testing/");
		getFileName(mainDir);
	}
	
	public static void parseVideoInfo() {
		//extract video title info from YouTube API
		GetTitleInfo titleInfo = new GetTitleInfo(videoId);
		String title = titleInfo.getTitle();
		String desc = titleInfo.getDesc();
		String category = titleInfo.getCategory();
		String channel = titleInfo.getChannel();
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(videoInfoLoc, true)));
			out.println(title);
			out.println(desc);
			out.println(category);
			out.println(channel);
			out.close();
		} catch(IOException e) {
			System.err.println(e.getMessage());
		}
		
	}

	public static void getFileName(File dir) {
		//accept only srt files
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".srt");
			}
		});
		for(File file: files) {
			if(file.isDirectory())
				getFileName(file);
			else {
				videoId = FilenameUtils.removeExtension(file.getName());
				videoInfoLoc = file;
				parseVideoInfo();
			}
		}
//		Debug: Only for single files
		/*File file = new File("./src/testing/5WlVFdlH_aM.srt");
		videoId = FilenameUtils.removeExtension(file.getName());
		videoInfoLoc = file;
		parseVideoInfo();*/
	}
	
}
