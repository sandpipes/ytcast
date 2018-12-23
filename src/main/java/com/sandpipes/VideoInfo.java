package com.sandpipes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class VideoInfo extends GridPane{
	
	private String videoURL;
	private Image image;
	private ImageView iview;
	private Label title;
	
	private GridPane parent;
	private int c;
	private int r;
	
	public VideoInfo(String url) {
		videoURL = url;		
		setAlignment(Pos.TOP_LEFT); 
		setMaxWidth(400);
	}
	
	public void updateInfo(GridPane parent, int c, int r) {
		this.r = r;
		this.c = c;
		this.parent = parent;
		
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				update();
			
			}
		});
	}

	private void update() {
		try {
			Document videoPage = Jsoup.connect(videoURL).get();
	
			Element videoIdMeta = videoPage.select("div[itemtype=http://schema.org/VideoObject] meta[itemprop=videoId]").first();
			if (videoIdMeta == null) {
	
			} else {
			    String videoId = videoIdMeta.attr("content");
	
			    String videoImageUrl = String.format("https://i.ytimg.com/vi/%s/maxresdefault.jpg", videoId);
			    Connection.Response response = Jsoup
			        .connect(videoImageUrl)
			        .ignoreContentType(true)
			        .execute();
			    
			    BufferedImage img = ImageIO.read(new ByteArrayInputStream(response.bodyAsBytes()));
				Platform.runLater(new Runnable() {
					public void run() {
					    image = SwingFXUtils.toFXImage(img, null);
					    iview = new ImageView(image);
					    iview.setPreserveRatio(true);
					    iview.setFitHeight(80);
					    
					    title = new Label(videoPage.select("meta[property=og:title]").first().attr("content"));
					    //title.setWrapText(true);
					    title.setAlignment(Pos.TOP_LEFT);
					    
					    setStyle("-fx-background-color:#eeeeee; -fx-opacity:1;");
					    
						add(iview, 0, 0); 
						add(title, 1, 0);
					    
					    parent.add(this, c, r);
					}
				});

			}
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Unable to update preview.");
		}
	}
	
}
