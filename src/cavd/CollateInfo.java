package cavd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FilenameUtils;

public class CollateInfo {

private static String videoId;
	
	private static File videoInfoLoc;
	
	public static void main(String[] args) {
		File mainDir = new File("./src/testing/");
		getFileName(mainDir);
	}
	
	public static void parseVideoInfo() {
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
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".srt");
			}
		});
		for(File file: files) {
//			System.out.println(file.toString());
			if(file.isDirectory())
				getFileName(file);
			else {
				videoId = FilenameUtils.removeExtension(file.getName());
				videoInfoLoc = file;
//				System.out.println(videoId);
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
