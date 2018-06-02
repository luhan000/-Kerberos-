import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

import DES.DES;
import RSA.RSA;

public class Vserverthread implements Runnable {
	private Socket socket;
	String mcpkey="01111";//�������Կ��
	String vregist="01000";//ע���
	String vconfirm="01001";//��������֤��
	String vchat="01010";//�����������
	String voffchat="10000";//������Ϣ�����
	String conline="10010";//�������������
	String vkey="helloser";//��Կ
	private Queue<ObjectOutputStream> cwrite;//�����û�Socket����
	private Queue<String> chatmess;//������Ϣ����
	JTextArea text1;
	public  Vserverthread(Socket socket,Queue<ObjectOutputStream> cwrite,Queue<String> chatmess,JTextArea text1)
	{
		this.socket=socket;
		this.cwrite=cwrite;
		this.chatmess=chatmess;
		this.text1=text1;
	}
    //ע�������AS
	//��֤�����Կͻ���
	//��������Կͻ���
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			final Lock lock = new ReentrantLock();//ͬһʱ��ֻ����һ���߳�ռ�����ݿ��޸�Ȩ��
			String adc=socket.getInetAddress().getHostAddress();//socket��Ӧ��IP��ַ
			int adclen=socket.getInetAddress().getHostAddress().length();//IP��ַ����
			System.out.println("IP���ȣ�"+adclen);
			ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
			String data=(String)read.readObject();//��ȡ����socket��Ϣ
			String clfi=data.substring(0, 5);//��ȡ���ݰ���ͷ
			System.out.println("���ݰ�ͷ"+clfi);
			String lengh=data.substring(5,13);//���ȣ����������������ݰ��ײ���ȷ��
			String info=data.substring(13);//��ȡ��Ч���ݰ�
			System.out.println("��Ч���ݰ�:"+info);
			if(clfi.equals(mcpkey))//����clfi��ֵ����仯 ���Կ��Բ��ö���IF�ṹ
			{
				//�������Կ��
				String sendback="";
				String MACid=info.substring(0,8);//MAC��ַ
				String pukey=info.substring(8);//��Կ
				ResultSet rs=null;
				Connection conn=null;  
				lock.lock();
				conn=Handledata.conmyVsql();//����V���������ݿ�
				String sql="select pkey  from mac_key where macid=?;";//��ѯ�ͻ����������Կ�Ƿ������ݿ�
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, MACid);//�������
			    rs=pStmt.executeQuery();//���ղ�ѯ���
			    if(!rs.next())//�������ź͹�Կ���������ݿ�
			    {
			    	sql="insert into mac_key values(?,?);";
			        pStmt=(PreparedStatement) conn.prepareStatement(sql);
			        pStmt.setString(1, MACid);//�������
			        pStmt.setString(2, pukey);
			        pStmt.executeUpdate();//�������ݿ�
			        sendback="1111100000000"+"already inserted into database!";
			        write.writeObject(sendback);//������Ϣ
			    }
			    else
			    {
			        sendback="1111100000000"+"have existed!";
			        write.writeObject(sendback);//������Ϣ
			    }
			    lock.unlock();
			}
            if(clfi.equals(vregist))
            {
            	//ע���
            	String idc=info.substring(0,3);//ע�����к�ID
            	String name=info.substring(3);//ע���ǳ�
                String date=Handledata.gettime();
                String regist=idc+" "+name+" ��"+date+"ע��";
                System.out.println("ע����Ϣ: "+regist);
                text1.setText(regist);//ʵʱ��ʾע����Ϣ
                lock.lock();//�����̰߳�ȫ����
				String sendback="";
				ResultSet rs=null;
				Connection conn=null;  
				conn=Handledata.conmyVsql();//����V���������ݿ�
				String sql="insert into new_table values(?,?,?,?);";//������Ϣ�����ݿ�
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, idc);//�������
			    pStmt.setString(2, name);
			    pStmt.setString(3, "");
			    pStmt.setInt(4, 0);
			    pStmt.executeUpdate();//ִ�����
			    lock.unlock();
			    sendback="1111100000000"+"regist succed!";//������Ϣ
			    write.writeObject(sendback);
            }
            if(clfi.equals(vconfirm))
            {
            	//��֤��
            	DES vdes=new DES(vkey);
            	String enticket=info.substring(0, Handledata.getlenght(lengh));//Ʊ���ֶ�
            	System.out.println("VƱ�ݣ�"+enticket);
            	String deticket=vdes.decryp(enticket);//����Ʊ�ݰ�
            	System.out.println("����VƱ�ݣ�"+deticket);
            	String idc=deticket.substring(8, 11);//�û�ID
            	System.out.println("�ͻ���ID��"+idc);
            	String cvkey=deticket.substring(0, 8);//�������ͻ�����ʱԿ��
            	System.out.println("�������ͻ�����ʱԿ��:"+cvkey);
            	String adct=deticket.substring(11,11+adclen);//Ʊ�ݿͻ���IP
            	System.out.println("v Ʊ�ֶε�IP��"+adct);
            	String enau=info.substring(Handledata.getlenght(lengh));//��֤�ֶ�
            	System.out.println("��֤�ֶΣ�"+enau);
            	DES audes=new DES(cvkey);
            	String deau=audes.decryp(enau);//������֤�ֶ�
            	System.out.println("���ܺ����֤�ֶΣ�"+deau);
            	String adcau=deau.substring(3,3+adclen);//��֤�ֶοͻ���IP
            	System.out.println("��֤�ֶε�IP��"+adct);
            	String ts5=deau.substring(3+adclen);//ͬ��ʱ���ֶ�
				ResultSet rs=null;
				Connection conn=null;
        		conn=Handledata.conmyVsql();//���ӷ��������ݿ�
        		String sql="select state from new_table where id=?;";
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, idc);
			    rs=pStmt.executeQuery();
			    rs.next();
			    int state=rs.getInt(1);
			    System.out.println(state);
			    if(state==1)
			    {
			    	String sendfinfo="10001"+"00000000"+"�ظ���¼!";//���������ݰ�
            		write.writeObject(sendfinfo);//������֤ʧ�ܰ�
			    }
			    else
			    {
            	if(adct.equals(adcau))//��֤�ɹ�
            	{
            		String back=ts5+"1";
            		System.out.println("���صļ���ǰ��ͬ���ֶ�"+back);
            		String enback=audes.encryp(back);//����
            		System.out.println("���ܵ�ͬ���ֶ�"+back);
            		String sendfinfo="01011"+"00000000"+enback;//���������ݰ�
            		write.writeObject(sendfinfo);
            		//�޸����ݿ���û�����״̬
            		//lock.lock();
    				/*ResultSet rs=null;
    				Connection conn=null;
            		conn=Handledata.conmyVsql();//���ӷ��������ݿ�*/
            		sql="update new_table set state=? where id=?;";//�޸ĸ�������״̬�����ݿ�
            		pStmt = (PreparedStatement) conn.prepareStatement(sql);
    			    pStmt.setInt(1, 1);
    			    pStmt.setString(2, idc);
    			    pStmt.executeUpdate();//ִ�����
    			    sql="select name from new_table where id=?;";
    			    pStmt=(PreparedStatement) conn.prepareStatement(sql);
    			    pStmt.setString(1, idc);
    			    rs=pStmt.executeQuery();
    			    rs.next();
    			    String name=rs.getString(1);
    			    //lock.unlock();
    			    //Ⱥ��ĳĳ��������
    			    ObjectOutputStream bwrite=null;
    			    String mess="";
    			    System.out.println("socket write���д�С:"+cwrite.size());
    			    if(cwrite.size()!=0)
    			    {
    			    int k=cwrite.size();
    			    System.out.println("����:"+Thread.currentThread().getId());
    			    lock.lock();
                    for(int i=0;i<k;i++)
                    {
                    	bwrite=cwrite.poll();
                    	mess=name+" ������������\n";
                    	System.out.println(mess);
                    	bwrite.writeObject(mess);
                    	cwrite.offer(bwrite);
                    }
                    lock.unlock();
    			    }
                    //csocket.offer(socket);//����½socket��������socket����
            	}
            	else//��֤ʧ��
            	{
            		String sendfinfo="10001"+"00000000"+"��������֤ʧ��";//���������ݰ�
            		write.writeObject(sendfinfo);//������֤ʧ�ܰ�
            	}
			    }
            }
            if(clfi.equals(vchat))
            {
            	//�����,����ǩ��ȷ�����,��������������Ϣ       Ⱥ����Ϣ,�����������ݰ�  �ر�socket��
            	cwrite.offer(write);
            	String macid=info.substring(0,8);//���ĸ� �������������
            	int len=Handledata.getlenght(lengh);//��һ��������������Ϣ�ĳ���
            	String idc=info.substring(8,11);//�û���Ӧ��ID
            	String dgsign=info.substring(11+len);//����ǩ��
            	System.out.println("����ǩ�� :"+dgsign);
        		lock.lock();
				ResultSet rs=null;
				Connection conn=null;
        		conn=Handledata.conmyVsql();//���ӷ��������ݿ���ҹ�Կ
        		String sql="select pkey from mac_key where macid=?;";//��ѯ���
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, macid);//�������
			    rs=pStmt.executeQuery();
			    rs.next();
			    String pkey=rs.getString(1);//��Ӧ�Ĺ�Կ
			    System.out.println("�ͻ��˹�Կ:"+pkey);
			    sql="select name from new_table where id=?;";
			    pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, idc);//�������
			    rs=pStmt.executeQuery();
			    rs.next();
			    String cname=rs.getString(1);
			    System.out.println("�ͻ��ǳƣ�"+cname);
			    lock.unlock();
			    String deidc=new String(RSA.decrypt(pkey, Base64.getDecoder().decode(dgsign.getBytes())));//���ܺ��ǩ��
			    System.out.println("���ܺ��ǩ��"+deidc);
			    if(deidc.equals(idc))//����ǩ����֤�ɹ�
			    {
			      /*if(mess.equals("offline"))
			      {
				        lock.lock();
				        sql="update new_table set state=? where name=?;";
				        pStmt = (PreparedStatement) conn.prepareStatement(sql);
				        pStmt.setInt(1, 0);//�������
				        pStmt.setString(2, cname);//�����û���
				        pStmt.executeUpdate();//ִ�����
				        lock.unlock();
				    	for(int i=0;i<csocket.size();i++)//�ڶ�����ɾ����socket
				    	{
				    		Socket comsocket=csocket.poll();
				    		if(!comsocket.equals(socket))
				    		{
				    			ObjectOutputStream bwrite = new ObjectOutputStream(comsocket.getOutputStream());
				    			bwrite.writeObject(cname+"quit the chatroom!");//����������û�����ĳĳ���ߵ���ʾ��Ϣ
				    			csocket.offer(comsocket);
				    		}
				    	}
			      }
			      else
			      {*/
			       //Ⱥ����һ����Ϣ
			     //System.out.println("�����û�����:"+csocket.size());
			     /* for(int i=0;i<count;i++)
			      {
			    		Socket comsocket=csocket.poll();
			    		System.out.println(comsocket.getInetAddress().getHostAddress());
			    		ObjectOutputStream bwrite;
			    		if(!comsocket.equals(socket))
			    		{
                           bwrite = new ObjectOutputStream(comsocket.getOutputStream());
			    		}
			    		else
			    		{
			    			System.out.println("bendi");
			    			bwrite=write;
			    		}
                        System.out.println("first!");
			    	    bwrite.writeObject(mess);//����������û�����ĳĳ���ߵ���ʾ��Ϣ
			    	    System.out.println("send!");
			    	    csocket.offer(comsocket);
			    	    System.out.println(i);
			      }*/
			      int count;
		    	  System.out.println("�����û�����:"+cwrite.size());
		    	  String chatme="";
			      while(true)//����������Ϣֱ�����յ�������Ϣ
			      {
			    	 chatme=(String)read.readObject();//��һ����֤�ɹ��󣬺��������İ���������Ϣ���ⶼ��ֻ��������Ϣ�ĸ�ʽ
			    	 System.out.println("���̺�:"+Thread.currentThread().getId());
				     count=cwrite.size();
			    	 if(chatme.equals("offline"))//�������͵�������Ϣ�ı�ʶ��
			    	 {
					        sql="update new_table set state=? where name=?;";
					        pStmt = (PreparedStatement) conn.prepareStatement(sql);
					        pStmt.setInt(1, 0);//�������
					        pStmt.setString(2, cname);//�����û���
					        pStmt.executeUpdate();//ִ�����
					        lock.lock();
					        count=cwrite.size();
					        ObjectOutputStream bwrite=null;
					    	for(int i=0;i<count;i++)//�ڶ�����ɾ����socket
					    	{
					    		  bwrite=cwrite.poll();
					    		  if(!bwrite.equals(write))
					    		  {
					    			  bwrite.writeObject(cname+" �뿪��������!\n");
					    			  cwrite.offer(bwrite);
					    		  }
					    		  else
					    		  {
                                  bwrite.writeObject("0");//����������Ϣ
					    		  }
					    	}
					    	lock.unlock();
					    	break;
			    	 }
			    	 else
			    	 {
				    	 System.out.println("���յ���������Ϣ:"+chatmess);
			    		  chatmess.offer(chatme);//����������Ϣ����
			    		  System.out.println("Ⱥ����Ϣ");
			    		  lock.lock();
			    		  count=cwrite.size();
			    		  System.out.println("��������"+count);
					      ObjectOutputStream bwrite=null;
					      for(int i=0;i<count;i++)
					      {
					    	    bwrite=cwrite.poll();
					    	    bwrite.writeObject(chatme);//����������û�����������Ϣ
					    	    cwrite.offer(bwrite);
					    	    System.out.println("fasong"+i);
					      }
					      lock.unlock();
			    	 }
			      }
			      }
			    else
			    {
			    	
			    }
            }
            //}
            if(clfi.equals(voffchat))
            {
            	//������Ϣ�����
            	String off="";
            	for(String x: chatmess)//����������Ϣ���
            	{
            		off=off+x;
            	}
            	System.out.println(off);
            	write.writeObject(off);
            }
            if(clfi.equals(conline))
            {
            	//�������������
            	System.out.println("������������!");
            	String online="";
				ResultSet rs=null;
				Connection conn=null;  
				lock.lock();
				conn=Handledata.conmyVsql();//����V���������ݿ�
				String sql="select name  from new_table where state=?;";//��ѯ�ͻ����������Կ�Ƿ������ݿ�
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setInt(1, 1);
			    rs=pStmt.executeQuery();
			    while(rs.next())
			    {
			      System.out.println(rs.getString(1));
			      online=online+rs.getString(1)+"\n";//˳���������
			    }
			    write.writeObject(online);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
		        if (socket!=null) {
		        	//Thread.sleep(10000);
		            socket.close();//�ر�Socket
		        }
		    } catch (Exception e2) {
		        e2.printStackTrace();
		    }
		}
	}
}
