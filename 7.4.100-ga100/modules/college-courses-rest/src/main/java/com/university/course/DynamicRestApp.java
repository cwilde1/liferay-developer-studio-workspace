package com.university.course;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.university.course.model.Course;
import com.university.course.service.CourseLocalService;

/**
 * @author papa-pc
 */
@Component(
		property = {
				JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/dynamic-courses",
				JaxrsWhiteboardConstants.JAX_RS_NAME + "=DynamicCourses.Rest",
				// Add this property to bypass authentication
				"auth.verifier.guest.allowed=true",
				"liferay.access.control.disable=true"
		},
		service = Application.class
)
public class DynamicRestApp extends Application {

	private static final Log _log = LogFactoryUtil.getLog(DynamicRestApp.class);

	@Reference
	private CourseLocalService _courseLocalService;

	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String heartbeat() {
		_log.info("Heartbeat endpoint called");
		return "{ \"message\": \"Dynamic Courses API heartbeat\", \"status\": \"success\" }";
	}

	@GET
	@Path("/courses")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCourses() {
		_log.info("getAllCourses endpoint called");
		try {
			List<Course> courses = _courseLocalService.getCourses(-1, -1);
			List<CourseResponse> courseResponses = new ArrayList<>();

			for (Course course : courses) {
				courseResponses.add(new CourseResponse(
						course.getCourseId(),
						course.getCourseName(),
						course.getDescription(),
						course.getCredits(),
						course.getStudents()
				));
			}

			_log.info("Returning " + courseResponses.size() + " courses");
			return Response.ok(courseResponses).build();
		} catch (Exception e) {
			_log.error("Error retrieving courses", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Error retrieving courses: " + e.getMessage() + "\"}").build();
		}
	}

	@GET
	@Path("/courses/{courseId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCourse(@PathParam("courseId") long courseId) {
		try {
			Course course = _courseLocalService.getCourse(courseId);
			CourseResponse response = new CourseResponse(
					course.getCourseId(),
					course.getCourseName(),
					course.getDescription(),
					course.getCredits(),
					course.getStudents()
			);
			return Response.ok(response).build();
		} catch (PortalException e) {
			_log.error("Course not found: " + courseId, e);
			return Response.status(Response.Status.NOT_FOUND)
					.entity("{\"error\":\"Course not found\"}").build();
		} catch (Exception e) {
			_log.error("Error retrieving course: " + courseId, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Error retrieving course: " + e.getMessage() + "\"}").build();
		}
	}

	@POST
	@Path("/course")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCourse(String courseJson) {
		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(courseJson);

			String courseName = jsonObject.getString("courseName");
			String description = jsonObject.getString("description");
			int credits = jsonObject.getInt("credits");

			Course course = _courseLocalService.createCourse(0);
			course.setCourseName(courseName);
			course.setDescription(description);
			course.setCredits(credits);
			if (jsonObject.has("students")) {
				course.setStudents(jsonObject.getInt("students"));
			}
			course = _courseLocalService.addCourse(course);

			CourseResponse response = new CourseResponse(
					course.getCourseId(),
					course.getCourseName(),
					course.getDescription(),
					course.getCredits(),
					course.getStudents()
			);

			return Response.status(Response.Status.CREATED).entity(response).build();
		} catch (Exception e) {
			_log.error("Error creating course", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Error creating course: " + e.getMessage() + "\"}").build();
		}
	}

	@PUT
	@Path("/courses/{courseId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCourse(@PathParam("courseId") long courseId, String courseJson) {
		try {
			Course course = _courseLocalService.getCourse(courseId);
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(courseJson);

			if (jsonObject.has("courseName")) {
				course.setCourseName(jsonObject.getString("courseName"));
			}
			if (jsonObject.has("description")) {
				course.setDescription(jsonObject.getString("description"));
			}
			if (jsonObject.has("credits")) {
				course.setCredits(jsonObject.getInt("credits"));
			}
			if (jsonObject.has("students")) {
				course.setStudents(jsonObject.getInt("students"));
			}

			course = _courseLocalService.updateCourse(course);

			CourseResponse response = new CourseResponse(
					course.getCourseId(),
					course.getCourseName(),
					course.getDescription(),
					course.getCredits(),
					course.getStudents()
			);

			return Response.ok(response).build();
		} catch (PortalException e) {
			_log.error("Course not found: " + courseId, e);
			return Response.status(Response.Status.NOT_FOUND)
					.entity("{\"error\":\"Course not found\"}").build();
		} catch (Exception e) {
			_log.error("Error updating course: " + courseId, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Error updating course: " + e.getMessage() + "\"}").build();
		}
	}

	@DELETE
	@Path("/courses/{courseId}")
	public Response deleteCourse(@PathParam("courseId") long courseId) {
		try {
			_courseLocalService.deleteCourse(courseId);
			return Response.noContent().build();
		} catch (PortalException e) {
			_log.error("Course not found: " + courseId, e);
			return Response.status(Response.Status.NOT_FOUND)
					.entity("{\"error\":\"Course not found\"}").build();
		} catch (Exception e) {
			_log.error("Error deleting course: " + courseId, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"error\":\"Error deleting course: " + e.getMessage() + "\"}").build();
		}
	}
}