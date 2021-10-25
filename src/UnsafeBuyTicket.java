public class UnsafeBuyTicket {
    public static void main(String[] args) {
        BuyTicket station = new BuyTicket();

        new Thread(station,"Tom").start();
        new Thread(station,"Jack").start();
        new Thread(station,"Mary").start();
    }
}

class BuyTicket implements Runnable {
    private int ticketNums = 10;
    boolean flag = true;
    @Override
    public void run() {
        while(flag){
            try {
                buy();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private synchronized void buy() throws InterruptedException {
        if(ticketNums<=0) {
            flag = false;
            return;
        }
        System.out.println(Thread.currentThread().getName()+" get "+ticketNums--);
    }
}