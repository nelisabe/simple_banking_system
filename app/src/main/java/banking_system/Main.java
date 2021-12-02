package banking_system;

import java.util.Scanner;

public class Main {
    private static Scanner  scanner;
    private static Cards    cards;

    private static void printMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    private static void createAnAccount() {
        Card    card;

        card = cards.createNewCard();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(card.getCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(card.getPin());
    }

    private static void loggedIntoAccount(Card card) {
        int     input;

        System.out.println("You have successfully logged in!");
        while (true) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            input = scanner.nextInt();
            if (input == 0) {
                System.out.println("Bye!");
                System.exit(0);
            }
            if (input == 1) {
                System.out.printf("Balance: %d\n", card.getBalance());
            }
            if (input == 2) {
                int     income;

                System.out.print("Enter income: ");
                income = scanner.nextInt();
                cards.addIncomeToCard(card, income);
                System.out.println("Income was added!");
            }
            if (input == 3) {
                String  cardNumber;
                int     transfer;

                System.out.println("Transfer");
                System.out.print("Enter card number: ");
                cardNumber = scanner.next();
                try {
                    if (cardNumber.equals(card.getCardNumber())) {
                        System.out.println("You can't transfer money to the same account!\n");
                        continue;
                    }
                    cards.checkCardNumber(cardNumber);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    continue;
                }
                System.out.print("Enter how much money you want to transfer: ");
                transfer = scanner.nextInt();
                if (card.getBalance() < transfer) {
                    System.out.println("Not enough money!");
                    continue;
                }
                try {
                    cards.transferMoney(card, cardNumber, transfer);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    continue;
                }
                System.out.println("Success!");
            }
            if (input == 4) {
                cards.deleteCard(card);
                System.out.println("The account has been closed!");
                return;
            }
            if (input == 5) {
                break;
            }
        }
        System.out.println("You have successfully logged out!");
    }

    private static void logIntoAccount() {
        String  cardNumber;
        Card    card;
        short   pin;

        System.out.println("Enter your card number:");
        cardNumber = scanner.next();
        System.out.println("Enter your PIN:");
        pin = scanner.nextShort();
        card = cards.logIntoCard(cardNumber, pin);
        loggedIntoAccount(card);
    }

    private static String parseArgs(String[] args) {
        if (args.length != 2) {
            throw new RuntimeException("Wrong number of arguments");
        }
        if (!args[0].equals("-fileName")) {
            throw new RuntimeException("Unknown argument \"" + args[0] + "\"");
        }
        return args[1];
    }

    public static void main(String[] args) {
        String  dbUrl;
        int     input;

        try {
            dbUrl = parseArgs(args);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return;
        }
        scanner = new Scanner(System.in);
        cards = new Cards(dbUrl);
        while (true) {
            printMenu();
            input = scanner.nextInt();
            if (input == 0) {
                break;
            }
            if (input == 1) {
                System.out.println();
                createAnAccount();
                System.out.println();
            }
            if (input == 2) {
                try {
                    logIntoAccount();
                } catch (Exception ex) {
                    System.out.println("Wrong card number or PIN!");
                }
                finally {
                    System.out.println();
                }
            }
        }
        System.out.println("Bye!");
    }
}