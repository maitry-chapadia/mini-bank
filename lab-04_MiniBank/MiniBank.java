import java.util.regex.Pattern;

enum TransactionType { DEPOSIT, WITHDRAW, TRANSFER }

record Command(TransactionType type, String accountNumber, long amount) {}

class Validator {
    private static final Pattern MOB = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern EMAIL =
            Pattern.compile("^[\\w.+\\-]+@[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PAN =
            Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
    private static final Pattern IFSC =
            Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    public static boolean isValidMobile(String s) {
        return s != null && MOB.matcher(s).matches();
    }
    public static boolean isValidEmail(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }
    public static boolean isValidPan(String s) {
        return s != null && PAN.matcher(s).matches();
    }
    public static boolean isValidIfsc(String s) {
        return s != null && IFSC.matcher(s).matches();
    }
}

class CommandParser {
    public static Command parse(String line) {
        String[] p = line.trim().split("\\s+");
        return new Command(
            TransactionType.valueOf(p[0].toUpperCase()),
            p[1],
            Long.parseLong(p[2])
        );
    }
}

class StatementFormatter {
    public static String buildStatement(String accNo, String owner, long bal) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- MINI BANK STATEMENT ---\n");
        sb.append("Account: ").append(accNo).append(" (").append(owner).append(")\n");
        sb.append("Closing Balance: INR ").append(bal);
        return sb.toString();
    }
}

public class MiniBank {
    public static void main(String[] args) {
        System.out.println("Mobile 9876543210: "
                + Validator.isValidMobile("9876543210")
                + " | 12345: " + Validator.isValidMobile("12345"));
        System.out.println("PAN ABCDE1234F: "
                + Validator.isValidPan("ABCDE1234F")
                + " | IFSC SBIN0001234: "
                + Validator.isValidIfsc("SBIN0001234"));

        Command cmd = CommandParser.parse("DEPOSIT AC0001 500");
        System.out.println("Parsed: type=" + cmd.type()
                + ", accountNumber=" + cmd.accountNumber()
                + ", amount=" + cmd.amount());
        System.out.println(
                StatementFormatter.buildStatement("AC0001", "Riya", 4500));
    }
}
