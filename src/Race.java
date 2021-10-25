public class Race implements Runnable {
    private static String winner;
    @Override
    public void run() {
        for (int i = 1; i < 101; i++) {
            boolean flag = gameOver(i);
            if(flag){
                break;
            }
            if(Thread.currentThread().getName().equals("兔子")&&(i&10)==0){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName()+"-->跑了"+i+"步");
        }
    }
    private boolean gameOver(int steps){
        if(winner!=null){
            return true;
        }
        else if(steps>=100){
            winner = Thread.currentThread().getName();
            System.out.println("Winner is "+winner);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Race race = new Race();
        new Thread(race,"兔子").start();
        new Thread(race,"乌龟").start();
    }
}
