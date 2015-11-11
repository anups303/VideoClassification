package cavd;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;

public class TestTitleInfo {
	
	private static YouTube youtube;
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static void main(String[] args) {
		String videoId;
		try {
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				} 
			}).setApplicationName("youtube-get-video-info").build();
			Properties properties = new Properties();
			String apiKey = properties.getProperty("youtube.apikey");
			File dir = new File("./src/training/animation/");
			File[] files = dir.listFiles();
			for(File file:files) {
				videoId = FilenameUtils.removeExtension(file.getName());
				YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet,contentDetails").setId(videoId);
				listVideosRequest.setKey(apiKey);
				VideoListResponse listResponse = listVideosRequest.execute();
				List<Video> videoList = listResponse.getItems();
				Video video = videoList.get(0);
				VideoSnippet snippet = video.getSnippet();
				VideoContentDetails contentDetails = video.getContentDetails();
				System.out.println(snippet.getChannelTitle());
				System.out.println(snippet.getTitle());
				System.out.println(contentDetails.getDuration());
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
