<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.college.staff.dto.StaffMember" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />

<%
    // Get the staff member from the request attribute (set by the portlet)
    StaffMember staffMember = (StaffMember) renderRequest.getAttribute("staffMember");
    long staffId = ParamUtil.getLong(renderRequest, "staffId", 0);
    
    // If no staff member found from attribute, try to get from parameter
    if (staffMember == null && staffId == 0) {
        staffId = ParamUtil.getLong(renderRequest, "staffId", 0);
    }
    
    // If still no staff member, create a default one (shouldn't happen in edit mode)
    if (staffMember == null) {
        staffMember = new StaffMember();
        staffMember.setId(staffId);
        staffMember.setName("");
        staffMember.setEmail("");
        staffMember.setDepartment("");
    }
    
    String staffName = (staffMember.getName() != null) ? staffMember.getName() : "";
    String staffEmail = (staffMember.getEmail() != null) ? staffMember.getEmail() : "";
    String staffDepartment = (staffMember.getDepartment() != null) ? staffMember.getDepartment() : "";
%>

<div class="container-fluid">
    <h2>Edit Staff Member (ID: <%= staffMember.getId() %>)</h2>
    
    <!-- Debug info - remove this after testing -->
    <p><small>Debug: Staff ID = <%= staffId %>, Name = "<%= staffName %>"</small></p>
    
    <!-- Error Messages -->
    <liferay-ui:error key="required-fields" message="Please fill in all required fields." />
    <liferay-ui:error key="staff-not-found" message="Staff member not found." />
    <liferay-ui:error key="error-updating-staff" message="An error occurred while updating the staff member." />
    
    <!-- Update Staff Form -->
    <portlet:actionURL name="updateStaff" var="updateStaffURL">
        <portlet:param name="staffId" value="<%= String.valueOf(staffMember.getId()) %>" />
    </portlet:actionURL>
    
    <aui:form action="<%= updateStaffURL %>" method="post" name="editStaffForm">
        <aui:input type="hidden" name="staffId" value="<%= staffMember.getId() %>" />
        
        <aui:fieldset>
            <aui:input 
                name="name" 
                type="text" 
                label="Full Name" 
                value="<%= staffName %>"
                required="true"
                placeholder="Enter staff member's full name"
            />
            
            <aui:input 
                name="email" 
                type="email" 
                label="Email Address" 
                value="<%= staffEmail %>"
                required="true"
                placeholder="Enter email address"
            />
            
            <aui:input 
                name="department" 
                type="text" 
                label="Department" 
                value="<%= staffDepartment %>"
                required="false"
                placeholder="Enter department (optional)"
            />
        </aui:fieldset>
        
        <aui:button-row>
            <aui:button type="submit" value="Update Staff Member" cssClass="btn-primary" />
            <portlet:renderURL var="backToListURL" />
            <aui:button href="<%= backToListURL %>" value="Cancel" cssClass="btn-secondary" />
        </aui:button-row>
    </aui:form>
</div>