<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${not empty pageTitle}">
        <c:out value="${pageTitle}" />
    </c:when>
</c:choose>