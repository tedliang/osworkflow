<%@ page import="com.opensymphony.user.User,
                 com.opensymphony.workflow.Workflow,
                 com.opensymphony.workflow.basic.BasicWorkflow,
                 com.opensymphony.workflow.spi.Step,
                 java.util.*,
                 com.opensymphony.workflow.loader.WorkflowDescriptor,
                 com.opensymphony.workflow.loader.ActionDescriptor"%>
<%
    Workflow wf = new BasicWorkflow((String) session.getAttribute("username"));

    long id = Long.parseLong(request.getParameter("id"));

    String doString = request.getParameter("do");
    if (doString != null && !doString.equals("")) {
        int action = Integer.parseInt(doString);
        wf.doAction(id, action, Collections.EMPTY_MAP);
    }


    int[] actions = wf.getAvailableActions(id, null);
    WorkflowDescriptor wd =  wf.getWorkflowDescriptor(wf.getWorkflowName(id));

    for (int i = 0; i < actions.length; i++) {
        String name = wd.getAction(actions[i]).getName();
        %>
        <li> <a href="test.jsp?id=<%=id%>&do=<%= actions[i] %>"><%= name %></a>
        <%
    }
%>

<hr>
<b>Permissions</b>
<p>

<%
    List perms = wf.getSecurityPermissions(id, null);
    for (Iterator iterator = perms.iterator(); iterator.hasNext();) {
        String perm = (String) iterator.next();
%>
    <li><%= perm %>
<%
    }
%>
<hr>


<table border="1">
<tr>
    <th>Step</th>
    <th>Action</th>
    <th>Status</th>
    <th>Owner</th>
    <th>Start Date</th>
    <th>Finish Date</th>
    <th>Previous</th>
</tr>


<%
    ArrayList steps = new ArrayList();
    steps.addAll(wf.getCurrentSteps(id));
    steps.addAll(wf.getHistorySteps(id));
    for (Iterator iterator = steps.iterator(); iterator.hasNext();) {
        Step step = (Step) iterator.next();
        String owner = step.getOwner();
        ActionDescriptor action = wd.getAction(step.getActionId());
        %>
        <tr>
            <td><%= wd.getStep(step.getStepId()).getName()%> (<%= step.getId() %>)</td>
            <td><%= action == null ? "NONE" : action.getName() %></td>
            <td><%= step.getStatus() %></td>
            <td><%= owner %></td>
            <td><%= step.getStartDate() %></td>
            <td><%= step.getFinishDate() %></td>
            <td><%
                long[] prevIds = step.getPreviousStepIds();
                if (prevIds != null) {
                    for (int i = 0; i < prevIds.length; i++) {
                        long prevId = prevIds[i];
                        out.print(prevId + ", ");
                    }
                } else {
                    out.print("none");
                }
            %></td>
        </tr>
        <%
    }
%>

</table>
<a href="#" onclick="window.open('viewlivegraph.jsp?id=<%=id%>')">Viw Live Graph</a>
<%@ include file="nav.jsp" %>