package io.github.apollozhu.mazesolver.controller;

import io.github.apollozhu.mazesolver.utilities.Resources;

import javax.swing.*;

public enum TopDialog {
    ;

    public static JDialog getDialog() {
        JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setIconImage(Resources.getIcon());
        return dialog;
    }
}
