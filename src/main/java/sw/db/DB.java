package sw.db;
//
//import amino.ui.util.FoundItem;
import crb.TBSConst;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Date;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import sw.RegQuery;
import sw.proConst;
//import sw.ui.optProgramSetting;
//import ui.old.ProgramSetting;
//import ui.proConst;
//import amino.ui.util.FoundItem;
//import db.Envilop;
//import tbs.TBSConst;

public class DB {

	Connection con;
static int lineNumber=0;
static int lineNumberRead=0;
static int lineNumberWrite=0;


	public DB(){
        lineNumber=0;
        lineNumberRead=0;
        lineNumberWrite=0;

//    try {
      con = getConn();
//    } catch (Exception ex) {
//      Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
//      System.out.println("db con error 01");
//      try {
//        con.commit();
//        con.close();
//        try {
//          con = getConn();
//        } catch (Exception ex1) {
//          Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex1);
//          System.out.println("db con error 02");
//        }
//      } catch (SQLException ex1) {
//        Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex1);
//        System.out.println("db con error 03");
//      }
//    }

	}

  public boolean isTableAvailabal(String string) {
    return true;
  }



  public ResultSet readData(Envilop env)throws SQLException{
		String sql=env.getSQL();
       // System.out.println("DB " + lineNumber++ + " :: " + lineNumberRead++ + " >> "  +sql);
//		try{
			Statement stm=con.createStatement();
			ResultSet rs=stm.executeQuery(sql);
			return rs;
//		}catch(SQLException e){
//			e.printStackTrace();
//		}
	//	return null;
	}

 	private static String  setToUpdateTable(String sql){
		String modified="";
		for(int x=0;x<sql.length();x++){
			String oneChar=sql.substring(x,x+1);
			modified+=oneChar;
			if(oneChar.equals("'")) modified+="'";
		}
		return modified;
	}
 
	public void updateData(Envilop env)throws SQLException {
		String sql=env.getSQL();

       // System.out.println("DB " + lineNumber++ + " :: " + lineNumberWrite++  + " >> "  +sql);


//		System.out.println("updateData@DB \n" + sql);
		Statement stm=con.createStatement();
		stm.execute(sql);
//		String bkup="INSERT INTO tbl_Bkup (bid, udate, qry, ByUser, ByDate) " +
//		"VALUES ("+ TBSConst.BankID +", '" + ctrlData.getDateString(new Date()) + "', '"+ setToUpdateTable(sql) +"', 'not updated', '" + ctrlData.getDateString(new Date()) + "')";
//		stm.execute(bkup);
		
		//System.out.println("Data Updated");
	}
	
	public void updateTmpRptData(Envilop env)throws SQLException {
		String sql=env.getSQL();
                //        lineNumber=0;
//        lineNumberRead=0;
//        lineNumberWrite=0;
    //    System.out.println("DB " + lineNumber++ + " :: " + lineNumberWrite++ + " >> "  +sql);

		Statement stm=con.createStatement();
		stm.execute(sql);
	}
	
	public void updateDataOld(Envilop env)throws SQLException {
		String sql=env.getSQL();
//		System.out.println("updateData@DB \n" + sql);
                //        lineNumber=0;
//        lineNumberRead=0;
//        lineNumberWrite=0;
   //     System.out.println("DB " + lineNumber++ + " :: " + lineNumberWrite++ + " >> "  +sql);

		Statement stm=con.createStatement();
		stm.execute(sql);
		//System.out.println("Data Updated");
	}
	
