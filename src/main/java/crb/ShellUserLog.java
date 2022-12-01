/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crb;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sw.db.DB;
import sw.db.Envilop;

/**
 *
 * @author amila
 */
public class ShellUserLog {

  public static boolean logSuccess(){
    boolean ans=false;
   
    System.out.println("====================================================");
    System.out.println("====================================================");
    System.out.println("\n\n\n");

    Console cnsl = null;
    String userName = null;
    String password = "";

//    gssysadmin  1001
//    esysadmin   1002

      try{
         cnsl = System.console();
         if (cnsl != null) {
            userName = cnsl.readLine("\t\tUSER NAME: ");
            char[] pwd = cnsl.readPassword("\t\tPASSWORD: ");
            for(int x=0;x<pwd.length;x++)
            password  = password + ""+ pwd[x];

         }
      }catch(Exception ex){

         // if any error occurs
         ex.printStackTrace();
      }
    //System.out.println("\n\n");

 


//    System.out.println("USER NAME: " + userName);
//    System.out.println("pw: " + password);



    DB db=new DB();
    String sql="SELECT BankID, Uname, Ulevel, ByUser FROM tbl_MUser WHERE (UserID = '"+ userName +"') AND (Upass = '"+ password +"')";
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      if(rs.next()){
        ans=true;
//				TBSConst.BankID=(int) rs.getDouble("BankID");
//				TBSConst.Uname=rs.getString("Uname");
			//	TBSConst.UserID=userName;
//				TBSConst.Ulevel=rs.getString("Ulevel");
//				TBSConst.ByUser=rs.getString("ByUser");
      }
    } catch (SQLException ex) {
      Logger.getLogger(ShellUserLog.class.getName()).log(Level.SEVERE, null, ex);
    }

    if(ans){
      System.out.println("====================================================");
      System.out.println("====================================================");
      System.out.println("\n\n\n");
      System.out.println("\tWelcome " + TBSConst.Uname );
//      System.out.println(" do you want to start SI Process ? (enter y)");

    }
    return ans;
  }

//  private static void clearConsol(){
//    for (int i = 0; i < 100; i++) {
//        System.out.println();
//    }
//  }
}
