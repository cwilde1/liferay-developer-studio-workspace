package com.university.course.jobs;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"cron.expression=0 * * * * ?", // Every minute
		"scheduler.description=Change organization for user with oldest changes",
		"scheduler.enabled=true",
		"osgi.command.function=changeUserOrgs",
		"osgi.command.scope=usercreator"
	},
	service = UserOrgCreatorScheduler.class
)
public class UserOrgCreatorScheduler extends BaseMessageListener {
	
	private static final Log _log = LogFactoryUtil.getLog(UserOrgCreatorScheduler.class);
	
	private static final String[] ORG_NAMES = {"ONE", "TWO", "THREE"};
	
	private Random _random = new Random();
	
	private static Map<Long, Date> _lastChangeMap = new HashMap<>();
	
	@Reference
	private UserLocalService _userLocalService;
	
	@Reference
	private OrganizationLocalService _organizationLocalService;
	
	@Reference
	private Portal _portal;

	@Override
	protected void doReceive(Message message) throws MessageListenerException {
		changeUserOrgs();
	}
	
	/**
	 * Main method - finds user with oldest changes and updates their organization
	 */
	public void changeUserOrgs() {
		_log.info("Starting organization change for user with oldest changes...");
		
		try {
			
			long companyId = _portal.getDefaultCompanyId();
			
			// Ensure organizations exist first
			ensureOrganizationsExist(companyId);
			
			// Get all non-system users (ID > 20000)
			List<User> allUsers = _userLocalService.getCompanyUsers(companyId, -1, -1);
			List<User> nonSystemUsers = new ArrayList<>();
			
			for (User user : allUsers) {
				if (user.getUserId() > 20000) {
					nonSystemUsers.add(user);
				}
			}
			
			if (nonSystemUsers.isEmpty()) {
				_log.warn("No non-system users found.");
				return;
			}
			
			// Find user with oldest changes (or never changed)
			User userToChange = findUserWithOldestChanges(nonSystemUsers);
			
			_log.info("=== BEFORE ORG CHANGE ===");
			logUserOrganization(userToChange);
			
			// Change organization for the selected user
			changeUserOrganization(userToChange, companyId);
			
			// Update the last change time
			_lastChangeMap.put(userToChange.getUserId(), new Date());
			
			_log.info("=== AFTER ORG CHANGE ===");
			logUserOrganization(userToChange);
			
			// Log rotation status
			logRotationStatus(nonSystemUsers);
			
		} catch (Exception e) {
			_log.error("Error changing user organizations", e);
		}
	}
	
	/**
	 * Find the user who was changed longest ago (or never changed)
	 */
	private User findUserWithOldestChanges(List<User> users) {
		User oldestUser = null;
		Date oldestDate = new Date(); // Current time as baseline
		
		for (User user : users) {
			Date lastChanged = _lastChangeMap.get(user.getUserId());
			
			if (lastChanged == null) {
				// Never changed - highest priority
				return user;
			} else if (lastChanged.before(oldestDate)) {
				oldestDate = lastChanged;
				oldestUser = user;
			}
		}
		
		// If all users have been changed, return the one changed longest ago
		return oldestUser != null ? oldestUser : users.get(0);
	}
	
	/**
	 * Change a user's organization to a random one
	 */
	private void changeUserOrganization(User user, long companyId) throws Exception {
		// Get user's current organizations
		List<Organization> currentOrgs = _organizationLocalService.getUserOrganizations(user.getUserId());
		
		// Pick a random new organization
		String newOrgName = ORG_NAMES[_random.nextInt(ORG_NAMES.length)];
		Organization newOrg = _organizationLocalService.fetchOrganization(companyId, newOrgName);
		
		if (newOrg == null) {
			_log.error("Organization " + newOrgName + " not found!");
			return;
		}
		
		// Remove user from all current organizations
		for (Organization currentOrg : currentOrgs) {
			_organizationLocalService.deleteUserOrganization(user.getUserId(), currentOrg.getOrganizationId());
			_log.info("Removed " + user.getScreenName() + " from organization: " + currentOrg.getName());
		}
		
		// Add user to new organization
		_organizationLocalService.addUserOrganization(user.getUserId(), newOrg.getOrganizationId());
		_log.info("Added " + user.getScreenName() + " to organization: " + newOrgName);
	}
	
	/**
	 * Log organization for a single user
	 */
	private void logUserOrganization(User user) throws Exception {
		List<Organization> userOrgs = _organizationLocalService.getUserOrganizations(user.getUserId());
		StringBuilder orgNames = new StringBuilder();
		
		if (userOrgs.isEmpty()) {
			orgNames.append("None");
		} else {
			for (int i = 0; i < userOrgs.size(); i++) {
				if (i > 0) {
					orgNames.append(", ");
				}
				orgNames.append(userOrgs.get(i).getName());
			}
		}
		
		Date lastChanged = _lastChangeMap.get(user.getUserId());
		String lastChangedStr = lastChanged != null ? lastChanged.toString() : "Never";
		
		_log.info("User: " + user.getScreenName() + " | Email: " + user.getEmailAddress() + 
				 " | Organizations: " + orgNames.toString() + " | Last Changed: " + lastChangedStr);
	}
	
	/**
	 * Log rotation status showing all users and when they were last changed
	 */
	private void logRotationStatus(List<User> users) throws Exception {
		_log.info("=== ROTATION STATUS ===");
		
		// Sort users by last change date (oldest first, never changed first)
		List<User> sortedUsers = new ArrayList<>(users);
		Collections.sort(sortedUsers, new Comparator<User>() {
			@Override
			public int compare(User u1, User u2) {
				Date d1 = _lastChangeMap.get(u1.getUserId());
				Date d2 = _lastChangeMap.get(u2.getUserId());
				
				if (d1 == null && d2 == null) return 0;
				if (d1 == null) return -1; // Never changed comes first
				if (d2 == null) return 1;
				
				return d1.compareTo(d2); // Oldest dates first
			}
		});
		
		for (User user : sortedUsers) {
			Date lastChanged = _lastChangeMap.get(user.getUserId());
			String status = lastChanged != null ? lastChanged.toString() : "NEVER CHANGED";
			_log.info("  " + user.getScreenName() + " - Last changed: " + status);
		}
		
		_log.info("=== END ROTATION STATUS ===");
	}
	
	/**
	 * Ensure organizations ONE, TWO, THREE exist
	 */
	private void ensureOrganizationsExist(long companyId) throws Exception {
		User defaultUser = _userLocalService.getGuestUser(companyId);
		long defaultUserId = defaultUser.getUserId();
		
		for (String orgName : ORG_NAMES) {
			Organization org = _organizationLocalService.fetchOrganization(companyId, orgName);
			
			if (org == null) {
				_log.info("Creating organization: " + orgName);
				
				ServiceContext serviceContext = new ServiceContext();
				serviceContext.setCompanyId(companyId);
				serviceContext.setUserId(defaultUserId);
				
				_organizationLocalService.addOrganization(
					defaultUserId,
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
					orgName,
					true // site
				);
				
				_log.info("Created organization: " + orgName);
			}
		}
	}
}
