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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * Apache Ant custom task for generating documentation from DocBook source.
 * @author Daniel Dyer
 */
public class DocBook extends Task
{
    private static final String PUBLISHER_CLASS = "org.uncommons.antlib.tasks.DocBookPublisher";

    /** Classpath to use when trying to load the XSL processor */
    private Path classpath = null;

    private File source;
    private File output;
    private static final String DEFAULT_OUTPUT = "application/pdf";


    public void setSource(String source)
    {
        this.source = new File(source);
    }


    public void setOutput(String output)
    {
        this.output = new File(output);
    }


    /**
     * Set the optional classpath to the XSL processor
     *
     * @param classpath the classpath to use when loading the XSL processor
     */
    public void setClasspath(Path classpath)
    {
        createClasspath().append(classpath);
    }


    /**
     * Set the optional classpath to the XSL processor
     *
     * @return a path instance to be configured by the Ant core.
     */
    public Path createClasspath()
    {
        if (classpath == null)
        {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }


    /**
     * Set the reference to an optional classpath to the XSL processor
     *
     * @param r The id of the Ant path instance to act as the classpath
     *          for loading the XSL processor.
     */
    public void setClasspathRef(Reference r)
    {
        createClasspath().setRefid(r);
    }


    @Override
    public void execute() throws BuildException
    {
        checkArguments();

        // We have to use a new class loader here because, unless Saxon or Xalan
        // is already in the Ant lib directory, we won't be able to load a working
        // XSLT2 transformer.
        AntClassLoader classLoader = null;
        try
        {
            if (classpath != null)
            {
                classLoader = getProject().createClassLoader(classpath);
                classLoader.setThreadContextLoader();
            }
            // We have to load the publisher class via reflection to avoid it
            // being loaded by the class loader that loaded this task.
            Class<?> publisherClass = classLoader == null
                                      ? Class.forName(PUBLISHER_CLASS)
                                      : Class.forName(PUBLISHER_CLASS, true, classLoader);
            Constructor<?> constructor = publisherClass.getConstructor();
            Object publisher = constructor.newInstance();
            Method method = publisherClass.getMethod("createDocument",
                                                     File.class,
                                                     File.class,
                                                     String.class);
            method.invoke(publisher, source, output, DEFAULT_OUTPUT);
        }
        catch (Exception ex)
        {
            throw new BuildException(ex);
        }
        finally
        {
            if (classLoader != null)
            {
                classLoader.resetThreadContextLoader();
                classLoader.cleanup();
            }
        }
    }


    /**
     * Make sure all necessary properties have been set and are valid.
     * @throws BuildException If the task is not properly configured.
     */
    private void checkArguments() throws BuildException
    {
        if (source == null)
        {
            throw new BuildException("Location of document source must be specified.");
        }
        if (output == null)
        {
            throw new BuildException("Output file must be specified.");
        }
    }
}
