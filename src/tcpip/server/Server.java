package tcpip.server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Bartosz Śledź
 */
public final class Server extends Thread {

    private static final int PORT = 9000;
    private static final int MAX_USERS = 10;
    private static final String CONNECT = "CONNECT";
    private static final String ONLINE = "ONLINE";
    private static final String MESSAGE = "MESSAGE";
    private static final String ERROR = "ERROR";
    private static final HashSet<String> USERS_NAMES = new HashSet<>();
    private static final HashSet<PrintWriter> WRITERS = new HashSet<>();

    private final Socket socket;
    private String username;
    private boolean error = false;
    private PrintWriter out;

    private Server(final Socket socket) {
        this.socket = socket;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        try {

            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                out.println(CONNECT);
                username = in.readLine();
                if (username == null) {
                    return;
                }
                synchronized (USERS_NAMES) {
                    if (!USERS_NAMES.contains(username)) {
                        if (USERS_NAMES.size() < MAX_USERS) {
                            USERS_NAMES.add(username);
                            break;
                        } else {
                            error = true;
                            sendErrorMessageToOne("Limit of online users on the server has been reached: " + MAX_USERS);
                            return;
                        }
                    } else {
                        error = true;
                        sendErrorMessageToOne("Login is already used!");
                        return;
                    }
                }
            }

            WRITERS.add(out);
            //System.out.println(String.format("%s jest online", username));
            sendMessageToAllOnline(ONLINE, null);

            while (true) {
                String input = in.readLine();
                if (input == null) {
                    return;
                }
                //System.out.println(String.format("%s: %s", username, input));
                sendMessageToAllOnline(MESSAGE, input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (username != null && !error) {
                USERS_NAMES.remove(username);
            }
            if (out != null) {
                WRITERS.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Sends the appropriate message type to all online users.
     *
     * @param messageType {@link String}
     * @param message     {@link String}
     */
    private void sendMessageToAllOnline(final String messageType, final String message) {
        for (PrintWriter writer : WRITERS) {
            switch (messageType) {
                case MESSAGE:
                    writer.println(String.format("%s %s: %s %s", MESSAGE, username, message, Arrays.toString(USERS_NAMES.toArray(new String[USERS_NAMES.size()]))));
                    break;
                case ONLINE:
                    writer.println(String.format("%s %s", messageType, Arrays.toString(USERS_NAMES.toArray(new String[USERS_NAMES.size()]))));
                    break;
            }
        }
    }

    /**
     * Sends the error message type to user.
     *
     * @param message {@link String}
     */
    private void sendErrorMessageToOne(final String message) {
        out.println(String.format("%s:%s", ERROR, message));
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Start Server.");
        ServerSocket serverSocket = new ServerSocket(PORT);
        try {
            while (true) {
                new Server(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }
}