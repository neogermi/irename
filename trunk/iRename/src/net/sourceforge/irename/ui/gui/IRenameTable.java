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

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.irename.Preferences;
import net.sourceforge.irename.ui.UiCallbackListener;
import net.sourceforge.irename.util.FileElement;

public class IRenameTable extends JTable {

    private static final long            serialVersionUID = -9108654075871796466L;

    private GUIImpl                      parent;

    private IRenameTableModel            tm;
    private IRenameTableCellRenderer     episodeNameRenderer;
    private Map<FileElement, RowElement> fe2re;
    private List<FileElement>            row2fe;

    public IRenameTable(GUIImpl parent) {
        super();
        this.parent = parent;

        init();
    }

    private void init() {
        fe2re = new HashMap<FileElement, RowElement>();
        row2fe = new ArrayList<FileElement>();

        tm = new IRenameTableModel();
        setModel(tm);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        episodeNameRenderer = new IRenameTableCellRenderer();

        for (int i = 0; i < tm.getColumnCount(); i++)
            getColumnModel().getColumn(i).setPreferredWidth(Preferences.columnWidth[i]);

        addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                int row = getSelectedRow();
                if (row >= 0) {
                    if (e.getKeyChar() == 'r') {
                        FileElement fe = row2fe.get(row);
                        RowElement re = fe2re.get(fe);

                        if (fe != null) {
                            re.performChanges = !re.performChanges;

                            boolean val = re.performChanges;
                            if (val) {
                                parent.renameFile(fe);
                            }
                            else
                                parent.undoRename(fe);
                            tm.fireTableRowsUpdated(row, row);
                        }
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

        });

        tm.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                int type = e.getType();

                if (type == TableModelEvent.UPDATE) {

                    int row = e.getFirstRow();
                    int col = e.getColumn();

                    if (col > 0 && row >= 0) {
                        FileElement fe = row2fe.get(row);
                        RowElement re = fe2re.get(fe);

                        if (fe != null) {
                            if (col == 6) { //rename or undo!
                                boolean val = re.performChanges;

                                if (val) {
                                    parent.renameFile(fe);
                                }
                                else
                                    parent.undoRename(fe);
                                tm.fireTableRowsUpdated(row, row);
                            }

                            UiCallbackListener.Type t = null;

                            if (col == 1) {
                                fe.setTvShowName(re.tvShowName);
                                t = UiCallbackListener.Type.TvShowName;
                            }
                            else if (col == 2) {
                                fe.setSeasonNumber(re.seasonNumber);
                                t = UiCallbackListener.Type.SeasonNumber;
                            }
                            else if (col == 3) {
                                fe.setEpisodeNumber(re.episodeNumber);
                                t = UiCallbackListener.Type.EpisodeNumber;
                            }
                            else if (col == 4) {
                                fe.setEpisodeName(re.episodeName);
                                t = UiCallbackListener.Type.EpisodeName;
                            }
                            else {
                                return;
                            }
                            parent.fireEvent(t, row2fe.get(row));

                        }
                    }
                }
            }

        });

        setIntercellSpacing(new Dimension(5, 5));
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 4) {
            return episodeNameRenderer;
        }
        else {
            return super.getCellRenderer(row, column);
        }

    }

    public void newDirOpened(File f) {
        fe2re.clear();
        row2fe.clear();
        tm.clear();
        tm.fireTableDataChanged();
    }

    public void addFileElement(FileElement fe) {
        RowElement re = new RowElement(fe);
        fe2re.put(fe, re);
        row2fe.add(fe);
        tm.addRow(re);
        tm.fireTableRowsInserted(tm.getRowCount() - 1, tm.getRowCount() - 1);
    }

    public void updateElement(FileElement fe) {
        RowElement re = fe2re.get(fe);
        re.update(fe);

        int row = tm.getRowIndex(re);
        tm.fireTableRowsUpdated(row, row);
    }

    public void startLoading(FileElement fe) {
        RowElement re = fe2re.get(fe);
        re.isLoading = true;

        int row = tm.getRowIndex(re);
        tm.fireTableRowsUpdated(row, row);
    }

    public void stopLoading(FileElement fe) {
        RowElement re = fe2re.get(fe);
        re.isLoading = false;

        int row = tm.getRowIndex(re);
        tm.fireTableRowsUpdated(row, row);
    }

    public boolean isLoading(int row) {
        FileElement fe = row2fe.get(row);
        RowElement re = fe2re.get(fe);

        return re.isLoading;
    }

}
