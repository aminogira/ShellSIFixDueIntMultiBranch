
import amino.data.ReadWrite;
import crb.ShellUserLog;
import java.io.Console;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
//import process.SISaving2CompoundSameLineMultiBranch;
//import process.SISaving2CompoundSameLineMultiBranchNew;
import process.SISaving2PMTLoan;
import sw.db.DB;
import sw.db.Envilop;
import sw.proConst;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author amila
 */
public class Main {

  public static void main(String args[]) {
    proConst.getOpenID();
    // if(ShellUserLog.logSuccess()){
    //    gssysadmin  1001
//    esysadmin   1002

    System.out.println("log success");
    try {
      String[] userList = ReadWrite.readFile("BankList.txt");
      String[] sitype = ReadWrite.readFile("settings/SiType.txt");
      
      SISaving2PMTLoan.SIType=sitype[0];
      copyTmpData();
      for (int x = 0; x < userList.length; x++) {
        String bid = userList[x];
        x++;
        String usr = userList[x];

        System.out.println("#################### Starting " + bid);
        if (bid.trim().length() > 0) //SISaving2CompoundSameLineMultiBranch.openSIList(bid.trim(),usr.trim());
        {
          SISaving2PMTLoan.openSIList(bid.trim(), usr.trim(), sitype[0].trim());
        }
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static void copyTmpData() {
    String sqlDel1 = "delete from tbl_SISub_tmp ";
    String sqlDel2 = "delete from tbl_SIMaster_tmp  ";
    String sqlInst1 = "INSERT INTO tbl_SISub_tmp(BankId, SIType, slno, dueno, DueDate, NoDChd, Result) "
            + " SELECT BankId, SIType, slno, dueno, DueDate, NoDChd, Result FROM tbl_SISub where sitype='" + SISaving2PMTLoan.SIType + "' and  Result='pending'";
    String sqlInst2 = "INSERT INTO tbl_SIMaster_tmp(BankId, SIType, slno, FAcNo, TAcNo, MoP, BFDetail, BFBank, PayDet, Remarks, MinAmt, MaxAmt, SDate, EDate, frqtype, frqnos, NoDFolup, Status, Cusid, ByUser, ByDate, curtype) "
            + " SELECT BankId, SIType, slno, FAcNo, TAcNo, MoP, BFDetail, BFBank, PayDet, Remarks, MinAmt, MaxAmt, SDate, EDate, frqtype, frqnos, NoDFolup, Status, Cusid, ByUser, ByDate, curtype FROM tbl_SIMaster where sitype='" + SISaving2PMTLoan.SIType + "' and  Status='ACTIVE'";

    DB dbTmp = new DB();
    try {
      dbTmp.updateData(new Envilop(sqlDel1));
    } catch (SQLException ex) {
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
      dbTmp.updateData(new Envilop(sqlDel2));
    } catch (SQLException ex) {
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
      dbTmp.updateData(new Envilop(sqlInst1));
    } catch (SQLException ex) {
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
      dbTmp.updateData(new Envilop(sqlInst2));
    } catch (SQLException ex) {
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void mainbb(String[] args) {
    System.out.println("dd");
    Console cnsl = null;
    String alpha = null;

    try {
      // creates a console object
      cnsl = System.console();

      // if console is not null
      if (cnsl != null) {

        // read line from the user input
        alpha = cnsl.readLine("Name: ");

        // prints
        System.out.println("Name is: " + alpha);

        // read password into the char array
        char[] pwd = cnsl.readPassword("Password: ", "*");

        System.out.println("Password is: " + pwd);
        for (int x = 0; x < pwd.length; x++) {
          System.out.print(pwd[x]);
        }
      }
    } catch (Exception ex) {

      // if any error occurs
      ex.printStackTrace();
    }
  }
}
