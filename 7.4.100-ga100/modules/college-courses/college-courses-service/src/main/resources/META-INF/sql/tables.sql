create table CollegeCourse_Course (
	courseId LONG not null primary key,
	courseName VARCHAR(75) null,
	description VARCHAR(75) null,
	credits INTEGER,
	students INTEGER,
	instructor VARCHAR(75) null
);