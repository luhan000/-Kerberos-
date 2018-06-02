

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import DES.DES;

public class Asthread implements Runnable{
	String regist="00000";//ע����ײ�
	String login="00001";//��¼��֤���ײ�
	String ctwo="00";
	String cone="0";
	String tgs_key="hellotgs";//AS��TGS֮��ĶԳ���Կ
	String tgsid="1";
	String vip="172.20.10.11";
	int vport=4444;
	private Socket socket;
	public  Asthread(Socket socket)
	{
		this.socket=socket;
	}
	@Override
	public void run() {
		final Lock lock = new ReentrantLock();//ͬһʱ��ֻ����һ���߳�ռ�����ݿ��޸�Ȩ��
		try {
			ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
			String data=(String)read.readObject();//AS�ӿͻ��˽��յ������ݰ�
		    String clfi=data.substring(0, 5);//��ȡ���ݰ��ײ�
		    String lenght=data.substring(5, 13);//��ȡ��Ч���ݰ������ַ���
		    int len=Handledata.getlenght(lenght);
			ResultSet rs=null;
			Connection conn=null;
		    String info=data.substring(13);//��ȡ��Ч���ݶ�
		    if(clfi.equals(regist))//ASע�ᴦ��
		    {
		      String name=info.substring(0,info.length()-8);//�õ�ע���ǳ�
		      String key=info.substring(name.length());//�õ��ԳƼ���Կ��
		      lock.lock();
		      conn=Handledata.conmyASsql();
		      String sql="select count(name)  from c_key;";//�û���ID�Ǹ���ע��˳������ĵ�һ��ע���λ001
		      PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
		      rs=pStmt.executeQuery();
		      rs.next();
		      int id=Integer.parseInt(rs.getString(1))+1;//�������ע���û������ݿ��ID
		      String idc=String.valueOf(id);//ת��ΪString
		      if(idc.length()==1)
		      {
		    	  idc=ctwo+idc;
		      }
		      else if (idc.length()==2)
		      {
		    	  idc=cone+idc;
		      }
		      sql="insert into c_key values(?,?,?);";
		      pStmt = (PreparedStatement) conn.prepareStatement(sql);
		      pStmt.setString(1, idc);
		      pStmt.setString(2, name);
		      pStmt.setString(3, key);
		      pStmt.executeUpdate();//�����û���Ϣ��ע��ɹ�
		      lock.unlock();//���ݿ��������ͷ�Ȩ��
		      String sendtov="0100000000001"+idc+name;//���͸�V��������ע����Ϣ
	          Socket vsocket=new Socket(vip,vport);
	          ObjectOutputStream vwrite = new ObjectOutputStream(vsocket.getOutputStream());
	          ObjectInputStream vread = new ObjectInputStream(vsocket.getInputStream());
	          vwrite.writeObject(sendtov);//����ע����Ϣ
	          System.out.println(((String)vread.readObject()).substring(13));//����̨���ע�ᷴ����Ϣ
	          vsocket.close();//������ɹر�socket
		      String sucback="0001000000001"+idc;
		      write.writeObject(sucback);//ע��ɹ��������к�
		      socket.close();//������ɹر�socket
		    } 
		    //���ش���TGSƱ�ݵİ���76λ   ���з���TGS��Ʊ���õ���AS��TGS֮��ĶԳ�Կ���ܵģ����ڱ���ϵͳֻ������һ��TGS������ȡ
		    if(clfi.equals(login))//AS��¼����
		    {
		       int lengh=0;
		       String name=info.substring(0,len-20);//��¼ID
		       System.out.println(name);
		       lock.lock();
		       conn=Handledata.conmyASsql();//�������ݿ�
		       String sql="select ckey from c_key where name=?;";//�û���ID�Ǹ���ע��˳������ĵ�һ��ע���λ001
			   PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			   pStmt.setString(1, name);
			   rs=pStmt.executeQuery();
			   lock.unlock();
			   if(!rs.next())
			   {
				   //�����û���������ʧ�ܰ�
				   String fail="00100"+"00000001"+"0";//�û����������ݰ�
				   write.writeObject(fail);//����ʧ����
			   }
			   else
			   {
			   String ckey=rs.getString(1);//��ȡ��Ӧ�ĶԳ���Կ
			   sql="select id from c_key where name=?;";
			   pStmt = (PreparedStatement) conn.prepareStatement(sql);
			   pStmt.setString(1, name);
			   lock.lock();
			   rs=pStmt.executeQuery();
			   rs.next();
			   String idc=rs.getString(1);
			   System.out.println("idc��"+idc);
			   String ctgskey=Handledata.random8();//������ɰ�λ��ʱtgs��C֮��ĶԳ���Կ
			   System.out.println("C,TGS֮����ʱ��Կ:"+ctgskey);
			   String curtime=Handledata.gettime();//��ǰʱ��
			   String lifetime="600";
			   String ADC=socket.getInetAddress().getHostAddress();//�ͻ���IP  �޸�
			   System.out.println(ADC);
			   System.out.println("�ͻ���IP"+ADC);
			   String bftgs=ctgskey+idc+ADC+tgsid+curtime+lifetime;
			   System.out.println("TGSƱ�����ģ�"+bftgs);
			   lengh=bftgs.length();
			   DES destgs=new DES(tgs_key);
			   String tgsticket=destgs.encryp(bftgs);//TGSƱ��
			   System.out.println("TGSƱ�ݣ�"+tgsticket);
			   String mess=ctgskey+idc+tgsid+curtime+lifetime;//������Ϣ�ж��һ���û��ǳƶ�Ӧ��ID
			   lengh=tgsticket.length();
			   DES desc=new DES(ckey);
			   System.out.println("AS���͸�C������:"+mess+tgsticket);
			   String message=desc.encryp(mess+tgsticket);//���ܺ�Ҫ���͵���Ч��Ϣ
			   System.out.println("AS���͸�C�ļ�������:"+message);
			   String astoc="00011"+Handledata.getbina(lengh)+message;
			   write.writeObject(astoc);//��ͻ��˷���TGSƱ�ݰ�
			   socket.close();
			   }
		    }
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
