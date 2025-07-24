/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.university.course.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;CollegeCourse_Course&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see Course
 * @generated
 */
public class CourseTable extends BaseTable<CourseTable> {

	public static final CourseTable INSTANCE = new CourseTable();

	public final Column<CourseTable, Long> courseId = createColumn(
		"courseId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CourseTable, String> courseName = createColumn(
		"courseName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CourseTable, String> description = createColumn(
		"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CourseTable, Integer> credits = createColumn(
		"credits", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CourseTable, Integer> students = createColumn(
		"students", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CourseTable, String> instructor = createColumn(
		"instructor", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private CourseTable() {
		super("CollegeCourse_Course", CourseTable::new);
	}

}