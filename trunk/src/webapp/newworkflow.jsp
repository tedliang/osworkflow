<%@ page import="com.opensymphony.workflow.Workflow,
                 com.opensymphony.workflow.basic.BasicWorkflow"%>

<%
    Workflow wf = new BasicWorkflow((String) session.getAttribute("username"));
    long id = wf.initialize("example", 100, null);
%>

New workflow entry <a href="test.jsp?id=<%=id%>">#<%=id%></a> created and initialized!

<%@ include file="nav.jsp" %>