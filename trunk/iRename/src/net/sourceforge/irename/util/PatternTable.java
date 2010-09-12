package net.sourceforge.irename.util;

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

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import net.sourceforge.irename.Preferences;

public class PatternTable extends JTable {

  private static final long serialVersionUID = -553924127145544859L;

  private PatternTableModel ptm;

  public PatternTable() {
    super();

    ptm = new PatternTableModel();

    setRowSelectionAllowed(true);
    this.setColumnSelectionAllowed(false);
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    this.setModel(ptm);

    //TODO: that is NOT beautiful!
    TableColumn column = null;
    for (int i = 0; i < ptm.getColumnCount(); i++) {
      column = this.getColumnModel().getColumn(i);
      if (i == 0)
        column.setPreferredWidth(180);
      else
        column.setPreferredWidth(63);
    }

    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
  }

  public void addPattern(Pattern p) {
    ptm.addPattern(p);
    ptm.fireTableDataChanged();
  }

  public void removePattern(Pattern p) {
    ptm.removePattern(p);
    ptm.fireTableDataChanged();
  }

  class PatternTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 2769465466600679325L;

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return String.class;
    }

    public int getColumnCount() {
      return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
      String[] names = new String[] { Preferences.getLocalizationString("regExpFieldLabel"), Preferences.getLocalizationString("tvShowFieldLabel"),
          Preferences.getLocalizationString("seasonFieldLabel"), Preferences.getLocalizationString("episodeFieldLabel"), };

      return names[columnIndex];
    }

    synchronized public int getRowCount() {
      return Preferences.patterns.size();
    }

    synchronized public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex >= 0 && rowIndex < getRowCount()) {
        if (columnIndex >= 0 && columnIndex < getColumnCount()) {
          Pattern p = Preferences.patterns.get(rowIndex);

          switch (columnIndex) {
            case 0:
              return p.getPattern();
            case 1:
              return p.getToShow();
            case 2:
              return p.getToSeason();
            case 3:
              return p.getToEpisode();
          }
        }
      }
      return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    synchronized public void addPattern(Pattern p) {
      Preferences.patterns.add(p);
    }

    synchronized public boolean containsPattern(Pattern p) {
      return Preferences.patterns.contains(p);
    }

    synchronized public void removePattern(Pattern p) {
      Preferences.patterns.remove(p);
    }

    synchronized public Pattern getPatternAt(int index) {
      return Preferences.patterns.get(index);
    }

    synchronized public void movePattern(int from, int to) {
      Pattern p = Preferences.patterns.remove(from);
      Preferences.patterns.add(to, p);
    }
  }

  public boolean containsPattern(Pattern p) {
    return ptm.containsPattern(p);
  }

  public Pattern getPatternAt(int index) {
    return ptm.getPatternAt(index);
  }

  public void movePattern(int from, int to) {
    ptm.movePattern(from, to);
    this.repaint();
  }
}
