class BankException extends Exception {
    public BankException(String msg) { super(msg); }
}

class InsufficientFundsException extends BankException {
    private final long shortfall;
    public InsufficientFundsException(long shortfall) {
        super("Withdrawal failed: short by " + shortfall);
        this.shortfall = shortfall;
    }
    public long getShortfall() { return shortfall; }
}

class InvalidAmountException extends BankException {
    public InvalidAmountException(long amt) {
        super("Invalid amount: " + amt + " (must be > 0)");
    }
}

class AuditLog implements AutoCloseable {
    public AuditLog() { System.out.println("[Audit] Session opened"); }
    public void record(String msg) { System.out.println("[Audit] " + msg); }
    public void close() { System.out.println("[Audit] Session closed"); }
}

class Account {
    private final String accNo;
    private long balance;

    public Account(String accNo, long balance) {
        this.accNo = accNo;
        this.balance = balance;
    }

    public void deposit(long amt) throws InvalidAmountException {
        if (amt <= 0) throw new InvalidAmountException(amt);
        balance += amt;
    }

    public void withdraw(long amt)
            throws InsufficientFundsException, InvalidAmountException {
        if (amt <= 0) throw new InvalidAmountException(amt);
        if (amt > balance) {
            throw new InsufficientFundsException(amt - balance);
        }
        balance -= amt;
    }

    public void transfer(Account to, long amt) throws BankException {
        try {
            this.withdraw(amt);
            to.deposit(amt);
        } finally {
            System.out.println("[Finally] Transfer attempt logged.");
        }
    }

    public long getBalance() { return balance; }
}

public class MiniBank {
    public static void main(String[] args) {
        Account a1 = new Account("AC0001", 1000);
        Account a2 = new Account("AC0002", 500);

        try (AuditLog log = new AuditLog()) {
            log.record("Attempting withdraw(5000) from AC0001 (bal=1000)");
            a1.withdraw(5000);
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage()
                    + " | Balance stays: " + a1.getBalance());
        } catch (BankException e) {
            System.out.println(e.getMessage());
        }

        try {
            a1.transfer(a2, 400);
            System.out.println("After transfer(400): AC0001="
                    + a1.getBalance() + ", AC0002=" + a2.getBalance());
        } catch (BankException e) {
            System.out.println(e.getMessage());
        }
    }
}
