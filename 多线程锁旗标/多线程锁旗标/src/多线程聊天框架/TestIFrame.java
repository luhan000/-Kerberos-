package 多线程聊天框架;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

public class TestIFrame extends JFrame{
    private JTextArea time=new JTextArea();
    private JScrollPane jsp=new JScrollPane(time);
    SimpleDateFormat myfmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Random8 rs=new Random8();

    public TestIFrame() throws InterruptedException{
        super();
        setBounds(100, 100, 200, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
		Timer t ;
		jsp.setBounds(30,30,410,200);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(jsp);
	    ActionListener timeActionListener = new ActionListener(){  
	        public void actionPerformed(ActionEvent e) {  
	            // TODO Auto-generated method stub  
	        //time.setText(myfmt.format(new java.util.Date()).toString());
	        	time.append("\n"+myfmt.format(new java.util.Date()).toString());
	        }     
	    };  
        t = new Timer(1000,timeActionListener);  
	    t.start();
        setVisible(true);
    }
    public static void main(String[] args) throws InterruptedException{
        TestIFrame rsa=new TestIFrame();
        Thread.sleep(1000);
        rsa.rs.info="31";
        Thread.sleep(1000);
        rsa.rs.info="14";
    }

}