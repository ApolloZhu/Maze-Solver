package io.github.apollozhu.mazesolver.utilities;

/**
 * @author ApolloZhu, Pd. 1
 */
public enum Safely {
    ;

    public static boolean execute(UnsafeRunnable unsafe) {
        try {
            unsafe.run();
            return true;
        } catch (Throwable e) {
        }
        return false;
    }
}
