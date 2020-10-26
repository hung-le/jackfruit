package com.hungle.jacktrip.jackfruit;

public class Commands {
    public static final Command[] createCommands() {
        return new Command[] {
                // Log
                new Command("Log - jacktrip", "/usr/bin/journalctl -t jacktrip",
                        "To get log entries for jacktrip process.\nUseful to check to see if there are any error on starting/stopping jacktrip.\n"),
                new Command("Log - jamulus", "/usr/bin/journalctl -t jamulus",
                        "To get log entries for jamulus process.\nUseful to check to see if there are any error on starting/stopping jamulus.\n"),
                new Command("Log - jack", "/usr/bin/journalctl -t jack",
                        "To get log entries for jack process.\nUseful to check to see if there are any error on starting/stopping jack.\n"),
                new Command("Log - jacktrip-agent", "/usr/bin/journalctl -t jacktrip-agent",
                        "To get log entries for jacktrip-agent process.\njacktrip-agent is the component that communicates with app.jacktrip.org.\n"),
                new Command("Log - jacktrip-patches", "/usr/bin/journalctl -t jacktrip-patches",
                        "To get log entries for jacktrip-patches process.\njacktrip-patches is the component that patches up the running images if there is an update.\n"),
                // Sound
                new Command("Sound - Get Volumes", "/usr/bin/amixer get Digital", "Show current volumes.\n"),
                new Command("Sound - Headphones Test (local)", "/usr/bin/speaker-test -c 2 -l 3 -t wav",
                        "Perform a headphones test locally. No server/network.\n"),
                // arecord -d 10 -f cd -t wav /var/tmp/test-mic.wav
                new Command("Sound - Test microphone", "/usr/bin/arecord -d 10 -f cd -t wav /var/tmp/test-mic.wav",
                        "Record a 10 seconds clip using your microphone. No server/network.\n"),
                // aplay /var/tmp/test-mic.wav
                new Command("Sound - Test playback", "/usr/bin/aplay /var/tmp/test-mic.wav",
                        "Playback the 10 seconds recording. No server/network.\n"),
                new Command("Sound - List Devices", "/usr/bin/aplay -l", "List sound devices."),
                new Command("Sound - List PCMs", "/usr/bin/aplay -L", "List PCMs"),
                // Network
                new Command("Network - Get interface info", "/usr/sbin/ifconfig -a", "To confirm IP's, MAC address"),
                new Command("Network - Get routing info", "/usr/bin/netstat -rn",
                        "To verify current routing info.\nUseful for getting the local gateway IP."),
                new Command("Network - Ping gateway", "/usr/bin/ping -c 3 " + "192.168.1.254",
                        "Ping from the box to local gateway."),
                new Command("Network - Ping loopback test server",
                        "/usr/bin/ping -c 3 " + JackFruitMain.getLoopbackTestServer(),
                        "To ping the loopback test server. Note the IP might need to be adjusted."),
                // Process
                new Command("Process - Get process info (jack only)", "/usr/bin/ps -ef | grep jack",
                        "Get process info (jack only): list process limited to matching \"jack\".\n"),
                new Command("Process - Get process info (all)", "/usr/bin/ps -ef",
                        "Get process info (all): list all processes.\n"), };
    }
}
