package gui;

import java.awt.*;

/**
 * FlowLayout replacement that correctly reports its preferred height after
 * wrapping. Standard FlowLayout always returns a single-row height regardless
 * of the container's actual width, which causes buttons to be clipped when the
 * window is narrow rather than wrapping onto a second row.
 *
 * Usage: replace {@code new FlowLayout(...)} with {@code new WrapLayout(...)}.
 */
public class WrapLayout extends FlowLayout {

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return computeSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return computeSize(target, false);
    }

    private Dimension computeSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            // Use a large sentinel when width is not yet known so all
            // components appear on one row — prevents a zero-height result.
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

            int hgap = getHgap(), vgap = getVgap();
            Insets ins = target.getInsets();
            int avail = targetWidth - ins.left - ins.right - hgap * 2;

            int x = 0, rowH = 0;
            int totalH = ins.top + vgap;

            for (int i = 0; i < target.getComponentCount(); i++) {
                Component c = target.getComponent(i);
                if (!c.isVisible()) continue;
                Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();

                if (x > 0 && x + hgap + d.width > avail) {
                    // Wrap to a new row
                    totalH += rowH + vgap;
                    x = 0;
                    rowH = 0;
                }
                x += (x > 0 ? hgap : 0) + d.width;
                rowH = Math.max(rowH, d.height);
            }
            totalH += rowH + ins.bottom + vgap;

            return new Dimension(targetWidth, totalH);
        }
    }
}
