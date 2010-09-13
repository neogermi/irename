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

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.irename.Preferences;

public class IRenameTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1141186853767305599L;

    public List<RowElement>   rows;

    public IRenameTableModel() {
        rows = new LinkedList<RowElement>();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return (column != 0 & column != 5);

    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return String.class;
        case 1:
            return String.class;
        case 2:
            return String.class;
        case 3:
            return String.class;
        case 4:
            return String.class;
        case 5:
            return String.class;
        case 6:
            return Boolean.class;
        }
        return null;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Preferences.getLocalizationString("sourceFilenameLabel");
        case 1:
            return Preferences.getLocalizationString("tvShowLabel");
        case 2:
            return Preferences.getLocalizationString("seasonNumberLabel");
        case 3:
            return Preferences.getLocalizationString("episodeNumberLabel");
        case 4:
            return Preferences.getLocalizationString("episodeNameLabel");
        case 5:
            return Preferences.getLocalizationString("targetFilenameLabel");
        case 6:
            return Preferences.getLocalizationString("renameUndoColumnLabel");
        }
        return "";

    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (0 <= rowIndex && rowIndex < getRowCount()) {
            if (0 <= columnIndex && columnIndex < getColumnCount()) {
                if (getColumnClass(columnIndex) == aValue.getClass()) {
                    RowElement re = rows.get(rowIndex);

                    switch (columnIndex) {
                    case 1:
                        re.tvShowName = (String) aValue;
                        break;
                    case 2:
                        re.seasonNumber = (String) aValue;
                        break;
                    case 3:
                        re.episodeNumber = (String) aValue;
                        break;
                    case 4:
                        re.episodeName = (String) aValue;
                        break;
                    case 5:
                        re.filenameAfter = (String) aValue;
                        break;
                    case 6:
                        re.isRenamed = (Boolean) aValue;
                        break;
                    }
                    fireTableCellUpdated(rowIndex, columnIndex);
                }
            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (0 <= rowIndex && rowIndex < getRowCount()) {
            if (0 <= columnIndex && columnIndex < getColumnCount()) {
                RowElement re = rows.get(rowIndex);

                switch (columnIndex) {
                case 0:
                    return re.filenameBefore;
                case 1:
                    return re.tvShowName;
                case 2:
                    return re.seasonNumber;
                case 3:
                    return re.episodeNumber;
                case 4:
                    return re.episodeName;
                case 5:
                    return re.filenameAfter;
                case 6:
                    return re.isRenamed;
                }

            }
        }
        return null;
    }

    public void addRow(RowElement rowElement) {
        rows.add(rowElement);
    }

    public int getRowIndex(RowElement re) {
        for (int i = 0; i < rows.size(); i++)
            if (rows.get(i).equals(re))
                return i;
        return -1;
    }

    public void clear() {
        rows.clear();
    }

}
