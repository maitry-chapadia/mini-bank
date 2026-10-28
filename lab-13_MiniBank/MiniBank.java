import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Id {}

class Account {
    @Id final String accountNumber;
    final String owner;
    final long balance;

    Account(String accountNumber, String owner, long balance) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = balance;
    }

    public String toString() {
        return accountNumber + ":" + owner + "(" + balance + ")";
    }
}

class Customer {
    @Id final String customerId;
    final String name;
    Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }
    public String toString() { return customerId + ":" + name; }
}

class Repository<T> {
    private final Map<String, T> store = new LinkedHashMap<>();

    public T save(T entity) {
        try {
            for (Field f : entity.getClass().getDeclaredFields()) {
                if (f.isAnnotationPresent(Id.class)) {
                    f.setAccessible(true);
                    String key = String.valueOf(f.get(entity));
                    store.put(key, entity);
                    return entity;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("No @Id field found");
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) save(item);
    }

    public T findById(String id) { return store.get(id); }
    public ArrayList<T> findAll() { return new ArrayList<>(store.values()); }
    public boolean delete(String id) { return store.remove(id) != null; }

    public static <N extends Number> double average(N[] values) {
        double sum = 0;
        for (N v : values) sum += v.doubleValue();
        return sum / values.length;
    }
}

public class MiniBank {
    public static void main(String[] args) {
        Repository<Account> accRepo = new Repository<>();
        accRepo.saveAll(
            new Account("AC0001", "Riya", 5000),
            new Account("AC0002", "Aarav", 2000),
            new Account("AC0003", "Kabir", 8000)
        );
        System.out.println("findById(AC0002): " + accRepo.findById("AC0002"));
        System.out.println("All Accounts: " + accRepo.findAll());

        Repository<Customer> custRepo = new Repository<>();
        custRepo.save(new Customer("CUST101", "Riya"));
        System.out.println("Customers: " + custRepo.findAll());

        Long[] bals = {5000L, 2000L, 8000L};
        System.out.println("Average Balance: " + Repository.average(bals));
    }
}
