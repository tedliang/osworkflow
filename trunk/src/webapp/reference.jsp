<%@ page import="com.opensymphony.user.UserManager,
                 com.opensymphony.user.User,
                 com.opensymphony.user.Group"%>
<%
    UserManager um = UserManager.getInstance();
    User test = um.createUser("test");
    test.setPassword("test");

    Group foos = um.createGroup("foos");
    Group bars = um.createGroup("bars");
    Group bazs = um.createGroup("bazs");

    test.addToGroup(foos);
    test.addToGroup(bars);
    test.addToGroup(bazs);

    response.sendRedirect(request.getContextPath() + "/default.jsp");
%>