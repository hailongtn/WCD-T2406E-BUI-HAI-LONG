<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Redirect thẳng đến danh sách Player
    response.sendRedirect(request.getContextPath() + "/player?action=list");
%>
