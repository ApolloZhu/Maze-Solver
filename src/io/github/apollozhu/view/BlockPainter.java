package io.github.apollozhu.view;

import java.awt.*;

/**
 * @author ApolloZhu, Pd. 1
 */
public interface BlockPainter {
    void paintBlock(Graphics g, int r, int c, int x, int y, int w, int h);
}
