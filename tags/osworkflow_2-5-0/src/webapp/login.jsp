<%@ page import="com.opensymphony.module.user.UserManager,
                 com.opensymphony.module.user.EntityNotFoundException"%>
<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    UserManager um = UserManager.getInstance();
    boolean authenticated = false;
    try {
        authenticated = um.getUser(username).authenticate(password);
    } catch (EntityNotFoundException e) {
    }

    if (authenticated) {
        session.setAttribute("username", username);
        response.sendRedirect("nav.jsp");
    } else {
        response.sendRedirect("default.jsp?auth_failed=true");
    }
%>