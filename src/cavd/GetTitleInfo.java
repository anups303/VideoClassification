package cavd;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Captions.Download;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import com.google.api.services.youtube.model.VideoCategorySnippet;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.collect.Lists;

public class GetTitleInfo {

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	//identifies file containing developer's API key
	private static final String PROPERTIES_FILENAME = "youtube.properties";
	
	private static final String SRT = "srt";
	
	//Used to make YouTube Data API requests
	private static YouTube youtube;
	
	private static String videoId;
	
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
	
//	public static void main(String[] args) {
	public GetTitleInfo(String videoId) {
		
		this.videoId = videoId;
		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
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
			
			
			//prompt for video id
			//String videoId = getVideoIdFromUser();
			
			//retrieve resources representing specified videos
			YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet").setId(videoId);
			listVideosRequest.setKey(apiKey);
			VideoListResponse listResponse = listVideosRequest.execute();
			
//			to check the HTTP request
//			System.out.println(listVideosRequest.buildHttpRequestUrl().toString());
			
			List<Video> videoList = listResponse.getItems();
			
			Video video = videoList.get(0);
			VideoSnippet snippet = video.getSnippet();
			String categoryId = snippet.getCategoryId();
			
//			only category ID retrieved from last req; match ID to actual category name
			YouTube.VideoCategories.List listCategoriesRequest = youtube.videoCategories().list("snippet").setId(categoryId);
			listCategoriesRequest.setKey(apiKey);
			listCategoriesRequest.setFields("items/snippet/title");
//			System.out.println(listCategoriesRequest.buildHttpRequestUrl().toString());
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
	
	private static String getVideoIdFromUser() throws IOException {
		
		String videoId = "";
		//Video ID should only be the string after v=
		System.out.print("Please enter video id: ");
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		videoId = bReader.readLine();
		
		return videoId;
	}
	
	private static void downloadCaption(String captionId, String apiKey) throws IOException {
		
		Download captionDownload = youtube.captions().download(captionId).setTfmt(SRT);
		
		MediaHttpDownloader downloader = captionDownload.getMediaHttpDownloader();
		
		downloader.setDirectDownloadEnabled(false);
		
		OutputStream outputFile = new FileOutputStream("captionFile.srt");
		captionDownload.setKey(apiKey);
		captionDownload.executeAndDownloadTo(outputFile);
		
	}
	
}
