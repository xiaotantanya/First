public class TestPC {
    public static void main(String[] args) {
        SynContainer container =new SynContainer();
        new Productor(container).start();
        new Consumer(container).start();
    }
}
//生产者
class Productor extends Thread{
    SynContainer container;
    public Productor(SynContainer container){
        this.container =container;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {

            try {
                container.push(new Chicken(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("product "+ (i+1)+" chicken!");
        }
    }
}
class Consumer extends Thread{
    SynContainer container;
    public Consumer(SynContainer container){
        this.container=container;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                System.out.println("consumer "+container.pop().ID+" chicken!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class Chicken{
    int ID;

    public Chicken(int ID) {
        this.ID = ID;
    }
}

class SynContainer{
    Chicken[] chickens =new Chicken[10];
     int count =0;
    //Product
    public synchronized  void push(Chicken chicken) throws InterruptedException {
        if (count ==chickens.length){

            this.wait();
        }
        Thread.sleep(1000);
        chickens[count]=chicken;
        count++;
        this.notifyAll();
    }
    //Consumer
    public synchronized Chicken pop() throws InterruptedException {
        if(count==0)
        {
            this.wait();

        }
        Thread.sleep(1000);
        Chicken chicken = chickens[count];
        count--;
        this.notifyAll();
        return chicken;
    }
}