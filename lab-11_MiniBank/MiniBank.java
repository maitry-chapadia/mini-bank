import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    final String accountNumber, ownerName;
    final long balance;

    Account(String accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    public String toString() {
        return accountNumber + " | " + ownerName + " | " + balance;
    }
}

class StatePersister {
    public static void save(Account[] accts, Path file) throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(
                Files.newOutputStream(file))) {
            out.writeObject(accts);
        }
    }

    public static Account[] load(Path file) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(
                Files.newInputStream(file))) {
            return (Account[]) in.readObject();
        }
    }
}

class TransactionLog {
    public static void append(Path file, String line) throws Exception {
        Files.writeString(file, line + "\n",
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}

class ReportGenerator {
    public static String generate(Path logDir, Path outReport)
            throws Exception {
        long deposits = 0, withdrawals = 0, totalSize = 0;
        try (DirectoryStream<Path> ds =
                Files.newDirectoryStream(logDir, "*.log")) {
            for (Path p : ds) {
                BasicFileAttributes attr =
                        Files.readAttributes(p, BasicFileAttributes.class);
                totalSize += attr.size();
                try (BufferedReader br = Files.newBufferedReader(p)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] t = line.trim().split("\\s+");
                        if (t.length < 3) continue;
                        long amt = Long.parseLong(t[2]);
                        if (t[0].equals("DEPOSIT")) deposits += amt;
                        else if (t[0].equals("WITHDRAW")) withdrawals += amt;
                    }
                }
            }
        }
        String summary = "Deposits: " + deposits + " | Withdrawals: "
                + withdrawals + " | Net: " + (deposits - withdrawals)
                + " | LogBytes: " + totalSize;
        Files.writeString(outReport, summary + "\n");
        return summary;
    }
}

public class MiniBank {
    public static void main(String[] args) throws Exception {
        Path dataDir = Paths.get("data");
        Path logsDir = dataDir.resolve("logs");
        Files.createDirectories(logsDir);

        Account[] saved = {
            new Account("AC0001", "Riya", 5500),
            new Account("AC0002", "Aarav", 3800)
        };
        Path accFile = dataDir.resolve("accounts.dat");
        StatePersister.save(saved, accFile);

        for (Account a : StatePersister.load(accFile)) {
            System.out.println("Reloaded: " + a);
        }

        Path logFile = logsDir.resolve("tx.log");
        Files.deleteIfExists(logFile);
        TransactionLog.append(logFile, "DEPOSIT AC0001 500");
        TransactionLog.append(logFile, "WITHDRAW AC0002 200");

        String rep = ReportGenerator.generate(
                logsDir, dataDir.resolve("report.txt"));
        System.out.println("Report: " + rep);
    }
}
