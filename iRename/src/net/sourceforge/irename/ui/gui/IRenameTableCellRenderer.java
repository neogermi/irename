package net.sourceforge.irename.ui.gui;

/*
 * Copyright (C) 2010 Sebastian Germesin
 * 
 *  This file is part of iRename.
 *
 *  iRename is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  iRename is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with iRename (see .../iRename/LICENSE.txt)
 *  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.Color;
import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.irename.util.AnimatedIcon;

public class IRenameTableCellRenderer extends JLabel implements TableCellRenderer {

    private static final long   serialVersionUID = 779052442287351934L;
    private static AnimatedIcon loadingImage;

    private static final Color  GREEN            = new Color(57, 115, 76);

    static {
        URL imgURL = IRenameTableCellRenderer.class.getResource("/icons/ajax-loader.gif");
        if (imgURL != null) {
            loadingImage = new AnimatedIcon(new ImageIcon(imgURL));
        }
        else {
            loadingImage = null;
        }
    }

    public IRenameTableCellRenderer() {
        super();
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        if (column == 6)
            return table.getDefaultRenderer(Boolean.class).getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);
        else {
            String text = (String) value;
            boolean isLoading = ((IRenameTable) table).isLoading(row);
            boolean isRenamed = ((IRenameTable) table).isRenamed(row);

            if (isRenamed)
                setForeground(GREEN);
            else
                setForeground(Color.black);

            if (isLoading && column == 4) {
                setText("");
                setIcon(loadingImage);
            }
            else {
                setIcon(null);
                setText(text);
            }
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                if (isRenamed)
                    setForeground(GREEN);
                else
                    setForeground(table.getSelectionForeground());
            }
            else {
                setBackground(table.getBackground());
                if (isRenamed)
                    setForeground(GREEN);
                else
                    setForeground(table.getForeground());
            }

            return this;
        }
    }
}
