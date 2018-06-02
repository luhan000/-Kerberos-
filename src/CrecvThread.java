import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

public class CrecvThread implements Runnable{

	Socket socket=null;
	final Lock lock = new ReentrantLock();
	JTextArea text1;
	ObjectInputStream read ;
	public CrecvThread(	ObjectInputStream read ,JTextArea text1) throws IOException
	{
        this.text1=text1;
	    this.read=read;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
	   try {
		System.out.println("running!");
		String info;
		while(true)
		{
        info=(String)read.readObject();
        System.out.println("Ïß³Ì"+Thread.currentThread().getId());
        System.out.println(info);
        if(info.equals("0"))
        {
        	break;
        }
        else
        {
        text1.append(info);
        text1.setSelectionStart(text1.getText().length());
        }
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
