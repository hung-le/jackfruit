package com.hungle.jacktrip.jackfruit;

class Command {
    private final String label;
    private final String command;
    private final String comment;

    public String getComment() {
        return comment;
    }

    Command(String label, String command, String comment) {
        super();
        this.label = label;
        this.command = command;
        this.comment = comment;
    }

    public Command(String label, String command) {
        this(label, command, "");
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public String getLabel() {
        return label;
    }

    public String getCommand() {
        return command;
    }
}