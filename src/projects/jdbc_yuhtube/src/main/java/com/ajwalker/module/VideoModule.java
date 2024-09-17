package com.ajwalker.module;

import com.ajwalker.controller.VideoController;
import com.ajwalker.dto.response.DtoUserLoginResponse;
import com.ajwalker.dto.response.DtoVideoThumbnail;
import com.ajwalker.entity.Video;
import com.ajwalker.dto.response.DtoVideoDetailed;
import com.ajwalker.model.VideoModel;
import com.sun.tools.javac.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class VideoModule {
	private Optional<String> token;
	private Scanner scanner = new Scanner(System.in);
	private VideoController videoController = VideoController.getInstance();
	private List<DtoVideoThumbnail> videosToWatch = new ArrayList<DtoVideoThumbnail>();
	
	private Integer choice(){ //TODO alt katmandan üst katmana ilerleme, yeni bir sınıf aç choice'ı oraya koy
		return MainMenu.getInstance().choice();
	}
	
	public Optional<String> videoModule(Optional<String> token) {
		this.token = token;
		int opt;
		do{
			System.out.println("""
					                   YuhTube
					                   Videos
					                   1. Show All Videos
					                   2. Filter By Title
					                   3. Show My Videos
					                   4. What's Trending
					                   5. Choose Video To Watch
					                   0. Go Back
					                   
					                   """);
			opt = mainMenuOptions(choice());
		}while(opt != 0);
		return token;
	}
	
	private int mainMenuOptions(int choice) {
		switch (choice){
			case 1:
				videosToWatch = showAllVideos();
				break;
			case 2:
				videosToWatch = filterByTitle();
				break;
			case 3:
				if (token.isEmpty()){
					System.out.println("You must log in to see your videos");
					token = MainMenu.getInstance().login();
					if(token.isEmpty()){
						System.out.println("Cannot see your videos since you have not logged in");
						break;
					}
				}
				videosToWatch = showMyVideos(token);
				break;
				case 4:
					// show what's trending
					break;
			case 5:
				chooseVideoToWatch();
				break;
			default:
				System.out.println("Invalid option!");
		}
		
		return choice;
	}
	
	private void chooseVideoToWatch() {
		int opt;
		do{
			System.out.println("Which video(ex: 1)");
			opt = choice();
			try{
				Long videoId = videosToWatch.get(opt - 1).getVideoId();
				Optional<Video> optVideo = videoController.findById(videoId);
				if (optVideo.isEmpty()){
					System.out.println("Video not found");
					return;
				}
				Video video = optVideo.get();
				DtoVideoDetailed dtoVideoDetailed= videoController.generateVideoModel(video);
				VideoModel videoModel = new VideoModel(dtoVideoDetailed);
				new WatchModule().watchMenu(videoModel, token);
				opt = 0;
			}
			catch (Exception e) {
				System.out.println("No such video (controller chosevideotowatch)..." + e.getMessage());
			}
			
		}while(opt != 0);
	}
	
	private List<DtoVideoThumbnail> showMyVideos(Optional<> user) {
		
		List<DtoVideoThumbnail> videos = videoController.showMyVideos(user);
		printVideos(videos);
		return videos;
	}
	
	private List<DtoVideoThumbnail> filterByTitle() {
		System.out.print("Apply filter> ");
		List<DtoVideoThumbnail> videos = videoController.showByName(scanner.nextLine());
		printVideos(videos);
		return videos;
	}
	
	private List<DtoVideoThumbnail> showAllVideos() {
		List<DtoVideoThumbnail> videos = videoController.showAllVideos();
		printVideos(videos);
		return videos;
	}
	
	private void printVideos(List<DtoVideoThumbnail> videos){
		System.out.println("####### VIDEOS #######");
		for (int i = 0; i < videos.size(); i++) {
			System.out.println((i + 1) + ". Video");
			System.out.println(videos.get(i));
			System.out.println("-  -  -   -   -   -   -   -  -   -   -   -   -   -   -   -\n");
		}
	}
}