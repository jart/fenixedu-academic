<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
<html:xhtml />

<style>

/*
Teacher Inquiry Specific Questions
*/

input.bright { position: absolute; bottom: 0; left: 70px; }
 
.question {
border-collapse: collapse;
margin: 10px 0;
width: 900px;
}
.question th {
padding: 5px 10px;
font-weight: normal;
text-align: left;
border: none;
border-top: 1px solid #ccc;
border-bottom: 1px solid #ccc;
background: #f5f5f5;
vertical-align: top;
}
.question td {
padding: 5px;
text-align: center;
border: none;
border-bottom: 1px solid #ccc;
border-top: 1px solid #ccc;
background-color: #fff;
}
 
th.firstcol {
width: 300px;
text-align: left;
}
 
.q1col td { text-align: left; }
 
.q9col .col1, .q9col .col9  { width: 30px; }
.q10col .col1, .q10col .col2, .q10col .col10  { width: 20px; }
.q11col .col1, .q11col .col2, .q11col .col3, .q11col .col11  { width: 20px; }
 
th.col1, th.col2, th.col3, th.col4, th.col5, th.col6, th.col7, th.col8, th.col9, th.col10 {
text-align: center !important;
} 
/* Teacher specific */

div#teacher-results div.workload-left {
margin-top: 0;
float: none;
}
</style>

<script src="<%= request.getContextPath() + "/javaScript/inquiries/jquery.min.js" %>" type="text/javascript" ></script>
<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/inquiries/hideButtons.js" %>"></script>
<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/inquiries/check.js" %>"></script>
<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/inquiries/checkall.js" %>"></script>
<link href="<%= request.getContextPath() %>/CSS/quc_results.css" rel="stylesheet" media="screen" type="text/css" />

<script type="text/javascript" language="javascript">switchGlobal();</script> 
<em><bean:message key="title.teacherPortal" bundle="INQUIRIES_RESOURCES"/></em>
<h2><bean:message key="title.teachingInquiries" bundle="INQUIRIES_RESOURCES"/></h2>

<h3><bean:write name="executionCourse" property="name"/> - <bean:write name="executionCourse" property="sigla"/> (<bean:write name="executionPeriod" property="semester"/>� Semestre <bean:write name="executionPeriod" property="executionYear.year"/>)</h3>

<p><bean:message key="message.teacher.inquiry" bundle="INQUIRIES_RESOURCES"/></p>

<div id="report">
<fr:form action="/teachingInquiry.do?method=saveChanges">
	<fr:edit id="teacherInquiryBean" name="teacherInquiryBean" visible="false"/>
	
	<!-- Teachers Inquiry Results -->
	
	<div id="teacher-results">
		<h3 class="separator2 mtop2"><span style="font-weight: normal;">1. Coment�rios �s Percep��es do Aluno</span></h3>
		<bean:define id="teacherToogleFunctions" value=""/>
		<logic:iterate indexId="teacherIter" id="teacherShiftTypeResult" name="teacherInquiryBean" property="teachersResults" type="net.sourceforge.fenixedu.dataTransferObject.inquiries.TeacherShiftTypeResultsBean">
			<div style="margin: 2.5em 0 3.5em 0;">
				<h3>
					<bean:write name="teacherShiftTypeResult" property="professorship.person.name"/> / 
					<bean:message name="teacherShiftTypeResult" property="shiftType.name"  bundle="ENUMERATION_RESOURCES"/>
				</h3>
				<bean:define id="professorshipOID" name="teacherShiftTypeResult" property="professorship.externalId"/>
				<bean:define id="shiftType" name="teacherShiftTypeResult" property="shiftType"/>
				<p class="mvert15">
					<html:link page="<%= "/viewTeacherResults.do?professorshipOID=" + professorshipOID + "&shiftType=" + shiftType %>"
							 module="/publico" target="_blank">
						<bean:message bundle="INQUIRIES_RESOURCES" key="link.inquiry.showTeacherResults"/>
					</html:link>
				</p>
				<logic:iterate indexId="iter" id="blockResult" name="teacherShiftTypeResult" property="blockResults" type="net.sourceforge.fenixedu.dataTransferObject.inquiries.BlockResultsSummaryBean">
					<bean:define id="teacherToogleFunctions">
						<bean:write name="teacherToogleFunctions" filter="false"/>
						<%= "$('#teacher-block" + teacherShiftTypeResult.getProfessorship().getExternalId() + teacherShiftTypeResult.getShiftType() + (Integer.valueOf(iter)+(int)1) + "').click(function()" 
							+ "{ $('#teacher-block" + teacherShiftTypeResult.getProfessorship().getExternalId() + teacherShiftTypeResult.getShiftType() + (Integer.valueOf(iter)+(int)1) + "-content').toggle('normal', function() { }); });" %>			
					</bean:define>
					<h4 class="mtop15">
						<logic:notEmpty name="blockResult" property="blockResultClassification">
							<div class="<%= "bar-" + blockResult.getBlockResultClassification().name().toLowerCase() %>"><div>&nbsp;</div></div>
						</logic:notEmpty>
						<bean:write name="blockResult" property="inquiryBlock.inquiryQuestionHeader.title"/>
						<bean:define id="expand" value=""/>
						<logic:notEqual value="true" name="blockResult" property="mandatoryComments">
							<span style="font-weight: normal;">| 
								<span id="<%= "teacher-block" + teacherShiftTypeResult.getProfessorship().getExternalId() + teacherShiftTypeResult.getShiftType()  + (Integer.valueOf(iter)+(int)1) %>" class="link">
									Mostrar resultados
								</span>
							</span>
							<bean:define id="expand" value="display: none;"/>
						</logic:notEqual>
					</h4>
					<div id="<%= "teacher-block" + teacherShiftTypeResult.getProfessorship().getExternalId() + teacherShiftTypeResult.getShiftType() + (Integer.valueOf(iter)+(int)1) + "-content"%>" style="<%= expand %>"> 
						<logic:iterate indexId="groupIter" id="groupResult" name="blockResult" property="groupsResults">
							<fr:edit id="<%= "teacherGroup" + teacherIter + iter + groupIter %>" name="groupResult" layout="inquiry-group-resume-input"/>
						</logic:iterate>
					</div>
				</logic:iterate>
			</div>
		</logic:iterate>
	</div>
	
	<!-- Teacher Inquiry -->
	<logic:iterate id="inquiryBlockDTO" name="teacherInquiryBean" property="teacherInquiryBlocks">
		<h3 class="separator2 mtop25">
			<span style="font-weight: normal;">
				<fr:view name="inquiryBlockDTO" property="inquiryBlock.inquiryQuestionHeader.title"/>
			</span>
		</h3>					
		<logic:iterate id="inquiryGroup" name="inquiryBlockDTO" property="inquiryGroups"indexId="index">					
			<fr:edit id="<%= "iter" + index --%>" name="inquiryGroup"/>
		</logic:iterate>
	</logic:iterate>
	<p class="mtop15">
		<html:submit bundle="HTMLALT_RESOURCES" altKey="submit.submit" styleClass="inputbutton">
			<bean:message key="button.saveInquiry" bundle="INQUIRIES_RESOURCES"/>
		</html:submit>
	</p>
</fr:form>
</div>

<bean:define id="scriptTeacherToogleFunctions">
	<script>
	<bean:write name="teacherToogleFunctions" filter="false"/>
	</script>
</bean:define>

<bean:write name="scriptTeacherToogleFunctions" filter="false"/>