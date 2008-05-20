package org.sakaiproject.site.tool.helper.managegroup.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.util.Participant;
import org.sakaiproject.site.util.SiteParticipantHelper;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Web;

/**
 * 
 * @author Joshua Ryan joshua.ryan@asu.edu
 *
 */
public class SiteManageGroupHandler {
	
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SiteManageGroupHandler.class);
	
	private Collection<Member> groupMembers;
	
    public Site site = null;
    public SiteService siteService = null;
    public AuthzGroupService authzGroupService = null;
    public ToolManager toolManager = null;
    public SessionManager sessionManager = null;
    public ServerConfigurationService serverConfigurationService;
    private Map groups = null;
    public String[] selectedSiteMembers = new String[] {};
    public String[] selectedGroupMembers = new String[] {};
    private Set unhideables = null;
    public String state = "";
    public String test = null;
    public boolean update = false;
    public boolean done = false;
    
    private String NULL_STRING = "";
    
    //Just something dumb to bind to in order to supress warning messages
    public String nil = null;
    
    private final String TOOL_CFG_FUNCTIONS = "functions.require";
    private final String TOOL_CFG_MULTI = "allowMultiple";
    private final String SITE_UPD = "site.upd";
    private final String HELPER_ID = "sakai.tool.helper.id";
    private final String UNHIDEABLES_CFG = "poh.unhideables";
    private final String GROUP_ADD = "group.add";
    private final String GROUP_DELETE = "group.delete";
    private final String GROUP_RENAME = "group.rename";
    private final String GROUP_SHOW = "group.show";
    private final String GROUP_HIDE = "group.hide";
    private final String SITE_REORDER = "group.reorder";
    private final String SITE_RESET = "group.reset";

    //System config for which tools can be added to a site more then once
    private final String MULTI_TOOLS = "sakai.site.multiPlacementTools";

    // Tool session attribute name used to schedule a whole page refresh.
    public static final String ATTR_TOP_REFRESH = "sakai.vppa.top.refresh"; 

    private String[] defaultMultiTools = {"sakai.news", "sakai.iframe"};
    
	private static final String GROUP_PROP_WSETUP_CREATED = "group_prop_wsetup_created";
    
	// group title
	private String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// group description
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
    /**
     * Gets the groups for the current site
     * @return Map of groups (id, group)
     */
    public Map getGroups() {
        if (site == null) {
            init();
        }
        if (update) {
            groups = new LinkedHashMap();
            if (site != null)
            {   
                // only show groups created by WSetup tool itself
    			Collection allGroups = (Collection) site.getGroups();
    			for (Iterator gIterator = allGroups.iterator(); gIterator.hasNext();) {
    				Group gNext = (Group) gIterator.next();
    				String gProp = gNext.getProperties().getProperty(
    						GROUP_PROP_WSETUP_CREATED);
    				if (gProp != null && gProp.equals(Boolean.TRUE.toString())) {
    					groups.put(gNext.getId(), gNext);
    				}
    			}
            }
        }
        return groups;
    }
    
    /**
     * Initialization method, just gets the current site in preperation for other calls
     *
     */
    public void init() {
        if (site == null) {
            String siteId = null;
            try {
                siteId = sessionManager.getCurrentToolSession()
                        .getAttribute(HELPER_ID + ".siteId").toString();
            }
            catch (java.lang.NullPointerException npe) {
                // Site ID wasn't set in the helper call!!
            }
            
            if (siteId == null) {
                siteId = toolManager.getCurrentPlacement().getContext();
            }
            
            try {    
                site = siteService.getSite(siteId);
            
            } catch (IdUnusedException e) {
                // The siteId we were given was bogus
                e.printStackTrace();
            }
        }
        update = siteService.allowUpdateSite(site.getId());
        title = site.getTitle();
        
        String conf = serverConfigurationService.getString(UNHIDEABLES_CFG);
        if (conf != null) {
            unhideables = new HashSet();
            String[] toolIds = conf.split(",");
            for (int i = 0; i < toolIds.length; i++) {
                unhideables.add(toolIds[i].trim());
            }
        }
        
        if (groupMembers == null)
        {
        	groupMembers = new Vector<Member>();
        }
    }
    
    /**
     * Wrapper around siteService to save a site
     * @param site
     * @throws IdUnusedException
     * @throws PermissionException
     */
    public void saveSite(Site site) throws IdUnusedException, PermissionException {
        siteService.save(site);
    }
    
    public Collection<Participant> getSiteParticipant()
    {
    	Collection<Participant> rv = new Vector<Participant>();
    	if (site != null)
    	{
    		String siteId = site.getId();
    		String realmId = siteService.siteReference(siteId);

    		List<String> providerCourseList = SiteParticipantHelper.getProviderCourseList(siteId);
    		rv = SiteParticipantHelper.prepareParticipants(siteId, providerCourseList);
    	}
    	
    	return rv;
    }
    
    public Collection<Member> getGroupParticipant()
    {
    	
    	return groupMembers;
    }
    
    /**
     * Allows the Cancel button to return control to the tool calling this helper
     *
     */
    public String cancel() {
        ToolSession session = sessionManager.getCurrentToolSession();
        session.setAttribute(ATTR_TOP_REFRESH, Boolean.TRUE);

        return "done";
    }
    
    /**
     * Cancel out of the current action and go back to main view
     * 
     */
    public String back() {
      return "back";
    }
    
    public String reset() {
        try {
            siteService.save(site);
            EventTrackingService.post(
                EventTrackingService.newEvent(SITE_RESET, "/site/" + site.getId(), false));

        } 
        catch (IdUnusedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (PermissionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }
    
    /**
     * Adds a new group to the current site
     * @param toolId
     * @param title
     * @return the newly added Group
     */
    public Group addGroup () {
    	String siteReference = siteService.siteReference(site.getId());
        Group group = null;
        try {
            group= site.addGroup();
            group.setTitle("new group");
            group.setDescription("");   
            
            if (state != null) {
                String[] members = state.split(",");
                for (int i = 0; i < members.length; i++) {
                	String memberId = members[i];
                	try {
    					User u = UserDirectoryService.getUser(memberId);
    					try
    					{
    						AuthzGroup authzGroup = authzGroupService.getAuthzGroup(siteReference);
	    					Member siteMember = authzGroup.getMember(memberId);
	    					group.addMember(memberId, siteMember.getRole().getId(), siteMember.isActive(), siteMember.isProvided());
    					}
    					catch (GroupNotDefinedException gNotDefinedException)
    					{
    						M_log.warn(this + ".addGroup: cannot find site " + siteReference, gNotDefinedException);
    					}
    				} catch (UserNotDefinedException e) {
    					M_log.warn(this + ".addGroup: cannot find user " + memberId, e);
    				}
                }
    		}
                
            siteService.save(site);
        } 
        catch (IdUnusedException e) {
        	M_log.warn(this + ".addGroup: cannot find site " + site.getId(), e);
            return null;
        } 
        catch (PermissionException e) {
        	M_log.warn(this + ".addGroup: cannot find site " + site.getId(), e);
            return null;
        }
        init();
        
        return group;
    }
    
    /**
     * Removes a group from the site
     * 
     * @param groupId
     * @return title of page removed
     * @throws IdUnusedException
     * @throws PermissionException
     */
    public String removeGroup(String groupId)
                            throws IdUnusedException, PermissionException {
        Group group = site.getGroup(groupId);
        site.removeGroup(group);
        saveSite(site);

        EventTrackingService.post(
            EventTrackingService.newEvent(GROUP_DELETE, "/site/" + site.getId() +
                                          "/group/" + group.getId(), false));
        
        return group.getTitle();
    }
   
}

