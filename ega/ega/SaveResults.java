//package utep.ais.gametheory.main;
package ega;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//import utep.ais.gametheory.util.Parameters;

public class SaveResults {

	private PrintWriter out;
	private DateFormat dateFormat;
	private Date date;
	
	// For Windows I need to create the file
	private boolean windows;
	//private File file;
	
	public SaveResults(){
		this.dateFormat = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
		this.date = new Date();
		try{
		out = new PrintWriter(Parameters.RESULTS_PATH + "ResultsExperiments "  +  dateFormat.format(date) + ".txt","UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void write(String line){
		out.println(line);
	}
	
	public void close(){
		out.close();
	}
}
