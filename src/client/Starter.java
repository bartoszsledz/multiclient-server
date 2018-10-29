package client;

import javax.swing.*;

/**
 * @author Bartosz Śledź
 */
public class Starter {

   public static void main(final String[] args) {
       SwingUtilities.invokeLater(() -> new Client(new ClientUserInterface()));
    }
}