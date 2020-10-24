package com.hungle.jacktrip.jackfruit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.hungle.jacktrip.jackfruit.eventbus.AbstractEbEvent;
import com.jcraft.jsch.JSchException;

public class JackFruitMain extends JFrame implements WindowListener, JackFruitEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(JackFruitMain.class);

//    private static final String DEFAULT_JACKTRIP_DEVICE_HOSTNAME = "192.168.1.90";
    static final String DEFAULT_JACKTRIP_DEVICE_HOSTNAME = "jacktrip.local";

    static final String DEFAULT_PASSWORD = "jacktrip";

    static final String DEFAULT_USERNAME = "pi";

    public static final String DEFAULT_LOOPBACK_TEST_SERVER = "13.52.186.20";

    private final Command[] commands;

    private final class MyAuthenticator implements Authenticator {
        private ExecutorService executor = Executors.newSingleThreadExecutor();

        @Override
        public ConnectionResult authenticate(String hostName, String userName, String password) {
            Future<ConnectionResult> future = authenticateUsingFuture(hostName, userName, password);

            while (!future.isDone()) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    LOGGER.warn(e);
                }
            }

            ConnectionResult result = null;
            try {
                result = future.get();
            } catch (InterruptedException e) {
                LOGGER.warn(e);
            } catch (ExecutionException e) {
                LOGGER.warn(e);
            }
            return result;
        }

        public Future<ConnectionResult> authenticateUsingFuture(String hostName, String userName, String password) {
            return executor.submit(() -> {
                JSchException exception = null;
                try {
                    LOGGER.info("> Connecting ...");
                    jackTripConnection = new JackTripConnection(hostName, userName, password);
                    LOGGER.info("< Connected.");
                } catch (JSchException e) {
                    exception = e;
                    LOGGER.error(e, e);
                }

                return new ConnectionResult(exception == null, exception);
            });
        }
    }

    public enum ConnectionState {
        UNKNOWN, DISCONNECTED, CONNECTING, CONNECTED,
    }

    public class JackfruitConnectionEvent extends AbstractEbEvent {
        private ConnectionState connectionState;

        public JackfruitConnectionEvent(ConnectionState connectionState) {
            super();
            this.connectionState = connectionState;
        }

        @Override
        public String toString() {
            return "JackfruitEvent [connectionState=" + connectionState + "]";
        }
    }

    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private JMenuItem connectionMenuItem;
    private JackTripConnection jackTripConnection;

    private LoginDialog loginDialog;
    private Authenticator authenticator;

    private ExecutorService eventExecutor = Executors.newCachedThreadPool();

    private JMenu commandMenu;
    private RSyntaxTextArea textArea;

    private boolean appIsReady = false;
    private JackfruitEventListener jackfruitEventListener;
    private boolean autoLogin = true;
    private JLabel statusLabel;
    private JTextField commandTf;
    private JButton commandButton;

    private DefaultComboBoxModel<Command> commandsModel;

    private JComboBox<Command> commandsComboBox;

    static final String JACKFRUIT_TEST_SERVER = "jackfruit.testServer";

    public JackFruitMain(String title) throws HeadlessException {
        super(title);

        this.commands = createCommands();

        this.jackfruitEventListener = new JackfruitEventListener(JackFruitMain.this);
        this.jackfruitEventListener.register();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        buildMenuBar(menuBar);

        buildMainView(getContentPane());

        this.authenticator = new MyAuthenticator();

        addWindowListener(this);
    }

    protected Command[] createCommands() {
        return Commands.createCommands();
    }

    private void buildMenuBar(JMenuBar menuBar) {
        addFileMenu(menuBar);

//        addCommandMenu(menuBar);

        connectionStateChanged(ConnectionState.DISCONNECTED);

//        setConnectionMenuItemText(connectionState);
    }

    private void connectionStateChanged(final ConnectionState newState) {

        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                connectionState = newState;

                JackfruitConnectionEvent event = new JackfruitConnectionEvent(newState);
                LOGGER.info("EVENT, POST event=" + event);
                event.post();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateConnectionUi();
                    }
                });
            }
        });
    }

    private void addCommandMenu(JMenuBar menuBar) {
        JMenu menu = new JMenu("Command");
        menuBar.add(menu);
        this.commandMenu = menu;
        this.commandMenu.setEnabled(false);

        JMenuItem menuItem = null;

        for (Command command : commands) {
            menuItem = new JMenuItem(new JackTripCommand(command.getLabel(), command.getCommand()) {

                @Override
                public void runCommand(String command) {
                    JackFruitMain.this.runCommand(command);
                }

            });
            menu.add(menuItem);
        }
    }

    private void addFileMenu(JMenuBar menuBar) {
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem menuItem = null;

        // Connect/Disconnect menu item
        menuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doConnectionCallback();
            }
        });
        menu.add(menuItem);
        this.connectionMenuItem = menuItem;

        menu.addSeparator();

        menuItem = new JMenuItem(new AbstractAction("Exit") {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItem);
    }

    protected void doConnectionCallback() {
        switch (this.connectionState) {
        case UNKNOWN:
            doConnectWithDialog();
            break;
        case CONNECTED:
            doDisconnect();
            break;
        case CONNECTING:
            doDisconnect();
            break;
        case DISCONNECTED:
            doConnectWithDialog();
            break;
        }
    }

    private void doDisconnect() {
        if (jackTripConnection == null) {
            LOGGER.warn("jackTripConnection is null.");
            return;
        }

        try {
            jackTripConnection.close();
        } finally {
            jackTripConnection = null;

            connectionStateChanged(ConnectionState.DISCONNECTED);
//
//            this.connectionState = ConnectionState.DISCONNECTED;
//            updateConnectionUi();
        }
    }

    private void doConnectWithDialog() {
        loginDialog.showDialog();

        // if logon successfully
        if (loginDialog.isSucceeded()) {
            connectionStateChanged(ConnectionState.CONNECTED);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    // clear text
                    textArea.setText("");
                    Object selectedItem = commandsComboBox.getSelectedItem();
                    if (selectedItem == null) {
                        commandsComboBox.setSelectedIndex(0);
                    }
                }
            });
