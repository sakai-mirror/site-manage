/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/site-manage/branches/cm-modification/site-manage-impl/impl/src/java/org/sakaiproject/site/impl/SampleCourseManagementProvider.java $
 * $Id: SampleCourseManagementProvider.java 8160 2006-04-22 01:37:10Z ggolden@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.site.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.impl.BaseMember;
import org.sakaiproject.authz.impl.BaseRole;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.site.api.Course;
import org.sakaiproject.site.api.CourseManagementProvider;
import org.sakaiproject.site.api.Term;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.util.ResourceLoader;

import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.impl.SectionCmImpl;

/**
 * <p>
 * Sample of course management provider.
 * </p>
 * <p>
 * Todo: %%% to be implemented; read course info from some config file.
 * </p>
 */
public class CourseManagementProviderDelegatingImpl implements CourseManagementProvider
{
	/** Resource bundle using current language locale */
	private static ResourceLoader rb = new ResourceLoader("SampleCourseManagementProvider");

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(CourseManagementProviderDelegatingImpl.class);
	private org.sakaiproject.coursemanagement.api.CourseManagementService cms =
	        (org.sakaiproject.coursemanagement.api.CourseManagementService) ComponentManager.
	          get(org.sakaiproject.coursemanagement.api.CourseManagementService.class);

	/** Sample coursed. */
	protected Course[] m_courses = null;
	protected HashMap m_coursesHash = new HashMap();
	protected HashMap m_termsHash = new HashMap();
	
	
	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			M_log.info("init()");
		}
		catch (Throwable t)
		{
			M_log.warn("init(): ", t);
		}

		// make sample courses
		Course c = new Course();
		c.setId("2005,FALL,SMPL,001,001");
		c.setTermId("FALL 2005");
		c.setTitle("Sample Course");
		m_courses = new Course[1];
		m_courses[0] = c;
		m_coursesHash.put(c.getId(), c);
		m_termsHash = getTermsHash();

	} // init

	/**
	 * Returns to uninitialized state. You can use this method to release resources thet your Service allocated when Turbine shuts down.
	 */
	public void destroy()
	{
		M_log.info("destroy()");

	} // destroy

	/**********************************************************************************************************************************************************************************************************************************************************
	 * CourseManagementProvider implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public List getCourseIdRequiredFields()
	{
		List rv = new Vector();
		rv.add(rb.getString("required_fields_subject"));
		rv.add(rb.getString("required_fields_course"));
		rv.add(rb.getString("required_fields_section"));
		return rv;

	} // getRequiredFieldsForCourseId

	/**
	 * Return a list of maximum field size for course id required fields
	 */
	public List getCourseIdRequiredFieldsSizes()
	{
		List rv = new Vector();
		rv.add(new Integer(8));
		rv.add(new Integer(3));
		rv.add(new Integer(3));
		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public String getCourseId(Term term, List requiredFields)
	{
		String rv = new String("");
		if (term != null)
		{
			rv = rv.concat(term.getYear() + "," + term.getTerm());
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

	/**
	 * @inheritDoc
	 */
	public String getCourseName(String courseId)
	{
		StringBuffer tab = new StringBuffer();
		String[] fields;

		// A single course with a single section; tab is course and section
		fields = courseId.split(",");
		if (fields.length == 5)
		{
			tab.append(fields[2]);
			tab.append(" ");
			tab.append(fields[3]);
			tab.append(" ");
			tab.append(fields[4]);
		}

		return tab.toString();

	}

	/**
	 * @inheritDoc
	 */
	public Course getCourse(String courseId)
	{
		Course c = null;
		Object o = m_coursesHash.get(courseId);
		if (o!=null)
			c = (Course)o;
       return c;
	}

	/**
	 * @inheritDoc
	 */
	public List getCourseMembers(String courseId)
	{
		Course c = getCourse(courseId);
		return c.getMembers();
	}

	/**
	 * @inheritDoc
	 */
	public List getInstructorCourses(String instructorId, String termYear, String termTerm)
	{
		List courseList = new Vector();
		HashMap courseOfferingHash = new HashMap();
		Vector courseOfferingVector = new Vector();
		Set sectionsSet = cms.findInstructingSections(instructorId);
		Iterator iter = sectionsSet.iterator();
		while (iter.hasNext()){
			SectionCmImpl section = (SectionCmImpl) iter.next();
			CourseOffering co = section.getCourseOffering();
			if (!courseOfferingVector.contains(co))
				courseOfferingVector.add(co);
		}
		
		m_courses = new Course[courseOfferingVector.size()];
		m_coursesHash = new HashMap();
		for (int i=0; i<courseOfferingVector.size(); i++){
			CourseOffering co = (CourseOffering)courseOfferingVector.get(i);
			Course c = createCourseFromCourseOffering(co);
			Term t = (Term) m_termsHash.get(c.getTermId());
			if (termYear !=null && termYear.equals(t.getYear())
					&& termTerm!=null && termTerm.equals(t.getTerm())){
				m_courses[i] = c;
				m_coursesHash.put(c.getId(),c);
				courseList.add(c);
			}
		}
		return courseList;
	}
	
	public Term getTerm(String id){
		Term t = null;
		Object o = m_termsHash.get(id);
		if (o != null)
			t = (Term)o;
		return t;
	}
	
	private Course createCourseFromCourseOffering(CourseOffering co){
		Course c = new Course();
		c.setId(co.getEid());
		c.setMembers(getMembers(cms.getCourseOfferingMemberships(co.getEid())));
		c.setSubject(co.getDescription());
		c.setTermId(co.getAcademicSession().getEid());
		c.setTitle(co.getTitle());
		return c;
	}
	
	private ArrayList getMembers(Set set){
		ArrayList list = new ArrayList();
		Iterator iter = set.iterator();
		while (iter.hasNext()){
			Membership ms=(Membership)iter.next();
			BaseMember m = new BaseMember(new BaseRole(ms.getRole()), true, false, ms.getUserId());
			list.add(m);
		}
		return list;	   
	}

	private HashMap getTermsHash(){
		HashMap h = new HashMap();
		List currentSessions = cms.getCurrentAcademicSessions();
		HashMap currentSessionsHash = prepareCurrentSessionsHash(currentSessions);
		List academicSessions = cms.getAcademicSessions(); 
		for (int i=0; i<academicSessions.size();i++){
			AcademicSession a = (AcademicSession)academicSessions.get(i);
			Term t = createTermFromAcademicSession(a, currentSessionsHash);
			h.put(t.getId(),t);
		}
		return h;
	}
		
    private Term createTermFromAcademicSession(AcademicSession a,
			HashMap currentSessionsHash) {
		Term t = new Term();
		t.setId(a.getEid());
		t.setListAbbreviation(a.getEid());
		t.setStartTime(TimeService.newTime(a.getStartDate().getTime()));
		t.setEndTime(TimeService.newTime(a.getEndDate().getTime()));
		t.setTerm(a.getTitle());
		String dateString = a.getStartDate().toString();
		String year = (dateString.substring(dateString.lastIndexOf(""))).trim();
		t.setYear(year);
		if (currentSessionsHash.get(a.getEid()) != null)
			t.setIsCurrentTerm(true);
		else
			t.setIsCurrentTerm(false);
		return t;
	}

	private HashMap prepareCurrentSessionsHash(List currentSessions) {
		HashMap h = new HashMap();
		for (int i=0; i<currentSessions.size();i++){
			AcademicSession a = (AcademicSession)currentSessions.get(i);
			h.put(a.getEid(),a);
		}
		return h;
	}

}
