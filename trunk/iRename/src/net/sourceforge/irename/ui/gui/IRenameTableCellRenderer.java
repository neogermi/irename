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

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.sourceforge.irename.util.AnimatedIcon;

public class IRenameTableCellRenderer extends DefaultTableCellRenderer {

    private static final long   serialVersionUID = 779052442287351934L;
    private static AnimatedIcon loadingImage;
    private static JLabel       label;

    static {
        URL imgURL = IRenameTableCellRenderer.class.getResource("/icons/ajax-loader.gif");
        if (imgURL != null) {
            loadingImage = new AnimatedIcon(new ImageIcon(imgURL));
        }
        else {
            loadingImage = null;
        }
        label = new JLabel();
        label.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        if (column == 4) {
            String text = (String) value;
            boolean isLoading = ((IRenameTable) table).isLoading(row);

            if (isLoading) {
                label.setIcon(loadingImage);
                label.setText(null);
            }
            else {
                label.setIcon(null);
                label.setText(text);
            }
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }
            else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }

            this.setSize(table.getColumnModel().getColumn(column).getWidth(), table
                    .getRowHeight(row));
            int height_wanted = 20;
            if (height_wanted != table.getRowHeight(row)) {
                table.setRowHeight(row, height_wanted + 2);
            }

            return label;
        }
        else
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);

    }
}
