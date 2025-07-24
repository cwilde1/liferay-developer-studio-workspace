<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />

<div class="container-fluid">
    <h2>Add New Staff Member</h2>
    
    <!-- Error Messages -->
    <liferay-ui:error key="required-fields" message="Please fill in all required fields." />
    <liferay-ui:error key="error-adding-staff" message="An error occurred while adding the staff member." />
    
    <!-- Add Staff Form -->
    <portlet:actionURL name="addStaff" var="addStaffURL" />
    
    <aui:form action="<%= addStaffURL %>" method="post" name="addStaffForm">
        <aui:fieldset>
            <aui:input 
                name="name" 
                type="text" 
                label="Full Name" 
                required="true"
                placeholder="Enter staff member's full name"
            />
            
            <aui:input 
                name="email" 
                type="email" 
                label="Email Address" 
                required="true"
                placeholder="Enter email address"
            />
            
            <aui:input 
                name="department" 
                type="text" 
                label="Department" 
                required="false"
                placeholder="Enter department (optional)"
            />
        </aui:fieldset>
        
        <aui:button-row>
            <aui:button type="submit" value="Add Staff Member" cssClass="btn-primary" />
            <portlet:renderURL var="backToListURL" />
            <aui:button href="<%= backToListURL %>" value="Cancel" cssClass="btn-secondary" />
        </aui:button-row>
    </aui:form>
</div>