import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Id {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Positive {
    String message() default "must be > 0";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
}

class Account {
    @Id
    private final String accountNumber;

    @MaxLength(12)
    private final String ownerName;

    @Positive
    private final long balance;

    public Account(String accountNumber, String ownerName, long balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }
}

class AnnotationValidator {
    public static String[] validate(Object obj) throws Exception {
        List<String> errs = new ArrayList<>();
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object val = f.get(obj);
            if (f.isAnnotationPresent(Positive.class)
                    && val instanceof Number n
                    && n.longValue() <= 0) {
                errs.add(f.getName() + ": "
                        + f.getAnnotation(Positive.class).message());
            }
            if (f.isAnnotationPresent(MaxLength.class)
                    && val instanceof String s) {
                int max = f.getAnnotation(MaxLength.class).value();
                if (s.length() > max) {
                    errs.add(f.getName() + ": length > " + max);
                }
            }
        }
        return errs.toArray(new String[0]);
    }
}

public class MiniBank {
    public static void main(String[] args) throws Exception {
        Account validAcc = new Account("AC0001", "Riya", 5000);
        Account badAcc = new Account("AC0002", "Aarav", -100);

        System.out.println("Valid Account Errors: "
                + Arrays.toString(AnnotationValidator.validate(validAcc)));
        System.out.println("Invalid Account (-100) Errors: "
                + Arrays.toString(AnnotationValidator.validate(badAcc)));
    }
}
