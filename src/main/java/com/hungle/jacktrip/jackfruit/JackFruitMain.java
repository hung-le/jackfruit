package com.hungle.jacktrip.jackfruit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.jcraft.jsch.JSchException;

public class JackFruitMain extends JFrame {
    private static final Logger LOGGER = LogManager.getLogger(JackFruitMain.class);

    private enum ConnectionState {
        UNKNOWN, DISCONNECTED, CONNECTING, CONNECTED,
    }

    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private JMenuItem connectionMenuItem;
    private JackTripConnection jackTripConnection;

    private LoginDialog loginDialog;
    private Authenticator authenticator;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private JMenu commandMenu;
    private RSyntaxTextArea textArea;

    public JackFruitMain(String title) throws HeadlessException {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        buildMenuBar(menuBar);

        buildMainView(getContentPane());

        this.authenticator = new Authenticator() {

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
        };
    }

    private void buildMenuBar(JMenuBar menuBar) {
        addFileMenu(menuBar);

        addCommandMenu(menuBar);

        setConnectionMenuItemText(connectionState);
    }

    private void addCommandMenu(JMenuBar menuBar) {
        JMenu menu = new JMenu("Command");
        menuBar.add(menu);
        this.commandMenu = menu;
        this.commandMenu.setEnabled(false);

        JMenuItem menuItem = null;

//      System.out.println(jacktrip.runCommand("/usr/sbin/ifconfig -a"));
        menuItem = new JMenuItem(new AbstractAction("Get interface info") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String command = "/usr/sbin/ifconfig -a";
                    textArea.setText("");
                    String result = jackTripConnection.runCommand(command);
                    textArea.append(result);
                } catch (JSchException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                } catch (IOException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                }
            }
        });
        menu.add(menuItem);

//      System.out.println(jacktrip.runCommand("/usr/bin/netstat -rn"));
        menuItem = new JMenuItem(new AbstractAction("Get routing info") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String command = "/usr/bin/netstat -rn";
                    textArea.setText("");
                    String result = jackTripConnection.runCommand(command);
                    textArea.append(result);
                } catch (JSchException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                } catch (IOException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                }
            }
        });
        menu.add(menuItem);

//      System.out.println(jacktrip.runCommand("/usr/bin/ping -c 3 192.168.1.254"));
        menuItem = new JMenuItem(new AbstractAction("Ping gateway") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String command = "/usr/bin/ping -c 3 192.168.1.254";
                    textArea.setText("");
                    String result = jackTripConnection.runCommand(command);
                    textArea.append(result);
                } catch (JSchException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                } catch (IOException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                }
            }
        });
        menu.add(menuItem);

        // // loopback: 54.193.29.161
//      System.out.println(jacktrip.runCommand("/usr/bin/ping -c 3 54.193.29.161"));
        menuItem = new JMenuItem(new AbstractAction("Ping loopback test server") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String command = "/usr/bin/ping -c 3 54.193.29.161";
                    textArea.setText("");
                    String result = jackTripConnection.runCommand(command);
                    textArea.append(result);
                } catch (JSchException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                } catch (IOException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                }
            }
        });
        menu.add(menuItem);

        // System.out.println(jacktrip.runCommand("/usr/bin/ps -ef | grep jack"));
        menuItem = new JMenuItem(new AbstractAction("Get process info") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String command = "/usr/bin/ps -ef | grep jack";
                    textArea.setText("");

                    String result = jackTripConnection.runCommand(command);
                    textArea.append(result);
                } catch (JSchException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                } catch (IOException e1) {
                    LOGGER.info(e1, e1);
                    textArea.append(e1.getMessage());
                }
            }
        });
        menu.add(menuItem);

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
            doConnect();
            break;
        case CONNECTED:
            doDisconnect();
            break;
        case CONNECTING:
            doDisconnect();
            break;
        case DISCONNECTED:
            doConnect();
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

            this.connectionState = ConnectionState.DISCONNECTED;
            updateConnectionUi();
        }
    }

    private void doConnect() {
        if (this.loginDialog == null) {
            Frame frame = JackFruitMain.this;
            this.loginDialog = new LoginDialog(frame, authenticator);
        }

        loginDialog.showDialog();

        // if logon successfully
        if (loginDialog.isSucceeded()) {
            this.connectionState = ConnectionState.CONNECTED;
            updateConnectionUi();
        } else {
            this.connectionState = ConnectionState.DISCONNECTED;
            updateConnectionUi();
        }

    }

    private void updateConnectionUi() {
        setConnectionMenuItemText(this.connectionState);
    }

    private void setConnectionMenuItemText(ConnectionState connectionState) {
        switch (connectionState) {
        case UNKNOWN:
            this.connectionMenuItem.setText("Connect");
            this.commandMenu.setEnabled(false);
            break;
        case CONNECTED:
            this.connectionMenuItem.setText("Disconnect");
            this.commandMenu.setEnabled(true);
            break;
        case CONNECTING:
            this.connectionMenuItem.setText("Disconnect");
            this.commandMenu.setEnabled(true);
            break;
        case DISCONNECTED:
            this.connectionMenuItem.setText("Connect");
            this.commandMenu.setEnabled(false);
            break;
        }
    }

    private void buildMainView(Container contentPane) {

        JPanel view = new JPanel();
        Dimension preferredSize = new Dimension(600, 400);

        view.setPreferredSize(preferredSize);
        view.setLayout(new BorderLayout());

        textArea = new RSyntaxTextArea();

        // textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        // accountJsonTextArea = textArea;
        textArea.setEditable(false);
        textArea.setLineWrap(false);

        view.add(sp, BorderLayout.CENTER);

        contentPane.add(view);
    }

    public static void main(String[] args) {
        LOGGER.info("> main");

        final JackFruitMain main = new JackFruitMain("jackfruit");
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

}
