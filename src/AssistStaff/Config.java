package AssistStaff;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;


public class Config {
	private static File Conf = new File(System.getProperty( "user.home") + File.separatorChar + "OnlyLZ.ini"); 
	public static void CheckConfig(){
		String cu_path = null, tmp_path = null;
		File directory1 = new File("");
		try{
			cu_path = directory1.getAbsolutePath();
			tmp_path = cu_path + File.separatorChar + "Temp" + File.separatorChar;
		}
		catch(Exception e){}

		if ( !Conf.exists()){
			try {
				FileWriter a = new FileWriter(Conf);
				a.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
			SetV("StorePath",cu_path);
			SetV("TempPath",tmp_path);
			SetV("SystemTray","yes");
			SetV("DownloadTemp","no");
			SetV("RewriteTxt","yes");
			SetV("BetweenLine","no");
			SetV("LineMark","");
			SetV("DefaultTxt","yes");
			SetV("DefaultPdf","no");
			SetV("DefaultPic","no");
		}
		
	}

	public static String GetV(String comment){
		CheckConfig();
		String value = null;
		Properties myProperties = new Properties();
		try{
			InputStreamReader	isr = new InputStreamReader(new FileInputStream(Conf));
			myProperties.load(isr);
			value = (String) myProperties.get(comment);
			isr.close();
		}
		catch(IOException e){
			return "false";
		}
		return value;
	}
	
	public static boolean SetV(String comment, String value){
		CheckConfig();
		Properties myProperties = new Properties();
		try{
			InputStreamReader	isr = new InputStreamReader(new FileInputStream(Conf));
			myProperties.load(isr);
			isr.close();
			OutputStream output = new FileOutputStream(Conf);
			myProperties.setProperty(comment,value);
			myProperties.store(output, comment);
			output.close();
		}
		catch(Exception e){
			return false;
		}
		return true;
	}
}
