package com.college.staff.portlet;

import com.college.staff.dto.StaffMember;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.service.component.annotations.Component;

import javax.portlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component(
    immediate = true,
    property = {
        "com.liferay.portlet.display-category=category.sample",
        "javax.portlet.display-name=Staff Announcements",
        "javax.portlet.init-param.template-path=/",
        "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.name=staff-announcements-portlet",
        "javax.portlet.security-role-ref=power-user,user"
    },
    service = Portlet.class
)
public class StaffAnnouncementsPortlet extends MVCPortlet {

    private static final Log _log = LogFactoryUtil.getLog(StaffAnnouncementsPortlet.class);
    
    private static final String STAFF_LIST_KEY = "STAFF_LIST";
    private static final String ID_COUNTER_KEY = "ID_COUNTER";

    @SuppressWarnings("unchecked")
    private List<StaffMember> getStaffListFromSession(PortletRequest request) {
        PortletSession session = request.getPortletSession();
        List<StaffMember> staffList = (List<StaffMember>) session.getAttribute(
            STAFF_LIST_KEY, PortletSession.APPLICATION_SCOPE);
        if (staffList == null) {
            staffList = new ArrayList<>();
            session.setAttribute(STAFF_LIST_KEY, staffList, PortletSession.APPLICATION_SCOPE);
            if (_log.isDebugEnabled()) {
                _log.debug("Created new staff list in session");
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("Retrieved staff list from session, count: " + staffList.size());
        }
        return staffList;
    }

    @ProcessAction(name = "showAdd")
    public void showAdd(ActionRequest request, ActionResponse response) {
        response.setRenderParameter("mvcPath", "/add.jsp");
    }

    @ProcessAction(name = "addStaff")
    public void addStaff(ActionRequest request, ActionResponse response) {
        String name = ParamUtil.getString(request, "name");
        String email = ParamUtil.getString(request, "email");
        String department = ParamUtil.getString(request, "department");

        if (Validator.isNull(name) || Validator.isNull(email)) {
            SessionErrors.add(request, "required-fields");
            response.setRenderParameter("mvcPath", "/add.jsp");
            return;
        }

        try {
            StaffMember member = new StaffMember();
            member.setId(getIdCounterFromSession(request).getAndIncrement());
            member.setName(name);
            member.setEmail(email);
            member.setDepartment(department != null ? department : "");
            
            List<StaffMember> staffList = getStaffListFromSession(request);
            staffList.add(member);
            
            PortletSession session = request.getPortletSession();
            session.setAttribute(STAFF_LIST_KEY, staffList, PortletSession.APPLICATION_SCOPE);
        } catch (Exception e) {
            _log.error("Error adding staff member", e);
            SessionErrors.add(request, "error-adding-staff");
        }

        response.setRenderParameter("mvcPath", "/view.jsp");
    }
    
    @ProcessAction(name = "showEdit")
    public void showEdit(ActionRequest request, ActionResponse response) {
        long staffId = ParamUtil.getLong(request, "staffId");
        
        List<StaffMember> staffList = getStaffListFromSession(request);
        StaffMember member = staffList.stream()
            .filter(s -> s.getId() == staffId)
            .findFirst()
            .orElse(null);
            
        if (member != null) {
            response.setRenderParameter("mvcPath", "/edit.jsp");
            response.setRenderParameter("staffId", String.valueOf(staffId));
        } else {
            SessionErrors.add(request, "staff-not-found");
            response.setRenderParameter("mvcPath", "/view.jsp");
        }
    }

    @ProcessAction(name = "updateStaff")
    public void updateStaff(ActionRequest request, ActionResponse response) {
        long staffId = ParamUtil.getLong(request, "staffId");
        String name = ParamUtil.getString(request, "name");
        String email = ParamUtil.getString(request, "email");
        String department = ParamUtil.getString(request, "department");

        if (Validator.isNull(name) || Validator.isNull(email)) {
            SessionErrors.add(request, "required-fields");
            response.setRenderParameter("mvcPath", "/edit.jsp");
            response.setRenderParameter("staffId", String.valueOf(staffId));
            return;
        }

        try {
            List<StaffMember> staffList = getStaffListFromSession(request);
            boolean updated = false;
            
            for (StaffMember member : staffList) {
                if (member.getId() == staffId) {
                    member.setName(name);
                    member.setEmail(email);
                    member.setDepartment(department != null ? department : "");
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                PortletSession session = request.getPortletSession();
                session.setAttribute(STAFF_LIST_KEY, staffList, PortletSession.APPLICATION_SCOPE);
            } else {
                SessionErrors.add(request, "staff-not-found");
            }
        } catch (Exception e) {
            _log.error("Error updating staff member", e);
            SessionErrors.add(request, "error-updating-staff");
        }

        response.setRenderParameter("mvcPath", "/view.jsp");
    }

    @ProcessAction(name = "deleteStaff")
    public void deleteStaff(ActionRequest request, ActionResponse response) {
        long staffId = ParamUtil.getLong(request, "staffId");

        try {
            List<StaffMember> staffList = getStaffListFromSession(request);
            boolean deleted = staffList.removeIf(member -> member.getId() == staffId);
            
            if (deleted) {
                PortletSession session = request.getPortletSession();
                session.setAttribute(STAFF_LIST_KEY, staffList, PortletSession.APPLICATION_SCOPE);
            } else {
                SessionErrors.add(request, "staff-not-found");
            }
        } catch (Exception e) {
            _log.error("Error deleting staff member", e);
            SessionErrors.add(request, "error-deleting-staff");
        }

        response.setRenderParameter("mvcPath", "/view.jsp");
    }

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {
        
        String mvcPath = ParamUtil.getString(renderRequest, "mvcPath", "/view.jsp");
        
        if ("/edit.jsp".equals(mvcPath)) {
            long staffId = ParamUtil.getLong(renderRequest, "staffId");
            if (staffId > 0) {
                List<StaffMember> staffList = getStaffListFromSession(renderRequest);
                StaffMember member = staffList.stream()
                    .filter(s -> s.getId() == staffId)
                    .findFirst()
                    .orElse(null);
                
                if (member != null) {
                    renderRequest.setAttribute("staffMember", member);
                    renderRequest.setAttribute("staffId", staffId);
                } else {
                    renderRequest.setAttribute("mvcPath", "/view.jsp");
                    SessionErrors.add(renderRequest, "staff-not-found");
                }
            }
        }
        
        if ("/view.jsp".equals(mvcPath)) {
            List<StaffMember> staffList = getStaffListFromSession(renderRequest);
            renderRequest.setAttribute("staffList", staffList);
        }

        super.doView(renderRequest, renderResponse);
    }

    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {
        
        String mvcPath = ParamUtil.getString(renderRequest, "mvcPath", "/view.jsp");
        
        List<StaffMember> staffList = getStaffListFromSession(renderRequest);
        renderRequest.setAttribute("staffList", staffList);
        
        if ("/edit.jsp".equals(mvcPath)) {
            long staffId = ParamUtil.getLong(renderRequest, "staffId");
            if (staffId > 0) {
                StaffMember member = staffList.stream()
                    .filter(s -> s.getId() == staffId)
                    .findFirst()
                    .orElse(null);
                
                if (member != null) {
                    renderRequest.setAttribute("staffMember", member);
                    renderRequest.setAttribute("staffId", staffId);
                }
            }
        }
        
        super.render(renderRequest, renderResponse);
    }
    
    private AtomicLong getIdCounterFromSession(PortletRequest request) {
        PortletSession session = request.getPortletSession();
        AtomicLong idCounter = (AtomicLong) session.getAttribute(
            ID_COUNTER_KEY, PortletSession.APPLICATION_SCOPE);
        if (idCounter == null) {
            idCounter = new AtomicLong(1);
            session.setAttribute(ID_COUNTER_KEY, idCounter, PortletSession.APPLICATION_SCOPE);
            if (_log.isDebugEnabled()) {
                _log.debug("Created new ID counter in session");
            }
        }
        return idCounter;
    }
}