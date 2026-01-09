<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.wcdf.model.Player" %>
<%@ page import="com.example.wcdf.model.Indexer" %>
<%@ page import="com.example.wcdf.model.PlayerIndex" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= request.getAttribute("playerIndex") != null && ((PlayerIndex)request.getAttribute("playerIndex")).getId() > 0 ? "Edit" : "Add" %> Player Index</title>
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, sans-serif;
            margin: 0; padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 600px;
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
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .back-link:hover { text-decoration: underline; }
        .error-box {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #dc3545;
        }
        .error-box ul { margin: 10px 0 0 0; padding-left: 20px; }
        .form-group { margin-bottom: 20px; }
        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-weight: 600;
            color: #333;
        }
        .form-group label .required { color: #dc3545; }
        .form-group input, .form-group select {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.1);
        }
        .range-info {
            color: #666;
            font-size: 13px;
            margin-top: 5px;
        }
        .btn {
            display: inline-block;
            padding: 10px 25px;
            background: #007bff;
            color: #fff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            margin-right: 10px;
        }
        .btn:hover { background: #0056b3; }
        .btn-secondary { background: #6c757d; }
        .btn-secondary:hover { background: #5a6268; }
    </style>
</head>
<body>
    <%
    PlayerIndex playerIndex = (PlayerIndex) request.getAttribute("playerIndex");
    List<Player> players = (List<Player>) request.getAttribute("players");
    List<Indexer> indexers = (List<Indexer>) request.getAttribute("indexers");
    List<String> errors = (List<String>) request.getAttribute("errors");
    Integer preselectedPlayerId = (Integer) request.getAttribute("preselectedPlayerId");
    boolean isEdit = playerIndex != null && playerIndex.getId() > 0;
    %>

    <div class="container">
        <h2><%= isEdit ? "Edit Index Value" : "Add New Index Value" %></h2>

        <a href="<%=request.getContextPath()%>/playerindex?action=list" class="back-link">&larr; Back to List</a>

        <!-- Validation Errors -->
        <% if (errors != null && !errors.isEmpty()) { %>
            <div class="error-box">
                <strong>Please fix the following errors:</strong>
                <ul>
                    <% for (String error : errors) { %>
                        <li><%= error %></li>
                    <% } %>
                </ul>
            </div>
        <% } %>

        <!-- Player Index Form -->
        <form action="<%=request.getContextPath()%>/playerindex" method="post">
            <input type="hidden" name="action" value="<%= isEdit ? "update" : "insert" %>">
            <% if (isEdit) { %>
                <input type="hidden" name="id" value="<%= playerIndex.getId() %>">
            <% } %>

            <div class="form-group">
                <label>Player <span class="required">*</span></label>
                <select name="playerId" required>
                    <option value="">-- Select Player --</option>
                    <% if (players != null) {
                        for (Player player : players) {
                            boolean selected = (playerIndex != null && playerIndex.getPlayerId() == player.getPlayerId())
                                || (preselectedPlayerId != null && preselectedPlayerId == player.getPlayerId());
                    %>
                        <option value="<%= player.getPlayerId() %>" <%= selected ? "selected" : "" %>>
                            <%= player.getName() %> - <%= player.getFullName() %>
                        </option>
                    <%
                        }
                    } %>
                </select>
            </div>

            <div class="form-group">
                <label>Index Type <span class="required">*</span></label>
                <select name="indexId" id="indexId" required onchange="showRange()">
                    <option value="" data-min="0" data-max="100">-- Select Index --</option>
                    <% if (indexers != null) {
                        for (Indexer indexer : indexers) {
                            boolean selected = playerIndex != null && playerIndex.getIndexId() == indexer.getIndexId();
                    %>
                        <option value="<%= indexer.getIndexId() %>"
                                data-min="<%= indexer.getValueMin() %>"
                                data-max="<%= indexer.getValueMax() %>"
                                <%= selected ? "selected" : "" %>>
                            <%= indexer.getName() %> (<%= indexer.getValueMin() %> - <%= indexer.getValueMax() %>)
                        </option>
                    <%
                        }
                    } %>
                </select>
            </div>

            <div class="form-group">
                <label>Value <span class="required">*</span></label>
                <input type="number" name="value" id="value"
                       value="<%= playerIndex != null && playerIndex.getValue() > 0 ? playerIndex.getValue() : "" %>"
                       required placeholder="Enter value">
                <div id="rangeInfo" class="range-info"></div>
            </div>

            <div style="margin-top: 25px;">
                <button type="submit" class="btn"><%= isEdit ? "Update Value" : "Save Value" %></button>
                <button type="reset" class="btn btn-secondary">Reset</button>
            </div>
        </form>
    </div>

    <script>
        function showRange() {
            var select = document.getElementById('indexId');
            var opt = select.options[select.selectedIndex];
            var min = opt.getAttribute('data-min');
            var max = opt.getAttribute('data-max');
            var info = document.getElementById('rangeInfo');
            var valueInput = document.getElementById('value');

            if (select.value !== '') {
                info.textContent = 'Valid range: ' + min + ' - ' + max;
                valueInput.min = min;
                valueInput.max = max;
            } else {
                info.textContent = '';
            }
        }
        showRange();
    </script>
</body>
</html>

