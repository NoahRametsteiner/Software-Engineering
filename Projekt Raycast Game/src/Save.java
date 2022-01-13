import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Save {
	
	File save = null;

	String lvl = "0";
	String playerx = "350";
	String playery = "350";
	
	public int getlvl() {
		return Integer.parseInt(lvl);
	}
	public double getplayerx() {
		return Double.parseDouble(playerx);
	}
	public double getplayery() {
		return Double.parseDouble(playery);
	}

	public void setWrite(int write1, double px, double py) {
		this.lvl = String.valueOf(write1);
		this.playerx = String.valueOf(px);
		this.playery = String.valueOf(py);
	    try {
		      FileWriter saver = new FileWriter("C:/tmp/gamesave.txt");
		      saver.write(lvl+"\n");
		      saver.write(playerx+"\n");
		      saver.write(playery);
		      saver.close();
		      System.out.println("Successfully saved game");
		    } catch (IOException e) {
		      System.out.println("Error will saving");
		      e.printStackTrace();
		  }
	}



	Save() {

	save = new File("C:/tmp/gamesave.txt");

	FileExist(save); // check if File exists
	
	try {
	      Scanner saveread = new Scanner(save);
	      while (saveread.hasNextLine()) {
		      String data = saveread.nextLine();
		      lvl = data;
		      
		      data = saveread.nextLine();
		      playerx = data;
		      
		      data = saveread.nextLine();
		      playery = data;
		      
	      }
	      saveread.close();
	    } catch (FileNotFoundException e) {
	      System.out.println("Error while loading save");
	      e.printStackTrace();
	    }
	}
	


public void FileExist(File save) {
	if(save.exists()==false) {
		try {
			save.createNewFile();
			System.out.println("File created");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	if(save.canRead()==false || save.canWrite()==false) {
		System.out.println("save failed");
	}
}

}

