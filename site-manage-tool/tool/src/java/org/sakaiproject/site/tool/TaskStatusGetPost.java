/**********************************************************************************
 * $URL:$
 * $Id:$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.site.tool;

import org.sakaiproject.component.cover.ServerConfigurationService;


import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TaskStatusGetPost {
	
	// get the task status service url
	public static String taskStatusUrl = ServerConfigurationService.getString("taskStatusServiceUrl", "");
	
	private static Log M_log = LogFactory.getLog(TaskStatusGetPost.class);
	
	public static boolean serviceExist()
	{
		boolean rv = false;
		
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(taskStatusUrl);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			else
			{
				rv = true;
			}

		} catch (HttpException e) {
			M_log.warn("TaskStatusGetPost:serviceExist: Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			M_log.warn("TaskStatusGetPost:serviceExist: Fatal transport error: " + e.getMessage());
		} catch (Exception e) {
			M_log.warn("TaskStatusGetPost:serviceExist: Fatal error: " + e.getMessage());
		} finally {
			// Release the connection.
			method.releaseConnection();
		} 
		
		return rv;
	}
	
	
	public static String getStatus(String taskStatusStreamUrl)
	{
		String rv = "";
		
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(taskStatusUrl);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data
			rv = new String(responseBody);

		} catch (HttpException e) {
			M_log.warn("TaskStatusGetPost:getStatus: Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			M_log.warn("TaskStatusGetPost:getStatus: Fatal transport error: " + e.getMessage());
		} catch (Exception e) {
			M_log.warn("TaskStatusGetPost:getStatus: Fatal error: " + e.getMessage());
		} finally {
			// Release the connection.
			method.releaseConnection();
		} 
		
		return rv;
	}
	
	/**
	 * This is to create a new TaskStatusStream as the container for future instances of TaskStatusEntry
	 */
	public static String postTaskStatusStream()
	{
		String rv = "";

	    // Prepare HTTP post
	    PostMethod post = new PostMethod(taskStatusUrl);
	    
	    // Get HTTP client
	    HttpClient httpclient = new HttpClient();
	    // Execute request
	    try {
	        int result = httpclient.executeMethod(post);
	        // Display status code
	        M_log.info("TaskStatusGetPost:postTaskStatusStream: Response status code: " + result);
	        // Display response
	        M_log.info("TaskStatusGetPost:postTaskStatusStream: Response body: ");
	        M_log.info("TaskStatusGetPost:postTaskStatusStream: " + post.getResponseBodyAsString());
	        rv = post.getResponseBodyAsString();
	        
	    } catch (HttpException e) {
			M_log.warn("TaskStatusGetPost:postTaskStatusStream: Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			M_log.warn("TaskStatusGetPost:postTaskStatusStream: Fatal transport error: " + e.getMessage());
		} catch (Exception e) {
			M_log.warn("TaskStatusGetPost:postTaskStatusStream: Fatal error: " + e.getMessage());
		} finally {
	        // Release current connection to the connection pool once you are done
	        post.releaseConnection();
	    }
		
		return rv;
	}
	
	
	/**
	 * post task status
	 * @param status
	 * @param applicationTag
	 * @param passThru
	 */
	public static void postStatus(String postStatusUrl, String status, String applicationTag, String passThru)
	{
	    // Get file to be posted
	   // String strXMLFilename = args[1];
	   // File input = new File(strXMLFilename);

	    // Prepare HTTP post
	    PostMethod post = new PostMethod(postStatusUrl);
	    // Request content will be retrieved directly
	    // from the input stream
	   // RequestEntity entity = new FileRequestEntity(input, "text/xml; charset=ISO-8859-1");
	    //post.setRequestEntity(entity);
		// status
		post.addParameter("status", status);
		// created
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		post.addParameter("created", dateFormat.format(new Date(System.currentTimeMillis())));
		// application tag
		post.addParameter("appTag", applicationTag);
	    post.addParameter("passthru", passThru);
	    
	    // Get HTTP client
	    HttpClient httpclient = new HttpClient();
	    // Execute request
	    try {
	        int result = httpclient.executeMethod(post);
	        // Display status code
	        M_log.info("TaskStatusGetPost:postStatus: Response status code: " + result);
	        // Display response
	        M_log.info("TaskStatusGetPost:postStatus: Response body: ");
	        M_log.info("TaskStatusGetPost:postStatus: " + post.getResponseBodyAsString());
	        String postSt = post.getResponseBodyAsString();
	        
	    } catch (HttpException e) {
			M_log.warn("TaskStatusGetPost:postStatus: Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			M_log.warn("TaskStatusGetPost:postStatus: Fatal transport error: " + e.getMessage());
		} catch (Exception e) {
			M_log.warn("TaskStatusGetPost:postStatus: Fatal error: " + e.getMessage());
		}finally {
	        // Release current connection to the connection pool once you are done
	        post.releaseConnection();
	    }
	}

}
