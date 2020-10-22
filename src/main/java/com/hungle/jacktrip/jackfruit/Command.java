package com.hungle.jacktrip.jackfruit;

class Command {
    @Override
    public String toString() {
        return getLabel();
    }

    private final String label;
    private final String command;

    Command(String label, String command) {
        super();
        this.label = label;
        this.command = command;
    }

    public String getLabel() {
        return label;
    }

    public String getCommand() {
        return command;
    }
}