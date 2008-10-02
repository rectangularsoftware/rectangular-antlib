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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
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
    private static final TransformerFactory TRANSFORMER_FACTORY;
    static
    {
        // Process XInclude tags.
        System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",
                           "org.apache.xerces.parsers.XIncludeParserConfiguration");

        TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    }

    private final Transformer formattingObjectsTransformer;


    /**
     * Create a DocBookPublisher that uses the specified XSL stylesheet to generate
     * its output.
     */
    public DocBookPublisher(File stylesheetFile) throws IOException,
                                                        TransformerConfigurationException,
                                                        SAXException
    {
        InputStream styleSheetInputStream = null;
        try
        {
            SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) TRANSFORMER_FACTORY;

            TemplatesHandler templatesHandler = saxTransformerFactory.newTemplatesHandler();
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(templatesHandler);

            styleSheetInputStream = new FileInputStream(stylesheetFile);
            InputSource inputSource = new InputSource(styleSheetInputStream);
            inputSource.setSystemId(stylesheetFile.getPath());
            Source styleSheetSource = new SAXSource(reader, inputSource);
            formattingObjectsTransformer = TRANSFORMER_FACTORY.newTransformer(styleSheetSource);
        }
        finally
        {
            if (styleSheetInputStream != null)
            {
                styleSheetInputStream.close();
            }
        }
    }


    /**
     * Create a DocBookPublisher that uses the specified XSL stylesheet to generate
     * its output.
     */
    public DocBookPublisher(String stylesheetPath) throws IOException,
                                                          TransformerConfigurationException,
                                                          SAXException
    {
        this(new File(stylesheetPath));
    }


    public static void main(String[] args) throws Exception
    {
        new DocBookPublisher(args[0]).createDocument(new File(args[1]),
                                                     new File(args[2]),
                                                     MimeConstants.MIME_PDF);
    }


    public void createDocument(File docbookSourceFile,
                               File outputFile,
                               String type) throws IOException,
                                                   TransformerException,
                                                   FOPException
    {
        InputStream docbookInputStream = null;
        OutputStream pdfOutputStream = null;
        try
        {
            pdfOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            docbookInputStream = new FileInputStream(docbookSourceFile);
            Source docbookSource = new SAXSource(new InputSource(docbookInputStream));
            docbookSource.setSystemId(docbookSourceFile.getPath());

            FopFactory fopFactory = FopFactory.newInstance();
            Fop fop = fopFactory.newFop(type, pdfOutputStream);
            formattingObjectsTransformer.transform(docbookSource,
                                                   new SAXResult(fop.getDefaultHandler()));
        }
        finally
        {
            if (docbookInputStream != null)
            {
                docbookInputStream.close();
            }
            if (pdfOutputStream != null)
            {
                pdfOutputStream.close();
            }
        }
    }
}
