<%@ page import="com.opensymphony.workflow.Workflow,
                 com.opensymphony.workflow.basic.BasicWorkflow,
                 com.opensymphony.workflow.spi.Step,
                 java.util.List" %>
<%
Workflow wf = new BasicWorkflow((String) session.getAttribute("username"));
long id = Long.parseLong(request.getParameter("id"));
%>
<div id="workflowCanvas" style="position:relative;height:566px;width:508px;">
<img src="example-export.png" border=0/>
</div>
<script type="text/javascript" src="js/wz_jsgraphics.js"></script>
<script type="text/javascript" src="js/xmlextras.js"></script>
<script type="text/javascript">
var currentStepIds = [];
<%List currentSteps = wf.getCurrentSteps(id);
for(int i = 0; i < currentSteps.size(); i++){%>
currentStepIds[<%=i%>] = <%=((Step) currentSteps.get(i)).getStepId()%>;
<%}%>

var xmlHttp = XmlHttp.create();
var async = true;
xmlHttp.open("GET", "example.lyt.xml", async);
xmlHttp.onreadystatechange = function () {
    if (xmlHttp.readyState == 4){
        //set up graphics
        var jg = new jsGraphics("workflowCanvas");
        jg.setColor("#ff0000");
        jg.setStroke(3);
        var xAdjust = 68;
        var yAdjust = -17;
        var widthAdjust = 3;
        var heightAdjust = 2;
        
        //parsing xml and paint;
        var cells = xmlHttp.responseXML.getElementsByTagName("cell");
        for(var i = 0; i < currentStepIds.length; i++){
            for(var n = 0; n < cells.length; n++){
                var cell = cells[n];
                if(cell.getAttribute("type") == "StepCell" && currentStepIds[i] == parseInt(cell.getAttribute("id"))){
                    jg.drawRect(parseInt(cell.getAttribute("x")) + xAdjust, parseInt(cell.getAttribute("y")) + yAdjust, parseInt(cell.getAttribute("width")) + widthAdjust, parseInt(cell.getAttribute("height")) + heightAdjust);
                }
            }
        }
        jg.paint();
    }
};
xmlHttp.send(null);    
</script>