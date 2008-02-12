/**********************************************************************************
 * $URL:  $
 * $Id:  $
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
package org.sakaiproject.site.util;

/**
 * The SiteSetupQuestion object is to store answers to SiteSetupQuestion.
 * @author zqian
 *
 */
public class SiteSetupQuestionAnswer
{
	/* whether the answer type is fill in blank*/
	private boolean isFillInBlank = false;
	
	/* answer prompt string */
	private String answer = "";
	
	/* user type in string */
	private String fillInBlankString = "";

	public boolean isFillInBlank() {
		return isFillInBlank;
	}

	public void setFillInBlank(boolean isFillInBlank) {
		this.isFillInBlank = isFillInBlank;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getFillInBlankString() {
		return fillInBlankString;
	}

	public void setFillInBlankString(String fillInBlankString) {
		this.fillInBlankString = fillInBlankString;
	}
	
}