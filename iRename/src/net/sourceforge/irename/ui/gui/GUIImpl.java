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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import net.sourceforge.irename.Preferences;
import net.sourceforge.irename.ui.Ui;
import net.sourceforge.irename.ui.UiCallbackListener;
import net.sourceforge.irename.util.FileElement;
import net.sourceforge.irename.util.Pattern;
import net.sourceforge.irename.util.PatternTable;

public class GUIImpl extends JFrame implements Ui {

    private static final long       serialVersionUID = -1241641185375728018L;

    private JFileChooser            directoryChooserOther;
    private FileDialog              directoryChooserMac;

    private JMenuItem               openDirMenuItem;
    private JCheckBoxMenuItem       retrieveOnlineDataMenuItem;
    private JMenuItem               manageFileendingsMenuItem;
    private JMenuItem               managePatternsMenuItem;
    private JMenuItem               changeReplacementRuleMenuItem;
    private JMenuItem               quitMenuItem;

    private JButton                 openDirButton;
    private JLabel                  currentDirectoryLabel;
    private JButton                 renameUndoAll;

    private IRenameTable            iRenameTable;

    private Set<UiCallbackListener> listener;

    public GUIImpl(UiCallbackListener iRename) {
        super(Preferences.getLocalizationString("title"));

        init(iRename);
    }

