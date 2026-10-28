import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class Account implements Comparable<Account> {
    final String accountNumber, ownerName;
    final long balance;

    Account(String accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    public String toString() {
        return accountNumber + " (" + ownerName + ": " + balance + ")";
    }
}

class AccountStore {
    private final Map<String, Account> map = new HashMap<>();
    private final ConcurrentHashMap<String, Account> concurrentMap =
            new ConcurrentHashMap<>();
    private final ArrayList<String> history = new ArrayList<>();

    public void add(Account a) {
        map.put(a.accountNumber, a);
        concurrentMap.put(a.accountNumber, a);
        history.add("ADDED " + a.accountNumber);
    }

    public Account get(String accNo) { return map.get(accNo); }

    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(map.values());
        list.sort(Comparator.comparingLong((Account a) -> a.balance)
                .reversed());
        return list;
    }

    public ArrayList<String> getHistory() { return history; }
}

public class MiniBank {
    public static void main(String[] args) {
        AccountStore store = new AccountStore();
        store.add(new Account("AC0001", "Riya", 5000));
        store.add(new Account("AC0002", "Aarav", 2000));
        store.add(new Account("AC0003", "Kabir", 9000));

        System.out.println("Lookup get(AC0002): " + store.get("AC0002"));
        System.out.println("Sorted by Balance (desc): "
                + store.sortedByBalance());
        System.out.println("History: " + store.getHistory());
    }
}
