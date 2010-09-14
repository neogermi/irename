package net.sourceforge.irename.util;

/* Copyright (c) 2006-2007 Timothy Wall, All Rights Reserved
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
* 
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.  
*/

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/** Ensures animated icons are properly handled within objects that use
* renderers within a {@link CellRendererPane} to render the icon.  Keeps
* a list of repaint rectangles to be used to queue repaint requests when
* the animated icon indicates an update.  The set of repaint rectangles
* is cleared after the repaint requests are queued.
* @author twall
*/
public class AnimatedIcon implements Icon {

    /** Returns whether the given icon is an animated GIF. */
    public static boolean isAnimated(Icon icon) {
        return true;
    }

    private ImageIcon        original;
    private Set<RepaintArea> repaints = new HashSet<RepaintArea>();

    /** For use by derived classes that don't have an original. */
    protected AnimatedIcon() {
    }

    /** Create an icon that takes care of animating itself on components
     * which use a CellRendererPane.
     */
    public AnimatedIcon(ImageIcon original) {
        this.original = original;
        new AnimationObserver(this, original);
    }

    /** Trigger a repaint on all components on which we've previously been 
     * painted.
     */
    protected synchronized void repaint() {
        for (Iterator<RepaintArea> i = repaints.iterator(); i.hasNext();) {
            i.next().repaint();
        }
        repaints.clear();
    }

    @Override
    public int getIconHeight() {
        return original.getIconHeight();
    }

    @Override
    public int getIconWidth() {
        return original.getIconWidth();
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        paintFrame(c, g, x, y);
        if (c != null) {
            int w = getIconWidth();
            int h = getIconHeight();
            AffineTransform tx = ((Graphics2D) g).getTransform();
            w = (int) (w * tx.getScaleX());
            h = (int) (h * tx.getScaleY());
            registerRepaintArea(c, x, y, w, h);
        }
    }

    protected void paintFrame(Component c, Graphics g, int x, int y) {
        original.paintIcon(c, g, x, y);
    }

    /** Register repaint areas, which get get cleared once the repaint request
     * has been queued.
     */
    protected void registerRepaintArea(Component c, int x, int y, int w, int h) {
        repaints.add(new RepaintArea(c, x, y, w, h));
    }

    /** Object to encapsulate an area on a component to be repainted. */
    private class RepaintArea {
        public int       x, y, w, h;
        public Component component;
        private int      hashCode;

        public RepaintArea(Component c, int x, int y, int w, int h) {
            Component ancestor = findNonRendererAncestor(c);
            if (ancestor != c) {
                Point pt = SwingUtilities.convertPoint(c, x, y, ancestor);
                c = ancestor;
                x = pt.x;
                y = pt.y;
            }
            component = c;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            String hash = String.valueOf(x) + "," + y + ":" + c.hashCode();
            hashCode = hash.hashCode();
        }

        /** Find the first ancestor <em>not</em> descending from a 
         * {@link CellRendererPane}.
         */
        private Component findNonRendererAncestor(Component c) {
            Component ancestor = SwingUtilities.getAncestorOfClass(CellRendererPane.class, c);
            if (ancestor != null && ancestor != c && ancestor.getParent() != null) {
                c = findNonRendererAncestor(ancestor.getParent());
            }
            return c;
        }

        /** Queue a repaint request for this area. */
        public void repaint() {
            component.repaint(x, y, w, h);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof RepaintArea) {
                RepaintArea area = (RepaintArea) o;
                return area.component == component && area.x == x && area.y == y && area.w == w
                        && area.h == h;
            }
            return false;
        }

        /** Since we're using a HashSet. */
        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            return "Repaint(" + component.getClass().getName() + "@" + x + "," + y + " " + w + "x"
                    + h + ")";
        }
    }

    /** Detect changes in the original animated image, and remove self
     * if the target icon is GC'd.
     * @author twall
     */
    private static class AnimationObserver implements ImageObserver {
        private WeakReference<AnimatedIcon> ref;
        private ImageIcon                   original;

        public AnimationObserver(AnimatedIcon animIcon, ImageIcon original) {
            this.original = original;
            this.original.setImageObserver(this);
            ref = new WeakReference<AnimatedIcon>(animIcon);
        }

        /** Queue repaint requests for all known painted areas. */
        @Override
        public boolean imageUpdate(Image img, int flags, int x, int y, int width, int height) {
            if ((flags & (FRAMEBITS | ALLBITS)) != 0) {
                AnimatedIcon animIcon = ref.get();
                if (animIcon != null) {
                    animIcon.repaint();
                }
                else
                    original.setImageObserver(null);
            }
            // Return true if we want to keep painting
            return (flags & (ALLBITS | ABORT)) == 0;
        }
    }
}