import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Numbers extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JTextArea text1 = new JTextArea();		//�������г�ʼ���ı����ı������
	private JButton exitButton=new JButton();
	private JScrollPane jsp=new JScrollPane(text1);
	private JLabel back=new JLabel();
	private String on;
	public Numbers(String on){
		this.setResizable(false); 		//�����޸Ĵ�С
		this.getContentPane().setLayout(null);
		this.setTitle("�����û� ");
		this.setBounds(100, 100, 400, 400);
		
		back=new JLabel();
		ImageIcon icon=new ImageIcon(this.getClass().getResource("info1.png"));
		back.setIcon(icon);
		back.setBounds(0, 0, 400, 400);
		
		on=on;	
		text1.setForeground(Color.black);
		//text1.setBounds(30,30, 300,300);
		text1.setFont(new Font("Serif",Font.PLAIN,12));
		text1.setLineWrap(true);
		text1.setOpaque(false);
		text1.setText(on);
		
		
		jsp.setBounds(30,30, 300,300);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setOpaque(false);
		jsp.getViewport().setOpaque(false);
		
		exitButton.setText("�˳�");
		exitButton.setFont(new Font("Dialog",0,12));
		exitButton.setBounds(350,320, 40,40);
		exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		exitButton.setBackground(getBackground());
		exitButton.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2); 
		exitButton.setBorder(b);
		exitButton.setVisible(true);
		
		exitButton.addActionListener(this);
		this.getContentPane().add(jsp);
		this.getContentPane().add(exitButton);
		this.getContentPane().add(back);

		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==exitButton)
		{
			this.dispose();//�˳�
		}
	}
	
}
