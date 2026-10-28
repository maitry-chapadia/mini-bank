import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Test {}

class BankException extends Exception {
    public BankException(String m) { super(m); }
}

class Account {
    final String accNo, owner;
    long balance;
    Account(String accNo, String owner, long balance) {
        this.accNo = accNo;
        this.owner = owner;
        this.balance = balance;
    }
    public synchronized void deposit(long amt) throws BankException {
        if (amt <= 0) throw new BankException("Invalid deposit");
        balance += amt;
    }
    public synchronized void withdraw(long amt) throws BankException {
        if (amt > balance) throw new BankException("Short by " + (amt - balance));
        balance -= amt;
    }
    public String toString() { return accNo + "(" + owner + ":" + balance + ")"; }
}

class BankService {
    private final Map<String, Account> repo = new ConcurrentHashMap<>();

    public Account openAccount(String accNo, String owner, long opening) {
        Account a = new Account(accNo, owner, opening);
        repo.put(accNo, a);
        return a;
    }
    public void deposit(String accNo, long amt) throws BankException {
        repo.get(accNo).deposit(amt);
    }
    public void withdraw(String accNo, long amt) throws BankException {
        repo.get(accNo).withdraw(amt);
    }
    public void transfer(String from, String to, long amt) throws BankException {
        repo.get(from).withdraw(amt);
        repo.get(to).deposit(amt);
    }
    public Collection<Account> listAccounts() { return repo.values(); }
}

public class MiniBank {
    @Test
    public static void testDepositIncreasesBalance() throws Exception {
        BankService svc = new BankService();
        svc.openAccount("AC01", "Riya", 1000);
        svc.deposit("AC01", 500);
        System.out.println("[PASS] testDepositIncreasesBalance: 1500");
    }

    @Test
    public static void testWithdrawOverLimitThrows() {
        BankService svc = new BankService();
        svc.openAccount("AC01", "Riya", 1000);
        try {
            svc.withdraw("AC01", 5000);
        } catch (BankException e) {
            System.out.println("[PASS] testWithdrawOverLimitThrows: "
                    + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        BankService bank = new BankService();
        bank.openAccount("AC0001", "Riya", 5000);
        bank.openAccount("AC0002", "Aarav", 3000);
        bank.deposit("AC0001", 1000);
        bank.transfer("AC0001", "AC0002", 1500);
        System.out.println("Active Accounts: " + bank.listAccounts());

        for (Method m : MiniBank.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) m.invoke(null);
        }
    }
}
