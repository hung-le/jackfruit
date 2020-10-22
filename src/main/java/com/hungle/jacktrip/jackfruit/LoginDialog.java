package com.hungle.jacktrip.jackfruit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class LoginDialog extends JDialog {

//    private static final String DEFAULT_HOSTNAME = JackFruitMain.DEFAULT_JACKTRIP_DEVICE_HOSTNAME;

    private JTextField tfHostName;

    private JTextField tfUserName;

    private JPasswordField tfPassword;

    private JButton btnLogin;
    private JButton btnCancel;

    private boolean succeeded;

    private Authenticator authenticator;

    private final String hostName;

    private final String userName;

    private final String password;

    public LoginDialog(Frame parent, String hostName, String userName, String password, Authenticator authenticator) {
        super(parent, "Login", true);

        this.hostName = hostName;
        this.userName = userName;
        this.password = password;

        this.authenticator = authenticator;

        build();

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void build() {
        JPanel mainView = buildMainView();

        JPanel commandView = buildCommandView();

        getContentPane().add(mainView, BorderLayout.CENTER);
        getContentPane().add(commandView, BorderLayout.PAGE_END);
    }

    private JPanel buildCommandView() {
        btnLogin = new JButton("Login");

        ActionListener loginAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final Component root = SwingUtilities.getRoot(getParent());

                root.setEnabled(false);
                root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                ConnectionResult connectionResult = null;
                try {
                    connectionResult = authenticator.authenticate(getHostName(), getUserName(), getPassword());
                } finally {
                    root.setEnabled(true);
                    root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                if ((connectionResult != null) && (connectionResult.isConnected())) {
                    succeeded = true;

                    JOptionPane.showMessageDialog(root, "You have successfully logged in.", "Login",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    succeeded = false;

                    JOptionPane.showMessageDialog(root,
                            "Failed to connect. Error: " + connectionResult.getException().getMessage(), "Login",
                            JOptionPane.ERROR_MESSAGE);
                    // reset username and password
//                    tfUserName.setText("");
//                    tfPassword.setText("");
                }
            }
        };
        btnLogin.addActionListener(loginAction);

        btnCancel = new JButton("Cancel");
        ActionListener cancelAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        btnCancel.addActionListener(cancelAction);

        JPanel view = new JPanel();
        view.add(btnLogin);
        view.add(btnCancel);
        return view;
    }

    private JPanel buildMainView() {
        JPanel view = new JPanel(new GridBagLayout());

        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        JLabel label;
        JTextField textField;

        label = new JLabel("Hostname: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        view.add(label, cs);

        textField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        view.add(textField, cs);
        tfHostName = textField;
        tfHostName.setText(this.hostName);

        label = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        view.add(label, cs);

        textField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        view.add(textField, cs);
        tfUserName = textField;
        tfUserName.setText(this.userName);

        label = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        view.add(label, cs);

        JPasswordField pwField = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        view.add(pwField, cs);
        view.setBorder(new LineBorder(Color.GRAY));
        tfPassword = pwField;
        tfPassword.setText(this.password);

        return view;
    }

    public String getUserName() {
        return tfUserName.getText().trim();
    }

    public String getPassword() {
        return new String(tfPassword.getPassword());
    }

    public String getHostName() {
        return tfHostName.getText().trim();
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void showDialog() {
        setVisible(true);
    }
}
