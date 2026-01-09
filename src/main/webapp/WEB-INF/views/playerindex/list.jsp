<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.wcdf.model.PlayerIndex" %>
<%@ page import="com.example.wcdf.model.Player" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Player Index List</title>
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
        .filter-info {
            background: #fff3cd;
            color: #856404;
            padding: 12px 15px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .filter-info a { color: #007bff; }
        .toolbar {
            margin-bottom: 20px;
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
        .value-cell {
            font-weight: 700;
            color: #007bff;
        }
        .range-cell {
            color: #666;
            font-size: 13px;
        }
        .actions a {
            margin-right: 10px;
            text-decoration: none;
            color: #007bff;
        }
        .actions a:hover { text-decoration: underline; }
        .actions a.delete { color: #dc3545; }
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
        <h2>Player Index Values</h2>

        <!-- Navigation -->
        <div class="nav">
            <a href="<%=request.getContextPath()%>/player?action=list">Players</a>
            <a href="<%=request.getContextPath()%>/playerindex?action=list" class="active">Player Indexes</a>
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

        <!-- Filter Info -->
        <% Player selectedPlayer = (Player) request.getAttribute("selectedPlayer"); %>
        <% if (selectedPlayer != null) { %>
            <div class="filter-info">
                Showing indexes for: <strong><%= selectedPlayer.getName() %></strong>
                &nbsp;&mdash;&nbsp;
                <a href="<%=request.getContextPath()%>/playerindex?action=list">Show All</a>
            </div>
        <% } %>

        <!-- Toolbar -->
        <div class="toolbar">
            <a href="<%=request.getContextPath()%>/playerindex?action=new<%= selectedPlayer != null ? "&playerId=" + selectedPlayer.getPlayerId() : "" %>" class="btn">
                + Add New Index Value
            </a>
        </div>

        <!-- Player Index Table -->
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Player</th>
                    <th>Index</th>
                    <th>Value</th>
                    <th>Range</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<PlayerIndex> playerIndexes = (List<PlayerIndex>) request.getAttribute("playerIndexes");
                if (playerIndexes != null && !playerIndexes.isEmpty()) {
                    for (PlayerIndex pi : playerIndexes) {
                %>
                <tr>
                    <td><%= pi.getId() %></td>
                    <td><%= pi.getPlayerName() %></td>
                    <td><%= pi.getIndexerName() %></td>
                    <td class="value-cell"><%= pi.getValue() %></td>
                    <td class="range-cell"><%= pi.getValueMin() %> - <%= pi.getValueMax() %></td>
                    <td class="actions">
                        <a href="<%=request.getContextPath()%>/playerindex?action=edit&id=<%= pi.getId() %>">Edit</a>
                        <a href="<%=request.getContextPath()%>/playerindex?action=delete&id=<%= pi.getId() %>"
                           class="delete" onclick="return confirm('Are you sure you want to delete this index value?');">Delete</a>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr><td colspan="6" class="empty">No index values found</td></tr>
                <% } %>
            </tbody>
        </table>

        <p class="total">Total: <strong><%= playerIndexes != null ? playerIndexes.size() : 0 %></strong> record(s)</p>
    </div>
</body>
</html>

