package com.university.course.jobs;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.university.course.model.Course;
import com.university.course.service.CourseLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import java.util.List;

@Component(
	immediate = true,
	property = {
		"cron.expression=0 0 0 * * ?",
		"scheduler.description=Delete courses with 0 students nightly",
		"scheduler.enabled=true",
		"osgi.command.function=runNow",
		"osgi.command.scope=deletecourses"
	},
	service = DeleteEmptyCoursesScheduler.class
)

public class DeleteEmptyCoursesScheduler extends BaseMessageListener {
	private static final Log _log = LogFactoryUtil.getLog(DeleteEmptyCoursesScheduler.class);

	//@Reference
	//private CourseLocalService _courseLocalService;

	@Override
	protected void doReceive(Message message) throws MessageListenerException {
		_log.info("Running scheduled job to delete courses with 0 students");
		try {
//			List<Course> courses = _courseLocalService.getCourses(-1, -1);
//			for (Course course : courses) {
//				if (course.getStudents() == 0) {
//					_courseLocalService.deleteCourse(course);
//					_log.info("Deleted course with ID: " + course.getCourseId());
//				}
//			}
		} catch (Exception e) {
			_log.error("Error deleting courses with 0 students", e);
		}
	}

	public void runNow() {
		_log.info("Manually triggered: deleting courses with 0 students");
		_log.error("ERROR from job");
		try {
		} catch (Exception e) {
			_log.error("Error deleting courses with 0 students", e);
		}
	}
}
