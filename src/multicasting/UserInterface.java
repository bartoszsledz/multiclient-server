package multicasting;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Bartosz Śledź
 */
public final class UserInterface extends JFrame {

    private JPanel contentPane;
    private JTextField sendTextArea, nickTextField;
    private JTextArea messagesTextArea, usersOnlineTextArea;
    private JButton sendBtn;
    private JLabel nickLabel;
    private JSeparator separator;

    public UserInterface() {
        initGuiElements();
        showUsernameMessageDialog();
    }

    /**
     * Inicializuje elementy UserInterface.
     */
    private void initGuiElements() {
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 570, 450);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        sendTextArea = new JTextField();
        sendTextArea.setBounds(10, 370, 386, 39);
        contentPane.add(sendTextArea);
        sendTextArea.setColumns(10);

        messagesTextArea = new JTextArea();
        messagesTextArea.setEditable(false);
        messagesTextArea.setBounds(10, 11, 386, 348);
        contentPane.add(messagesTextArea);

        usersOnlineTextArea = new JTextArea();
        usersOnlineTextArea.setBounds(406, 60, 149, 300);
        usersOnlineTextArea.setEditable(false);
        contentPane.add(usersOnlineTextArea);

        sendBtn = new JButton("Send");
        sendBtn.setBounds(406, 370, 149, 39);
        contentPane.add(sendBtn);

        separator = new JSeparator();
        separator.setBounds(406, 50, 149, 20);
        contentPane.add(separator);

        nickTextField = new JTextField();
        nickTextField.setColumns(10);
        nickTextField.setEditable(false);
        nickTextField.setBounds(466, 8, 89, 20);
        contentPane.add(nickTextField);

        nickLabel = new JLabel("Nick:");
        nickLabel.setBounds(406, 11, 56, 14);
        contentPane.add(nickLabel);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Adds listener for send button.
     *
     * @param actionListener {@link ActionListener}
     */
    public void addSendButtonActionListener(final ActionListener actionListener) {
        sendBtn.addActionListener(actionListener);
    }

    /**
     * Adds listener for send text area (enter key).
     *
     * @param actionListener {@link ActionListener}
     */
    public void addSendTextAreaActionListener(final ActionListener actionListener) {
        sendTextArea.addActionListener(actionListener);
    }

    /**
     * Adds listener for exit app.
     *
     * @param windowAdapter {@link WindowAdapter}
     */
    public void addExitListener(final WindowAdapter windowAdapter) {
        this.addWindowListener(windowAdapter);
    }

    /**
     * Returns the text to send.
     *
     * @return {@link String}
     */
    public String getSendText() {
        return sendTextArea.getText();
    }

    /**
     * Sets message on text area.
     *
     * @param sendText {@link String}
     */
    public void setSendText(final String sendText) {
        sendTextArea.setText(sendText);
    }

    /**
     * Displays received messages from others users.
     *
     * @param message {@link String}
     */
    public void displayMessage(final String message) {
        messagesTextArea.append(message);
    }

    /**
     * Returns the current username.
     *
     * @return {@link String}
     */
    public String getUsername() {
        return nickTextField.getText();
    }

    /**
     * Displays all users who are online.
     *
     * @param usersSet {@link HashSet}
     */
    public void setOnlineUsers(final HashSet<String> usersSet) {
        usersOnlineTextArea.setText("");
        for (String username : usersSet) {
            usersOnlineTextArea.append(username + "\n");
        }
    }

    public void showUsernameMessageDialog() {
        final String nick = JOptionPane.showInputDialog(
                this,
                "Podaj nick:",
                "Info",
                JOptionPane.PLAIN_MESSAGE);

        if (nick == null) {
            System.exit(1);
        } else {
            nickTextField.setText(nick);
        }
    }

    /**
     * Displays a window with information.
     *
     * @param info {@link String}
     */
    public void showInfo(final String info) {
        JOptionPane.showMessageDialog(
                this,
                info,
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

}
