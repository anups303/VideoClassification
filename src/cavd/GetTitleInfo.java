package cavd;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import com.google.api.services.youtube.model.VideoCategorySnippet;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;

/*
 * Used by CollateInfo.java to extract video title info. Uses youtube.properties file to utilize YT API.
 * Store developer key in youtube.properties
 */

public class GetTitleInfo {

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	//identifies file containing developer's API key
	private static final String PROPERTIES_FILENAME = "youtube.properties";
	
	//Used to make YouTube Data API requests
	private static YouTube youtube;
	
	private String title, desc, category, channel;
	
	public String getTitle() {
		return title;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public GetTitleInfo(String videoId) {
		
		//read developer key from properties file
		Properties properties = new Properties();
		try {
            InputStream in = GetTitleInfo.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);
        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause() + " : " + e.getMessage());
            System.exit(1);
        }
		
		try {
			
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				} 
			}).setApplicationName("youtube-get-video-info").build();
			
			String apiKey = properties.getProperty("youtube.apikey");
			YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet").setId(videoId);
			listVideosRequest.setKey(apiKey);
			VideoListResponse listResponse = listVideosRequest.execute();
			
			List<Video> videoList = listResponse.getItems();
			
			Video video = videoList.get(0);
			VideoSnippet snippet = video.getSnippet();
			String categoryId = snippet.getCategoryId();
			
//			only category ID retrieved from last req; match ID to actual category name
			YouTube.VideoCategories.List listCategoriesRequest = youtube.videoCategories().list("snippet").setId(categoryId);
			listCategoriesRequest.setKey(apiKey);
			listCategoriesRequest.setFields("items/snippet/title");
			VideoCategoryListResponse categoryListResponse = listCategoriesRequest.execute();
			
			List<VideoCategory> categoryList = categoryListResponse.getItems();
			
			VideoCategory category = categoryList.get(0);
			VideoCategorySnippet categorySnippet = category.getSnippet();
			
			this.title = snippet.getTitle();
			this.desc = snippet.getDescription();
			this.category = categorySnippet.getTitle();
			this.channel = snippet.getChannelTitle();
			
		} catch (GoogleJsonResponseException e) {
        	System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
	}
	
}
