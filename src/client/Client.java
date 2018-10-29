package client;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bartosz Śledź
 */
final class Client {

    private static final String CONNECT = "CONNECT";
    private static final String ONLINE = "ONLINE";
    private static final String MESSAGE = "MESSAGE";
    private static final String ERROR = "ERROR";

    private final ClientUserInterface clientUserInterface;
    private PrintWriter out;
    private BufferedReader in;

    Client(final ClientUserInterface clientUserInterface) {
        this.clientUserInterface = clientUserInterface;
        sendButtonListener();
        sendTextAreaListener();
        connectButtonListener();
    }

    /**
     * Adds listener for send button.
     */
    private void sendButtonListener() {
        clientUserInterface.addSendButtonActionListener(e -> sendMessage());
    }

    /**
     * Adds listener for send text area (enter key).
     */
    private void sendTextAreaListener() {
        clientUserInterface.addSendTextAreaActionListener(e -> sendMessage());
    }

    /**
     * Adds listener for connect button.
     */
    private void connectButtonListener() {
        clientUserInterface.addConnectButtonActionListener(arg0 -> {
            if (clientUserInterface.canConnect()) {
                connect();
            } else {
                clientUserInterface.showInfo("Wypełnij wymagane pola!");
            }
        });
    }

    /**
     * Sends message to all online users.
     */
    private void sendMessage() {
        out.println(clientUserInterface.getSendText());
        clientUserInterface.setSendText("");
    }

    /**
     * Create a new connection in new thread.
     */
    private void connect() {
        new Thread(() -> {
            try {
                final Socket socket = new Socket(clientUserInterface.getServerAddress(), clientUserInterface.getServerPort());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    final String line = in.readLine();
                    if (line != null) {
                        if (line.startsWith(CONNECT)) {
                            out.println(clientUserInterface.getUsername());
                        } else if (line.startsWith(ONLINE)) {
                            refreshOnlineUsers(line);
                            clientUserInterface.afterConnect();
                        } else if (line.startsWith(MESSAGE)) {
                            refreshOnlineUsers(line);
                            parseMessage(line);
                        } else if (line.startsWith(ERROR)) {
                            refreshOnlineUsers(line);
                            clientUserInterface.showInfo(line);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                clientUserInterface.showInfo("Parse error!");
            }
        }).start();
    }

    /**
     * Prepares the received message from the server for display.
     *
     * @param line {@link String}
     */
    private void parseMessage(final String line) {
        final Pattern pattern = Pattern.compile(String.format("%s(.*?)\\[", MESSAGE));
        final Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            clientUserInterface.displayMessage(matcher.group(1) + "\n");
        }

    }

    /**
     * Refreshes the list of active users.
     *
     * @param line {@link String}
     */
    private void refreshOnlineUsers(final String line) {
        final Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        final Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            clientUserInterface.setOnlineUsers(new HashSet<>(Arrays.asList(matcher.group(1).split(", "))));
        }
    }

}