	public static Connection getConn() {
    //try{
            Connection con=null;
    try {
      try {
        Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
      } catch (InstantiationException ex) {
        Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        System.out.println("DB Lib Error 01");
      } catch (IllegalAccessException ex) {
        Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        System.out.println("DB Lib Error 01");
      }
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
      System.out.println("DB Lib Error 01");
    }
            //System.out.println("jdbc:jtds:sqlserver:/" + proConst.serverIp.trim() + ":" + proConst.SERVER_PORT.trim() + "/" + proConst.dbName.trim());
    try {
      // con=DriverManager.getConnection("jdbc:jtds:sqlserver:/localhost:2166/master", "test", "123");
      con = DriverManager.getConnection("jdbc:jtds:sqlserver:/" + proConst.serverIp.trim() + ":" + proConst.SERVER_PORT.trim() + "/" + proConst.dbName.trim(), proConst.user.trim(), proConst.passwd.trim());
      
    } catch (SQLException ex) {
      Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);

    }
    //System.out.println("connected");
    //            con.close();
      //con.setAutoCommit(false);
      return con;
//    }catch (Exception e){
//        System.out.println("DB Connection ERROR");
//       // new ProgramSetting();
//        e.printStackTrace();
//    }
//    return null;
    }
	
	
	public void commit(){
//		try{
//			con.commit();
//		}catch(SQLException e){
//			e.printStackTrace();
//		}
	}
	public void rollback(){
//		try {
//			con.rollback();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	public void rollbackclose(){
//		try {
//			con.rollback();
//			con.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	public void commitclose(){
//		try{
//			if (!(con.isClosed())){
//				con.commit();
//				con.close();
//			}
//		}catch(SQLException e){
//			e.printStackTrace();
//		}
	}

	public void commitclose2(){
		try{
			if (!(con.isClosed())){
//				con.commit();
				con.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}


	public void close(){
		try{
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}



	public int getNewNo(String tblName,String colName){
		String sql="select max(" + colName + ")+1 as NextID from " + tblName;
		//System.out.println(sql);
		ResultSet rs=null;
//		try{
//			Statement stm=con.createStatement();
//			rs=stm.executeQuery(sql);
//		}catch (SQLException e){
//			e.printStackTrace();
//		}
		try {
			rs=readData(new Envilop(sql));
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		int ans=1;
		try{
			rs.next();
			ans=rs.getInt("NextID");
		}catch(SQLException e){
			ans=1;
		}
		if (ans<=0) ans=1;
		//commit();
		return ans;
	}

	public int getId(String tblName, String name){
		int ans=-2;
		ResultSet rs=null;
		String sql="select id from " + tblName + " where name = '" + name + "'";
		try {
			rs=readData(new Envilop(sql));
			rs.first();
			ans=rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ans;
	}
	public String getName(String tblName, int id){
		String ans="";
		ResultSet rs=null;
		String sql="select name from " + tblName + " where id = " + id;

		try {
			rs=readData(new Envilop(sql));
			ans=rs.getString("name");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ans;
	}

	public int getId(String tblName,String colName, String value){
		int ans=-5;
		ResultSet rs=null;
		String sql="select id from " + tblName + " where "+ colName +" = '" + value + "'";
		try {
			rs=readData(new Envilop(sql));
			rs.first();
			ans=rs.getInt("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ans;
	}

	//TODO check this
	public ResultSet findData(String tblName,String colName,String value){
		String sql="SELECT * FROM " + tblName + " where " + colName + "='" + value + "'";
//		System.out.println(sql);
		return findRecord(sql);
	}

	public ResultSet findData(String tblName,String colName,int datum){
		String sql="SELECT * FROM " + tblName + " where " + colName + "=" + datum;
		return findRecord(sql);
	}

	public Object findDatum(String tblName,String findColName,int findValue,String datumColumn) throws SQLException{
		Object datum;
		ResultSet rs=findData(tblName,findColName,findValue);
		rs.next();
		datum=rs.getObject(datumColumn);
		return datum;
	}
	public Object findDatum(String tblName,String findColName,String findValue,String datumColumn) throws SQLException{
		Object datum;
		ResultSet rs=findData(tblName,findColName,findValue);
		rs.next();
		datum=rs.getObject(datumColumn);
		return datum;
	}
//	public Object findDatum(String tblName,String findColName,String findValue,int datumColumn) throws SQLException{
//		Object datum;
//		ResultSet rs=findData(tblName,findColName,findValue);
//		datum=rs.getObject(datumColumn);
//		return datum;
//	}


	public ResultSet findRecord(String sql){

		try {
			return readData(new Envilop(sql));
		} catch (SQLException e) {
			e.printStackTrace();
		}

//		try{
//			Statement stm=con.createStatement();
//			return stm.executeQuery(sql);
//		}catch (SQLException e){
//			e.printStackTrace();
//		}
//		commitclose();
		return null;
	}
	//TODO check this its not working correctly
//	public boolean isNotFoundDatum(String tblName,String colName,String datum){
//
//		boolean ans=true;
//		ResultSet rs=findData(tblName,colName,datum);
//		try {
//			rs.next();
//			ans=false;
//		} catch (SQLException e) {
//			ans=true;
//		}
//		return ans;
//	}

	public boolean isNotFoundDatum(String tblName,String colName,int datum){
		boolean ans=true;
		ResultSet rs=findData(tblName,colName,datum);
		try {
			rs.next();
			ans=false;
		} catch (SQLException e) {
			ans=true;
		}
		return ans;
	}

	public boolean isDataFound(String tblName,String colName,int datum){
		boolean ans = false;
		String sql="Select * from "+ tblName +" where "+ colName +" = " + datum;
		ResultSet rs=findRecord(sql);
		try{
			if (rs.next()) {
				ans=true;
			}else{
				ans=false;
			}
		}catch(Exception e){
			ans=false;
		}
		return ans;
	}
	
	public boolean isDataFound(String tblName,String colName,String datum){
		boolean ans=false;
		String sql="Select * from "+ tblName +" where "+ colName +" = '" + datum + "'";
		ResultSet rs=findRecord(sql);
		try{
			if (rs.next()) {
				ans=true;
			}else{
				ans=false;
			}
		}catch(Exception e){
			ans=false;
		}
		return ans;
	}
	

//	public FoundItem[] getFoundItem(ResultSet rs,String showingData){
//		FoundItem fi[] = null;
//		Stack<FoundItem> stk=new Stack<FoundItem>();
//		//Queue<FoundItem> q=new Queue<FoundItem>();
//		int count=0;
//		try {
//			while(rs.next()){
//				//System.out.println(rs.getInt(1));
//				//System.out.println(rs.getInt(1) + " : (" + rs.getString("code") + ") " + rs.getString("name") );
//				FoundItem tmp=new FoundItem(rs.getInt(1),rs.getString("code"),rs.getString("name"),showingData);
//				count++;
//				stk.push(tmp);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		fi=new FoundItem[count];
//		for (int n=0;n<count;n++){
//			fi[n]=stk.pop();
//			//fi[n]=q.remove();
//		}
//		return fi;
//	}
//
//	public FoundItem[] getFoundItem(String tblName,String showingData){
////		FoundItem fi[] = null;
//		String sql="";
//		try{
//			if (tblName.substring(0, 6).trim().compareToIgnoreCase("SELECT")==0)
//				sql=tblName;
//			//######################################################################################
//			//EXAMPLE
//			//"SELECT transId,socityId,CONCAT(transId,':',name,' ', socityId) as name,code FROM tLoanAggrement,mSocity where socityId=socId order by transId"
//			//THE FIRST COLUMN MUST BE THE ID
//			//THERE MUST BE COLUMNs AS name AND code
//			else
//				sql="SELECT * FROM " + tblName + " group by " + showingData;
//		}catch(StringIndexOutOfBoundsException e){
//			sql="SELECT * FROM " + tblName + " group by " + showingData;
//		}
//		//System.out.println(sql);
//		ResultSet rs=findRecord(sql);
//		return getFoundItem(rs,showingData);
//
////		Stack<FoundItem> stk=new Stack<FoundItem>();
////		//Queue<FoundItem> q=new Queue<FoundItem>();
////		int count=0;
////		try {
////			while(rs.next()){
////				//System.out.println(rs.getInt(1));
////				//System.out.println(rs.getInt(1) + " : (" + rs.getString("code") + ") " + rs.getString("name") );
////				FoundItem tmp=new FoundItem(rs.getInt(1),rs.getString("code"),rs.getString("name"),showingData);
////				count++;
////				stk.push(tmp);
////			}
////		} catch (SQLException e) {
////			e.printStackTrace();
////		}
////		fi=new FoundItem[count];
////		for (int n=0;n<count;n++){
////			fi[n]=stk.pop();
////			//fi[n]=q.remove();
////		}
////		return fi;
//	}
	public void updateTransDetail(int transNo,String refNo,Date date){
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//        String sql="Insert into tmpPrintTrans(trnsNo,refNo,trnsDate) " +
//                    "values(" + transNo + ",'" + refNo + "','" + sdf.format(date) + "')";
//		Envilop env=new Envilop(sql);
//		try {
//			updateData(new Envilop("delete from tmpPrintTrans"));
//            updateData(env);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
  public Date getCalDate(String bankid){
    Date ans=new Date();
    ans=ctrlData.getDate("2000-01-01");
    String sql="select left(CDate,10) as today from tbl_MCalendar where today=1 and bankid='" + bankid + "'";
   // System.out.println("sql sql " + sql);
		Statement stm;
    try {
      stm = con.createStatement();
      //System.out.println("sql sql " + sql);
      ResultSet rs=stm.executeQuery(sql);
      if(rs.next()){
        ans=ctrlData.getDate(rs.getString("today"));
      }
    } catch (SQLException ex) {
      Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
    }
    return ans;
  }

	//public void showDataRow(ResultSet rs){
	//}
}
