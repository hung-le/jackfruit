package com.hungle.jacktrip.jackfruit;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public abstract class JackTripCommand extends AbstractAction {
    private final String command;

    public JackTripCommand(String name, String command) {
        super(name);
        this.command = command;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        this.runCommand(command);
    }

    public abstract void runCommand(String command);
}