// ============================================================================
//   Copyright 2008-2012 Daniel W. Dyer
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

import org.apache.fop.apps.MimeConstants;

/**
 * Enumerates the output formats supported by the DocBook Ant task.
 * @author Daniel Dyer
 */
enum OutputFormat
{
    PDF("fo.xsl", null, MimeConstants.MIME_PDF, ".pdf"),
    RTF("fo.xsl", null, MimeConstants.MIME_RTF, ".rtf"),
    HTML("html.xsl", "html-chunked.xsl", null, ".html");

    private final String stylesheet;
    private final String chunkedStylesheet;
    private final String fopMimeType;
    private final String fileExtension;

    OutputFormat(String stylesheet,
                 String chunkedStyleSheet,
                 String fopMimeType,
                 String fileExtension)
    {
        this.stylesheet = stylesheet;
        this.chunkedStylesheet = chunkedStyleSheet;
        this.fopMimeType = fopMimeType;
        this.fileExtension = fileExtension;
    }


    public String getStylesheet(boolean chunked)
    {
        // Chunking is ignored if it is not supported for this format.
        return chunked && chunkedStylesheet != null ? chunkedStylesheet : stylesheet;
    }

    
    public String getFopMimeType()
    {
        return fopMimeType;
    }


    public String getFileExtension()
    {
        return fileExtension;
    }
}
