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

public class FileElement {

    private File   sourceFile;
    private String tvShowName;
    private String seasonNumber;
    private String episodeNumber;
    private String episodeName;
    private String targetFilename;

    public FileElement(File f) {
        sourceFile = f;

        tvShowName = Util.guessTvShowName(this);
        seasonNumber = Util.guessSeasonNumber(this);
        episodeNumber = Util.guessEpisodeNumber(this);
        episodeName = "";

        targetFilename = Util.applyPattern(this);
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setTvShowName(String tvShowName) {
        this.tvShowName = tvShowName;

    }

    public String getTvShowName() {
        return tvShowName;
    }

    public void setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;

    }

    public String getSeasonNumber() {
        return seasonNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;

    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;

    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setTargetFilename(String filename) {
        targetFilename = filename;

    }

    public String getTargetFilename() {
        return targetFilename;
    }

    @Override
    public int hashCode() {
        return sourceFile.getAbsolutePath().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof FileElement) {
            FileElement fe = (FileElement) o;
            return sourceFile.getAbsolutePath().equals(fe.getSourceFile().getAbsolutePath());
        }
        return false;
    }
}
