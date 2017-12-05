package io.github.apollozhu.mazesolver.utilities;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author ApolloZhu, Pd. 1
 */
public enum Resources {
    ;

    public static Image getIcon() {
        return imageNamed(getIconName());
    }

    public static String getIconName() {
        return isMacOS() ? "Icon-Mac.png" : "Icon.png";
    }

    public static boolean trySetMacOSDockIcon() {
        return Safely.execute(() -> {
            // FIXME: Will be deprecated by Java 9+
            // TODO: http://openjdk.java.net/jeps/272
            Class NSApplication = Class.forName("com.apple.eawt.Application");
            Method sharedApplication = NSApplication.getMethod("getApplication");
            Object shared = sharedApplication.invoke(NSApplication);
            Method setApplicationIconImage = NSApplication.getMethod("setDockIconImage", Image.class);
            setApplicationIconImage.invoke(shared, Resources.imageNamed("Icon-Mac.png"));
        });
    }

    public static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static String getAppVersion() {
        return "1.0.3";
    }

    public static Image imageNamed(String name) {
        try {
            URL url = Resources.class.getClassLoader().getResource(name);
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            return null;
        }
    }
}
