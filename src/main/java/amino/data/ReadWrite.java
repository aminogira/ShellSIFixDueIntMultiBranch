package amino.data;
//
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author amila giragama
 *
 */
public class ReadWrite {
	
	final String orgi= "QWERTYUIOPLKJHGFDSAZXCVBNMqwertyuioplkjhgfd sazxcvbnm<,>.?/:;'{[}|+=_-)0(9*8&7^6%5$4#3@2!1~`";
	final String entr= "qtyYerUXFDjhgNuioCwVcvGMk<QWfd2!1~ m4#3@`SBOKsaplPLAE(9*8'{IZ[_-)0%5$TzxR&bnJH7^6}|+=,>.?/:;";
	
	public static void writeFile(String filenama,String[] data){
		FileOutputStream fout;		
		try{
		    fout = new FileOutputStream (filenama + ".ai");
		    for (int x=0;x<data.length;x++)
		    	new PrintStream(fout).println (data[x]);
		    fout.close();		
		}catch (IOException e){
			System.err.println ("Unable to write to file");
		}
	}
	
	
	public static String[] readFile(String filename,int length)throws FileNotFoundException{
		File file = new File(filename + ".ai");
		//StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		String[] line=new String[length];
		try{
			reader = new BufferedReader(new FileReader(file));
//   ################  DO NOT REMOVE STUDY THIS 3 LINES			
		  
		  String text="";
		  int x=0;
		  while ((text = reader.readLine()) != null){
		  	//contents.append(text).append(System.getProperty("line.separator"));
       // System.out.println(text);
		  	line[x]=text;
		  	x++;
		  }
			
			//txtPassword.setText(reader.readLine());

		}catch (FileNotFoundException e){
			throw e;
			//new optProgramSetting();
		  //	e.printStackTrace();
		}catch (IOException e){
		    e.printStackTrace();
		}finally{
		  try{
		    if (reader != null){
		    	reader.close();
		    }
		  }catch (IOException e){
		    e.printStackTrace();
		  }
	  }
		return line;
	}
	
	//====================================================================
  public static String[] readFile(String filename)throws FileNotFoundException{
		File file = new File(filename );
		//StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		String[] tmpLine=new String[500];
    String[] line=null;
		int count=0;
		try{
			reader = new BufferedReader(new FileReader(file));
//   ################  DO NOT REMOVE STUDY THIS 3 LINES

		  String text="";
		  int x=0;
		  while ((text = reader.readLine()) != null){
		  	//contents.append(text).append(System.getProperty("line.separator"));
		  	tmpLine[x]=text;
		  	x++;
		  }
			count=x;
			//txtPassword.setText(reader.readLine());
      line=new String[count];
      for(int a=0;a<count;a++){
        line[a]=tmpLine[a];
      }

		}catch (FileNotFoundException e){
			throw e;
			//new optProgramSetting();
		  //	e.printStackTrace();
		}catch (IOException e){
		    e.printStackTrace();
		}finally{
		  try{
		    if (reader != null){
		    	reader.close();
		    }
		  }catch (IOException e){
		    e.printStackTrace();
		  }
	  }
		return line;
	}

	//=============================

	public static void fileAppend(String filename,String[] data){
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(filename+ ".ai", true));
			for (int x=0;x<data.length;x++)
				out.write(data[x]  + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void fileWrite(String filenama,String[] data){
		FileOutputStream fout;		
		try{
		    fout = new FileOutputStream (filenama+ ".ai");
		    if (data!=null)
			    for (int x=0;x<data.length;x++)
			    	new PrintStream(fout).println (data[x]);
		    fout.close();		
		}catch (IOException e){
			System.err.println ("Unable to write to file");
		}
	}
	public static String[] fileRead(String filename,int length)throws FileNotFoundException{
		File file = new File(filename);
		//StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		String[] line=new String[length];
		
		try{
			reader = new BufferedReader(new FileReader(file));
//   ################  DO NOT REMOVE STUDY THIS 3 LINES			
		  
		  String text="";
		  int x=0;
		  while ((text = reader.readLine()) != null){
		  	//contents.append(text).append(System.getProperty("line.separator"));
		  	line[x]=text;
		  	x++;
		  }
			
			//txtPassword.setText(reader.readLine());

		}catch (FileNotFoundException e){
			throw e;
			//new optProgramSetting();
		  //	e.printStackTrace();
		}catch (IOException e){
		    e.printStackTrace();
		}finally{
		  try{
		    if (reader != null){
		    	reader.close();
		    }
		  }catch (IOException e){
		    e.printStackTrace();
		  }
	  }
		return line;
	}

	
	
}
