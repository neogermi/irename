package net.sourceforge.irename;

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

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.irename.ui.Ui;
import net.sourceforge.irename.ui.UiCallbackListener;
import net.sourceforge.irename.ui.gui.GUIImpl;
import net.sourceforge.irename.util.FileElement;
import net.sourceforge.irename.util.Util;
import net.sourceforge.irename.util.VideoFileFilter;

public class IRename implements UiCallbackListener {

    private Ui              ui;
    private VideoFileFilter videoFileFilter;

    private ExecutorService httpRequestThreadPool;
    private ExecutorService renameThreadPool;

    public IRename() {
        videoFileFilter = new VideoFileFilter(Preferences.validFileEndings);
        httpRequestThreadPool = Executors.newFixedThreadPool(5);
        renameThreadPool = Executors.newFixedThreadPool(5);

        ui = new GUIImpl(this);
    }

    @Override
    public void openNewDirectory(File d) {
        Preferences.lastOpenedDirectory = d.getAbsolutePath();

        File[] filesFound = d.listFiles(videoFileFilter);

        if (filesFound.length == 0)
            ui.noVideosFound();
        else {
            for (File f : filesFound) {
                final FileElement fe = new FileElement(f);
                ui.addFileElement(fe);

                if (Preferences.retrieveOnlineData) {
                    Runnable runner = new Runnable() {
                        @Override
                        public void run() {
                            ui.startLoading(fe);
                            fe.setEpisodeName(Util.retrieveEpisodeName(fe));
                            fe.setTargetFilename(Util.applyPattern(fe));
                            ui.stopLoading(fe);
                            ui.updateData(fe);
                        }
                    };
                    httpRequestThreadPool.execute(runner);
                }
            }
        }
    }

    @Override
    public void dataChanged(final Type type, final FileElement fe) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                if (Preferences.retrieveOnlineData && type != UiCallbackListener.Type.EpisodeName) {
                    ui.startLoading(fe);
                    fe.setEpisodeName(Util.retrieveEpisodeName(fe));
                    ui.stopLoading(fe);
                }
                fe.setTargetFilename(Util.applyPattern(fe));
                ui.updateData(fe);
            }
        };
        httpRequestThreadPool.execute(runner);
    }

    @Override
    public void undoRename(final FileElement fe) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                File sourceFile = new File(fe.getSourceFile().getParent(), fe.getTargetFilename());
                File targetFile = fe.getSourceFile();

                System.out.println("undoRename(" + sourceFile + " -> " + targetFile);

                //TODO: DEBUG
                //                if (targetFile.exists()) {
                //                    ui.cannotUndo("", fe);//TODO
                //                }
                //
                //                if (!sourceFile.renameTo(targetFile)) {
                //                    ui.cannotUndo("", fe);//TODO
                //                }
                //                else
                ui.fileSuccessfullyUndoed(fe);//TODO
            }
        };
        renameThreadPool.execute(runner);

    }

    @Override
    public void renameFile(final FileElement fe) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                File sourceFile = fe.getSourceFile();
                File targetFile = new File(fe.getSourceFile().getParent(), fe.getTargetFilename());
                System.out.println("renameFile(" + sourceFile + " -> " + targetFile);

                //TODO: DEBUG
                //                if (targetFile.exists()) {
                //                    ui.cannotRename("", fe);//TODO
                //                }
                //
                //                if (!sourceFile.renameTo(targetFile)) {
                //                    ui.cannotRename("", fe);//TODO
                //                }
                //                else
                ui.fileSuccessfullyRenamed(fe);//TODO
            }
        };
        renameThreadPool.execute(runner);

    }

    @Override
    public void quitApplication() {
        Preferences.writeChanges();
        System.exit(0);
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        IRename iRename = new IRename();
    }

    @Override
    public void addValidFileEnding(String newFileEnding) {

        Preferences.validFileEndings.add(newFileEnding);
        videoFileFilter.addFileEnding(newFileEnding);
    }

    @Override
    public void removeValidFileEnding(String newFileEnding) {

        Preferences.validFileEndings.remove(newFileEnding);
        videoFileFilter.removeFileEnding(newFileEnding);
    }

}
