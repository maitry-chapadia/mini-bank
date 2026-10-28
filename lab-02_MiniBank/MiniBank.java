class Customer {
    private static long customerCounter = 100;
    private final String customerId;
    private String name, email, mobile;

    private static String generateCustomerId() {
        return "CUST" + (++customerCounter);
    }

    public Customer(String name, String email, String mobile) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
}

class Account {
    private static long accCounter = 0;
    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", ++accCounter);
        this.ownerName = ownerName;
        this.balance = Math.max(0, openingBalance);
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public void deposit(long amount) {
        if (amount > 0) balance += amount;
    }

    public boolean withdraw(long amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public long getBalance() { return balance; }
    public boolean isActive() { return active; }
}

public class MiniBank {
    public static void main(String[] args) {
        Customer c1 = new Customer("Riya", "riya@mail.com", "9876543210");
        System.out.println("Customer: " + c1.getCustomerId()
                + " (" + c1.getName() + ")");

        Account[] accts = {
            new Account("Riya", 5000),
            new Account("Aarav", 3000),
            new Account("Kabir")
        };

        accts[0].deposit(2000);
        System.out.println("After deposit(2000): " + accts[0].getBalance());
        boolean ok1 = accts[0].withdraw(3000);
        System.out.println("withdraw(3000)=" + ok1
                + " | Balance: " + accts[0].getBalance());
        boolean ok2 = accts[0].withdraw(10000);
        System.out.println("withdraw(10000)=" + ok2
                + " | Balance: " + accts[0].getBalance());
    }
}
