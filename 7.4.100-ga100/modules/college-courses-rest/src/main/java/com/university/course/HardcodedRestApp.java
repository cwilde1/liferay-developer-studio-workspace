package com.university.course;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author papa-pc
 */
@Component(
		property = {
				JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/hardcoded-courses",
				JaxrsWhiteboardConstants.JAX_RS_NAME + "=HardcodedCourses.Rest",
				// Add this property to bypass authentication
				"auth.verifier.guest.allowed=true",
				"liferay.access.control.disable=true"
		},
		service = Application.class
)
public class HardcodedRestApp extends Application {

	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String heartbeat() {
		return "{ \"message\": \"Hardcoded Courses API heartbeat\", \"status\": \"success\" }";
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCourse(@PathParam("id") long id) {
		return "{ \"courseId\": " + id + ", \"courseName\": \"Sample Course " + id + "\", \"credits\": 3, \"department\": \"Computer Science\" }";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCourse(String courseData) {
		// Dummy response for creating a course
		String response = "{ \"courseId\": 123, \"courseName\": \"New Course\", \"credits\": 3, \"department\": \"Computer Science\", \"status\": \"created\" }";
		return Response.status(Response.Status.CREATED).entity(response).build();
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourse(@PathParam("id") long id) {
		// Dummy response for deleting a course
		String response = "{ \"courseId\": " + id + ", \"message\": \"Course deleted successfully\", \"status\": \"deleted\" }";
		return Response.ok(response).build();
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public String searchCourses(@QueryParam("department") String department) {
		return "{ \"results\": [ { \"courseId\": 1, \"courseName\": \"Introduction to " + department + "\", \"credits\": 3, \"department\": \"" + department + "\" }, { \"courseId\": 2, \"courseName\": \"Advanced " + department + "\", \"credits\": 4, \"department\": \"" + department + "\" } ], \"total\": 2 }";
	}
}