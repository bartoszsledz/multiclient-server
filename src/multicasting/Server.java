package multicasting;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bartosz Śledź
 */
public class Server {

    private static final HashSet<String> USERS_NAMES = new HashSet<>();
    private static final int MAX_USERS = 10;
    private byte[] buf = new byte[20000];
    private DatagramPacket dp = new DatagramPacket(buf, buf.length);
    private static final int SEND_PORT = 4446;
    private static final int RECEIVE_PORT = 4444;
    private static final String ADDRESS = "230.0.0.0";

    public static void main(String args[]) throws Exception {
        new Server().start();
    }

    public void start() {
        receiveMessages();
    }

    /**
     * Receive messages from clients.
     */
    private void receiveMessages() {
        try {
            final MulticastSocket ms = new MulticastSocket(RECEIVE_PORT);
            ms.joinGroup(InetAddress.getByName(ADDRESS));
            while (true) {
                ms.receive(dp);
                final String msg = new String(dp.getData(), 0, dp.getLength(), "UTF-8");

                if (userLeft(msg)) {
                    deleteUsername(msg);
                    sendMessage(msg);
                    continue;
                }

                if (USERS_NAMES.size() < MAX_USERS) {
                    saveUsername(msg);
                    sendMessage(msg);
                    System.out.println(msg);
                }

            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Delete username from online lsit.
     *
     * @param msg {@link String}
     */
    private void deleteUsername(final String msg) {
        final String username = getUsername(msg);

        if (username != null) {
            USERS_NAMES.remove(username);
        }
    }

    /**
     * Check user left from server.
     *
     * @param msg {@link String}
     * @return {@link boolean}
     */
    private boolean userLeft(final String msg) {
        final Pattern pattern = Pattern.compile("(.*?): left");
        final Matcher matcher = pattern.matcher(msg);

        return matcher.find();
    }

    /**
     * Send messages to clients.
     *
     * @param msg {@link String}
     */
    public void sendMessage(final String msg) {
        try {
            final MulticastSocket ms = new MulticastSocket(SEND_PORT);
            ms.joinGroup(InetAddress.getByName(ADDRESS));
            final byte[] buffer = String.format("%s%s", msg, USERS_NAMES).getBytes();
            final DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ADDRESS), SEND_PORT);
            ms.send(datagram);
            ms.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save new user.
     *
     * @param msg {@link String}
     */
    private void saveUsername(final String msg) {
        final String username = getUsername(msg);

        if (username != null) {
            USERS_NAMES.add(username);
        }
    }

    /**
     * Get username from user message.
     *
     * @param msg {@link String}
     * @return {@link String}
     */
    private String getUsername(final String msg) {
        final Pattern pattern = Pattern.compile("(.*?):");
        final Matcher matcher = pattern.matcher(msg);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

}