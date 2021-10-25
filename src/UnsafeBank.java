public class UnsafeBank {
    public static void main(String[] args) {
        Account account =new Account(10000,"marry money");
        Drawing you =new Drawing(account,50,"you");
        Drawing girlFriend = new Drawing(account,100,"girlFriend");

        you.start();
        girlFriend.start();
    }
}
class Account {
     int money;
     String name;

    public Account(int money,String name) {
        this.money = money;
        this.name = name;
    }
}
class Drawing extends Thread{
    private Account account;
    private int drawingMoney;
    private int nowMoney;
    public Drawing(Account account,int drawingMoney,String name)
    {
        super(name);
        this.account =account;
        this.drawingMoney =drawingMoney;
        this.nowMoney = nowMoney;
    }

    @Override
    public void run() {
        synchronized (account){
            if(account.money-drawingMoney<0){
                System.out.println(Thread.currentThread().getName()+" Money is not enough!");
                return ;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            account.money =account.money-drawingMoney;
            nowMoney =nowMoney+drawingMoney;
            System.out.println(account.name+" account money:"+account.money);
            System.out.println(this.getName()+" now money:"+nowMoney);
        }

    }
}