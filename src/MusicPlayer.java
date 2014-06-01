import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

import javazoom.jl.player.Player;


public class MusicPlayer extends Thread implements Serializable{
	 private ArrayList<String> fileLocations;
	    private boolean loop;
	    private SoundPlayer prehravac;
	    private int index;

	    public MusicPlayer(ArrayList<String> fileLocation, boolean loop) {
	        fileLocations = fileLocation;
	        this.loop = loop;
	        index = (int) Math.round(Math.random() * (fileLocations.size()));
	    }

	    public void run() {

	        try {
	            do {
	            	if (index > fileLocations.size()-1)
	            		index = 0;
	            	InputStream buff = this.getClass().getResourceAsStream(fileLocations.get(index));
	                prehravac = new SoundPlayer(buff);
	                prehravac.play();
	                index++;
	            } while (loop);
	            
	        } catch (Exception ioe) {
	            // TODO error handling
	        	ioe.printStackTrace();
	        }
	    }

	    public void close(){
	        loop = false;
	        prehravac.close();
	        this.interrupt();
	    }
}
