package multicasting;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bartosz Śledź
 */
public class Client {

    private static final int SEND_PORT = 4444;
    private static final int RECEIVE_PORT = 4446;
    private static final String ADDRESS = "230.0.0.0";
    private static final String JOINED = "joined";
    private static final String LEFT = "left";

    private final UserInterface ui;

    public Client(final UserInterface ui) {
        this.ui = ui;
        sendButtonListener();
        sendTextAreaListener();
        addExitListener();
        receiveMessages();
        sendMessage(JOINED);
    }

    /**
     * Receive messages from server.
     */
    public void receiveMessage() {
        try {
            final InetAddress group = InetAddress.getByName(ADDRESS);
            final MulticastSocket socket = new MulticastSocket(RECEIVE_PORT);
            socket.setTimeToLive(0);
            socket.joinGroup(group);

            while (true) {
                final byte[] buffer = new byte[20000];
                final DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, RECEIVE_PORT);
                final String message;
                try {
                    socket.receive(datagram);
                    message = new String(buffer, 0, datagram.getLength(), "UTF-8");
                    parseMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send messages to the server.
     *
     * @param message {@link String}
     */
    public void sendMessage(final String message) {
        try {
            final DatagramSocket socket = new DatagramSocket();
            final String msg = String.format("%s: %s", ui.getUsername(), message);
            final byte[] buffer = msg.getBytes();
            final DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ADDRESS), SEND_PORT);
            socket.send(datagram);
            socket.close();
            ui.setSendText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds listener for send button.
     */
    private void sendButtonListener() {
        ui.addSendButtonActionListener(e -> sendMessage(ui.getSendText()));
    }

    /**
     * Adds listener for send text area (enter key).
     */
    private void sendTextAreaListener() {
        ui.addSendTextAreaActionListener(e -> sendMessage(ui.getSendText()));
    }

    /**
     * Adds listener for exit user.
     */
    private void addExitListener() {
        ui.addExitListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                sendMessage(LEFT);
                System.exit(1);
            }
        });
    }

    /**
     * Create a new connection in new thread.
     */
    private void receiveMessages() {
        new Thread(this::receiveMessage).start();
    }

    /**
     * Prepares the received message from the tcpip.server for display.
     *
     * @param line {@link String}
     */
    private void parseMessage(final String line) {
        final Matcher users = Pattern.compile("\\[(.*?)]").matcher(line);

        if (users.find()) {
            ui.setOnlineUsers(new HashSet<>(Arrays.asList(users.group(1).split(", "))));
        }

        final Matcher text = Pattern.compile("(.*?)\\[").matcher(line);

        if (text.find()) {
            ui.displayMessage(text.group(1) + "\n");
        }
    }

    /**
     * Check username exist in online list.
     *
     * @param users {@link HashSet}
     * @return {@link boolean}
     */
    private boolean userExist(final HashSet<String> users) {
        final String username = ui.getUsername();

        if (username != null) {
            if (users.contains(username)) {
                return true;
            }
        }

        return false;
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> new Client(new UserInterface()));
    }

}
