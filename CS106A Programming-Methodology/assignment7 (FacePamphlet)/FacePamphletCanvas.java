/*
 * File: FacePamphletCanvas.java
 * -----------------------------
 * This class represents the canvas on which the profiles in the social
 * network are displayed.  NOTE: This class does NOT need to update the
 * display when the window is resized.
 */


import acm.graphics.*;
import java.awt.*;
import java.util.*;

public class FacePamphletCanvas extends GCanvas 
					implements FacePamphletConstants {
	private GLabel message;

	private GLabel name;
	private GLabel status;
	private GLabel friends;


	
	/** 
	 * Constructor
	 * This method takes care of any initialization needed for 
	 * the display
	 */
	public FacePamphletCanvas() {
		message = new GLabel("");
	}

	
	/** 
	 * This method displays a message string near the bottom of the 
	 * canvas.  Every time this method is called, the previously 
	 * displayed message (if any) is replaced by the new message text 
	 * passed in.
	 */
	public void showMessage(String msg) {
		message.setLabel(msg);
		message.setFont(MESSAGE_FONT);
		message.setColor(Color.BLACK);
		message.setLocation((getWidth()-message.getWidth()) / 2, getHeight()  - BOTTOM_MESSAGE_MARGIN);
		add(message);
	}

	/** 
	 * This method displays the given profile on the canvas.  The 
	 * canvas is first cleared of all existing items (including 
	 * messages displayed near the bottom of the screen) and then the 
	 * given profile is displayed.  The profile display includes the 
	 * name of the user from the profile, the corresponding image 
	 * (or an indication that an image does not exist), the status of
	 * the user, and a list of the user's friends in the social network.
	 */

	private void update(FacePamphletProfile profile){
		removeAll();
		message.setLabel("");
		add(message);

		status = new GLabel(profile.getStatus());
		status.setFont(PROFILE_STATUS_FONT);
		add(status, LEFT_MARGIN, TOP_MARGIN + IMAGE_HEIGHT + STATUS_MARGIN + status.getAscent());

		showFriends(profile);
	}

	// displays whole profile with everything
	public void displayProfile(FacePamphletProfile profile) {
		update(profile);
		name = new GLabel(profile.getName());
		name.setFont(PROFILE_NAME_FONT);
		name.setColor(Color.BLUE);
		add(name, LEFT_MARGIN , TOP_MARGIN + name.getAscent()/4);
		addFriendLabel();
		addImage(profile);
	}
	//adds user's image on the canvas
	private void addImage(FacePamphletProfile profile){
		if(profile.getImage() == null){
			GRect rec = new GRect(IMAGE_WIDTH, IMAGE_HEIGHT);
			add(rec,LEFT_MARGIN,TOP_MARGIN + IMAGE_MARGIN);

			GLabel lab = new GLabel("NO IMAGE");
			lab.setFont(PROFILE_IMAGE_FONT);
			add(lab, LEFT_MARGIN + (IMAGE_WIDTH - lab.getWidth())/2, IMAGE_MARGIN + TOP_MARGIN + rec.getHeight()/2 + lab.getAscent()/2);
		}else{
			GImage im = profile.getImage();
			im.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
			add(im, LEFT_MARGIN + (IMAGE_WIDTH - im.getWidth())/2, IMAGE_MARGIN + TOP_MARGIN);
		}
	}

	// just adding friend label
	private void addFriendLabel(){
		GLabel text = new GLabel("friends");
		text.setFont(PROFILE_FRIEND_LABEL_FONT);
		add(text, getWidth()/2,TOP_MARGIN + IMAGE_MARGIN);
	}

	//displays friend list
	private void showFriends(FacePamphletProfile profile){
		int count = 1;
		double listStartY = TOP_MARGIN + IMAGE_MARGIN;

		Iterator<String> it = profile.getFriends();
		while(it.hasNext()){
			GLabel fr = new GLabel(it.next());
			fr.setFont(PROFILE_FRIEND_FONT);
			add(fr, getWidth()/2, listStartY + count * fr.getHeight());
			count ++;
		}
	}

	
}
