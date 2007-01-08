/**
 * 
 */
package org.sakaiproject.util;

import java.util.Calendar;
import java.util.List;
import org.sakaiproject.coursemanagement.api.AcademicSession;

/**
 * @author daisyflemming
 *
 */
public class CourseIdGenerator {
	public static String getCourseId(AcademicSession a, List requiredFields)
	{
		String rv = new String("");
		if (a != null)
		{
			String dateString = a.getStartDate().toString();
			String year = dateString.substring(dateString.length()-4);
			rv = rv.concat(year+ "," + a.getTitle());
		}
		else
		{
			rv = rv.concat(",,");
		}
		for (int i = 0; i < requiredFields.size(); i++)
		{
			rv = rv.concat(",").concat((String) requiredFields.get(i));
		}
		return rv;

	} // getCourseId

}
