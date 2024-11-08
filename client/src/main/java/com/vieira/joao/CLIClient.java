package com.vieira.joao;

import java.util.Map;
import java.util.Scanner;

public class CLIClient implements Client {

    private final ClientService clientService;
    private String current_user = null;
    private final Map<String, String> privateKeyPaths;
    private final Map<String, String> publicKeyPaths;

    public CLIClient(ClientService clientService, Map<String, String> privateKeyPaths, Map<String, String> publicKeyPaths) {
        this.clientService = clientService;
        this.privateKeyPaths = privateKeyPaths;
        this.publicKeyPaths = publicKeyPaths;
    }

    @Override
    public void startClient() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print("\n$ ");
            String cmd = scanner.nextLine();

            String[] cmd_args = cmd.split(" ");
            command = cmd_args[0];

            if (command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit")) {
                System.out.println("Exiting...");
                break;
            }

            switch (command.toLowerCase()) {
                case "login":
                    String users = clientService.getUsers();
                    System.out.println("Possible users: \n" + users);
                    System.out.print("\nYour username: ");
                    String user = scanner.nextLine();
                    current_user = users.contains(user) ? user : null;
                    if (current_user == null) {
                        System.err.println("Username not found...");
                    } else {
                        System.out.println("Succesfully logged in...");
                    }
                    break;
                case "logout":
                    current_user = null;
                    break;
                case "users":
                    System.out.print(clientService.getUsers());
                    break;
                case "info":
                    System.out.print(clientService.getInfo());
                    break;
                case "find":
                    if (current_user == null) {
                        System.err.println("You need to be logged in to use this command...");
                        break;
                    }
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
        System.out.println("users            - list available users");
        System.out.println("login            - login as a user");
        System.out.println("logout           - logout");
        System.out.println("info             - requests a list of available restaurants");
        System.out.println("find <ID>|<Name> - requests information about restaurant with id <ID>");
        System.out.println("review           - creates a review for a restaurant and sends it to the server");
        System.out.println("givevoucher      - creates a request to give a voucher to another user");
        System.out.println("vouchers         - list the current user's vouchers");
        System.out.println("quit|exit        - quits the application");
        System.out.println("----------------------------------------------------\n");
    }
}
