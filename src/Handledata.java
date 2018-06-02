import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Handledata {
	public static int getlenght(String lenght)//����Ч���ݰ����ȴӶ������ַ�ת��Ϊ����
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
	public static String getbina(int lenght)//��ʮ����ת��Ϊ��λ������
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
	public  static String random8()//������ɰ�λ��ʱ��Կ
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
	public static Connection conmyASsql()//����AS���ݿ�
	{
	    String driver = "com.mysql.jdbc.Driver";//����������  
	    String url = "jdbc:mysql://127.0.0.1:3306/as_key";    // URLָ��Ҫ���ʵ����ݿ���Test  
	    String user = "root";// MySQL����ʱ���û���  
	    String password = "wyy19970410";// Java����MySQL����ʱ������  
		Connection conn=null;  
	    try {         // ������������  
	    Class.forName(driver);  
	    conn = DriverManager.getConnection(url, user, password);//�������ݿ�
	    } 
	    catch(Exception e){  
	        System.out.println("Succeeded connecting Not to the Database!");  
	        e.printStackTrace();
	    } 
	    return conn;
	}
	public static Connection conmyVsql()//���ӷ��������ݿ�
	{
	    String driver = "com.mysql.jdbc.Driver";//����������  
	    String url = "jdbc:mysql://127.0.0.1:3306/v_database";    // URLָ��Ҫ���ʵ����ݿ���Test  
	    String user = "root";// MySQL����ʱ���û���  
	    String password = "wyy19970410";// Java����MySQL����ʱ������  
		Connection conn=null;  
	    try {         // ������������  
	    Class.forName(driver);  
	    conn = DriverManager.getConnection(url, user, password);//�������ݿ�
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
