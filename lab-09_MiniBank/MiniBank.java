class Account {
    private long unsafeBalance = 0;
    private long safeBalance = 0;

    public void depositUnsafe(long amount) {
        long next = unsafeBalance + amount;
        if ((next & 127) == 0) Thread.yield();
        unsafeBalance = next;
    }

    public synchronized void depositSafe(long amount) {
        safeBalance += amount;
    }

    public long getUnsafeBalance() { return unsafeBalance; }
    public long getSafeBalance() { return safeBalance; }
}

class AccountWorker implements Runnable {
    private final Account account;
    private final int times;
    private final long amount;
    private final boolean syncMode;

    public AccountWorker(Account acc, int times, long amt, boolean sync) {
        this.account = acc;
        this.times = times;
        this.amount = amt;
        this.syncMode = sync;
    }

    public void run() {
        for (int i = 0; i < times; i++) {
            if (syncMode) account.depositSafe(amount);
            else account.depositUnsafe(amount);
        }
    }
}

public class MiniBank {
    public static void main(String[] args) throws InterruptedException {
        Account acc = new Account();
        Thread[] tUnsafe = new Thread[10];
        for (int i = 0; i < 10; i++) {
            tUnsafe[i] = new Thread(
                new AccountWorker(acc, 1000, 1, false), "Worker-" + (i + 1));
            tUnsafe[i].start();
        }
        for (Thread t : tUnsafe) t.join();
        System.out.println("Before synchronization (10x1000): "
                + acc.getUnsafeBalance() + " (Race Condition!)");

        Thread[] tSafe = new Thread[10];
        for (int i = 0; i < 10; i++) {
            tSafe[i] = new Thread(
                new AccountWorker(acc, 1000, 1, true), "SyncWorker-" + (i + 1));
            tSafe[i].start();
        }
        for (Thread t : tSafe) t.join();
        System.out.println("After synchronization  (10x1000): "
                + acc.getSafeBalance() + " (Exact!)");
    }
}
