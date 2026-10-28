abstract class Account {
    protected final String accountNumber, ownerName;
    protected long balance;

    public Account(String accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public abstract double interestRate();
    public abstract boolean canWithdraw(long amount);
}

class SavingsAccount extends Account {
    private final long minBalance;

    public SavingsAccount(String accNo, String owner, long bal, long min) {
        super(accNo, owner, bal);
        this.minBalance = min;
    }

    public double interestRate() { return 4.0; }
    public boolean canWithdraw(long amt) {
        return (balance - amt) >= minBalance;
    }
    public long getMinBalance() { return minBalance; }
}

class CurrentAccount extends Account {
    private final long overdraftLimit;

    public CurrentAccount(String accNo, String owner, long bal, long od) {
        super(accNo, owner, bal);
        this.overdraftLimit = od;
    }

    public double interestRate() { return 0.0; }
    public boolean canWithdraw(long amt) {
        return (balance - amt) >= -overdraftLimit;
    }
}

class FixedDepositAccount extends Account {
    public FixedDepositAccount(String accNo, String owner, long bal) {
        super(accNo, owner, bal);
    }

    public double interestRate() { return 7.0; }
    public boolean canWithdraw(long amt) { return false; }
}

public class MiniBank {
    public static void main(String[] args) {
        Account[] accounts = {
            new SavingsAccount("AC0001", "Riya", 8000, 1000),
            new CurrentAccount("AC0002", "Aarav", 5000, 10000),
            new FixedDepositAccount("AC0003", "Kabir", 25000)
        };

        for (Account acc : accounts) {
            System.out.println(acc.getClass().getSimpleName()
                    + " (" + acc.accountNumber + ") Rate: "
                    + acc.interestRate() + "% | canWithdraw(6000): "
                    + acc.canWithdraw(6000));
            if (acc instanceof SavingsAccount sa) {
                System.out.println("  -> Savings minBalance: "
                        + sa.getMinBalance());
            }
        }
    }
}
