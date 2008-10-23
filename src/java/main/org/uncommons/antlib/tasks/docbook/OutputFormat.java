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

import org.apache.fop.apps.MimeConstants;

/**
 * Enumerates the output formats supported by the DocBook Ant task.
 * @author Daniel Dyer
 */
enum OutputFormat
{
    PDF("fo/docbook.xsl", MimeConstants.MIME_PDF),
    RTF("fo/docbook.xsl", MimeConstants.MIME_RTF),
    HTML("html/docbook.xsl", null);

    private final String stylesheet;

    private final String fopMimeType;

    private OutputFormat(String stylesheet, String fopMimeType)
    {
        this.stylesheet = stylesheet;
        this.fopMimeType = fopMimeType;
    }


    public String getStylesheet()
    {
        return stylesheet;
    }

    public String getFopMimeType()
    {
        return fopMimeType;
    }
}
