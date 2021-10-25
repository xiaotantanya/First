public class TestPC2 {
    public static void main(String[] args) {
        TV tv = new TV();
        new Player(tv).start();
        new Watcher(tv).start();
    }
}

class Player extends Thread {
    TV tv;

    public Player(TV tv) {
        this.tv = tv;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            if(i%2==0){
                try {
                    this.tv.play("interesting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    this.tv.play("fantastic!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class Watcher extends Thread {
    TV tv;

    public Watcher(TV tv) {
        this.tv = tv;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            try {
                tv.watch();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class TV {
    String voice;
    boolean flag = true;

    public synchronized void play(String voice) throws InterruptedException {
        if (!flag) {
            this.wait();
        }
        System.out.println("Player play" + voice);
        this.notifyAll();
        this.voice = voice;
        this.flag = !this.flag;
    }

    public synchronized void watch() throws InterruptedException {
        if (flag) {
            this.wait();
        }
        System.out.println("Watcher watch" + voice);
        this.notifyAll();
        this.flag = !this.flag;
    }
}