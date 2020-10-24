package com.hungle.jacktrip.jackfruit;

public class Commands {
    public static final Command[] createCommands() {
        return new Command[] {
                // Log
                new Command("Log - jacktrip", "/usr/bin/journalctl -t jacktrip",
                        "Get log entries for jacktrip process.\nUseful to check to see if there are any error on starting/stopping jacktrip.\n"),
                new Command("Log - jamulus", "/usr/bin/journalctl -t jamulus",
                        "Get log entries for jamulus process.\nUseful to check to see if there are any error on starting/stopping jamulus.\n"),
                new Command("Log - jack", "/usr/bin/journalctl -t jack",
                        "Get log entries for jack process.\nUseful to check to see if there are any error on starting/stopping jack.\n"),
                new Command("Log - jacktrip-agent", "/usr/bin/journalctl -t jacktrip-agent",
                        "Get log entries for jacktrip-agent process.\njacktrip-agent is the component that communicates with app.jacktrip.org.\n"),
                new Command("Log - jacktrip-patches", "/usr/bin/journalctl -t jacktrip-patches",
                        "Get log entries for jacktrip-patches process.\njacktrip-patches is the component that patches up the running images if there is an update.\n"),
                // Sound
                new Command("Sound - Get Volumes", "/usr/bin/amixer get Digital", "Show current volumes.\n"),
                new Command("Sound - Headphones Test (local)", "/usr/bin/speaker-test -c 2 -l 3 -t wav",
                        "Perform a headphones test locally without using server/network.\n"),
                new Command("Sound - List Devices", "/usr/bin/aplay -l"),
                new Command("Sound - List PCMs", "/usr/bin/aplay -L"),
                // Network
                new Command("Network - Get interface info", "/usr/sbin/ifconfig -a"),
                new Command("Network - Get routing info", "/usr/bin/netstat -rn"),
                new Command("Network - Ping gateway", "/usr/bin/ping -c 3 " + "192.168.1.254"),
                new Command("Network - Ping loopback test server",
                        "/usr/bin/ping -c 3 " + JackFruitMain.getLoopbackTestServer()),
                // Process
                new Command("Process - Get process info (jack only)", "/usr/bin/ps -ef | grep jack"),
                new Command("Process - Get process info (all)", "/usr/bin/ps -ef"), };
    }
}
