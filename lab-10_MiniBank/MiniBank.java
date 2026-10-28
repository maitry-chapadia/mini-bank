import java.util.concurrent.*;

class Account {
    final String accountNumber;
    long balance;

    Account(String accountNumber, long balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public static void transferSafe(Account from, Account to, long amt) {
        Account first = from.accountNumber.compareTo(to.accountNumber) < 0
                ? from : to;
        Account second = (first == from) ? to : from;
        synchronized (first) {
            synchronized (second) {
                if (from.balance >= amt) {
                    from.balance -= amt;
                    to.balance += amt;
                }
            }
        }
    }
}

class TransactionProcessor {
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    public void submit(Runnable task) { pool.execute(task); }

    public void stop() throws InterruptedException {
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }
}

class TxBuffer {
    private String slot;
    public synchronized void put(String tx) throws InterruptedException {
        while (slot != null) wait();
        slot = tx;
        notifyAll();
    }
    public synchronized String take() throws InterruptedException {
        while (slot == null) wait();
        String res = slot;
        slot = null;
        notifyAll();
        return res;
    }
}

public class MiniBank {
    public static void main(String[] args) throws Exception {
        Account a = new Account("AC0001", 5000);
        Account b = new Account("AC0002", 5000);

        TransactionProcessor proc = new TransactionProcessor();
        proc.submit(() -> Account.transferSafe(a, b, 1000));
        proc.submit(() -> Account.transferSafe(b, a, 400));
        proc.stop();
        System.out.println("Pool Lock-Ordered Transfers Done: AC0001="
                + a.balance + ", AC0002=" + b.balance);

        TxBuffer buf = new TxBuffer();
        Thread prod = new Thread(() -> {
            try { buf.put("DEPOSIT AC0001 500"); } catch (Exception ignored) {}
        });
        Thread cons = new Thread(() -> {
            try {
                System.out.println("Consumed from buffer: " + buf.take());
            } catch (Exception ignored) {}
        });
        prod.start(); cons.start();
        prod.join(); cons.join();
    }
}
