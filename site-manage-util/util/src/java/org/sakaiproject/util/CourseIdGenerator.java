/**
 * 
 */
package org.sakaiproject.util;

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

	public static String getCourseId(AcademicSession a, List requiredFields) {
		String delimiter = ServerConfigurationService.getString(
				"site-manage.courseId.delimiter", ",");
		String format = ServerConfigurationService.getString(
				"site-manage.courseId.format", null);

		List requiredFieldsList = getCourseIdRequiredFields();
		String rv = new String("");
		if (a != null && format == null) {
			// default courseId format = 2007,WINTER,SMPL,001,001
			String dateString = a.getStartDate().toString();
			String year = dateString.substring(dateString.length() - 4);
			rv = rv.concat(year + delimiter + a.getTitle());
		} else {
			rv = rv.concat(delimiter + delimiter);
		}
		for (int i = 0; i < requiredFields.size(); i++) {
			if ("".equals(rv)) {
				rv = (String) requiredFields.get(i);
			} else {
				rv = rv.concat(delimiter)
						.concat((String) requiredFields.get(i));
			}
		}
		return rv;
	} // getCourseId

	/*
	 * making assumption that sectionEid = "subject course section
	 * academicSession.eid"
	 */
	public static String getSubject(String courseId) {
		String subject = "";
		String format = ServerConfigurationService
				.getString("site-manage.courseId.requiredField.format",
						"required_fields_subject required_fields_course required_fields_section");

		String delimiter = ServerConfigurationService.getString(
				"site-manage.courseId.delimiter", ",");
		String[] required_fields = format.split(" ");
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

	public static List getCourseIdRequiredFields() {
		String format = ServerConfigurationService
				.getString("site-manage.courseId.requiredFields.format",
						"required_fields_subject required_fields_course required_fields_section");
		String[] required_fields = format.split(" ");

		List rv = new Vector();
		for (int i = 0; i < required_fields.length; i++) {
			rv.add(rb.getString(required_fields[i].trim()));
			System.out.println(required_fields[i]+"="+rb.getString(required_fields[i].trim()));
		}
		return rv;

	}

	public static List getCourseIdRequiredFieldsSizes() {
		String formatSize = ServerConfigurationService.getString(
				"site-manage.courseId.requiredFields.size", "8 3 3");
		String[] required_fields = formatSize.split(" ");
		List rv = new Vector();
		for (int i = 0; i < required_fields.length; i++) {
			rv.add(new Integer(required_fields[i].trim()));
		}
		return rv;
	}

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
		for (int i = 2; i < fields.length; i++) {
			if ("".equals(name)) {
				name = fields[i];
			} else { // courseName format = SMPL,001,001
				name = nameDelimiter + fields[i];
			}
		}
		return name;
	}

	public static String getProviderId (List providerIdList)
	{
		String rv = "";
		
		for (int i = 0; i < providerIdList.size(); i++)
		{
			// concatinate list items by "+"
			if (i > 0)
			{
				rv = rv.concat("+").concat((String) providerIdList.get(i));
			}
			else
			{
				rv = rv.concat((String) providerIdList.get(i));
			}
		}
		return rv;
	}
}
