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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Utility class for generating formatted output from DocBook source files.
 * @author Daniel Dyer
 */
public class DocBookPublisher
{
    /**
     * Path to DocBook FO stylesheet on the classpath.
     */
    private static final String STYLESHEET_PATH = "docbook-xsl/";

    private static final TransformerFactory TRANSFORMER_FACTORY;
    static
    {
        // Process XInclude tags.
        System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",
                           "org.apache.xerces.parsers.XIncludeParserConfiguration");

        TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    }

    private final OutputFormat format;
    private final Transformer docBookTransformer;


    /**
     * Create a DocBookPublisher that uses the specified XSL stylesheet to generate
     * its output.
     */
    public DocBookPublisher(String outputFormat) throws IOException,
                                                        TransformerConfigurationException,
                                                        SAXException
    {
        format = OutputFormat.valueOf(outputFormat.toUpperCase());

        SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) TRANSFORMER_FACTORY;

        TemplatesHandler templatesHandler = saxTransformerFactory.newTemplatesHandler();
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(templatesHandler);

        URL styleSheetURL = getClass().getClassLoader().getResource(STYLESHEET_PATH + format.getStylesheet());
        InputSource inputSource = new InputSource(styleSheetURL.openStream());
        inputSource.setSystemId(styleSheetURL.toExternalForm());
        Source styleSheetSource = new SAXSource(reader, inputSource);
        docBookTransformer = TRANSFORMER_FACTORY.newTransformer(styleSheetSource);
    }


    public static void main(String[] args) throws Exception
    {
        new DocBookPublisher(OutputFormat.PDF.name()).createDocument(new File(args[0]),
                                                                     new File(args[1]),
                                                                     new HashMap<String, String>(0));
    }


    public void createDocument(File docbookSourceFile,
                               File outputFile,
                               Map<String, String> parameters) throws IOException,
                                                                      TransformerException,
                                                                      FOPException
    {
        InputStream docbookInputStream = null;
        OutputStream outputStream = null;
        try
        {
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            docbookInputStream = new FileInputStream(docbookSourceFile);
            Source docbookSource = new SAXSource(new InputSource(docbookInputStream));
            docbookSource.setSystemId(docbookSourceFile.getPath());

            // Set DocBook XSL parameters.
            for (Map.Entry<String, String> entry : parameters.entrySet())
            {
                docBookTransformer.setParameter(entry.getKey(), entry.getValue());
            }

            if (format.getFopMimeType() == null) // FO is not used as an intermediate representation.
            {
                docBookTransformer.transform(docbookSource, new StreamResult(outputFile));
            }
            else
            {
                FopFactory fopFactory = FopFactory.newInstance();
                Fop fop = fopFactory.newFop(format.getFopMimeType(), outputStream);
                docBookTransformer.transform(docbookSource,
                                                       new SAXResult(fop.getDefaultHandler()));
            }
        }
        finally
        {
            if (docbookInputStream != null)
            {
                docbookInputStream.close();
            }
            if (outputStream != null)
            {
                outputStream.close();
            }
        }
    }
}
