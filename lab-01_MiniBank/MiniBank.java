import java.util.Scanner;

record BankInfo(String name, String branch) {}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
}

public class MiniBank {
    public static void main(String[] args) {
        BankInfo info = new BankInfo("CHARUSAT MiniBank", "Anand");
        System.out.println("=== " + info.name()
                + " (" + info.branch() + " Branch) ===");

        String simulatedInput = "1\n2\n5\n";
        Scanner sc = new Scanner(simulatedInput);
        MenuOption[] opts = MenuOption.values();

        while (sc.hasNextInt()) {
            System.out.println("1.OPEN 2.DEPOSIT 3.WITHDRAW 4.TRANSFER 5.EXIT");
            int ch = sc.nextInt();
            System.out.println("Choice: " + ch);
            if (ch < 1 || ch > opts.length) {
                System.out.println("Invalid option! Try again.");
                continue;
            }
            MenuOption selected = opts[ch - 1];
            String msg = switch (selected) {
                case OPEN_ACCOUNT ->
                    "Open Account - to be implemented in a later lab";
                case DEPOSIT ->
                    "Deposit - to be implemented in a later lab";
                case WITHDRAW ->
                    "Withdraw - to be implemented in a later lab";
                case TRANSFER ->
                    "Transfer - to be implemented in a later lab";
                case EXIT ->
                    "Thank you for banking with MiniBank. Goodbye!";
            };
            System.out.println(msg);
            if (selected == MenuOption.EXIT) break;
        }
        sc.close();
    }
}
