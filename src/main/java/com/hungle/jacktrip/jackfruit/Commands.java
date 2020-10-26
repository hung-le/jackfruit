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
                new Command("Sound - List Devices", "/usr/bin/aplay -l", "List sound devices.\n"),
                new Command("Sound - List PCMs", "/usr/bin/aplay -L", "List PCMs.\n"),
                // Network
                new Command("Network - Get interface info", "/usr/sbin/ifconfig -a", "To confirm IP's, MAC address.\n"),
                new Command("Network - Get routing info", "/usr/bin/netstat -rn",
                        "To verify current routing info.\nUseful for getting the local gateway IP.\n"),
                new Command("Network - Ping gateway", "/usr/bin/ping -c 3 " + "192.168.1.254",
                        "Ping from the box to local gateway.\n"),
                new Command("Network - Ping loopback test server",
                        "/usr/bin/ping -c 3 " + JackFruitMain.getLoopbackTestServer(),
                        "To ping the loopback test server. Note the IP might need to be adjusted.\n"),
                // netcat -zv -w 5 13.52.186.20 4464
                new Command("Network - Check TCP port 4464",
                        "/usr/bin/netcat -zv -w 5 " + JackFruitMain.getLoopbackTestServer() + " 4464",
                        "Check to see if TCP connection can be made to the loopback test server.\nNote the IP might need to be adjusted.\n"),
                // Process
                new Command("Process - Get process info (jack only)", "/usr/bin/ps -ef | grep jack",
                        "Get process info (jack only): list process limited to matching \"jack\".\n"),
                new Command("Process - Get process info (all)", "/usr/bin/ps -ef",
                        "Get process info (all): list all processes.\n"), };
    }
}
