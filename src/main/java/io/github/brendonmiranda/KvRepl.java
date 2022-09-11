package io.github.brendonmiranda;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class KvRepl {

    static class Transaction {

        Map<String, String> storage;

        Transaction parent;

        Transaction child;

        Transaction(Map<String, String> storage, Transaction parent) {
            this.storage = storage;
            this.parent = parent;
        }
    }

    public static void main(String... args) {

        Scanner scanner = new Scanner(System.in);

        Transaction transaction = new Transaction(new HashMap<>(), null);

        boolean live = true;

        printInstructions();

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

    private static void printInstructions() {
        System.out.println("READ Reads and prints, to stdout, the val associated with key. If the value is not present an error is printed to stderr.\n" +
                "WRITE Stores val in key.\n" +
                "DELETE Removes all key from store. Future READ commands on that key will return an error.\n" +
                "START Start a transaction.\n" +
                "COMMIT Commit a transaction. All actions in the current transaction are committed to the parent transaction or the root store. If there is no current transaction an error is output to stderr.\n" +
                "ABORT Abort a transaction. All actions in the current transaction are discarded.\n" +
                "QUIT Exit the REPL cleanly. A message to stderr may be output\n");
    }

    private static void delete(Transaction transaction, String[] command) {
        try {
            String key = command[1];
            String v = transaction.storage.remove(key);
            if (v == null)
                System.err.println("Sorry, you need to enter a valid key.");
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
                System.err.println("Sorry, you need to enter a valid key.");

        } catch (IndexOutOfBoundsException e) {
            System.err.println("Sorry, you need to enter a key.");
        }
    }

    private static void write(Transaction transaction, String[] command) {
        try {
            String key = command[1];
            String value = command[2];
            if (!"".equals(value)) {
                transaction.storage.put(key, value);
            } else {
                System.err.println("Sorry, you need to enter a valid value.");
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Sorry, you need to enter a key and a value.");
        }
    }

    private static String[] parseCommand(String command) {
        String[] commandArr = command.split(" ");

        String instruction = commandArr[0]; // READ, WRITE, etc...

        if (commandArr.length > 1) {
            String key = commandArr[1];
            StringBuilder value = new StringBuilder();

            for (int i = 2; i < commandArr.length; i++) {
                value.append(" ").append(commandArr[i]);
            }

            return new String[]{instruction, key, value.toString().trim()};
        }

        return new String[]{instruction};
    }

}

