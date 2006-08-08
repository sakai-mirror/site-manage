/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/site-manage/branches/cm-modification/site-manage-impl/impl/src/java/org/sakaiproject/site/impl/BasicCourseManagementService.java $
 * $Id: BasicCourseManagementService.java 8655 2006-05-02 02:31:46Z ggolden@umich.edu $
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Course;
import org.sakaiproject.site.api.CourseManagementProvider;
import org.sakaiproject.site.api.CourseManagementService;
import org.sakaiproject.site.api.Term;
import org.sakaiproject.time.cover.TimeService;

import org.sakaiproject.time.api.Time;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.impl.BaseMember;
import org.sakaiproject.authz.impl.BaseRole;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Membership;


/**
 * <p>
 * BasicCourseManagementService is a course management service.
 * </p>
 */
public class CourseManagementServiceDelegatingImpl implements CourseManagementService
{
	/** Our logger. */
	private static Log M_log = LogFactory.getLog(CourseManagementServiceDelegatingImpl.class);

    private org.sakaiproject.coursemanagement.api.CourseManagementService cms =
        (org.sakaiproject.coursemanagement.api.CourseManagementService) ComponentManager.
          get(org.sakaiproject.coursemanagement.api.CourseManagementService.class);

	/** A course management provider. */
	protected CourseManagementProviderDelegatingImpl m_provider = null;

	/** The term information */
	protected List m_terms = null;
	protected HashMap termsHash = null;

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Configuration: set the course management provider helper service.
	 * 
	 * @param provider
	 *        the user directory provider helper service.
	 */
	public void setProvider(CourseManagementProvider provider)
	{
		m_provider = (CourseManagementProviderDelegatingImpl) provider;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		// if we didn't get a provider, use the registered one
		if (m_provider == null)
		{
			// first see if one is registered
			m_provider = (CourseManagementProviderDelegatingImpl) ComponentManager.get(CourseManagementProvider.class.getName());
		}		
		
		// if still no provider, use the sample one
		if (m_provider == null)
		{
			CourseManagementProviderDelegatingImpl provider = new CourseManagementProviderDelegatingImpl ();
			provider.init();
			m_provider = provider;
			M_log.info("init() : using the sample site-manage-impl CourseManagementProvider - NOT RECOMMENDED FOR PRODUCTION");
		}

		m_terms = new Vector();
		List termTerms = new Vector();
		List termYears = new Vector();
		List termListAbbrs = new Vector();
		List termIsCurrent = new Vector();
		List termStartTimes = new Vector();
		List termEndTimes = new Vector();
		
		List academicSessions = cms.getAcademicSessions(); 
		for (int i=0; i<academicSessions.size();i++){
			AcademicSession a = (AcademicSession)academicSessions.get(i);
			Term t = m_provider.getTerm(a.getEid());

			// add to termTerms, termYears, termListAbbrs, termIsCurrent, 
			// termStartTimes, termEndTimes
			termTerms.add(t.getTerm());
			termYears.add(t.getYear());
			termListAbbrs.add(t.getListAbbreviation());
			termIsCurrent.add(t.isCurrentTerm());
			termStartTimes.add(t.getStartTime());
			termEndTimes.add(t.getEndTime());
		}

		M_log.info("init(): provider: " + m_provider.getClass().getName());

	} // init
	
	


	/**
	 * Returns to uninitialized state. You can use this method to release resources thet your Service allocated when Turbine shuts down.
	 */
	public void destroy()
	{
		m_provider = null;

		M_log.info("destroy()");

	} // destroy

	/**********************************************************************************************************************************************************************************************************************************************************
	 * CourseManagementService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public List getCourseIdRequiredFields()
	{
		return m_provider.getCourseIdRequiredFields();

	} // getCourseIdRequriedFields

	/**
	 * @inheritDoc
	 */
	public List getCourseIdRequiredFieldsSizes()
	{
		return m_provider.getCourseIdRequiredFieldsSizes();

	} // getCourseIdRequiredFieldsSizes

	/**
	 * @inheritDoc
	 */
	public String getCourseId(Term term, List requiredFields)
	{
		return m_provider.getCourseId(term, requiredFields);

	} // getCourseId

	/**
	 * Access all terms
	 * 
	 * @return A course object containing the coure information
	 * @exception IdUnusedException
	 *            if not found
	 */
	public List getTerms()
	{
		return m_terms;

	} // getTerms

	/**
	 * Access a term
	 * 
	 * @param termId
	 *        The term id
	 * @exception IdUnusedException
	 *            if not found
	 */
	public Term getTerm(String termId)
	{
		Term t = null;
		Object o = termsHash.get(termId);
		if (o != null)
			t = (Term)o;
		return t;

	} // getTerms

	/**
	 * Find the course object (only - no provider check).
	 * 
	 * @param id
	 *        The course id.
	 * @return The course object found, or null if not found.
	 */
	protected Course findCourse(String courseId)
	{
		Course c = new Course();
		try {
			c= getCourse(courseId);
		} catch (IdUnusedException e) {
			M_log.error(e.getMessage());
		}
		return c;
	} // findCourse

	/**
	 * @inheritDoc
	 */
	public Course getCourse(String id) throws IdUnusedException
	{
		if (id != null)
		{
			return m_provider.getCourse(id);
		}
		else
		{
			return new Course();
		}

	} // getCourse

	/**
	 * @inheritDoc
	 */
	public List getCourseMembers(String id) throws IdUnusedException
	{
		if (id != null)
		{
			return m_provider.getCourseMembers(id);
		}
		else
		{
			return new Vector();
		}

	} // getCourseMembers

	/**
	 * @inheritDoc
	 */
	public String getCourseName(String id) throws IdUnusedException
	{
		return m_provider.getCourseName(id);

	} // getCourseName

	/**
	 * @inheritDoc
	 */
	public List getInstructorCourses(String instructorId, String termYear, String termTerm)
	{
		// TODO: make this more efficient
		List rv = new Vector();

		rv = m_provider.getInstructorCourses(instructorId, termYear, termTerm);

		return rv;
	}

} // CourseManagementServiceDelegatingImpl

