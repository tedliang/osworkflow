/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.workflow;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.memory.MemoryPropertySet;

import com.opensymphony.workflow.basic.BasicWorkflow;

import junit.framework.TestCase;

import java.util.HashMap;


/**
 *
 *
 * @author $Author: hani $
 * @version $Revision: 1.2 $
 */
public class TestAbstractWorkflow extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    AbstractWorkflow wf = new AbstractWorkflow();

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testVariableTranslation() {
        HashMap transients = new HashMap();
        MemoryPropertySet ps = new MemoryPropertySet();
        ps.init(null, null);

        A a = new A("aName", new B(100, "bName"));
        A a2 = new A("biff", new B(-1, "jack"));
        transients.put("a", a);
        transients.put("blah", "blah");
        ps.setString("blah", "NOT BLAH");
        ps.setObject("a2", a2);
        ps.setString("foo", "bar");

        assertEquals("aName", wf.getVariableFromMaps("a.name", transients, ps));
        assertEquals(a, wf.getVariableFromMaps("a", transients, ps));
        assertEquals("blah", wf.getVariableFromMaps("blah", transients, ps));
        assertEquals("jack", wf.getVariableFromMaps("a2.b.name", transients, ps));
        assertEquals(new Integer(-1), wf.getVariableFromMaps("a2.b.age", transients, ps));
        assertEquals("bar", wf.getVariableFromMaps("foo", transients, ps));

        assertEquals("hello, jack, what is your age? -1", wf.translateVariables("hello, ${a2.b.name}, what is your age? ${a2.b.age}", transients, ps));
        assertEquals("hello, , what is your age? -1", wf.translateVariables("hello, ${I.Don't.EXIST}, what is your age? ${a2.b.age}", transients, ps));
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    public class A {
        B b;
        String name;

        public A(String name, B b) {
            this.name = name;
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public String getName() {
            return name;
        }
    }

    public class B {
        String name;
        int age;

        public B(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }
    }
}
