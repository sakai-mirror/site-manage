package org.sakaiproject.sitemanage.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.sitemanage.api.TaskStatusService;
import org.sakaiproject.time.api.TimeService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.id.api.IdManager;

public class TaskStatusServiceSqlImpl implements TaskStatusService 
{

	private static Log logger = LogFactory.getLog(TaskStatusServiceSqlImpl.class);
	
	protected SqlService sqlService = null;
	public void setSqlService(SqlService sqlService)
	{
		this.sqlService = sqlService;
	}
	
	protected IdManager idManager = null;
	public void setIdManager(IdManager idManager)
	{
		this.idManager = idManager;
	}
	
	protected TimeService timeService = null;
	public void setTimeService(TimeService timeService)
	{
		this.timeService = timeService;
	}
	
	public void init()
	{
		if(this.sqlService != null) // && autoDdl
		{
			this.sqlService.ddl(this.getClass().getClassLoader(), "sakai_taskstatus");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean serviceExist()
	{
		// always return true
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getMostRecentTaskStatusForUser(String userId)
	{
		// always modify
		String statement = "select STATUS from TASKSTATUS where USER_ID = ? order by CREATED_TIME desc";
		
		boolean ok = true;
		Object[] selectFields = new Object[1];
		selectFields[0] = userId;
		List rv = sqlService.dbRead(statement, selectFields, new SqlReader()
		{
			public Object readSqlResultRecord(ResultSet result)
			{
				try
				{
					String status = result.getString(1);
					return status;
				}
				catch (SQLException e)
				{
					return null;
				}
			}
		});
		if (rv == null || rv.size() == 0)
		{
			logger.info("postTaskStatus: cannot find record for user = " + userId);
			return null;
		}
		else
		{
			return (String) rv.get(0);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getTaskStatus(String entityReference, String userId)
	{
		// always modify
		String statement = "select ID, SITE_ID, USER_ID, STATUS from TASKSTATUS where SITE_ID = ? AND USER_ID = ? order by CREATED_TIME desc";
		
		boolean ok = true;
		Object[] selectFields = new Object[2];
		selectFields[0] = entityReference;
		selectFields[1] = userId;
		List rv = sqlService.dbRead(statement, selectFields, new SqlReader()
		{
			public Object readSqlResultRecord(ResultSet result)
			{
				try
				{
					String id = result.getString(1);
					String siteId = result.getString(2);
					String userId = result.getString(3);
					String status = result.getString(4);
					return status;
				}
				catch (SQLException e)
				{
					return null;
				}
			}
		});
		if (rv == null || rv.size() == 0)
		{
			logger.info("postTaskStatus: cannot find record for " + entityReference + " user = " + userId);
			return null;
		}
		else
		{
			return (String) rv.get(0);
		}
	}
	

	/**
	 * {@inheritDoc}
	 */
	public void postTaskStatus(String entityReference, String userId, String status)
	{
		// always modify
		String statement = "select ID, SITE_ID, USER_ID, STATUS from TASKSTATUS where SITE_ID = ? AND USER_ID = ? order by CREATED_TIME desc";
		
		boolean ok = true;
		Object[] selectFields = new Object[2];
		selectFields[0] = entityReference;
		selectFields[1] = userId;
		List rv = sqlService.dbRead(statement, selectFields, new SqlReader()
		{
			public Object readSqlResultRecord(ResultSet result)
			{
				try
				{
					String id = result.getString(1);
					String siteId = result.getString(2);
					String userId = result.getString(3);
					String status = result.getString(4);
					if (!status.equals(SiteConstants.ENTITYCOPY_THREAD_STATUS_FINISHED) && !status.equals(SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR))
					{
						// status is updatable if not finished or error
						return id;
					}
					else
					{
						return null;
					}
				}
				catch (SQLException e)
				{
					return null;
				}
			}
		});
		if (rv == null || rv.size() == 0)
		{
			logger.info("postTaskStatus: cannot find record for " + entityReference + " user = " + userId);
		}
		else
		{
			try
			{
			final Connection connection = sqlService.borrowConnection();
			boolean wasCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			
			// based on constraint, there could only be one task per user per site
			String statusId = (String) rv.get(0);
			String updateStatement = "update TASKSTATUS set STATUS = ? WHERE ID = ?";
			Object updateFields[] = new Object[2];
			updateFields[0] = status;
			updateFields[1] = statusId;
			ok = sqlService.dbWrite(connection, updateStatement, updateFields);

			if (!ok)
				logger.info(this + " postTaskStatus error of updating post status for " + entityReference + " user = " + userId);
			}
			catch (SQLException e)
			{
				logger.warn(this + " postTaskStatus error of updating post status for " + entityReference + " user = " + userId + " " + e.getMessage());
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addTaskStatus(String entityReference, String userId, String status)
	{
		String taskStatusId = idManager.createUuid();
		
		String timeStampStringDefault = "CURRENT_TIMESTAMP";
		if (sqlService.getVendor().equals("oracle"))
		{
			timeStampStringDefault = "CURRENT_TIMESTAMP()";
		}
		// always modify
		String statement = "insert into taskstatus (ID, SITE_ID, USER_ID, STATUS, CREATED_TIME) values ( ?, ?, ?, ?, " + timeStampStringDefault + ")";
		
		boolean ok = true;
		Object[] fields = new Object[4];
		fields[0] = taskStatusId;
		fields[1] = entityReference;
		fields[2] = userId;
		fields[3] = status;
		ok = sqlService.dbWrite(statement, fields);
		
		// log
		logger.info(this + "addTaskStatus: taskstatus for " + entityReference + " user = " + userId + "has been created with status " + status + " " + ok);
	}
	
	

}
