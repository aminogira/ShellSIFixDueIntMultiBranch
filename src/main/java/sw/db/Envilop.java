package sw.db;

//
//import sw.um.User;

public class Envilop {

	String sql;
	
//	User usr;
	String des;
	
	//TODO Write code for this
	String pc;
	
	public Envilop(String sql){
		this.sql=sql;
		des= System.getProperty("user.name") + " @ " + System.getProperty("os.name");		
	}
//	public Envilop( String sql,User user,String pc){
//		this(sql);
//		this.usr=user;
//		this.pc=pc;
//	}
	public String getSQL(){
		return sql;
	}
//	public User getUser(){
//		return usr;
//	}
	public String getDescription(){
		return des;
	}
	public String getPc(){
		return pc;
	}
}
