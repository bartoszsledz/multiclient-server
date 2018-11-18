package multicasting;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bartosz Śledź
 */
public class Client extends Thread {

    private static final int PORT = 4444;
    private static final String ADDRESS = "230.0.0.0";
    private static final String JOINED = "joined";
    private static final String LEFT = "left";
    private static final String TEXT = "text";
    private static final String INFO = "info";
    private static final int MAX_USERS = 10;
    private static final HashSet<String> USERS_NAMES = new HashSet<>();

    private final UserInterface ui;
    private final DatagramSocket sendSocket;
    private final MulticastSocket receiveSocket;
    private final InetAddress group;

    public Client(final DatagramSocket sendSocket,
                  final MulticastSocket receiveSocket,
                  final InetAddress group,
                  final UserInterface userInterface) {
        this.ui = userInterface;
        this.sendSocket = sendSocket;
        this.receiveSocket = receiveSocket;
        this.group = group;
        init();
    }

    private void init() {
        sendButtonListener();
        sendTextAreaListener();
        addExitListener();
        sendMessage(JOINED, "");
        new Thread(this::uploadOnlineUsersList).start();
    }

    public void run() {
        while (true) {
            final byte[] buffer = new byte[20000];
            final DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, PORT);
            final String msg;
            try {
                receiveSocket.receive(datagram);
                msg = new String(buffer, 0, datagram.getLength(), StandardCharsets.UTF_8);
                final String action = getAction(msg);
                final String text = getText(msg);
                final String username = getUsername(msg);

                switch (action) {
                    case LEFT:
                        deleteUsername(username);
                        ui.displayMessage(String.format("%s: %s", username, LEFT) + "\n");
                        break;
                    case JOINED:
                        saveUsername(username);
                        USERS_NAMES.addAll(getUsers(msg));
                        break;
                    case TEXT:
                        ui.displayMessage(String.format("%s: %s", username, text) + "\n");
                        USERS_NAMES.addAll(getUsers(msg));
                        break;
                    case INFO:
                        USERS_NAMES.addAll(getUsers(msg));
                        break;
                }

                ui.setOnlineUsers(USERS_NAMES);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send messages to clients.
     *
     * @param message {@link String}
     * @param action  {@link String}
     */
    public void sendMessage(final String action, final String message) {
        try {
            final String msg = String.format("%s: %s{%s:%s}", ui.getUsername(), message, action, USERS_NAMES);
            final byte[] buffer = msg.getBytes();
            final DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ADDRESS), PORT);
            sendSocket.send(datagram);
            if (!action.equals(INFO)) {
                ui.setSendText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Every x seconds sends info about current online users.
     */
    private void uploadOnlineUsersList() {
        while (true) {
            try {
                Thread.sleep(2000);
                sendMessage(INFO, "");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds listener for send button.
     */
    private void sendButtonListener() {
        ui.addSendButtonActionListener(e -> sendMessage(TEXT, ui.getSendText()));
    }

    /**
     * Adds listener for send text area (enter key).
     */
    private void sendTextAreaListener() {
        ui.addSendTextAreaActionListener(e -> sendMessage(TEXT, ui.getSendText()));
    }

    /**
     * Adds listener for exit user.
     */
    private void addExitListener() {
        ui.addExitListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                sendMessage(LEFT, "");
                System.exit(1);
            }
        });
    }

    /**
     * Delete username from online lsit.
     *
     * @param username {@link String}
     */
    private void deleteUsername(final String username) {
        if (username != null) {
            USERS_NAMES.remove(username);
        }
    }

    /**
     * Save new user.
     *
     * @param username {@link String}
     */
    private void saveUsername(final String username) {
        if (!USERS_NAMES.contains(username)) {
            if (USERS_NAMES.size() < MAX_USERS) {
                USERS_NAMES.add(username);
            } else {
                /* ui.showInfo("Limit osiągnięty!");
                return;*/
            }
        } else {
           /* ui.showInfo("Login zajęty!");
            return;*/
        }

        ui.displayMessage(String.format("%s: %s", username, JOINED) + "\n");
    }

    /**
     * Get action from receive msg.
     *
     * @param msg {@link String}
     * @return {@link String}
     */
    private String getAction(final String msg) {
        Matcher matcher = Pattern.compile("\\{(.*?):").matcher(msg);

        return matcher.find() ? matcher.group(1) : null;
    }

    /**
     * Get online users from receive msg.
     *
     * @param msg {@link String}
     * @return {@link String}
     */
    private HashSet<String> getUsers(final String msg) {
        final Matcher matcher = Pattern.compile("\\[(.*?)]").matcher(msg);

        if (matcher.find() && !matcher.group(1).isEmpty()) {
            return new HashSet<>(Arrays.asList(matcher.group(1).split(", ")));
        }

        return USERS_NAMES;
    }

    /**
     * Get text from receive msg.
     *
     * @param msg {@link String}
     * @return {@link String}
     */
    private String getText(final String msg) {
        final Matcher matcher = Pattern.compile(":(.*?)\\{").matcher(msg);

        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * Get username from user message.
     *
     * @param msg {@link String}
     * @return {@link String}
     */
    private String getUsername(final String msg) {
        final Matcher matcher = Pattern.compile("(.*?):").matcher(msg);

        return matcher.find() ? matcher.group(1) : null;
    }

    public static void main(final String[] args) throws IOException {
        final DatagramSocket sendSocket = new DatagramSocket();
        final MulticastSocket receiveSocket = new MulticastSocket(PORT);
        final InetAddress group = InetAddress.getByName(ADDRESS);
        receiveSocket.setTimeToLive(0);
        receiveSocket.joinGroup(group);
        new Client(sendSocket, receiveSocket, group, new UserInterface()).start();
    }

}
