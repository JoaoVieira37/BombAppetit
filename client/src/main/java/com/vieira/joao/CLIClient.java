package com.vieira.joao;

import java.util.Scanner;

public class CLIClient implements Client {

    private final ClientService clientService;

    public CLIClient(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void startClient() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print("\nEnter command: ");
            String cmd = scanner.nextLine();

            String[] cmd_args = cmd.split(" ");
            command = cmd_args[0];

            if (command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit")) {
                System.out.println("Exiting...");
                break;
            }

            switch (command.toLowerCase()) {
                case "info":
                    clientService.getInfo();
                    break;
                case "find":
                    if (cmd_args.length < 2) {
                        System.out.println("find command need 1 more argument");
                        printUsage();
                        break;
                    }
                    System.out.println("Find command executed with argument " + cmd_args[1]);
                    break;
                case "review":
                    System.out.println("Review command executed.");
                    break;
                case "givevoucher":
                    System.out.println("Give command executed.");
                    break;
                case "vouchers":
                    System.out.println("Vouchers command executed.");
                    break;
                case "help":
                    printUsage();
                    break;
                default:
                    System.out.println("Unknown command { " + command + " }");
                    printUsage();
                    break;
            }
        }

        scanner.close();
    }

    private static void printUsage() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("Available commands:");
        System.out.println("help             - print usage information");
        System.out.println("info             - requests a list of available restaurants");
        System.out.println("find <ID>|<Name> - requests information about restaurant with id <ID>");
        System.out.println("review           - creates a review for a restaurant and sends it to the server");
        System.out.println("givevoucher      - creates a request to give a voucher to another user");
        System.out.println("vouchers         - list the current user's vouchers");
        System.out.println("quit|exit        - quits the application");
        System.out.println("----------------------------------------------------\n");
    }
}
