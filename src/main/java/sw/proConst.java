package sw;

import amino.data.Encript;
import java.io.FileNotFoundException;

import amino.data.ReadWrite;
import crb.TBSConst;
import javax.swing.JOptionPane;

public class proConst {	
	public static final String fln= "reg";

  public static String serverIp="localhost";
  public static String SERVER_PORT="";
	public static String dbName="";
	public static String user="";
	public static String passwd="";
	//public static String bankId="";

  public static String l10n="";
  public static String reportPath="";
  public static String tmpOutputPath="";
  public static String branchID="";

  	public static void getOpenID(){
    l10n= "l10n/Eng";
		String line[]=null;
		try{
			line=ReadWrite.readFile(fln,23);
		}catch(FileNotFoundException fnfe){
      JOptionPane.showMessageDialog(null, "Use Reg File Editer and create the connection file");
			//new optProgramSetting();
		}
		serverIp= Encript.decript2(line[0]);

		dbName=Encript.decript2(line[1]);
		user=Encript.decript2(line[2]);
		passwd=Encript.decript2(line[3]);
		reportPath=Encript.decript2(line[4]);
    SERVER_PORT=Encript.decript2(line[5].trim());

    tmpOutputPath=Encript.decript2(line[7]);
//    reseivedFilePath=Encript.decript2(line[7]);
		branchID=Encript.decript2(line[8]);
		//qtlyIntPostSystem=Encript.decript2(line[9]);
    if(SERVER_PORT.equals("")){
      SERVER_PORT=RegQuery.getServerPort();
    }
        System.out.println("DB " +dbName);
//		isUserLogging=line[10];
//    slipPath=line[11];
//    slipOutPath=line[12];
	}


	public static void getOpenIDOld(){
		String line[]=null;
		try{
			line=ReadWrite.readFile(fln,10);
		}catch(FileNotFoundException fnfe){
			
		}

		serverIp=line[0];
		SERVER_PORT=line[1].trim();
    dbName=line[2];
		user=line[3];
		passwd=line[4];
//    TBSConst.BankID=new Integer(line[5]);
//    TBSConst.Uname=line[6];

//				=

    if(SERVER_PORT.equals("")){
      SERVER_PORT=RegQuery.getServerPort();
    }
	}
	
}