//          this.connectionState = ConnectionState.CONNECTED;
//            updateConnectionUi();
        } else {
            connectionStateChanged(ConnectionState.DISCONNECTED);
//            this.connectionState = ConnectionState.DISCONNECTED;
//            updateConnectionUi();
        }

    }

    private void updateConnectionUi() {
        setConnectionMenuItemText(this.connectionState);
        if (this.statusLabel != null) {
            this.statusLabel.setText(this.connectionState.toString());
        }
    }

    private void setConnectionMenuItemText(ConnectionState connectionState) {
        switch (connectionState) {
        case UNKNOWN:
            this.connectionMenuItem.setText("Connect");
            if (this.commandMenu != null) {
                this.commandMenu.setEnabled(false);
            }
            if (this.commandButton != null) {
                this.commandButton.setEnabled(false);
            }
            break;
        case CONNECTED:
            this.connectionMenuItem.setText("Disconnect");
            if (this.commandMenu != null) {
                this.commandMenu.setEnabled(true);
            }
            if (this.commandButton != null) {
                this.commandButton.setEnabled(true);
            }
            break;
        case CONNECTING:
            this.connectionMenuItem.setText("Disconnect");
            if (this.commandMenu != null) {
                this.commandMenu.setEnabled(true);
            }
            if (this.commandButton != null) {
                this.commandButton.setEnabled(true);
            }
            break;
        case DISCONNECTED:
            this.connectionMenuItem.setText("Connect");
            if (this.commandMenu != null) {
                this.commandMenu.setEnabled(false);
            }
            if (this.commandButton != null) {
                this.commandButton.setEnabled(false);
            }
            break;
        }
    }

    private void buildMainView(Container parent) {
        Border border = null;

        JPanel view = new JPanel();
        Dimension preferredSize = new Dimension(600, 400);

        view.setPreferredSize(preferredSize);
        view.setLayout(new BorderLayout());

        border = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        view.setBorder(border);

        parent.add(view);

        JPanel commandView = new JPanel();
        commandView.setLayout(new BoxLayout(commandView, BoxLayout.LINE_AXIS));
        border = BorderFactory.createTitledBorder("Command");
        commandView.setBorder(border);

        view.add(commandView, BorderLayout.NORTH);
        commandsModel = new DefaultComboBoxModel<>();
        commandsModel.addAll(Arrays.asList(commands));
        commandsComboBox = new JComboBox<>(commandsModel);
        commandsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Command command = (Command) commandsComboBox.getSelectedItem();
                LOGGER.info("COMMAND - SELECTED - " + command.getCommand());
                commandTf.setText(command.getCommand());
                if (connectionState == ConnectionState.CONNECTED) {
                    commandButton.setEnabled(true);
                }
                String comment = command.getComment();
                textArea.setText("");
                if ((comment != null) && (comment.length() > 0)) {
                    textArea.append(comment);
                }
            }
        });
        commandView.add(commandsComboBox);

        this.commandTf = new JTextField(20);
        commandView.add(commandTf);

        this.commandButton = new JButton(new AbstractAction("Run") {

            @Override
            public void actionPerformed(ActionEvent e) {
                String command = commandTf.getText();
                if (command != null) {
                    command = command.trim();
                }
                if ((command != null) && (command.length() > 0)) {
                    runCommand(command);
                }
            }
        });
        commandButton.setEnabled(false);
        commandView.add(commandButton);

        textArea = new RSyntaxTextArea();

        // textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        // accountJsonTextArea = textArea;
        textArea.setEditable(false);
        textArea.setLineWrap(false);

        view.add(sp, BorderLayout.CENTER);

        JPanel statusView = new JPanel();
        statusView.setLayout(new BoxLayout(statusView, BoxLayout.LINE_AXIS));
        this.statusLabel = new JLabel(this.connectionState.toString());
        statusView.add(Box.createHorizontalGlue());
        statusView.add(this.statusLabel);
        view.add(statusView, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        LOGGER.info("> main");

        String title = "jackfruit";
        final JackFruitMain main = new JackFruitMain(title);
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                main.showMainFrame();
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    protected void showMainFrame() {
//        setLocationRelativeTo(null);
        setLocation(200, 200);
        pack();
        setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosing(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowActivated(WindowEvent e) {
        LOGGER.info("> windowActivated, e=" + e.toString());

        if (!appIsReady) {
            appIsReady = true;
            if (this.loginDialog == null) {
                Frame frame = JackFruitMain.this;
                String hostName = System.getProperty("jackfruit.hostName", DEFAULT_JACKTRIP_DEVICE_HOSTNAME);
                String userName = System.getProperty("jackfruit.userName", DEFAULT_USERNAME);
                String password = System.getProperty("jackfruit.password", DEFAULT_PASSWORD);

                LOGGER.info("hostName=" + hostName);
                this.loginDialog = new LoginDialog(frame, hostName, userName, password, authenticator);
            }

            if (autoLogin) {
                autoLogin();
            }
        }
    }

    private void autoLogin() {
        if (!isConnected()) {
            textArea.setText("");
            textArea.append("Trying to connect to my jacktrip device" + " (" + this.loginDialog.getHostName() + ")"
                    + ". Please wait ...");

            final Component root = SwingUtilities.getRoot(JackFruitMain.this);

            root.setEnabled(false);
            root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            Runnable doRun = new Runnable() {

                @Override
                public void run() {
                    ConnectionResult connectionResult = null;
                    try {
                        String hostName = loginDialog.getHostName();
                        String userName = loginDialog.getUserName();
                        String password = loginDialog.getPassword();
                        connectionResult = authenticator.authenticate(hostName, userName, password);
                    } finally {
                        root.setEnabled(true);
                        root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }

                    if ((connectionResult != null) && (connectionResult.isConnected())) {
                        JOptionPane.showMessageDialog(root, "You have successfully logged in.", "Login",
                                JOptionPane.INFORMATION_MESSAGE);

                        textArea.setText("");
                        Object selectedItem = commandsComboBox.getSelectedItem();
                        if (selectedItem == null) {
                            commandsComboBox.setSelectedIndex(0);
                        }

                        connectionStateChanged(ConnectionState.CONNECTED);
                    } else {
                        textArea.setText("");
                        textArea.append("Error: " + connectionResult.getException().getMessage());

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                doConnectWithDialog();
                            }
                        });
                    }
                }
            };

            SwingUtilities.invokeLater(doRun);
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    protected boolean isConnected() {
        switch (connectionState) {
        case CONNECTED:
            return true;
        case CONNECTING:
            return false;
        case DISCONNECTED:
            return false;
        case UNKNOWN:
            return false;
        }
        return false;
    }

    private void runCommand(final String command) {
        textArea.setText("Running command=" + command + " ...\n");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    String result = jackTripConnection.runCommand(command);
                    textArea.append("\n");
                    textArea.append(result);
                } catch (JSchException e) {
                    LOGGER.info(e, e);
                    textArea.append("\n");
                    textArea.append(e.getMessage());
                } catch (IOException e) {
                    LOGGER.info(e, e);
                    textArea.append("\n");
                    textArea.append(e.getMessage());
                }
            }
        });
    }

    static String getLoopbackTestServer() {
        return System.getProperty(JACKFRUIT_TEST_SERVER, DEFAULT_LOOPBACK_TEST_SERVER);
    }

}
