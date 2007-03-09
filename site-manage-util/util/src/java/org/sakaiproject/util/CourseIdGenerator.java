/**
 * 
 */
package org.sakaiproject.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.AcademicSession;

/**
 * @author daisyflemming
 * 
 */
public class CourseIdGenerator {
	private static ResourceLoader rb = new ResourceLoader(
			"SampleCourseManagementProvider");

	/*
	 * making assumption that sectionEid = "subject course section
	 * academicSession.eid"
	 */
	public static String getSubject(String courseId) {
		String subject = "";
		String format = ServerConfigurationService
				.getString("site-manage.courseId.requiredField.format",
						"required_fields_courseId");

		String delimiter = ServerConfigurationService.getString(
				"site-manage.courseId.delimiter", ",");
		String[] required_fields = format.split(",");
		String[] fields = courseId.split(delimiter);
		for (int i = 0; i < required_fields.length; i++) {
			if ("required_fields_subject".equals(required_fields[i].trim())) {
				subject = fields[i + 2]; // default courseId format =
											// 2007,WINTER,SMPL,001,001
				return subject;
			}
		}
		return subject;
	} // getSubject


	// courseName is made up of required fields only and separated by " "
	public static String getCourseName(String courseId) {
		// default courseId format = 2007,WINTER,SMPL,001,001 (2007, Winter,
		// required_field[])
		String name = "";
		String idDelimiter = ServerConfigurationService.getString(
				"site-manage.courseId.delimiter", ",");
		String nameDelimiter = ServerConfigurationService.getString(
				"site-manage.courseName.delimiter", " ");
		String[] fields = courseId.split(idDelimiter);
		if (fields.length > 2) {
			for (int i = 2; i < fields.length; i++) {
				if ("".equals(name)) {
					name = fields[i];
				} else { // courseName format = SMPL,001,001
					name = name + nameDelimiter + fields[i];
				}
			}
		} else {
			name = courseId;
		}
		return name;
	}

}
