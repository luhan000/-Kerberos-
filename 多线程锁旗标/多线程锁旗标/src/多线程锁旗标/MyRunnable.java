package ���߳������;

public class MyRunnable implements Runnable{

    private int num=5;    //�ܹ�Ʊ���趨Ϊ5
    @Override
    public void run() {
        for(int i=0; i<10; i++){
            if(this.num>0){   //��ӡ��Ʊ��Ϣ
            	if(this.num==2)
            	{
            	   break;
            	}
                System.out.println(Thread.currentThread().getName() + "@��Ʊ: " + this.num--);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        MyRunnable myRunnable = new MyRunnable();

        Thread th1 = new Thread(myRunnable,"��Ʊ��һ");    //�߳�һ
        Thread th2 = new Thread(myRunnable,"��Ʊ�ڶ�");    //�̶߳�             

        th1.start();
        th2.start();
        Thread.sleep(4000);
        if(!th1.isAlive())
        {
        	System.out.println(1);
        }

    }
}