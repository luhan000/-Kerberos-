package ���߳�������;

import java.util.Queue; 
import java.util.concurrent.LinkedBlockingQueue; 

/** 
* ���еı��� 
* 
* @author leizhimin 2009-7-22 15:05:14 
*/ 
public class TestQueue { 
        public static void main(String[] args) { 
                Queue<Integer> q = new LinkedBlockingQueue<Integer>(); 
                String j="";
                //��ʼ������ 
                for (int i = 0; i < 5; i++) { 
                        q.offer(i); 
                } 
                System.out.println("-------1-----" ); 
                //���Ϸ�ʽ������Ԫ�ز��ᱻ�Ƴ� 
                for (Integer x : q) { 
                	j=j+"\n"+x.toString();//����
                    System.out.println(x); 
                } 
                System.out.println("j=:"+j); 
                System.out.println("-------2-----" ); 
                //���з�ʽ������Ԫ��������Ƴ� 
                while (q.peek() != null ) { 
                        System.out.println(q.poll()); 
                } 
        } 
}