package tcpip.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.util.HashSet;

/**
 * @author Bartosz Śledź
 */
final class ClientUserInterface extends JFrame {

    private JTextField sendTextArea, addressTextField, portTextField, nickTextField;
    private JTextArea messagesTextArea, usersOnlineTextArea;
    private JButton connectBtn, sendBtn;
    private JLabel connectionStatus;

    ClientUserInterface() {
        initGuiElements();
    }

    /**
     * Inicializuje elementy UserInterface.
     */
    private void initGuiElements() {
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 570, 450);
        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        sendTextArea = new JTextField();
        sendTextArea.setBounds(10, 370, 386, 39);
        contentPane.add(sendTextArea);
        sendTextArea.setColumns(10);

        addressTextField = new JTextField();
        addressTextField.setText("127.0.0.1");
        addressTextField.setBounds(466, 8, 89, 20);
        contentPane.add(addressTextField);
        addressTextField.setColumns(10);

        portTextField = new JTextField();
        portTextField.setText("9000");
        portTextField.setColumns(10);
        portTextField.setBounds(466, 39, 89, 20);
        contentPane.add(portTextField);

        messagesTextArea = new JTextArea();
        messagesTextArea.setEditable(false);
        messagesTextArea.setBounds(10, 11, 386, 348);
        contentPane.add(messagesTextArea);

        usersOnlineTextArea = new JTextArea();
        usersOnlineTextArea.setBounds(406, 138, 149, 220);
        usersOnlineTextArea.setEditable(false);
        contentPane.add(usersOnlineTextArea);

        sendBtn = new JButton("Send");
        sendBtn.setBounds(406, 370, 149, 39);
        sendBtn.setEnabled(false);
        contentPane.add(sendBtn);

        final JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(406, 11, 56, 14);
        contentPane.add(addressLabel);

        final JLabel portTextLabel = new JLabel("Port:");
        portTextLabel.setBounds(406, 42, 56, 14);
        contentPane.add(portTextLabel);

        connectBtn = new JButton("Connect");
        connectBtn.setBounds(466, 101, 89, 23);
        contentPane.add(connectBtn);

        final JSeparator separator = new JSeparator();
        separator.setBounds(406, 131, 149, 20);
        contentPane.add(separator);

        nickTextField = new JTextField();
        nickTextField.setColumns(10);
        nickTextField.setBounds(466, 70, 89, 20);
        contentPane.add(nickTextField);

        final JLabel nickLabel = new JLabel("Nick:");
        nickLabel.setBounds(406, 73, 56, 14);
        contentPane.add(nickLabel);

        connectionStatus = new JLabel("");
        connectionStatus.setBounds(406, 105, 46, 14);
        contentPane.add(connectionStatus);

        this.setVisible(true);
    }

    /**
     * Adds listener for send button.
     *
     * @param actionListener {@link ActionListener}
     */
    void addSendButtonActionListener(final ActionListener actionListener) {
        sendBtn.addActionListener(actionListener);
    }

    /**
     * Adds listener for connect button.
     *
     * @param actionListener {@link ActionListener}
     */
    void addConnectButtonActionListener(final ActionListener actionListener) {
        connectBtn.addActionListener(actionListener);
    }

    /**
     * Adds listener for send text area (enter key).
     *
     * @param actionListener {@link ActionListener}
     */
    void addSendTextAreaActionListener(final ActionListener actionListener) {
        sendTextArea.addActionListener(actionListener);
    }

    /**
     * Sets message on text area.
     *
     * @param sendText {@link String}
     */
    void setSendText(final String sendText) {
        sendTextArea.setText(sendText);
    }

    /**
     * Returns the text to send.
     *
     * @return {@link String}
     */
    String getSendText() {
        return sendTextArea.getText();
    }

    /**
     * Returns the address needed to connect.
     *
     * @return {@link String}
     */
    String getServerAddress() {
        return addressTextField.getText();
    }

    /**
     * Returns the port needed to connect.
     *
     * @return {@link int}
     */
    int getServerPort() throws NumberFormatException {
        return Integer.parseInt(portTextField.getText());
    }

    /**
     * Displays received messages from others users.
     *
     * @param message {@link String}
     */
    void displayMessage(final String message) {
        messagesTextArea.append(message);
    }

    /**
     * Returns the current username.
     *
     * @return {@link String}
     */
    String getUsername() {
        return nickTextField.getText();
    }

    /**
     * Blocks elements after success connection.
     */
    void afterConnect() {
        nickTextField.setEnabled(false);
        addressTextField.setEnabled(false);
        portTextField.setEnabled(false);
        connectBtn.setEnabled(false);
        sendBtn.setEnabled(true);
        setConnectionStatus("OK");
    }

    /**
     * Sets connection status on status label.
     *
     * @param status {@link String}
     */
    private void setConnectionStatus(final String status) {
        connectionStatus.setText(status);
    }

    /**
     * Checks if all the required fields are required to establish a connection.
     *
     * @return {@link Boolean}
     */
    boolean canConnect() {
        return !nickTextField.getText().equals("") && !portTextField.getText().equals("") && !addressTextField.getText().equals("");
    }

    /**
     * Displays all users who are online.
     *
     * @param usersSet {@link HashSet}
     */
    void setOnlineUsers(final HashSet<String> usersSet) {
        usersOnlineTextArea.setText("");
        for (String username : usersSet) {
            usersOnlineTextArea.append(username + "\n");
        }
    }

    /**
     * Displays a window with information.
     *
     * @param info {@link String}
     */
    void showInfo(final String info) {
        JOptionPane.showMessageDialog(
                this,
                info,
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

}
