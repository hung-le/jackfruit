package com.hungle.jacktrip.jackfruit;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JackTripConnection implements Closeable {
    private static final Logger LOGGER = LogManager.getLogger(JackTripConnection.class);

    private static final String DEFAULT_PASSWORD = "jacktrip";

    private static final String DEFAULT_USERNAME = "pi";

    private static final String DEFAULT_HOSTNAME = JackFruitMain.DEFAULT_JACKTRIP_DEVICE_HOSTNAME;

    private Session session;

    private String hostName = DEFAULT_HOSTNAME;
    private String userName = DEFAULT_USERNAME;
    private String password = DEFAULT_PASSWORD;

    public JackTripConnection(String hostName, String userName, String password) throws JSchException {
        super();

        this.hostName = hostName;
        this.userName = userName;
        this.password = password;

        this.open();
    }

    public JackTripConnection() throws JSchException {
        this(DEFAULT_HOSTNAME, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public JackTripConnection(String hostName) throws JSchException {
        this(hostName, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    protected void open() throws JSchException {
        open(this.hostName, this.userName, this.password);
    }

    protected void open(String hostName, String userName, String password) throws JSchException {
        JSch jSch = new JSch();

        session = jSch.getSession(userName, hostName, 22);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no"); // not recommended
        session.setConfig(config);
        session.setPassword(password);

        LOGGER.info("Connecting SSH to " + hostName + " - Please wait for few seconds... ");
        session.connect();
        LOGGER.info("Connected!");
    }

    public String[] runCommand(String command) throws JSchException, IOException {
        LOGGER.info("> command=" + command);

        String resultOut = "";
        String resultErr = "";

        if (!session.isConnected()) {
            throw new RuntimeException("Not connected to an open session.  Call open() first!");
        }

        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");

            channel.setCommand(command);
            channel.setInputStream(null);

//        PrintStream out = new PrintStream(channel.getOutputStream());
            InputStream in = channel.getInputStream(); // channel.getInputStream();
            InputStream err = channel.getErrStream();
            
            channel.connect();

            // you can also send input to your running process like so:
            // String someInputToProcess = "something";
            // out.println(someInputToProcess);
            // out.flush();

            resultOut = getChannelOutput(channel, in, 1000L);
            resultErr = getChannelOutput(channel, err, 1000L);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }

        LOGGER.info("< command=" + command);

        String[] result = new String[2];
        result[0] = resultOut;
        result[1] = resultErr;
        return result;
    }

    protected String getChannelOutput(ChannelExec channel, InputStream in) throws IOException {
        return getChannelOutput(channel, in, -1L);
    }

    protected String getChannelOutput(Channel channel, InputStream in, long sleep) throws IOException {
        byte[] buffer = new byte[1024];

        StringBuilder sb = new StringBuilder();

//        String line = "";
        while (true) {
            while (in.available() > 0) {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) {
                    break;
                }
                sb.append(new String(buffer, 0, i));
//                LOGGER.info(line);
            }

//            if (line.contains("logout")) {
//                break;
//            }

            if (channel.isClosed()) {
                break;
            }

            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (Exception e) {
                    LOGGER.warn(e);
                }
            }
        }

        return sb.toString();
    }

    public void close() {
        if (session != null) {
            session.disconnect();
            LOGGER.info("session disconnected.");
        }
    }
}