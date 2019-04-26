package librarysystem.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * 
 * Used for logging application transactions stored in a logs.txt file
 *
 */
public class Log {
	
	private static final String LOG_FILE = "logs.txt";
	
	
	public static final void log(String content) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))){
			 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			 LocalDateTime now = LocalDateTime.now();
			 writer.write("\r\n" + "[" + dtf.format(now)+ "] " + content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	

}
