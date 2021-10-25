public class DeadLock {
    public static void main(String[] args) {
        MakeUp makeUp = new MakeUp(0,"Marry");
        MakeUp makeUp1 = new MakeUp(1,"Jary");
        makeUp1.start();
        makeUp.start();
    }
}
class Lipstick{

}
class Mirror{

}
class MakeUp extends Thread{
    static Lipstick lipstick =new Lipstick();
    static Mirror mirror = new Mirror();

    int choice;
    String girlName;
    MakeUp(int choice,String girlName){
        this.choice =choice;
        this.girlName =girlName;
    }
    @Override
    public void run() {
        try {
            makeup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void makeup() throws InterruptedException {
        if(choice ==0){
            synchronized (lipstick){
                System.out.println(this.girlName+" have the lipstick.");
                Thread.sleep(2000);
                synchronized (mirror){
                    System.out.println(this.girlName+" have the mirror.");
            }
            }
        }
        else{
            synchronized (mirror){
                System.out.println(this.girlName+" have the mirror.");
                Thread.sleep(2000);
                synchronized (lipstick){
                    System.out.println(this.girlName+" have the lipstick.");
            }
            }
        }
    }
}