import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/*
In this exercise we ask you to write a command line REPL (read-eval-print loop) that drives a simple in-memory key/value
storage system. This system should also allow for nested transactions. A transaction can then be committed or aborted.

• READ <key> Reads and prints, to stdout, the val associated with key. If the value is not present an error is printed to stderr.
• WRITE <key> <val> Stores val in key.
• DELETE <key> Removes all key from store. Future READ commands on that key will return an error.
• START Start a transaction.
• COMMIT Commit a transaction. All actions in the current transaction are committed to the parent transaction or the root store. If there is no
current transaction an error is output to stderr.
• ABORT Abort a transaction. All actions in the current transaction are discarded.
• QUIT Exit the REPL cleanly. A message to stderr may be output

Example Run
$ my-program
> WRITE a hello
> READ a
hello
> START
> WRITE a hello-again
> READ a
hello-again
> START
> DELETE a
> READ a
Key not found: a
> COMMIT
> READ a
Key not found: a
> WRITE a once-more
> READ a
once-more
> ABORT
> READ a
hello
> QUIT
Exiting..
 */
public class Repl {

    // todo: unit tests
    // todo: docker ?
    // todo: catch exceptions

    static class Transaction {

        public Map<String, String> storage;

        public Transaction parent;

        public Transaction child;

        public Transaction(Map<String, String> storage, Transaction parent) {
            this.storage = storage;
            this.parent = parent;
        }
    }

    public static void main(String... args) {

        Scanner scanner = new Scanner(System.in);

        Transaction transaction = new Transaction(new HashMap<>(), null);

        boolean live = true;

        while (live) {
            final String[] command = parseCommand(scanner.nextLine());

            switch (command[0].toUpperCase()) {
                case "READ":
                    read(transaction, command);
                    break;
                case "WRITE":
                    write(transaction, command);
                    break;
                case "DELETE":
                    delete(transaction, command);
                    break;
                case "START":
                    transaction.child = new Transaction(new HashMap<>(transaction.storage), transaction);
                    transaction = transaction.child;
                    break;
                case "COMMIT":
                    if (transaction.parent != null) {
                        transaction.parent.storage = transaction.storage;
                        transaction = transaction.parent;
                        transaction.child = null;
                    } else {
                        System.err.println("Sorry, there is no transaction to commit.");
                    }
                    break;
                case "ABORT":
                    if (transaction.parent != null) {
                        transaction = transaction.parent;
                        transaction.child = null;
                    } else {
                        System.err.println("Sorry, there is no transaction to abort.");
                    }
                    break;
                case "QUIT":
                    System.out.println("Exiting...");
                    live = false;
                    break;
                default:
                    System.err.println("Sorry, unknown command. Please, try again.");
            }
        }
    }

    private static void delete(Transaction transaction, String[] command) {
        try {
            String key = command[1];
            transaction.storage.remove(key);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Sorry, you need to enter a key.");
        }
    }

    private static void read(Transaction transaction, String[] command) {
        try {
            String key = command[1];
            String value = transaction.storage.get(key);

            if (value != null)
                System.out.println(value);
            else
                System.err.println("Key not found: " + key);

        } catch (IndexOutOfBoundsException e) {
            System.err.println("Sorry, you need to enter a key.");
        }
    }

    private static void write(Transaction transaction, String[] command) {
        try {
            String key = command[1];
            String value = command[2];
            transaction.storage.put(key, value);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Sorry, you need to enter a key and a value.");
        }
    }

    private static String[] parseCommand(String command) {
        String[] commandArr = command.split(" ");

        String instruction = commandArr[0]; // READ, WRITE, etc...

        if (commandArr.length > 1) {
            String key = commandArr[1];
            String value = "";

            for (int i = 2; i < commandArr.length; i++) {
                value = value + " " + commandArr[i];
            }

            return new String[]{instruction, key, value.trim()};
        }

        return new String[]{instruction};
    }

}

