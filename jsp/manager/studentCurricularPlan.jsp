<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %><%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %><%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %><%@ taglib uri="/WEB-INF/taglibs-datetime.tld" prefix="dt" %><h2><bean:message key="link.manager.studentsManagement"/></h2><br /><html:form action="/studentsManagement" focus="number">	<html:hidden property="method" value="show"/>	<html:hidden property="page" value="1"/>	<html:hidden property="selectedStudentCurricularPlanId" value="0"/>	<table>		<tr>			<td class="listClasses-header">				<bean:message key="property.student.number"/>			</td>			<td class="listClasses-header">				<bean:message key="property.student.degreeType"/>			</td>		</tr>		<tr>			<td class="listClasses">				<html:text property="number" size="5" onchange="this.form.submit();"/>			</td>			<td class="listClasses">				<html:select property="degreeType" size="1" onchange="this.form.submit();">					<html:options collection="degreeTypes" property="value" labelProperty="label"/>				</html:select>			</td>		</tr>		<logic:present name="infoStudentCurricularPlans">			<logic:iterate id="infoStudentCurricularPlan" name="infoStudentCurricularPlans" length="1">				<tr>					<td colspan="2" class="listClasses">						<bean:define id="email" name="infoStudentCurricularPlan" property="infoStudent.infoPerson.email" type="java.lang.String"/>						<a href="mailto:<%= email %>">							<bean:write name="infoStudentCurricularPlan" property="infoStudent.infoPerson.nome"/>						</a>					</td>				</tr>			</logic:iterate>		</logic:present>	</table></html:form>	<br/>	<logic:present name="infoStudentCurricularPlans">		<br/>		<bean:define id="deleteConfirmStudentCurricularPlan">			return confirm('<bean:message key="message.confirm.delete.studentCurricularPlan"/>')		</bean:define>		<bean:define id="deleteConfirmEnrollment">			return confirm('<bean:message key="message.confirm.delete.enrollment"/>')		</bean:define>				<logic:iterate id="infoStudentCurricularPlan" name="infoStudentCurricularPlans">			<bean:define id="studentCurricularPlanId" name="infoStudentCurricularPlan" property="idInternal"/>			<bean:define id="studentNumber" name="infoStudentCurricularPlan" property="infoStudent.number"/>			<bean:define id="degreeType" name="infoStudentCurricularPlan" property="infoStudent.degreeType.tipoCurso"/>			<table>				<tr>					<td colspan="2" rowspan="3" class="listClasses-header">						<bean:message key="label.studentCurricularPlan"/>					</td>					<td colspan="4" class="listClasses">						<bean:define id="studentCurricularPlanStateString" type="java.lang.String" name="infoStudentCurricularPlan" property="currentState.stringPt"/>						<bean:define id="onChangeString" type="java.lang.String">this.form.method.value='changeStudentCurricularPlanState';this.form.selectedStudentCurricularPlanId.value=<%= studentCurricularPlanId.toString() %>;this.form.submit();</bean:define><html:form action="/studentsManagement" focus="number">	<html:hidden property="method" value="show"/>	<html:hidden property="page" value="1"/>	<html:hidden property="selectedStudentCurricularPlanId" value="0"/>	<html:hidden property="number"/>	<html:hidden property="degreeType"/>						<html:select property="studentCurricularPlanState" size="1"								onchange="<%= onChangeString %>" value="<%= studentCurricularPlanStateString %>">							<html:options collection="studentCurricularPlanStates" property="value" labelProperty="label"/>						</html:select></html:form>						<bean:write name="infoStudentCurricularPlan" property="currentState.stringPt"/>						<logic:present name="infoStudentCurricularPlan" property="startDate">							:							<dt:format pattern="yyyy-MM-dd">								<bean:write name="infoStudentCurricularPlan" property="startDate.time"/>							</dt:format>						</logic:present>											</td>					<td colspan="1" rowspan="3" class="listClasses">						<html:link page="<%= "/studentsManagement.do?method=deleteStudentCurricularPlan&amp;page=0&amp;number="									+ pageContext.findAttribute("studentNumber").toString()									+ "&amp;degreeType="									+ pageContext.findAttribute("degreeType").toString()									+ "&amp;studentCurricularPlanId="									+ pageContext.findAttribute("studentCurricularPlanId").toString()								%>"								onclick='<%= pageContext.findAttribute("deleteConfirmStudentCurricularPlan").toString() %>'>							<bean:message key="link.delete"/>						</html:link>					</td>				</tr>				<tr>					<td  colspan="4" class="listClasses">						<bean:write name="infoStudentCurricularPlan" property="infoDegreeCurricularPlan.infoDegree.nome"/>					</td>				</tr>				<tr>					<td colspan="4" class="listClasses">						<bean:write name="infoStudentCurricularPlan" property="infoDegreeCurricularPlan.name"/>						:						<logic:present name="infoStudentCurricularPlan" property="infoDegreeCurricularPlan.initialDate">							<dt:format pattern="yyyy-MM-dd">								<bean:write name="infoStudentCurricularPlan" property="infoDegreeCurricularPlan.initialDate.time"/>							</dt:format>						</logic:present>						<logic:notPresent name="infoStudentCurricularPlan" property="infoDegreeCurricularPlan.initialDate">							...						</logic:notPresent>						-						<logic:present name="infoStudentCurricularPlan" property="infoDegreeCurricularPlan.endDate">							<dt:format pattern="yyyy-MM-dd">								<bean:write name="infoStudentCurricularPlan" property="infoDegreeCurricularPlan.endDate.time"/>							</dt:format>						</logic:present>						<logic:notPresent name="infoStudentCurricularPlan" property="infoDegreeCurricularPlan.endDate">							...						</logic:notPresent>					</td>				</tr>					<tr>						<td class="listClasses-header">							<bean:message key="label.executionYear"/>						</td>						<td class="listClasses-header">							<bean:message key="label.manager.semester"/>						</td>						<td class="listClasses-header">							<bean:message key="label.manager.degree"/>						</td>						<td class="listClasses-header">							<bean:message key="label.course.code"/>						</td>						<td class="listClasses-header">							<bean:message key="label.course.name"/>						</td>						<td class="listClasses-header">							<bean:message key="label.grade"/>						</td>						<td class="listClasses-header">						</td>					</tr>				<logic:iterate id="infoEnrollmentGrade" name="infoStudentCurricularPlan" property="infoEnrolments">					<bean:define id="enrollmentId" name="infoEnrollmentGrade" property="infoEnrollment.idInternal"/>					<tr>						<td class="listClasses">							<bean:write name="infoEnrollmentGrade" property="infoEnrollment.infoExecutionPeriod.infoExecutionYear.year"/>						</td>						<td class="listClasses">							<bean:write name="infoEnrollmentGrade" property="infoEnrollment.infoExecutionPeriod.semester"/>						</td>						<td class="listClasses">							<bean:write name="infoEnrollmentGrade" property="infoEnrollment.infoCurricularCourse.infoDegreeCurricularPlan.infoDegree.sigla"/>						</td>						<td class="listClasses">							<bean:write name="infoEnrollmentGrade" property="infoEnrollment.infoCurricularCourse.code"/>						</td>						<td class="listClasses">							<bean:write name="infoEnrollmentGrade" property="infoEnrollment.infoCurricularCourse.name"/>						</td>						<td class="listClasses">							<bean:write name="infoEnrollmentGrade" property="grade"/>						</td>						<td class="listClasses">							<html:link page="<%= "/studentsManagement.do?method=deleteEnrollment&amp;page=0&amp;number="										+ pageContext.findAttribute("studentNumber").toString()										+ "&amp;degreeType="										+ pageContext.findAttribute("degreeType").toString()										+ "&amp;enrollmentId="										+ pageContext.findAttribute("enrollmentId").toString()									%>"									onclick='<%= pageContext.findAttribute("deleteConfirmEnrollment").toString() %>'>								<bean:message key="link.delete"/>							</html:link>						</td>					</tr>				</logic:iterate>			</table>			<br />		</logic:iterate>	</logic:present>