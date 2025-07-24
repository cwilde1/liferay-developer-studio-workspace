/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.university.course.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link Course}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Course
 * @generated
 */
public class CourseWrapper
	extends BaseModelWrapper<Course> implements Course, ModelWrapper<Course> {

	public CourseWrapper(Course course) {
		super(course);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("courseId", getCourseId());
		attributes.put("courseName", getCourseName());
		attributes.put("description", getDescription());
		attributes.put("credits", getCredits());
		attributes.put("students", getStudents());
		attributes.put("instructor", getInstructor());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long courseId = (Long)attributes.get("courseId");

		if (courseId != null) {
			setCourseId(courseId);
		}

		String courseName = (String)attributes.get("courseName");

		if (courseName != null) {
			setCourseName(courseName);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Integer credits = (Integer)attributes.get("credits");

		if (credits != null) {
			setCredits(credits);
		}

		Integer students = (Integer)attributes.get("students");

		if (students != null) {
			setStudents(students);
		}

		String instructor = (String)attributes.get("instructor");

		if (instructor != null) {
			setInstructor(instructor);
		}
	}

	@Override
	public Course cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the course ID of this course.
	 *
	 * @return the course ID of this course
	 */
	@Override
	public long getCourseId() {
		return model.getCourseId();
	}

	/**
	 * Returns the course name of this course.
	 *
	 * @return the course name of this course
	 */
	@Override
	public String getCourseName() {
		return model.getCourseName();
	}

	/**
	 * Returns the credits of this course.
	 *
	 * @return the credits of this course
	 */
	@Override
	public int getCredits() {
		return model.getCredits();
	}

	/**
	 * Returns the description of this course.
	 *
	 * @return the description of this course
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the instructor of this course.
	 *
	 * @return the instructor of this course
	 */
	@Override
	public String getInstructor() {
		return model.getInstructor();
	}

	/**
	 * Returns the primary key of this course.
	 *
	 * @return the primary key of this course
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the students of this course.
	 *
	 * @return the students of this course
	 */
	@Override
	public int getStudents() {
		return model.getStudents();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the course ID of this course.
	 *
	 * @param courseId the course ID of this course
	 */
	@Override
	public void setCourseId(long courseId) {
		model.setCourseId(courseId);
	}

	/**
	 * Sets the course name of this course.
	 *
	 * @param courseName the course name of this course
	 */
	@Override
	public void setCourseName(String courseName) {
		model.setCourseName(courseName);
	}

	/**
	 * Sets the credits of this course.
	 *
	 * @param credits the credits of this course
	 */
	@Override
	public void setCredits(int credits) {
		model.setCredits(credits);
	}

	/**
	 * Sets the description of this course.
	 *
	 * @param description the description of this course
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the instructor of this course.
	 *
	 * @param instructor the instructor of this course
	 */
	@Override
	public void setInstructor(String instructor) {
		model.setInstructor(instructor);
	}

	/**
	 * Sets the primary key of this course.
	 *
	 * @param primaryKey the primary key of this course
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the students of this course.
	 *
	 * @param students the students of this course
	 */
	@Override
	public void setStudents(int students) {
		model.setStudents(students);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected CourseWrapper wrap(Course course) {
		return new CourseWrapper(course);
	}

}