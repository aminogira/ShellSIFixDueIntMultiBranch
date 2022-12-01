package sw;

import java.io.*;

public class RegQuery {

  private static final String REGQUERY_UTIL = "reg query ";
  private static final String REGSTR_TOKEN = "REG_SZ";
  //private static final String REGDWORD_TOKEN = "REG_DWORD";
  public static String SERVER_PORT ="";
 // public static String SQLPATH ="";
  private static final String PERSONAL_FOLDER_CMD = REGQUERY_UTIL +
   "\"HKLM\\Software\\Microsoft\\Microsoft SQL Server\\MSSQL.1\\MSSQLServer\\SuperSocketNetLib\\Tcp\\IPAll\" /v TcpDynamicPorts" ;
 // "\"HKLM\\Software\\Microsoft\\Microsoft SQL Server\\SQLEXPRESS\\MSSQLServer\\SuperSocketNetLib\\Tcp\" /v TcpPort" ;
  private static final String SQL_PATH_CMD = REGQUERY_UTIL +
   "\"HKLM\\Software\\Microsoft\\Microsoft SQL Server\\SQLEXPRESS\\Setup\" /v SQLPath" ;

  public static String getCurrentUserPersonalFolderPath() {
    try {
      Process process = Runtime.getRuntime().exec(PERSONAL_FOLDER_CMD);
      StreamReader reader = new StreamReader(process.getInputStream());

      reader.start();
      process.waitFor();
      reader.join();

      String result = reader.getResult();
      int p = result.indexOf(REGSTR_TOKEN);

      if (p == -1) return null;

      return result.substring(p + REGSTR_TOKEN.length()).trim();
    }catch (Exception e) {
      return null;
    }
  }

  public static String getSQLPath() {
    try {
      Process process = Runtime.getRuntime().exec(SQL_PATH_CMD);
      StreamReader reader = new StreamReader(process.getInputStream());

      reader.start();
      process.waitFor();
      reader.join();

      String result = reader.getResult();
      int p = result.indexOf(REGSTR_TOKEN);

      if (p == -1) return null;

      return result.substring(p + REGSTR_TOKEN.length()).trim();
    }catch (Exception e) {
      return null;
    }
  }

  public static String getCOMMON(String PATH,String TYPE) {
    try {
      Process process = Runtime.getRuntime().exec(PATH);
      StreamReader reader = new StreamReader(process.getInputStream());

      reader.start();
      process.waitFor();
      reader.join();

      String result = reader.getResult();
      int p = result.indexOf(TYPE);
      if (p == -1) return null;
      return result.substring(p + TYPE.length()).trim();
    }catch (Exception e) {
      return null;
    }
  }

  static class StreamReader extends Thread {
    private InputStream is;
    private StringWriter sw;

    StreamReader(InputStream is) {
      this.is = is;
      sw = new StringWriter();
    }

    public void run() {
      try {
        int c;
        while ((c = is.read()) != -1)
          sw.write(c);
      }catch (IOException e) { }
    }

    String getResult() {
      return sw.toString();
    }
  }
  public static void GetIP() {
	  SERVER_PORT= getCurrentUserPersonalFolderPath();
    System.out.println(SERVER_PORT);
//	  SQLPATH=getSQLPath();
  }
  public static String getServerPort(){
    return getCurrentUserPersonalFolderPath();
  }

  public static String getRegfilePath(){
    return   System.getProperty("java.home") + System.getProperty("file.separator");
  }


}

