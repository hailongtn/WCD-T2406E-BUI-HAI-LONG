<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.wcdf.model.Player" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Player List</title>
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, sans-serif;
            margin: 0; padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: #fff;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        h2 {
            margin: 0 0 20px 0;
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        .nav {
            margin-bottom: 20px;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        .nav a {
            text-decoration: none;
            color: #555;
            margin-right: 20px;
            padding: 8px 15px;
            border-radius: 4px;
            transition: all 0.2s;
        }
        .nav a:hover, .nav a.active {
            background: #007bff;
            color: #fff;
        }
        .msg-success {
            background: #d4edda;
            color: #155724;
            padding: 12px 15px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .msg-error {
            background: #f8d7da;
            color: #721c24;
            padding: 12px 15px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 10px;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background: #007bff;
            color: #fff;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        .btn:hover { background: #0056b3; }
        .btn-sm { padding: 6px 12px; font-size: 13px; }
        .btn-danger { background: #dc3545; }
        .btn-danger:hover { background: #c82333; }
        .btn-secondary { background: #6c757d; }
        .btn-secondary:hover { background: #5a6268; }
        .search-form { display: flex; gap: 8px; }
        .search-form input[type="text"] {
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            width: 200px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        th {
            background: #f8f9fa;
            font-weight: 600;
            color: #333;
        }
        tr:hover { background: #f8f9fa; }
        .actions { white-space: nowrap; }
        .actions a {
            display: inline-block;
            width: 32px;
            height: 32px;
            line-height: 32px;
            text-align: center;
            text-decoration: none;
            font-size: 18px;
            border-radius: 4px;
            margin-right: 5px;
            transition: all 0.2s;
        }
        .actions a:hover {
            background: #e9ecef;
        }
        .actions a.delete { color: #dc3545; }
        .actions a.delete:hover { background: #f8d7da; }
        .total {
            margin-top: 15px;
            color: #666;
            font-size: 14px;
        }
        .empty {
            text-align: center;
            padding: 40px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Player Evaluation System</h2>

        <!-- Navigation -->
        <div class="nav">
            <a href="<%=request.getContextPath()%>/player?action=list" class="active">Players</a>
        </div>

        <!-- Messages -->
        <% String successMsg = (String) session.getAttribute("successMessage"); %>
        <% String errorMsg = (String) session.getAttribute("errorMessage"); %>
        <% if (successMsg != null) { %>
            <div class="msg-success"><%= successMsg %></div>
            <% session.removeAttribute("successMessage"); %>
        <% } %>
        <% if (errorMsg != null) { %>
            <div class="msg-error"><%= errorMsg %></div>
            <% session.removeAttribute("errorMessage"); %>
        <% } %>

        <!-- Toolbar -->
        <div class="toolbar">
            <a href="<%=request.getContextPath()%>/player?action=new" class="btn">+ Add New Player</a>

            <form action="<%=request.getContextPath()%>/player" method="get" class="search-form">
                <input type="hidden" name="action" value="search">
                <input type="text" name="searchName" placeholder="Search by name..." value="<%= request.getAttribute("searchName") != null ? request.getAttribute("searchName") : "" %>">
                <button type="submit" class="btn btn-secondary btn-sm">Search</button>
                <% if (request.getAttribute("searchName") != null) { %>
                    <a href="<%=request.getContextPath()%>/player?action=list" class="btn btn-sm" style="background:#6c757d;">Clear</a>
                <% } %>
            </form>
        </div>

        <!-- Player Table -->
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Age</th>
                    <th>Index Name</th>
                    <th>Value</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Player> players = (List<Player>) request.getAttribute("players");
                if (players != null && !players.isEmpty()) {
                    for (Player player : players) {
                %>
                <tr>
                    <td><%= player.getPlayerId() %></td>
                    <td><%= player.getName() %></td>
                    <td><%= player.getAge() %></td>
                    <td><%= player.getIndexerName() != null ? player.getIndexerName() : "-" %></td>
                    <td><%= player.getIndexValue() != null ? player.getIndexValue() : "-" %></td>
                    <td class="actions">
                        <a href="<%=request.getContextPath()%>/player?action=edit&id=<%= player.getPlayerId() %>" title="Edit">&#9998;</a>
                        <a href="<%=request.getContextPath()%>/player?action=delete&id=<%= player.getPlayerId() %>"
                           class="delete" title="Delete" onclick="return confirm('Are you sure you want to delete this player?');">&#128465;</a>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr><td colspan="6" class="empty">No players found</td></tr>
                <% } %>
            </tbody>
        </table>

        <p class="total">Total: <strong><%= players != null ? players.size() : 0 %></strong> player(s)</p>
    </div>
</body>
</html>

