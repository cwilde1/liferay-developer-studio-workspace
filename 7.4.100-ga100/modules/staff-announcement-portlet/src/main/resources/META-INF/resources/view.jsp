<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <h2>Staff Management(Session only)</h2>            
            
            <!-- Error Messages -->
            <liferay-ui:error key="staff-not-found" message="Staff member not found!" />
            <liferay-ui:error key="error-adding-staff" message="Error adding staff member!" />
            <liferay-ui:error key="error-updating-staff" message="Error updating staff member!" />
            <liferay-ui:error key="error-deleting-staff" message="Error deleting staff member!" />
            
            <!-- Add Staff Button -->
            <div class="btn-toolbar mb-3">
                <portlet:actionURL name="showAdd" var="addStaffURL" />
                <aui:button href="<%= addStaffURL %>" cssClass="btn btn-primary" value="Add New Staff Member" />
            </div>

            <!-- Staff List -->
            <c:choose>
                <c:when test="${empty staffList}">
                    <div class="alert alert-info">
                        <strong>No staff members found.</strong> Click "Add New Staff Member" to get started.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="staff-list">
                        <c:forEach var="staff" items="${staffList}" varStatus="status">
                            <div class="card mb-3">
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-8">
                                            <h5 class="card-title">${staff.name}</h5>
                                            <p class="card-text">
                                                <strong>ID:</strong> ${staff.id}<br/>
                                                <strong>Email:</strong> 
                                                <a href="mailto:${staff.email}">${staff.email}</a><br/>
                                                <strong>Department:</strong> 
                                                <c:choose>
                                                    <c:when test="${empty staff.department}">
                                                        <span class="text-muted">Not specified</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${staff.department}
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </div>
                                        <div class="col-md-4 text-right">
                                            <div class="btn-group" role="group">
                                                <!-- Edit Button -->
                                                <portlet:actionURL name="showEdit" var="editStaffURL">
                                                    <portlet:param name="staffId" value="${staff.id}" />
                                                </portlet:actionURL>
                                                <aui:button href="<%= editStaffURL %>" cssClass="btn btn-sm btn-outline-primary" value="Edit" />
                                                
                                                <!-- Delete Button -->
                                                <portlet:actionURL name="deleteStaff" var="deleteStaffURL">
                                                    <portlet:param name="staffId" value="${staff.id}" />
                                                </portlet:actionURL>
                                                <aui:button href="<%= deleteStaffURL %>" cssClass="btn btn-sm btn-outline-danger" value="Delete" 
                                                    onClick="return confirm('Are you sure you want to delete ${staff.name}?');" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    
                    <!-- Staff Count -->
                    <div class="mt-3">
                        <small class="text-muted">
                            Total staff members: ${staffList.size()}
                        </small>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<style>
.staff-list .card {
    transition: box-shadow 0.2s ease-in-out;
}

.staff-list .card:hover {
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.btn-group .btn {
    margin-left: 5px;
}

.card-title {
    color: #0066cc;
    margin-bottom: 10px;
}

.card-text {
    margin-bottom: 0;
}

.alert {
    border-radius: 4px;
}
</style>