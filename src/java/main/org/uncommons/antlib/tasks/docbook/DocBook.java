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
package org.uncommons.antlib.tasks.docbook;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
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
    private static final String PUBLISHER_CLASS = "org.uncommons.antlib.tasks.docbook.DocBookPublisher";

    /** Classpath to use when trying to load the XSL processor */
    private Path classpath = null;
    private File source;
    private File outputDir;
    private String format = "PDF"; // Default to PDF, can be over-riden in build script.
    private boolean chunked = false;

    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * @param source The path of the DocBook root source file.
     */
    public void setSource(String source)
    {
        this.source = new File(source);
    }


    /**
     * @param output The path of the target output file.
     */
    public void setOutputDir(String output)
    {
        this.outputDir = new File(output);
    }


    /**
     * @param format An output format mime-type recognised by FOP (defaults to "application/pdf").
     */
    public void setFormat(String format)
    {
        this.format = format;
    }


    /**
     * If the output format supports it (e.g. HTML), then the output is chunked if this
     * property is set to true.
     * @param chunked Whether or not to divide the output into multiple files (or "chunks").
     */
    public void setChunked(boolean chunked)
    {
        this.chunked = chunked;
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


    /**
     * Adds a parameter to control the DocBook XSL translation.  These parameters
     * can over-ride defaults such as paper size, margins, fonts, page orientation, etc.
     * @param parameter Name and value of the DocBook parameter.
     */
    public void addConfiguredParameter(Parameter parameter)
    {
        parameters.put(parameter.getName(), parameter.getValue());
    }


    @Override
    public void execute() throws BuildException
    {
        checkArguments();

        // We have to use a new class loader here because, unless Saxon or Xalan
        // is already in the Ant lib directory, we won't be able to load a working
        // XSLT transformer.
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
            Constructor<?> constructor = publisherClass.getConstructor(String.class, Boolean.TYPE);
            Object publisher = constructor.newInstance(format, chunked);
            Method method = publisherClass.getMethod("createDocument",
                                                     File.class,
                                                     File.class,
                                                     Map.class);
            method.invoke(publisher, source, outputDir, parameters);
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
        if (outputDir == null || !outputDir.isDirectory())
        {
            throw new BuildException("Output directory must be specified.");
        }
        if (!format.equalsIgnoreCase("PDF")
            && !format.equalsIgnoreCase("RTF")
            && !format.equalsIgnoreCase("HTML"))
        {
            throw new BuildException("Unsupported output format: " + format);
        }
    }


    /**
     * Models parameters passed to the DocBook stylesheets.
     */
    public static class Parameter
    {
        private String name;
        private String value;


        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }
    }
}
