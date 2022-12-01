/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package process;

//import crb.TBSConst;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import sw.db.DB;
import sw.db.Envilop;
import sw.db.ctrlData;
import sw.proConst;

/**
 *
 * @author amino
 */
public class SISaving2PMTLoan {

  static Date today;
  private static final int N_LN_SCM = 50;
  private static final int N_SV_SCM = 50;
  public static String SIType = "S2I";
  static int nLnScm, nSvScm;
  static double intPer = 0;
  static double lcbal = 0;
  static String lactDate = "";
  static String loanStatus;
  static int curLonScmRowId;
  static int curSavScmRowId;
  static int scmId[];
  static String explgcode[];
  static String lgcodeact[], intcodeact[], pencodeact[];
  static String lgcodedor[], intcodedor[], pencodedor[];
  static String lgcodepss[], intcodepss[], pencodepss[];
  static String lgcodesh1[], intcodesh1[], pencodesh1[];
  static String lgcodesh2[], intcodesh2[], pencodesh2[];
  static String lgcodesh3[], intcodesh3[], pencodesh3[];
  static String lgcodesh4[], intcodesh4[], pencodesh4[];
  static int savScmId[];
  static String savlgcode[], savdomlgcode[], savdeadlgcode[];

  public static void openSIList(String gcrentBankID, String gcrntUserName, String siType) {
    System.out.println("open SI List");
    SIType = siType;
    ////////////////////
    DB db = new DB();
    DB dbWrite = new DB();

    today = db.getCalDate(gcrentBankID + "");
    loadSavingsLedgerCode(gcrentBankID, db);
    loadLoanLedgerCodes(gcrentBankID, db);

    String sysDate = ctrlData.getDateTimeString(db.getCalDate(gcrentBankID + ""));
//    String sql="SELECT tbl_SIMaster_tmp.slno, tbl_SIMaster_tmp.FAcNo, tbl_SIMaster_tmp.TAcNo, tbl_SIMaster_tmp.MaxAmt, tbl_SIMaster_tmp.frqtype, tbl_SIMaster_tmp.frqnos, tbl_SIMaster_tmp.NoDFolup, tbl_SIMaster_tmp.Cusid, tbl_SISub_tmp.dueno, tbl_SISub_tmp.DueDate, tbl_SISub_tmp.NoDChd, tbl_SavAc.scurbal,tbl_SavAc.sstatus " +
//    " FROM tbl_SavAc INNER JOIN tbl_SIMaster_tmp INNER JOIN tbl_SISub_tmp ON tbl_SIMaster_tmp.BankId = tbl_SISub_tmp.BankId AND tbl_SIMaster_tmp.SIType = tbl_SISub_tmp.SIType AND tbl_SIMaster_tmp.slno = tbl_SISub_tmp.slno ON tbl_SavAc.sacno = tbl_SIMaster_tmp.FAcNo " +
//    " WHERE (tbl_SIMaster_tmp.Status = 'active') AND (tbl_SIMaster_tmp.BankId = "+ proConst.bankID +") AND (tbl_SIMaster_tmp.SIType = '"+ SIType +"') AND (tbl_SISub_tmp.Result = 'Pending') AND (tbl_SISub_tmp.DueDate BETWEEN '1900-01-01' AND '"+ ctrlData.getDateString(today).substring(0,10) +" 23:59:59') ORDER BY tbl_SISub_tmp.dueno,tbl_SIMaster_tmp.slno";

    String sql = "SELECT tbl_SIMaster_tmp.slno, tbl_SIMaster_tmp.FAcNo, tbl_SIMaster_tmp.TAcNo, tbl_SIMaster_tmp.MaxAmt, tbl_SIMaster_tmp.frqtype, tbl_SIMaster_tmp.frqnos, tbl_SIMaster_tmp.NoDFolup, tbl_SIMaster_tmp.Cusid, tbl_SISub_tmp.dueno, tbl_SISub_tmp.DueDate, tbl_SISub_tmp.NoDChd "
            + " FROM tbl_SIMaster_tmp INNER JOIN tbl_SISub_tmp ON tbl_SIMaster_tmp.BankId = tbl_SISub_tmp.BankId AND tbl_SIMaster_tmp.SIType = tbl_SISub_tmp.SIType AND tbl_SIMaster_tmp.slno = tbl_SISub_tmp.slno "
            + " WHERE (tbl_SIMaster_tmp.Status = 'active') AND (tbl_SIMaster_tmp.BankId = " + gcrentBankID + ") AND (tbl_SIMaster_tmp.SIType = '" + SIType + "') AND (tbl_SISub_tmp.Result = 'Pending') AND (tbl_SISub_tmp.DueDate BETWEEN '1900-01-01' AND '" + ctrlData.getDateString(today).substring(0, 10) + " 23:59:59') ORDER BY tbl_SIMaster_tmp.slno,tbl_SISub_tmp.dueno";

    //  System.out.println("01 a " + sql);
    //tbl_SISub., tbl_SISub., tbl_SISub.NoDChd, tbl_SavAc.scurbal
    String sqlInst = "";
    try {
      //System.out.println(" 01 aa ");
      ResultSet rs = db.readData(new Envilop(sql));
      //System.out.println(" o1 bbb");
      int crntSlNo = 0;
      double lastPayment = 0;
      double savingsBalance = 0;
      while (rs.next()) {
        int slNo = rs.getInt("slno");
        String savAc = rs.getString("FAcNo");
        String lnAc = rs.getString("TAcNo");

        lnAc = lnAc.trim();

//        if(lnAc.equals("1511-2101-10020")  ){// ||  lnAc.equals("1511-2401-10782") || lnAc.equals("1511-2421-10121")
//          System.out.println("found acc");
//        }
        double siAmo = rs.getDouble("MaxAmt");
        String cusId = rs.getString("Cusid");
        int dueNo = rs.getInt("dueno");
        String dueDate = rs.getString("DueDate");
        int NoDFolup = rs.getInt("NoDFolup");
        int NoDChd = rs.getInt("NoDChd");
        System.out.println(SIType + " Loan :- " + lnAc);

        if (crntSlNo == slNo) {
          savingsBalance -= lastPayment;
        } else {
          crntSlNo = slNo;
          savingsBalance = findSavingsWithdrawableBaleBalance(savAc, db);      //rs.getDouble("scurbal");
        }

//if(true)return;
        double expAmo = findExpences(lnAc, db);
        double fineAmo = findFine(lnAc, db);
        double intAmo = calInterest(lnAc, db);//no new calculation check only balance avalable
        double oldIntAmo = findOldInt(lnAc, dueDate, db);
        double dueAmo = findAllDue(lnAc, dueDate, db);

        boolean siOnProb = false;
        String remarkReson = "";
        if (intAmo == -200) {
          siOnProb = true;
          remarkReson = "Zero Loan Balance";
          intAmo = 0;
        }
        if (intAmo == -300) {
          siOnProb = true;
          remarkReson = "Bad Loan Type";
          intAmo = 0;
        }
//          if(savingsBalance<(siAmo+intAmo+expAmo+fineAmo+oldIntAmo)){
        // if(savingsBalance<(siAmo+expAmo+fineAmo)){
//        if(saving sBalance<(dueAmo+expAmo+fineAmo+oldIntAmo)){
        if (savingsBalance < (expAmo + fineAmo + oldIntAmo + dueAmo)) {//
          siOnProb = true;
          remarkReson = "Insufficient Balance";
        }
        if (siOnProb) {
          //System.out.println("03 On Prob " + remarkReson + " :: " + savAc + " >> " + lnAc);
          String sqlUpdtSub = "";
          if (NoDFolup > (NoDChd + 1)) {
            sqlUpdtSub = "UPDATE tbl_SISub SET NoDChd =NoDChd+1 "
                    + " WHERE (BankId = " + gcrentBankID + ") AND (SIType = '" + SIType + "') AND (slno = " + slNo + ") AND (dueno = " + dueNo + ") ";
          } else {
            sqlUpdtSub = "UPDATE tbl_SISub SET NoDChd = NoDChd + 1, Result = 'Cancelled' "
                    + " WHERE (BankId = " + gcrentBankID + ") AND (SIType = '" + SIType + "') AND (slno = " + slNo + ") AND (dueno = " + dueNo + ")";
          }
          sqlInst = "INSERT INTO tbl_SIrun(BankId, SIType, slno, rundate, duedate, result, reson, Amt, ByUser, ByDate) "
                  + " VALUES (" + gcrentBankID + ", '" + SIType + "', " + slNo + ", '" + sysDate + "', '" + dueDate.substring(0, 10) + " 00:00:00', 'Pending', '" + remarkReson + "', 0, '" + gcrntUserName + "', '" + sysDate + "')";
          //System.out.println("03 insr " + sqlInst);
          //System.out.println("03 updt " + sqlUpdtSub);
          dbWrite.updateData(new Envilop(sqlUpdtSub));
          dbWrite.updateData(new Envilop(sqlInst));

        } else {
          //System.out.println("ok si ready to process");
          int lonScm = new Integer(lnAc.substring(6, 8));
          int savScm = new Integer(savAc.substring(6, 8));
          //System.out.println("04 " + savAc + " >> " + lnAc);
          //System.out.println("04 a " + savScm + " >> " + lonScm);
          for (int r = 0; r < nLnScm; r++) {
            if (scmId[r] == lonScm) {
              curLonScmRowId = r;
            }
            // System.out.println("04 b " + scmId[r]);
          }
          for (int r = 0; r < nSvScm; r++) {
            if (savScmId[r] == savScm) {
              curSavScmRowId = r;
            }
            //System.out.println("04 c " + savScmId[r]);
          }
          remarkReson = "-";
          //System.out.println("05 " + curSavScmRowId + " >> " + curLonScmRowId);
          double totPayAmt = settleLoan(lnAc, expAmo, dueAmo, sysDate, savAc, fineAmo, oldIntAmo, gcrentBankID, gcrntUserName, db, dbWrite);
          lastPayment = totPayAmt;
          double paidDue = totPayAmt - (expAmo + fineAmo + oldIntAmo);
          String siStatus = "Pending";
          if (paidDue >= dueAmo) {
            siStatus = "Success";
          }
          if (totPayAmt > 0) {
            if (updateSaving(savAc, lnAc, totPayAmt, sysDate, gcrentBankID, gcrntUserName, db)) {

              String sqlUpdtSub = "UPDATE tbl_SISub SET NoDChd = NoDChd + 1, Result = '" + siStatus + "' "
                      + " WHERE (BankId = " + gcrentBankID + ") AND (SIType = '" + SIType + "') AND (slno = " + slNo + ") AND (dueno = " + dueNo + ")";

              sqlInst = "INSERT INTO tbl_SIrun(BankId, SIType, slno, rundate, duedate, result, reson, Amt, ByUser, ByDate) "
                      + " VALUES (" + gcrentBankID + ", '" + SIType + "', " + slNo + ", '" + sysDate + "', '" + dueDate.substring(0, 10) + " 00:00:00', '" + siStatus + "', '" + remarkReson + "', " + siAmo + ", '" + gcrntUserName + "', '" + sysDate + "')";
              dbWrite.updateData(new Envilop(sqlUpdtSub));
              dbWrite.updateData(new Envilop(sqlInst));
              System.out.println("Settled  " + savAc + ">>>" + lnAc + ":::" + intAmo + " + " + siAmo + " + " + expAmo);
            } else {
              //TODO log file
              //System.out.println("06 saving update error  " + savAc);
            }
          } else {
            //TODO log file
//            System.out.println("07 loan update error " + lnAc);
            System.out.println("07 loan update exception zero amount  " + lnAc);
          }
        }
      }
    } catch (SQLException ex) {
      ////System.out.println("20201005\n 101" + sqlInst);

      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static double settleLoan(String lnAcNo, double expAmo, double dueAmo, String sysDate, String savAcNo, double fineAmo, double oldIntAmo, String gcrentBankID, String gcrntUserName, DB dbRead, DB dbWrite) {
    //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
    //      + lnAcNo + "  due >>" + dueAmo + " int >>" + oldIntAmo + " fine>>" + fineAmo + " exp>>" + expAmo);
    double totPay = 0;

//    DB dbRead=new DB();
//    DB dbWrite=new DB();
    boolean ans = true;
    int settleNo = 0;

    //double totSettleAmount=0;
    String sqlMax = "select max(lsno) as lastNo from tbl_LOANSettle where lacno like '" + gcrentBankID + "-%'";
    //System.out.println("settleLoan " + sqlMax);
    try {
      ResultSet rsMax = dbRead.readData(new Envilop(sqlMax));
      if (rsMax.next()) {
        settleNo = rsMax.getInt("lastNo");
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 102" + sqlMax);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    int glTransNo = 0;
    String sqlMaxGL = "select max(tno) as lastNo from tbl_gltransaction where ttype like 'LSST' and bankid=" + gcrentBankID + "";
    //System.out.println("settleLoan " + sqlMaxGL);
    try {
      ResultSet rsMax = dbRead.readData(new Envilop(sqlMaxGL));
      if (rsMax.next()) {
        glTransNo = rsMax.getInt("lastNo");
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 103" + sqlMaxGL);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    glTransNo++;
    settleNo++;
    if (expAmo > 0) {
      String sqlExp = "SELECT lexno, lexbal FROM tbl_LOANExpenses WHERE (lacno = '" + lnAcNo + "') AND (lexbal > 0) ORDER BY lexno";
      //System.out.println("settleLoan " + sqlExp);
      String sqlInst = "";
      try {
        ResultSet rsExp = dbRead.readData(new Envilop(sqlExp));
        while (rsExp.next()) {
          int lexno = rsExp.getInt("lexno");
          double lexbal = rsExp.getDouble("lexbal");
          if (lexbal > 0) {
            String sqlUpdtExp = "UPDATE tbl_LOANExpenses SET lexbal = 0 WHERE (lacno = '" + lnAcNo + "') AND (lexno = " + lexno + ")";
            sqlInst = "INSERT INTO tbl_LOANSettlesub(lacno, lsno, lsdocno, lsamt, lgcode) "
                    + " VALUES('" + lnAcNo + "', " + settleNo + ", 'EXP-" + lexno + "', " + lexbal + ", '" + explgcode[curLonScmRowId] + "')";
            String sqlGlTrans = "INSERT INTO tbl_GLTransaction(bankid, ttype, tno, tdate, lgcode, lgdes, tamt, crdr, cshtype, tremarks, ByUser, ByDate) "
                    + " VALUES (" + gcrentBankID + ", 'LSST', " + glTransNo + ", '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', '" + explgcode[curLonScmRowId].substring(0, 6) + "', '" + explgcode[curLonScmRowId].substring(7) + "', " + lexbal + ", 'CR', 'LKR', '" + lnAcNo + ":SI/" + SIType + " Trans From :" + savAcNo + "', '" + gcrntUserName + "', '" + sysDate + "')";
            totPay += lexbal;
            dbWrite.updateData(new Envilop(sqlUpdtExp));
            dbWrite.updateData(new Envilop(sqlInst));
            dbWrite.updateData(new Envilop(sqlGlTrans));
            //totSettleAmount+= lexbal;
            //System.out.println("settleLoan " + sqlUpdtExp);
            //System.out.println("settleLoan " + sqlInst);
            System.out.println("settleLoan " + sqlGlTrans);
          }
        }
      } catch (SQLException ex) {
        //System.out.println("20201005\n 104" + sqlInst);
        Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
        ans = false;
      }
    }
    String statusGLCodeInt = "";
    String statusGLCode = "";
    String statusGLCodePen = "";
    if (loanStatus.equals("ACTIVE")) {
      statusGLCode = lgcodeact[curLonScmRowId];
      statusGLCodeInt = intcodeact[curLonScmRowId];
      statusGLCodePen = pencodeact[curLonScmRowId];
    } else if (loanStatus.equals("DORMANT") || loanStatus.equals("AWPRMIKA")) {
      statusGLCode = lgcodedor[curLonScmRowId];
      statusGLCodeInt = intcodedor[curLonScmRowId];
      statusGLCodePen = pencodedor[curLonScmRowId];
    } else if (loanStatus.equals("PASSDUE") || loanStatus.equals("SEKASHITHA")) {
      statusGLCode = lgcodepss[curLonScmRowId];
      statusGLCodeInt = intcodepss[curLonScmRowId];
      statusGLCodePen = pencodepss[curLonScmRowId];
    } else if (loanStatus.equals("SHOOTFIELD1") || loanStatus.equals("NETHIU/PADU")) {
      statusGLCode = lgcodesh1[curLonScmRowId];
      statusGLCodeInt = intcodesh1[curLonScmRowId];
      statusGLCodePen = pencodesh1[curLonScmRowId];
    } else if (loanStatus.equals("SHOOTFIELD2") || loanStatus.equals("Pass Due")) {
      statusGLCode = lgcodesh2[curLonScmRowId];
      statusGLCodeInt = intcodesh2[curLonScmRowId];
      statusGLCodePen = pencodesh2[curLonScmRowId];
    } else if (loanStatus.equals("SHOOTFIELD3")) {
      statusGLCode = lgcodesh3[curLonScmRowId];
      statusGLCodeInt = intcodesh3[curLonScmRowId];
      statusGLCodePen = pencodesh3[curLonScmRowId];
    } else if (loanStatus.equals("SHOOTFIELD4")) {
      statusGLCode = lgcodesh4[curLonScmRowId];
      statusGLCodeInt = intcodesh4[curLonScmRowId];
      statusGLCodePen = pencodesh4[curLonScmRowId];
    }
    //System.out.println("s2p 01");

    if (fineAmo > 0) {
      String sql = "SELECT lfno, lfbal FROM tbl_LOANFineDet "
              + " WHERE (lacno = '" + lnAcNo + "') AND (lfbal > 0) ORDER BY lfno";
      double fineAmoBal = fineAmo;
      double finePaid = 0;
      String sqlGlTrans = "";
      try {
        ResultSet rs = dbRead.readData(new Envilop(sql));
        while (rs.next()) {
          int lfno = rs.getInt("lfno");
          double lfbal = rs.getDouble("lfbal");
          if (fineAmoBal >= lfbal) {
            if (lfbal > 0) {
              String sqlFine = "UPDATE tbl_LOANFineDet SET lfbal = 0 "
                      + " WHERE (lacno = '" + lnAcNo + "') AND (lfno = " + lfno + ")";
              String sqlInst = "INSERT INTO tbl_LOANSettlesub(lacno, lsno, lsdocno, lsamt, lgcode) "
                      + " VALUES('" + lnAcNo + "', " + settleNo + ", 'PEN-" + lfno + "', " + lfbal + ", '" + statusGLCodeInt + "')";
              dbWrite.updateData(new Envilop(sqlFine));
              dbWrite.updateData(new Envilop(sqlInst));
              fineAmoBal -= lfbal;
              finePaid += lfbal;
              totPay += lfbal;
            }
          } else {
            System.err.print("No part payment accepted for fine");
          }

        }
        if (finePaid > 0) {
          sqlGlTrans = "INSERT INTO tbl_GLTransaction(bankid, ttype, tno, tdate, lgcode, lgdes, tamt, crdr, cshtype, tremarks, ByUser, ByDate) "
                  + " VALUES (" + gcrentBankID + ", 'LSST', " + glTransNo + ", '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', '" + statusGLCodePen.substring(0, 6) + "', '" + statusGLCodePen.substring(7) + "', " + finePaid + ", 'CR', 'LKR', '" + lnAcNo + ":SI/" + SIType + " Trans From :" + savAcNo + "-PEN', '" + gcrntUserName + "', '" + sysDate + "')";
          System.out.println("fine fine " + sqlGlTrans);
          dbWrite.updateData(new Envilop(sqlGlTrans));
        }

      } catch (SQLException ex) {
        //System.out.println("20201005\n 105 " + sqlGlTrans);
        Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    if (oldIntAmo > 0) {
      String sqlOldInt = "SELECT lino, libal FROM tbl_LOANInt "
              + " WHERE (lacno = '" + lnAcNo + "') AND (libal > 0) AND (liedate BETWEEN '1990-01-01' AND '" + sysDate.substring(0, 10) + " 23:59:59') ORDER BY lino";
      double oldIntAmoBal = oldIntAmo;
      double totOldInt = 0;
      String sqlGlTrans = "";
      try {
        ResultSet rsOldInt = dbRead.readData(new Envilop(sqlOldInt));
        while (rsOldInt.next()) {
          int lino = rsOldInt.getInt("lino");
          double libal = rsOldInt.getDouble("libal");
          if (oldIntAmoBal >= libal) {
            if (libal > 0) {
              String sql = "UPDATE tbl_LOANInt SET libal = 0 "
                      + " WHERE (lacno = '" + lnAcNo + "') AND (lino = " + lino + ")";
              String sqlInst = "INSERT INTO tbl_LOANSettlesub(lacno, lsno, lsdocno, lsamt, lgcode) "
                      + " VALUES('" + lnAcNo + "', " + settleNo + ", 'INT-" + lino + "', " + libal + ", '" + statusGLCodeInt + "')";
              dbWrite.updateData(new Envilop(sql));
              dbWrite.updateData(new Envilop(sqlInst));
              oldIntAmoBal -= libal;
              totOldInt += libal;
              totPay += libal;
            }
          } else {
            System.err.print(lnAcNo + " Part settlement not accepted \n");
          }
        }
        if (totOldInt > 0) {
          sqlGlTrans = "INSERT INTO tbl_GLTransaction(bankid, ttype, tno, tdate, lgcode, lgdes, tamt, crdr, cshtype, tremarks, ByUser, ByDate) "
                  + " VALUES (" + gcrentBankID + ", 'LSST', " + glTransNo + ", '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', '" + statusGLCodeInt.substring(0, 6) + "', '" + statusGLCodeInt.substring(7) + "', " + totOldInt + ", 'CR', 'LKR', '" + lnAcNo + ":SI/" + SIType + " Trans From :" + savAcNo + "', '" + gcrntUserName + "', '" + sysDate + "')";
          dbWrite.updateData(new Envilop(sqlGlTrans));
          // System.out.println("20201005\n 106" + sqlGlTrans);
        }
      } catch (SQLException ex) {
        //System.out.println("20201005\n 106" + sqlGlTrans);
        Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    //System.out.println("s2p 02");
    if (dueAmo > 0) {
      String sqlLnAcUpdt = "";
      double totDueSettled = 0;
      double runningDueAmo = dueAmo;
      String sqlDue = "select ldno, dubal from tbl_LOANDues where lacno='" + lnAcNo + "' and dubal>0 order by ldno";
      try {
        ResultSet rsDue = dbRead.readData(new Envilop(sqlDue));
        while (runningDueAmo > 0) {
          if (rsDue.next()) {
            int ldno = rsDue.getInt("ldno");
            double duBal = rsDue.getDouble("dubal");
            double payAmo = duBal;
            if (duBal > runningDueAmo) {
              payAmo = runningDueAmo;
            }
            if (payAmo > 0) {
              String sqlUpdtDue = "UPDATE tbl_LOANDues SET dubal = dubal- " + payAmo + " "
                      + "WHERE (lacno = '" + lnAcNo + "') AND (ldno = " + ldno + ")";
              String sqlInst = "INSERT INTO tbl_LOANSettlesub(lacno, lsno, lsdocno, lsamt, lgcode) "
                      + " VALUES('" + lnAcNo + "', " + settleNo + ", 'DUE-" + ldno + "', " + payAmo + ", '" + statusGLCode + "')";
              String sqlGlTrans = "INSERT INTO tbl_GLTransaction(bankid, ttype, tno, tdate, lgcode, lgdes, tamt, crdr, cshtype, tremarks, ByUser, ByDate) "
                      + " VALUES (" + gcrentBankID + ", 'LSST', " + glTransNo + ", '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', '" + statusGLCode.substring(0, 6) + "', '" + statusGLCode.substring(7) + "', " + payAmo + ", 'CR', 'LKR', '" + lnAcNo + ":SI/" + SIType + " Trans From :" + savAcNo + "', '" + gcrntUserName + "', '" + sysDate + "')";
              totPay += payAmo;
              //System.out.println("ln due 01" + sqlInst);
              dbWrite.updateData(new Envilop(sqlInst));
              //System.out.println("ln due 02" + sqlUpdtDue);
              dbWrite.updateData(new Envilop(sqlUpdtDue));
              //System.out.println("ln due 03" + sqlGlTrans);
              dbWrite.updateData(new Envilop(sqlGlTrans));

              totDueSettled += payAmo;

              runningDueAmo -= payAmo;
            }
          } else {
            runningDueAmo = 0;
            //log file
          }
        }
        String closePart = "";
        if (lcbal <= totDueSettled) {
          closePart = ",Status = 'CLOSED'";
        }
        sqlLnAcUpdt = "UPDATE tbl_LOANAc SET llactdate = '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', lcbal = lcbal - " + totDueSettled + " " + closePart + " WHERE (loanacno = '" + lnAcNo + "')";

        dbWrite.updateData(new Envilop(sqlLnAcUpdt));
        //System.out.println("ln ac ================================" + sqlLnAcUpdt);
      } catch (SQLException ex) {
        //System.out.println("20201005\n 107" + sqlLnAcUpdt);
        Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    if (totPay > 0) {
      String sqlLoanSettle = "INSERT INTO tbl_LOANSettle(lsno, lacno, lsdate, lstype, lsamt, lsremarks, ByUser, ByDate, setlgcode, bid, status) "
              + " VALUES (" + settleNo + ", '" + lnAcNo + "', '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', 'SI/" + SIType + "', " + totPay + ", 'SI/" + SIType + " Trans From :" + savAcNo + "', '" + gcrntUserName + "', '" + sysDate + "', 'SI/" + SIType + "-Sav A/c No :" + savAcNo + "', " + gcrentBankID + ", '" + loanStatus + "')";

      try {
        dbWrite.updateData(new Envilop(sqlLoanSettle));

      } catch (SQLException ex) {
        //System.out.println("20201005\n 108" + sqlLoanSettle);
        Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    String updtClose = "UPDATE tbl_LOANAc SET Status = 'CLOSED' "
            + " WHERE   (loanacno = '" + lnAcNo + "') and (lcbal = 0) AND (Status NOT LIKE 'c%')";
    try {
      dbWrite.updateData(new Envilop(updtClose));
    } catch (SQLException ex) {
      //System.out.println("20201005\n 109" + updtClose);
      System.out.println("ex " + ex);
    }

    //System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    return totPay;
  }

  private static boolean updateSaving(String savAcNo, String lnAcNo, double amount, String sysDate, String gcrentBankID, String gcrntUserName, DB db) {
    boolean ans = true;
    double savingsBalance = 0;
    String sql = "SELECT top(1) slno, sacno, tdate, ttype, cusid, samt, curtype, sbal, ByUser, ByDate, crdr, tremarks, PBPrinted "
            + " FROM tbl_SavAcTrans WHERE (sacno = '" + savAcNo + "') ORDER BY slno DESC";
    int transNo = 0;
    String cusId = "";
    //DB db=new DB();
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      if (rs.next()) {
        transNo = rs.getInt("slno");
        cusId = rs.getString("cusid");
      }
    } catch (SQLException ex) {
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
      //System.out.println("20201005\n 110" + sql);
      ans = false;
    }
    if (cusId.trim().length() == 0) {
      cusId = "0";
    }
    transNo++;
    sql = "select scurbal from tbl_savac where sacno='" + savAcNo + "'";
    ResultSet rsCurBal;
    try {
      rsCurBal = db.readData(new Envilop(sql));
      if (rsCurBal.next()) {
        savingsBalance = rsCurBal.getDouble("scurbal");
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 111" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    String sqlInst = "INSERT INTO tbl_SavAcTrans(slno, sacno, tdate, ttype, cusid, samt, curtype, sbal, ByUser, ByDate, crdr, tremarks, PBPrinted) "
            + " VALUES (" + transNo + ", '" + savAcNo + "', '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', 'STROT', " + cusId + ", " + amount + ", 'LKR', " + (savingsBalance - amount) + ", '" + gcrntUserName + "', '" + sysDate + "', 'DR', 'SI" + SIType + " Trans to:" + lnAcNo + "', 0)";
    String sqlUpdt = "UPDATE tbl_SavAc SET slactdate = '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', scurbal = " + (savingsBalance - amount) + " WHERE (sacno = '" + savAcNo + "')";
    String glTransDr = "INSERT INTO tbl_GLTransaction(bankid, ttype, tno, tdate, lgcode, lgdes, tamt, crdr, cshtype, tremarks, ByUser, ByDate) "
            + " VALUES (" + gcrentBankID + ", 'STROT', " + transNo + ", '" + ctrlData.getDateString(today).substring(0, 10) + " 00:00:00', '" + savlgcode[curSavScmRowId].substring(0, 6) + "', '" + savlgcode[curSavScmRowId].substring(7) + "', " + amount + ", 'DR', 'LKR', '" + savAcNo + ":SI" + SIType + " Trans to:" + lnAcNo + "', '" + gcrntUserName + "', '" + sysDate + "')";

    //System.out.println("dr dr dr   " + glTransDr);
    //System.out.println("========================================  savAcNo === " + savAcNo);
    try {
      db.updateData(new Envilop(sqlInst));
      db.updateData(new Envilop(sqlUpdt));
      db.updateData(new Envilop(glTransDr));
    } catch (SQLException ex) {
      System.err.println("sql01 \n" + sqlInst);
      System.err.println("sql02 \n" + sqlUpdt);
      System.err.println("sql03 \n" + glTransDr);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    return ans;
  }

  private static double findExpences(String lnAcNo, DB db) {
    double ans = 0;
    //DB db=new DB();
    String sql = "select sum(lexbal) as totExpences from tbl_LOANExpenses where lacno='" + lnAcNo + "' and lexbal>0";
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      if (rs.next()) {
        ans = rs.getDouble("totExpences");
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 112" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    return ans;
  }

  private static double calInterest(String lnAcNo, DB db) {
    double ans = 0;
    String lnStatus = "";
    int lontype = 0;
    String sql = "SELECT Status,lcbal, intper, llactdate,DATEDIFF(dd,llactdate,'" + ctrlData.getDateString(today).substring(0, 10) + "') as duration ,lontype "
            + " FROM tbl_LOANAc WHERE (loanacno = '" + lnAcNo + "')";
    //DB db=new DB();
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      if (rs.next()) {
//        intPer=rs.getDouble("intper");
        lontype = rs.getInt("lontype");
        lcbal = rs.getDouble("lcbal");
//        lnStatus=rs.getString("Status");
//        lactDate=rs.getString("llactdate").substring(0,10);
        loanStatus = rs.getString("Status");
        //Date lactDate=ctrlData.getDate(rs.getString("llactdate"));
//        int duration=rs.getInt("duration");
//        double intAmt=lcbal*intPer*duration/36500;
//        ans=Math.round(intAmt);
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 113" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    if ((lcbal == 0) || (loanStatus.equalsIgnoreCase("CLOSED"))) {
      ans = -200;
    }
    if (lontype == 1) {
      ans = -300;
    }
    return ans;
  }

  private static void loadSavingsLedgerCode(String gcrentBankID, DB db) {
    savScmId = new int[N_SV_SCM];
    savlgcode = new String[N_SV_SCM];
    savdomlgcode = new String[N_SV_SCM];
    savdeadlgcode = new String[N_SV_SCM];
    String sql = "SELECT scid, sclgcode, scdomlgcode, scdeadlgcode "
            + " FROM tbl_SavScheme WHERE (bankid = " + gcrentBankID + ") order by scid";
    //DB db=new DB();
    //System.out.println("25 a " + sql);
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      int r = 0;
      while (rs.next()) {
        savScmId[r] = rs.getInt("scid");
        //System.out.println("25 l " + savScmId[r]);
        savlgcode[r] = rs.getString("sclgcode");
        savdomlgcode[r] = rs.getString("scdomlgcode");
        savdeadlgcode[r] = rs.getString("scdeadlgcode");
        r++;
      }
      nSvScm = r;
    } catch (SQLException ex) {
      //System.out.println("20201005\n 114" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static void loadLoanLedgerCodes(String gcrentBankID, DB db) {
    scmId = new int[N_LN_SCM];
    explgcode = new String[N_LN_SCM];
    lgcodeact = new String[N_LN_SCM];
    intcodeact = new String[N_LN_SCM];
    pencodeact = new String[N_LN_SCM];
    lgcodedor = new String[N_LN_SCM];
    intcodedor = new String[N_LN_SCM];
    pencodedor = new String[N_LN_SCM];
    lgcodepss = new String[N_LN_SCM];
    intcodepss = new String[N_LN_SCM];
    pencodepss = new String[N_LN_SCM];
    lgcodesh1 = new String[N_LN_SCM];
    intcodesh1 = new String[N_LN_SCM];
    pencodesh1 = new String[N_LN_SCM];
    lgcodesh2 = new String[N_LN_SCM];
    intcodesh2 = new String[N_LN_SCM];
    pencodesh2 = new String[N_LN_SCM];
    lgcodesh3 = new String[N_LN_SCM];
    intcodesh3 = new String[N_LN_SCM];
    pencodesh3 = new String[N_LN_SCM];
    lgcodesh4 = new String[N_LN_SCM];
    intcodesh4 = new String[N_LN_SCM];
    pencodesh4 = new String[N_LN_SCM];
    String sql = "SELECT scid, actlgcode, domlgcode, passduelgcode, sh1lgcode, sh2lgcode, sh3lgcode, sh4lgcode, actintcode, domintcode, passdueintcode, sh1intcode, sh2intcode,  "
            + " sh3intcode, sh4intcode, actpencode, dompencode, passduepencode, sh1pencode, sh2pencode, sh3pencode, sh4pencode, explgcode FROM tbl_LOANScheme  WHERE (bankid = " + gcrentBankID + ") order by scid";
    //DB db=new DB();
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      int r = 0;
      while (rs.next()) {
        scmId[r] = rs.getInt("scid");
        lgcodeact[r] = rs.getString("actlgcode");
        intcodeact[r] = rs.getString("actintcode");
        pencodeact[r] = rs.getString("actpencode");
        lgcodedor[r] = rs.getString("domlgcode");
        intcodedor[r] = rs.getString("domintcode");
        pencodedor[r] = rs.getString("dompencode");
        lgcodepss[r] = rs.getString("passduelgcode");
        intcodepss[r] = rs.getString("passdueintcode");
        pencodepss[r] = rs.getString("passduepencode");
        lgcodesh1[r] = rs.getString("sh1lgcode");
        intcodesh1[r] = rs.getString("sh1intcode");
        pencodesh1[r] = rs.getString("sh1pencode");
        lgcodesh2[r] = rs.getString("sh2lgcode");
        intcodesh2[r] = rs.getString("sh2intcode");
        pencodesh2[r] = rs.getString("sh2pencode");
        lgcodesh3[r] = rs.getString("sh3lgcode");
        intcodesh3[r] = rs.getString("sh3intcode");
        pencodesh3[r] = rs.getString("sh3pencode");
        lgcodesh4[r] = rs.getString("sh4lgcode");
        intcodesh4[r] = rs.getString("sh4intcode");
        pencodesh4[r] = rs.getString("sh4pencode");
        explgcode[r] = rs.getString("explgcode");
        r++;
      }
      nLnScm = r;
    } catch (SQLException ex) {
      //System.out.println("20201005\n 115" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static double findFine(String lnAc, DB db) {
    double ans = 0;
    //DB db=new DB();
    String sql = "select sum(lfbal) as totFine from tbl_loanfinedet where lacno='" + lnAc + "'";
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      if (rs.next()) {
        ans = rs.getDouble("totFine");
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 116" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    return ans;
  }

  private static double findOldInt(String lnAc, String crntDate, DB db) {
    double ans = 0;
    //DB db=new DB();
    String sql = "select sum(libal) as totOldInt from tbl_loanint where lacno='" + lnAc + "' and liedate between '1990-01-01' and '" + crntDate.substring(0, 10) + " 23:59:59'";
//  remove because of Metikumbura Due and SI table Date Prob
//    String sql="select libal as totOldInt from tbl_loanint where lacno='"+ lnAc +"' and liedate between '1990-01-01' and '"+ crntDate.substring(0,10) +" 23:59:59' order by lino";
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      if (rs.next()) {
        ans = rs.getDouble("totOldInt");
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 117" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    return ans;
  }

  private static double findAllDue(String lnAc, String crntDate, DB db) {
    double ans = 0;
    //DB db=new DB();
//    String sql="select dubal as totDue from tbl_loandues where lacno='"+ lnAc +"' and duedate between '1990-01-01' and '"+ crntDate.substring(0,10) +" 23:59:59' ORDER BY ldno";
    String sql = "select sum(dubal) as totDue from tbl_loandues where lacno='" + lnAc + "' and duedate between '1990-01-01' and '" + crntDate.substring(0, 10) + " 23:59:59'";
    try {
      ResultSet rs = db.readData(new Envilop(sql));
      if (rs.next()) {
        ans = rs.getDouble("totDue");
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 118" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    return ans;
  }

  private static double findSavingsBalance(String savAc, DB dbSav) {
    double ans = 0;
    String sql = "select scurbal from tbl_savac where sacno='" + savAc + "'";
    //DB dbSav=new DB();
    try {
      ResultSet rsSav = dbSav.readData(new Envilop(sql));
      if (rsSav.next()) {
        ans = rsSav.getDouble("scurbal");
      } else {
        ans = 0;
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 119" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    return ans;
  }

  private static double findSavingsWithdrawableBaleBalance(String savAc, DB dbSav) {
    double ans = 0;
    String sql = "select scurbal, (scurbal-sminbal) as withdrowable , scurbal-sfreezeamt as withdrowable2 from tbl_savac where sacno='" + savAc + "'";

    /*  
     //String sql = "select scurbal, (scurbal-sminbal) as withdrowable  from tbl_savac where sacno='" + savAc + "'";
     Sachith aiya request to avoid freez amount bucause he use free to something else;
    
     */
    //DB dbSav=new DB();
    try {
      ResultSet rsSav = dbSav.readData(new Envilop(sql));
      if (rsSav.next()) {
        double aa = rsSav.getDouble("withdrowable");
        double bb = rsSav.getDouble("withdrowable2");
        if (aa <= bb) {
          ans = aa;
        } else {
          ans = bb;
        }

      } else {
        ans = 0;
      }
    } catch (SQLException ex) {
      //System.out.println("20201005\n 120" + sql);
      Logger.getLogger(SISaving2PMTLoan.class.getName()).log(Level.SEVERE, null, ex);
    }
    return ans;
  }
}
