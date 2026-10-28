import static java.lang.Math.round;

interface Transactable {
    void deposit(long amount);
    boolean withdraw(long amount);
}

interface InterestBearing {
    double interestRate();
    long getBalance();
    default double yearlyInterest() {
        return round(getBalance() * interestRate() / 100.0);
    }
}

@FunctionalInterface
interface WithdrawRule {
    boolean allow(Account account, long amount);
}

interface Premium {}

class Account implements Transactable, InterestBearing, Premium {
    private final String accountNumber;
    private long balance;

    public Account(String accountNumber, long balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(long amount) { balance += amount; }
    public boolean withdraw(long amount) {
        if (amount <= balance) { balance -= amount; return true; }
        return false;
    }
    public double interestRate() { return 4.0; }
    public long getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
}

public class MiniBank {
    public static void main(String[] args) {
        Account acc = new Account("AC0001", 10000);

        WithdrawRule anonRule = new WithdrawRule() {
            public boolean allow(Account a, long amt) {
                return amt <= 5000 && a.getBalance() >= amt;
            }
        };
        WithdrawRule lambdaRule =
                (a, amt) -> amt <= 5000 && a.getBalance() >= amt;

        System.out.println("Anonymous allow(3000): "
                + anonRule.allow(acc, 3000));
        System.out.println("Lambda allow(3000):    "
                + lambdaRule.allow(acc, 3000));
        System.out.println("Yearly Interest: INR " + acc.yearlyInterest()
                + " | Premium: " + (acc instanceof Premium));
    }
}
