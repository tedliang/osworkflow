/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 30-nov-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.workflow.spi.hibernate3;

import org.hibernate.type.StringType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class NotNullStringType extends StringType {
    //~ Constructors ///////////////////////////////////////////////////////////

    public NotNullStringType() {
        super();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object get(ResultSet rs, String name) throws SQLException {
        return "!void!".equals(super.get(rs, name)) ? "" : super.get(rs, name);
    }

    public void set(PreparedStatement st, Object value, int index) throws SQLException {
        if (!"".equals(value)) {
            super.set(st, value, index);
        } else {
            super.set(st, "!void!", index);
        }
    }
}
