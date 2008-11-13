// ============================================================================
//   Copyright 2008 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.antlib.tasks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import java.util.List;
import java.util.LinkedList;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

/**
 * The default Ant GZip task is only useful for zipping individually identified
 * files.  This task zips each file in a given directory (in-place).  For example,
 * if a dirctory contains a.txt, b.txt and c.txt, it will contain
 * a.txt.gz, b.txt.gz and c.txt.gz after this task has executed on it.
 * @author Daniel Dyer
 */
public class GZip extends Task
{
    private boolean replaceOriginals = true;
    private List<FileSet> filesets = new LinkedList<FileSet>();


    /**
     * If this property is true, the uncompressed source files are
     * removed and only the zipped files remain.  If the property is
     * false, the originals are retained alongside the compressed
     * versions.
     * @param replaceOriginals Whether or not to remove the original files.
     */
    public void setReplaceOriginals(boolean replaceOriginals)
    {
        this.replaceOriginals = replaceOriginals;
    }

    
    public void addFileset(FileSet fileset)
    {
        filesets.add(fileset);
    }


    @Override
    public void execute() throws BuildException
    {
        checkArguments();
        try
        {
            for (FileSet fileset : filesets)
            {
                DirectoryScanner scanner = fileset.getDirectoryScanner(getProject());
                for (String file : scanner.getIncludedFiles())
                {
                    gzipFile(new File(fileset.getDir(), file));
                }
            }
        }
        catch (IOException ex)
        {
            throw new BuildException(ex);
        }
    }


    /**
     * GZips a single file, creating a new file, with a .gz suffix, in the same directory.
     * @param file The file to compress.
     * @throws IOException If there is a problem reading the source file or writing to the
     * target file.
     */
    private void gzipFile(File file) throws IOException
    {
        InputStream input = null;
        GZIPOutputStream gzipOutput = null;
        try
        {
            input = new BufferedInputStream(new FileInputStream(file));

            String zippedPath = file.getAbsolutePath() + ".gz";
            gzipOutput = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(zippedPath)));

            int value = input.read();
            while (value >= 0)
            {
                gzipOutput.write(value);
                value = input.read();
            }
            gzipOutput.flush();
            gzipOutput.close();
            if (replaceOriginals)
            {
                file.delete();
            }
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
            if (gzipOutput != null)
            {
                gzipOutput.close();
            }
        }
    }


    private void checkArguments() throws BuildException
    {
        if (filesets.isEmpty())
        {
            throw new BuildException("At least one file set must be specified.");
        }
    }
}
