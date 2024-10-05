/*
 * File: FacePamphlet.java
 * -----------------------
 * When it is finished, this program will implement a basic social network
 * management system.
 */

import acm.program.*;
import acm.graphics.*;
import acm.util.*;

import java.awt.event.*;
import javax.swing.*;

public class FacePamphlet extends Program
        implements FacePamphletConstants {

    private FacePamphletCanvas canvas = new FacePamphletCanvas();
    private FacePamphletDatabase fpdb;
    private FacePamphletProfile curProf;

    /**
     * This method has the responsibility for initializing the
     * interactors in the application, and taking care of any other
     * initialization that needs to be performed.
     */

    /*
     * WEST PART
     * */
    private JTextField statusField;
    private JButton statusBut;
    private JTextField pictureField;
    private JButton pictureBut;
    private JTextField friendField;
    private JButton friendBut;
    /*
     * NORTH PART
     * */
    private JTextField name;
    private JButton add;
    private JButton delete;
    private JButton lookup;

    /*
     * CENTRAL PART
     * */

    private GImage pic;

    private void addWestPart(){
        // initialize west part
        statusField = new JTextField(TEXT_FIELD_SIZE);
        add(statusField, WEST);
        statusBut = new JButton("CHANGE STATUS");
        add(statusBut, WEST);
        statusField.addActionListener(this);

        add(new JLabel(EMPTY_LABEL_TEXT), WEST);

        pictureField = new JTextField(TEXT_FIELD_SIZE);
        add(pictureField, WEST);
        pictureBut = new JButton("CHANGE PICTURES");
        add(pictureBut, WEST);
        pictureField.addActionListener(this);

        add(new JLabel(EMPTY_LABEL_TEXT), WEST);

        friendField = new JTextField(TEXT_FIELD_SIZE);
        add(friendField, WEST);
        friendBut = new JButton("ADD FRIENDS");
        add(friendBut, WEST);
        friendField.addActionListener(this);
    }
    private void addNorthPart(){
        // initialize north part

        JLabel nameLab = new JLabel("NAME");
        add(nameLab, NORTH);

        name = new JTextField(TEXT_FIELD_SIZE);
        add(name, NORTH);
        name.addActionListener(this);

        add = new JButton("ADD");
        add(add, NORTH);

        delete = new JButton("DELETE");
        add(delete, NORTH);

        lookup = new JButton("LOOK UP");
        add(lookup, NORTH);
    }

    public void init() {
        add(canvas);
        addWestPart();
        addNorthPart();
        addActionListeners();
        fpdb = new FacePamphletDatabase();
        curProf = null;
    }


    /**
     * This class is responsible for detecting when the buttons are
     * clicked or interactors are used, so you will have to add code
     * to respond to these actions.
     */
    public void actionPerformed(ActionEvent e) {
        //  canvas.showMessage("yeaa");
        if (e.getSource() == add && !name.getText().equals("")) {
            addUser();
        } else if (e.getSource() == delete && !name.getText().equals("")) {
            deleteAccount();
        } else if (e.getSource() == lookup && !name.getText().equals("")) {
            lookupAccount();
        } else if (e.getSource() == statusField || e.getSource() == statusBut) {
            changeStatus();
        } else if (e.getSource() == pictureField || e.getSource() == pictureBut) {
            changePicture();
        } else if (e.getSource() == friendField || e.getSource() == friendBut) {
            addFriends();
        }
    }
    
    //adding account
    private void addUser() {

        if (!fpdb.containsProfile(name.getText())) {
            FacePamphletProfile profile = new FacePamphletProfile(name.getText());
            curProf = profile;
            curProf.setStatus("no current Status");
            //fotozec egre
            fpdb.addProfile(profile);
            canvas.displayProfile(curProf);
            canvas.showMessage("new profile added");
            statusField.setText("");
            friendField.setText("");
        } else {
            curProf = fpdb.getProfile(name.getText());
            curProf.setStatus(curProf.getStatus());
            canvas.displayProfile(curProf);
            canvas.showMessage("profile with this name already exists");
            statusField.setText("");
            friendField.setText("");
        }
        name.setText("");
    }

    // deleting account
    private void deleteAccount() {
        if (fpdb.containsProfile(name.getText())) {
            fpdb.deleteProfile(name.getText());
            canvas.removeAll();
            canvas.showMessage("a profile with name " + curProf.getName() + " deleted");
        } else {
            canvas.showMessage("profile with that name doesn't exist");
        }
        name.setText("");
        curProf = null;
    }

    private void lookupAccount() {
        if (fpdb.containsProfile(name.getText())) {
            curProf = fpdb.getProfile(name.getText());
            canvas.displayProfile(curProf);
            canvas.showMessage("Displaying profile " + fpdb.getProfile(curProf.getName()));
            statusField.setText("");
            friendField.setText("");
        } else {
            canvas.showMessage("profile with that name doesn't exist");
            curProf = null;
        }
    }

    // updating status
    private void changeStatus() {
        if (curProf != null) {
            curProf.setStatus(statusField.getText());
            canvas.displayProfile(curProf);
            canvas.showMessage("status changed");
        } else {
            canvas.showMessage("chose profile first");
        }
    }

    private void addFriends() {
        String friend = friendField.getText();
        String cur = name.getText();
        if (friend.equals("")) {
            canvas.showMessage("write friend name first");
            return;
        }
        if (curProf != null) {
            if (fpdb.containsProfile(friend)) {
                if (!cur.equals(friend)) {
                    curProf.addFriend(friend);
                    // as friendship is two-sided
                    canvas.displayProfile(curProf);
                    FacePamphletProfile  heOrShe = fpdb.getProfile(friend);
                    heOrShe.addFriend(curProf.getName());
                    canvas.showMessage("user with profile name " + friend + " added to a friend list");
                } else {
                    canvas.showMessage("you are already friends or you entered your name");
                }
            } else {
                canvas.showMessage("user with that name does not exist");
            }
        } else {
            canvas.showMessage("chose Profile first");
        }
    }

    // updating picture
    private void changePicture(){
        pic = null;
        try {
            pic = new GImage(pictureField.getText());
            curProf.setImage(pic);
            pictureField.setText("");
            canvas.displayProfile(curProf);
            canvas.showMessage("profile picture updated");
        } catch (ErrorException ex) {
          System.out.println("invalid address");
        }
    }
}
