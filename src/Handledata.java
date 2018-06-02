import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Handledata {
	public static int getlenght(String lenght)//将有效数据包长度从二进制字符转化为整形
	{
	   int len=0;
	   String str=lenght;
	   int temp;
	   for(int i=0;i<8;i++)
	   {
		   temp=str.charAt(7-i)-48;
		   len=(int) (len+temp*Math.pow(2,i));
	   }
	   return len;
	}
	public static String getbina(int lenght)//将十进制转化为八位二进制
	{
		int temp=lenght;
		String com="0";
		String fina="";
		String trans=java.lang.Integer.toBinaryString(temp); 
		for(int i=0;i<8-trans.length();i++)
		{
			fina+=com;
		}
		fina+=trans;
		return fina;
	}
	public  static String random8()//随机生成八位临时秘钥
	{
		String str = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		return str;
	}
	public static String gettime()
	{
	    Date day=new Date();    
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        String data=df.format(day);   
		return data;
	}
	public static Connection conmyASsql()//连接AS数据库
	{
	    String driver = "com.mysql.jdbc.Driver";//驱动程序名  
	    String url = "jdbc:mysql://127.0.0.1:3306/as_key";    // URL指向要访问的数据库名Test  
	    String user = "root";// MySQL配置时的用户名  
	    String password = "wyy19970410";// Java连接MySQL配置时的密码  
		Connection conn=null;  
	    try {         // 加载驱动程序  
	    Class.forName(driver);  
	    conn = DriverManager.getConnection(url, user, password);//连接数据库
	    } 
	    catch(Exception e){  
	        System.out.println("Succeeded connecting Not to the Database!");  
	        e.printStackTrace();
	    } 
	    return conn;
	}
	public static Connection conmyVsql()//连接服务器数据库
	{
	    String driver = "com.mysql.jdbc.Driver";//驱动程序名  
	    String url = "jdbc:mysql://127.0.0.1:3306/v_database";    // URL指向要访问的数据库名Test  
	    String user = "root";// MySQL配置时的用户名  
	    String password = "wyy19970410";// Java连接MySQL配置时的密码  
		Connection conn=null;  
	    try {         // 加载驱动程序  
	    Class.forName(driver);  
	    conn = DriverManager.getConnection(url, user, password);//连接数据库
	    } 
	    catch(Exception e){  
	        System.out.println("Succeeded connecting Not to the Database!");  
	        e.printStackTrace();
	    } 
	    return conn;
	}
	public static void main(String []arges)
	{
		String str="10000011";
		int test=7;
		System.out.println(getlenght(str));
		System.out.println(getbina(test));
	}

}
