/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow.loader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;


/**
 * @author Hani Suleiman (hani@formicary.net)
 * Date: Sep 14, 2003
 * Time: 4:25:40 PM
 */
public class DTDEntityResolver implements EntityResolver {
    //~ Methods ////////////////////////////////////////////////////////////////

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId == null) {
            return null;
        }

        URL url = new URL(systemId);
        String file = url.getFile();

        if ((file != null) && (file.indexOf('/') > -1)) {
            file = file.substring(file.lastIndexOf('/') + 1);
        }

        if ("www.opensymphony.com".equals(url.getHost())) {
            InputStream is = getClass().getResourceAsStream("/META-INF/" + file);

            if (is == null) {
                is = getClass().getResourceAsStream("/" + file);
            }

            if (is != null) {
                return new InputSource(is);
            }
        }

        return null;
    }
}