    private void init(UiCallbackListener iRename) {
        listener = new HashSet<UiCallbackListener>();
        listener.add(iRename);

        if (Preferences.isMacOS) {
            directoryChooserMac = new FileDialog(this);
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
        }
        else {
            directoryChooserOther = new JFileChooser();
            directoryChooserOther.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        setSize(Preferences.width, Preferences.height);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quitApplication();
            }
        });

        setTransferHandler(new MyTransferHandler());

        setJMenuBar(assembleMenuBar());

        iRenameTable = new IRenameTable(this);

        openDirButton = new JButton(Preferences.getLocalizationString("openDirButtonLabel"));
        openDirButton.setToolTipText(Preferences.getLocalizationString("openDirButtonTooltip"));
        openDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (openDirButton.getText().equals(
                        Preferences.getLocalizationString("openDirButtonLabel"))) {
                    openNewDirectory();
                }
            }
        });

        currentDirectoryLabel = new JLabel(Preferences.getLocalizationString("currentDirLabel"));

        renameUndoAll = new JButton(Preferences.getLocalizationString("renameAllLabel"));
        renameUndoAll.setEnabled(false);
        renameUndoAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
            }
        });

        JPanel top2Panel = new JPanel();
        top2Panel.setLayout(new BorderLayout());
        top2Panel.add(BorderLayout.WEST, currentDirectoryLabel);
        top2Panel.add(BorderLayout.EAST, renameUndoAll);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));

        topPanel.add(openDirButton);
        topPanel.add(top2Panel);

        JPanel mainPanel = new JPanel();
        Border paneEdge = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        mainPanel.setBorder(paneEdge);
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(BorderLayout.NORTH, topPanel);
        mainPanel.add(BorderLayout.CENTER, new JScrollPane(iRenameTable,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        setContentPane(mainPanel);

        setVisible(true);
    }

    private JMenuBar assembleMenuBar() {

        int ae = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu(Preferences.getLocalizationString("fileMenuLabel"));
        openDirMenuItem = new JMenuItem(Preferences.getLocalizationString("openDirMenuLabel"));
        openDirMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ae));
        openDirMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openNewDirectory();
            }
        });

        retrieveOnlineDataMenuItem = new JCheckBoxMenuItem(Preferences
                .getLocalizationString("retrieveOnlineDataMenuLabel"),
                Preferences.retrieveOnlineData);
        retrieveOnlineDataMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ae));
        retrieveOnlineDataMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences.retrieveOnlineData = retrieveOnlineDataMenuItem.isSelected();
            }

        });

        manageFileendingsMenuItem = new JMenuItem(Preferences
                .getLocalizationString("manageFileendingsMenuLabel"));
        manageFileendingsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ae));
        manageFileendingsMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                manageFileendings();
            }

        });

        managePatternsMenuItem = new JMenuItem(Preferences
                .getLocalizationString("managePatternsMenuLabel"));
        managePatternsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ae));
        managePatternsMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                managePatterns();
            }
        });

        changeReplacementRuleMenuItem = new JMenuItem(Preferences
                .getLocalizationString("changeReplacementRuleMenuLabel"));
        changeReplacementRuleMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ae));
        changeReplacementRuleMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeReplacementPattern();
            }
        });

        quitMenuItem = new JMenuItem(Preferences.getLocalizationString("quitMenuLabel"));
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ae));
        quitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                quitApplication();
            }
        });

        fileMenu.add(openDirMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(retrieveOnlineDataMenuItem);
        fileMenu.add(manageFileendingsMenuItem);
        fileMenu.add(managePatternsMenuItem);
        fileMenu.add(changeReplacementRuleMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);

        JMenu helpMenu = new JMenu(Preferences.getLocalizationString("helpMenuLabel"));
        JMenuItem aboutMenuItem = new JMenuItem(Preferences.getLocalizationString("aboutMenuLabel"));
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }

        });
        helpMenu.add(aboutMenuItem);

        menu.add(fileMenu);
        menu.add(helpMenu);

        return menu;
    }

    private void changeReplacementPattern() {
        String newRule = (String) JOptionPane.showInputDialog(this, Preferences
                .getLocalizationString("changeReplacementPatternDialogMessage"), Preferences
                .getLocalizationString("changeReplacementPatternDialogTitle"),
                JOptionPane.PLAIN_MESSAGE, null, null, Preferences.replacementRule);

        if (newRule == null)
            return;
        if (newRule.trim().equals("")) {
            JOptionPane.showMessageDialog(this, Preferences
                    .getLocalizationString("noChangesOfReplacementPatternWarningLabel"),
                    Preferences.getLocalizationString("errorDialogTitle"),
                    JOptionPane.WARNING_MESSAGE);
        }
        else {
            Preferences.replacementRule = newRule;
            JOptionPane.showMessageDialog(this, Preferences
                    .getLocalizationString("changesOfReplacementPatternSuccessfullLabel"),
                    Preferences.getLocalizationString("informDialogTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void manageFileendings() {
        JDialog dialog = new JDialog(this, Preferences
                .getLocalizationString("manageFileendingsDialogTitle"));

        final DefaultListModel listModel = new DefaultListModel();
        final JList list = new JList();
        final JScrollPane listScrollPane = new JScrollPane(list);
        final JButton addButton = new JButton(Preferences.getLocalizationString("addButtonLabel"));
        final JButton removeButton = new JButton(Preferences
                .getLocalizationString("removeButtonLabel"));
        final JTextField newFileEndingField = new JTextField(5);
        newFileEndingField.getDocument().addDocumentListener(new DocumentListener() {

            private void checkAndAct(DocumentEvent e) {
                if (checkIfInputAvailable(e)) {
                    if (checkIfInputMatchesFileending(e)) {
                        addButton.setEnabled(false);
                        removeButton.setEnabled(true);
                    }
                    else {
                        addButton.setEnabled(true);
                        removeButton.setEnabled(false);
                    }
                }
                else {
                    addButton.setEnabled(false);
                    removeButton.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            private boolean checkIfInputAvailable(DocumentEvent e) {
                try {
                    String currentText = e.getDocument().getText(0, e.getDocument().getLength());

                    return (!currentText.equals(""));
                }
                catch (BadLocationException e1) {
                    // ignore
                }
                return false;
            }

            private boolean checkIfInputMatchesFileending(DocumentEvent e) {
                try {
                    String currentText = e.getDocument().getText(0, e.getDocument().getLength());

                    return (Preferences.validFileEndings.contains(currentText));
                }
                catch (BadLocationException e1) {
                    // ignore
                }
                return false;
            }

        });

        for (String fileending : Preferences.validFileEndings) {
            listModel.addElement(fileending);
        }

        list.setModel(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedIndex() == -1) {
                    // No selection.
                    removeButton.setEnabled(false);
                    newFileEndingField.setText("");
                }
                else {
                    removeButton.setEnabled(true);
                    newFileEndingField.setText((String) list.getSelectedValue());
                }

            }

        });
        list.setVisibleRowCount(5);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        addButton.setEnabled(false);
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String newFileEnding = newFileEndingField.getText();
                if (!Preferences.validFileEndings.contains(newFileEnding)) {
                    for (UiCallbackListener uil : listener)
                        uil.addValidFileEnding(newFileEnding);
                    listModel.addElement(newFileEnding);
                }
                newFileEndingField.setText("");
                addButton.setEnabled(false);
            }

        });

        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String newFileEnding = newFileEndingField.getText();
                removeButton.setEnabled(false);
                listModel.removeElement(newFileEnding);
                for (UiCallbackListener uil : listener)
                    uil.removeValidFileEnding(newFileEnding);
                newFileEndingField.setText("");
                list.clearSelection();
            }

        });

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(addButton);
        buttonPane.add(newFileEndingField);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(removeButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        dialog.add(listScrollPane, BorderLayout.CENTER);
        dialog.add(buttonPane, BorderLayout.PAGE_END);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void managePatterns() {
        JDialog dialog = new JDialog(this, Preferences
                .getLocalizationString("managePatternsDialogTitle"));

        final PatternTable table = new PatternTable();
        final JScrollPane listScrollPane = new JScrollPane(table);
        listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        table.setFillsViewportHeight(true);
        final JButton addButton = new JButton(Preferences.getLocalizationString("addButtonLabel"));
        final JButton removeButton = new JButton(Preferences
                .getLocalizationString("removeButtonLabel"));

        final JButton upButton = new JButton("up");
        upButton.setEnabled(false);
        final JButton downButton = new JButton("down");
        downButton.setEnabled(false);

        final JLabel regExpLabel = new JLabel(Preferences.getLocalizationString("regExpFieldLabel"));
        final JTextField regExpField = new JTextField(20);
        final JLabel tvShowLabel = new JLabel(Preferences.getLocalizationString("tvShowFieldLabel"));
        final JTextField tvShowField = new JTextField(2);
        final JLabel seasonLabel = new JLabel(Preferences.getLocalizationString("seasonFieldLabel"));
        final JTextField seasonField = new JTextField(2);
        final JLabel episodeLabel = new JLabel(Preferences
                .getLocalizationString("episodeFieldLabel"));
        final JTextField episodeField = new JTextField(2);

        regExpField.getDocument().addDocumentListener(new DocumentListener() {

            private void checkAndAct(DocumentEvent e) {
                String regExp = regExpField.getText();
                String tvShow = tvShowField.getText();
                String season = seasonField.getText();
                String episode = episodeField.getText();
                Pattern p = new Pattern(regExp, tvShow, season, episode);

                boolean patternIsValid = true;

                try {
                    java.util.regex.Pattern.compile(p.getPattern());

                    if (!p.getPattern().equals(""))
                        patternIsValid &= (p.getPattern().matches("([^(]*\\([^)]*?\\)[^(]*?){3}"));

                }
                catch (PatternSyntaxException pse) {
                    patternIsValid = false;
                }

                if (patternIsValid) {
                    regExpField.setBackground(Color.white);
                    if (table.containsPattern(p)) {
                        addButton.setEnabled(false);
                        removeButton.setEnabled(true);
                    }
                    else {
                        addButton.setEnabled(true);
                        removeButton.setEnabled(false);
                    }
                }
                else {
                    regExpField.setBackground(Color.red);
                }

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

        });

        tvShowField.getDocument().addDocumentListener(new DocumentListener() {

            private void checkAndAct(DocumentEvent e) {
                String regExp = regExpField.getText();
                String tvShow = tvShowField.getText();
                String season = seasonField.getText();
                String episode = episodeField.getText();
                Pattern p = new Pattern(regExp, tvShow, season, episode);

                boolean inputIsValid = true;

                inputIsValid &= (!tvShow.equals(""));
                inputIsValid &= (!tvShow.equals(season));
                inputIsValid &= (!tvShow.equals(episode));
                inputIsValid &= tvShow.matches("\\$[123]");

                if (inputIsValid) {
                    tvShowField.setBackground(Color.white);
                    if (table.containsPattern(p)) {
                        addButton.setEnabled(false);
                        removeButton.setEnabled(true);
                    }
                    else {
                        addButton.setEnabled(true);
                        removeButton.setEnabled(false);
                    }
                }
                else {
                    if (!tvShow.equals(""))
                        tvShowField.setBackground(Color.red);
                }

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

        });

        seasonField.getDocument().addDocumentListener(new DocumentListener() {

            private void checkAndAct(DocumentEvent e) {
                String regExp = regExpField.getText();
                String tvShow = tvShowField.getText();
                String season = seasonField.getText();
                String episode = episodeField.getText();
                Pattern p = new Pattern(regExp, tvShow, season, episode);

                boolean inputIsValid = true;

                inputIsValid &= (!season.equals(""));
                inputIsValid &= (!season.equals(tvShow));
                inputIsValid &= (!season.equals(episode));
                if (!season.equals(""))
                    inputIsValid &= season.matches("\\$[123]");

                if (inputIsValid) {
                    seasonField.setBackground(Color.white);
                    if (table.containsPattern(p)) {
                        addButton.setEnabled(false);
                        removeButton.setEnabled(true);
                    }
                    else {
                        addButton.setEnabled(true);
                        removeButton.setEnabled(false);
                    }
                }
                else {
                    if (!season.equals(""))
                        seasonField.setBackground(Color.red);
                }

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

        });

        episodeField.getDocument().addDocumentListener(new DocumentListener() {

            private void checkAndAct(DocumentEvent e) {
                String regExp = regExpField.getText();
                String tvShow = tvShowField.getText();
                String season = seasonField.getText();
                String episode = episodeField.getText();
                Pattern p = new Pattern(regExp, tvShow, season, episode);

                boolean inputIsValid = true;

                inputIsValid &= (!episode.equals(""));
                inputIsValid &= (!episode.equals(season));
                inputIsValid &= (!episode.equals(tvShow));
                if (!episode.equals(""))
                    inputIsValid &= episode.matches("\\$[123]");

                if (inputIsValid) {
                    episodeField.setBackground(Color.white);
                    if (table.containsPattern(p)) {
                        addButton.setEnabled(false);
                        removeButton.setEnabled(true);
                    }
                    else {
                        addButton.setEnabled(true);
                        removeButton.setEnabled(false);
                    }
                }
                else {
                    if (!episode.equals(""))
                        episodeField.setBackground(Color.red);
                }

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkAndAct(e);
            }

        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = table.getSelectedRow();

                if (index >= 0) {
                    Pattern p = table.getPatternAt(index);
                    if (p != null) {
                        regExpField.setText(p.getPattern());
                        tvShowField.setText(p.getToShow());
                        seasonField.setText(p.getToSeason());
                        episodeField.setText(p.getToEpisode());
                        upButton.setEnabled(index != 0);
                        downButton.setEnabled(index + 1 < table.getModel().getRowCount());
                        removeButton.setEnabled(true);
                    }
                }
                else {
                    regExpField.setText("");
                    tvShowField.setText("");
                    seasonField.setText("");
                    episodeField.setText("");
                    regExpField.setBackground(Color.white);
                    tvShowField.setBackground(Color.white);
                    seasonField.setBackground(Color.white);
                    episodeField.setBackground(Color.white);
                    upButton.setEnabled(false);
                    downButton.setEnabled(false);
                    addButton.setEnabled(false);
                    removeButton.setEnabled(false);
                }
            }

        });

        table.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = table.rowAtPoint(e.getPoint());
                if (index < 0)
                    table.clearSelection();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

        });

        listScrollPane.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = table.rowAtPoint(e.getPoint());
                if (index < 0)
                    table.clearSelection();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

        });

        addButton.setEnabled(false);
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String regExp = regExpField.getText();
                String tvShow = tvShowField.getText();
                String season = seasonField.getText();
                String episode = episodeField.getText();
                Pattern p = new Pattern(regExp, tvShow, season, episode);

                table.addPattern(p);
                listScrollPane.invalidate();
                listScrollPane.validate();
                regExpField.setText("");
                tvShowField.setText("");
                seasonField.setText("");
                episodeField.setText("");
                addButton.setEnabled(false);
                removeButton.setEnabled(false);
            }

        });

        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String regExp = regExpField.getText();
                String tvShow = tvShowField.getText();
                String season = seasonField.getText();
                String episode = episodeField.getText();
                Pattern p = new Pattern(regExp, tvShow, season, episode);

                table.removePattern(p);
                regExpField.setText("");
                tvShowField.setText("");
                seasonField.setText("");
                episodeField.setText("");
                addButton.setEnabled(false);
                removeButton.setEnabled(false);
            }

        });

        upButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int index = table.getSelectedRow();

                if (index >= 0) {
                    int to = index - 1;
                    table.movePattern(index, to);
                    table.setRowSelectionInterval(to, to);
                }

            }

        });

        downButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int index = table.getSelectedRow();

                if (index >= 0) {
                    int to = index + 1;
                    table.movePattern(index, to);
                    table.setRowSelectionInterval(to, to);
                }
            }

        });

        JPanel buttonPane = new JPanel();

        Box regExpBox = Box.createVerticalBox();

        regExpBox.add(regExpLabel);
        regExpBox.add(regExpField);

        Box tvShowBox = Box.createVerticalBox();

        tvShowBox.add(tvShowLabel);
        tvShowBox.add(tvShowField);

        Box seasonBox = Box.createVerticalBox();

        seasonBox.add(seasonLabel);
        seasonBox.add(seasonField);

        Box episodeBox = Box.createVerticalBox();

        episodeBox.add(episodeLabel);
        episodeBox.add(episodeField);

        buttonPane.setLayout(new GridBagLayout());

        int yCoordinate = 0;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = yCoordinate;
        buttonPane.add(regExpLabel, c);

        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = yCoordinate;
        buttonPane.add(tvShowLabel, c);

        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = yCoordinate;
        buttonPane.add(seasonLabel, c);

        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = yCoordinate;
        buttonPane.add(episodeLabel, c);

        yCoordinate++;

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = yCoordinate;
        c.fill = GridBagConstraints.HORIZONTAL;
        buttonPane.add(regExpField, c);

        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = yCoordinate;
        c.fill = GridBagConstraints.HORIZONTAL;
        buttonPane.add(tvShowField, c);

        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = yCoordinate;
        c.fill = GridBagConstraints.HORIZONTAL;
        buttonPane.add(seasonField, c);

        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = yCoordinate;
        c.fill = GridBagConstraints.HORIZONTAL;
        buttonPane.add(episodeField, c);

        yCoordinate++;

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = yCoordinate;
        buttonPane.add(addButton, c);

        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = yCoordinate;
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL), c);

        c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = yCoordinate;
        buttonPane.add(removeButton, c);

        JPanel resortPane = new JPanel();
        resortPane.setLayout(new BoxLayout(resortPane, BoxLayout.PAGE_AXIS));

        resortPane.add(Box.createVerticalGlue());
        resortPane.add(upButton);
        resortPane.add(Box.createVerticalStrut(5));
        resortPane.add(downButton);
        resortPane.add(Box.createVerticalGlue());

        dialog.add(listScrollPane, BorderLayout.CENTER);
        dialog.add(resortPane, BorderLayout.EAST);
        dialog.add(buttonPane, BorderLayout.PAGE_END);

        dialog.setSize(440, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void showAboutDialog() {
        JOptionPane
                .showMessageDialog(this, Preferences.getLocalizationString("aboutBoxText"),
                        Preferences.getLocalizationString("aboutBoxTitle"),
                        JOptionPane.INFORMATION_MESSAGE);
    }

    public void openNewDirectory() {
        File lastOpenedDir = new File(Preferences.lastOpenedDirectory);
        if (lastOpenedDir.isDirectory()) {
            if (Preferences.isMacOS)
                directoryChooserMac.setDirectory(lastOpenedDir.getAbsolutePath());
            else
                directoryChooserOther.setCurrentDirectory(lastOpenedDir);
        }

        File d;

        if (Preferences.isMacOS) {
            directoryChooserMac.setVisible(true);
            String dir = directoryChooserMac.getDirectory();
            dir += directoryChooserMac.getFile();
            if (dir != null)
                d = new File(dir);
            else
                return;
        }
        else {
            int ret = directoryChooserOther.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION)
                d = directoryChooserOther.getSelectedFile();
            else
                return;
        }
        if (d.isDirectory())
            openNewDirectoryHelper(d);
    }

    private void openNewDirectoryHelper(File f) {
        iRenameTable.newDirOpened(f);
        for (UiCallbackListener uil : listener)
            uil.openNewDirectory(f);
        updateCurrentDirectoryInformation(f);
        renameUndoAll.setEnabled(true);
    }

    private void updateCurrentDirectoryInformation(File f) {

        currentDirectoryLabel.setText(Preferences.getLocalizationString("currentDirLabel") + " "
                + f.getAbsolutePath());
    }

    @Override
    public void noVideosFound() {
        JOptionPane.showMessageDialog(this, Preferences
                .getLocalizationString("noMovieFilesFoundInDirectory"), Preferences
                .getLocalizationString("warningDialogTitle"), JOptionPane.WARNING_MESSAGE);
    }

    private void quitApplication() {
        int ans = JOptionPane.showConfirmDialog(this, Preferences
                .getLocalizationValue("quitApplicationQuestion"), Preferences
                .getLocalizationString("quitApplicationTitle"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (ans == JOptionPane.YES_OPTION) {
            for (UiCallbackListener uil : listener)
                uil.quitApplication();
        }
    }

    @Override
    public void cannotRename(String errorMessage, FileElement fe) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fileSuccessfullyRenamed(FileElement fe) {
        // TODO Auto-generated method stub

    }

    public void fireEvent(UiCallbackListener.Type t, FileElement fe) {
        for (UiCallbackListener uil : listener) {
            uil.dataChanged(t, fe);
        }
    }

    class MyTransferHandler extends TransferHandler {

        private static final long serialVersionUID = -3521054658672544145L;

        @SuppressWarnings("unchecked")
        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable t = support.getTransferable();

            try {
                List<File> list = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

                if (list.size() == 1 && list.get(0).isDirectory()) {
                    openNewDirectoryHelper(list.get(0));
                }
            }
            catch (UnsupportedFlavorException e) {
                return false;
            }
            catch (IOException e) {
                return false;
            }

            return true;

        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

    }

    @Override
    public void addFileElement(FileElement fe) {
        iRenameTable.addFileElement(fe);
    }

    @Override
    public void updateData(FileElement fe) {
        iRenameTable.updateElement(fe);
    }

    @Override
    public void cannotUndo(String errorMessage, FileElement fe) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fileSuccessfullyUndoed(FileElement fe) {
        // TODO Auto-generated method stub

    }

    public void renameFile(FileElement fe) {
        for (UiCallbackListener uil : listener)
            uil.renameFile(fe);
    }

    public void undoRename(FileElement fe) {
        for (UiCallbackListener uil : listener)
            uil.undoRename(fe);
    }

    @Override
    public void startLoading(FileElement fe) {
        iRenameTable.startLoading(fe);
    }

    @Override
    public void stopLoading(FileElement fe) {
        iRenameTable.stopLoading(fe);
    }

}
