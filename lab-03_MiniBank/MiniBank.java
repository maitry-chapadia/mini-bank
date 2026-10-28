import java.util.Objects;

class Customer implements Cloneable {
    public static class Address {
        private final String line, city, pincode;
        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }
    }

    private final String customerId, name;
    private final Address address;

    public Customer(String id, String name, Address address) {
        this.customerId = id;
        this.name = name;
        this.address = address;
    }

    public Address getAddress() { return address; }

    public Customer clone() throws CloneNotSupportedException {
        return (Customer) super.clone();
    }

    public String toString() {
        return customerId + " | " + name + " | " + address;
    }
}

class Account {
    private final String accountNumber, ownerName;
    private final long balance;

    public Account(String accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public String toString() {
        return accountNumber + " | " + ownerName + " | " + balance;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account other)) return false;
        return Objects.equals(accountNumber, other.accountNumber);
    }

    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}

public class MiniBank {
    public static void main(String[] args) throws Exception {
        Account a1 = new Account("AC0001", "Riya", 4000);
        Account a2 = new Account("AC0001", "Riya", 4000);

        System.out.println("Account a1: " + a1);
        System.out.println("a1.equals(a2): " + a1.equals(a2));
        System.out.println("a1 instanceof Account: "
                + (a1 instanceof Account));

        Customer.Address addr =
                new Customer.Address("MG Road", "Anand", "388001");
        Customer c1 = new Customer("CUST101", "Riya", addr);
        Customer c2 = c1.clone();
        System.out.println("Cloned Customer: " + c2);
    }
}
