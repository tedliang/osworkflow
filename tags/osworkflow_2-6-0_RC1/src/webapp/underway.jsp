<%@ page import="com.opensymphony.user.User,
                 com.opensymphony.workflow.Workflow,
                 com.opensymphony.workflow.basic.BasicWorkflow,
                 com.opensymphony.workflow.spi.Step,
                 java.util.*,
                 com.opensymphony.workflow.loader.WorkflowDescriptor,
                 com.opensymphony.workflow.loader.ActionDescriptor,
                 com.opensymphony.workflow.query.WorkflowQuery"%>
<%
    Workflow wf = new BasicWorkflow((String) session.getAttribute("username"));
    WorkflowQuery queryLeft = new WorkflowQuery(WorkflowQuery.OWNER, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, session.getAttribute("username"));
    WorkflowQuery queryRight = new WorkflowQuery(WorkflowQuery.STATUS, WorkflowQuery.CURRENT, WorkflowQuery.EQUALS, "Underway");
    WorkflowQuery query = new WorkflowQuery(queryLeft, WorkflowQuery.AND, queryRight);
    List workflows = wf.query(query);
    for (Iterator iterator = workflows.iterator(); iterator.hasNext();) {
        Long wfId = (Long) iterator.next();
%>
    <li><a href="test.jsp?id=<%= wfId %>"><%= wfId %></li>
<%
    }
%>

<%@ include file="nav.jsp" %>