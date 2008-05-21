package org.sakaiproject.site.tool.helper.managegroup.rsf;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.tool.helper.managegroup.impl.SiteManageGroupHandler;
import org.sakaiproject.site.tool.helper.managegroup.rsf.GroupEditViewParameters;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.user.api.User;

import uk.ac.cam.caret.sakai.rsf.producers.FrameAdjustingProducer;
import uk.ac.cam.caret.sakai.rsf.util.SakaiURLUtil;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.flow.jsfnav.DynamicNavigationCaseReporter;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.RawViewParameters;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.stringutil.StringList;

/**
 * 
 * @author
 *
 */
public class GroupListProducer 
        implements ViewComponentProducer, DynamicNavigationCaseReporter, DefaultView {
    
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(GroupListProducer.class);
	
    public static final String VIEW_ID = "GroupList";
    public Map siteGroups;
    public SiteManageGroupHandler handler;
    public MessageLocator messageLocator;
    public SessionManager sessionManager;
    public FrameAdjustingProducer frameAdjustingProducer;
    
    public String getViewID() {
        return VIEW_ID;
    }

    public void fillComponents(UIContainer tofill, ViewParameters viewparams,
            ComponentChecker checker) {

    		UIOutput.make(tofill, "group-list-title", messageLocator.getMessage("group.list"));
    		
    		UIForm deleteForm = UIForm.make(tofill, "delete-group-form");
    		
    		boolean renderDelete = false;
    		
    		List<Group> groups = null;

    		groups = handler.getGroups();
            if (groups != null)
            {
	            for (Iterator<Group> it=groups.iterator(); it.hasNext(); ) {
	            	Group group = it.next();
	                UIBranchContainer grouprow = 
	                    UIBranchContainer.make(deleteForm, "group-row:", group.getId());
	    
	                grouprow.decorate(new UITooltipDecorator(messageLocator
	                        .getMessage("group_click_n_drag")));
	                
	                UIOutput.make(grouprow, "group-name", group.getTitle());
	                UIInput name = 
	                    UIInput.make(grouprow, "group-name-input", "#{SitegroupEditHandler.nil}", group.getTitle());
	                UIOutput nameLabel = 
	                    UIOutput.make(grouprow, "group-name-label", messageLocator.getMessage("title"));
	                
	                nameLabel.decorate(new UILabelTargetDecorator(name));
	            }
            }

    		// Create a multiple selection control for the tasks to be deleted.
    		// We will fill in the options at the loop end once we have collected them.
    		UISelect deleteselect = UISelect.makeMultiple(deleteForm, "delete-group",
    				null, "#{SiteManageGroupHandler.deleteGroupIds}", new String[] {});

    		//get the headers for the table
    		UIMessage.make(deleteForm, "group-title-title","group.title");
    		UIMessage.make(deleteForm, "group-size-title", "group.number");
    		UIMessage.make(deleteForm, "group-remove-title", "editgroup.remove");

    		StringList deletable = new StringList();
    		M_log.debug(this + "fillComponents: got a list of " + groups.size() + " groups");
    		for (int i = 0 ; i < groups.size(); i++) {
    			Group group = (Group) groups.get(i);
    			UIBranchContainer grouprow = UIBranchContainer.make(deleteForm, "group-row:", group.getId());

    			UIOutput.make(grouprow,"group-title",group.getTitle());
    			UIOutput.make(grouprow,"group-size",String.valueOf(group.getMembers().size()));

    			UIInternalLink editLink = UIInternalLink.make(grouprow,"group-revise",messageLocator.getMessage("editgroup.revise"),  
    						new GroupEditViewParameters(/*GroupEditProducer.VIEW_ID, group.getId().toString()*/));
    				editLink.decorators = new DecoratorList(new UITooltipDecorator(messageLocator.getMessage("editgroup.revise")+ ":" + group.getTitle()));

    			deletable.add(group.getId());
				UISelectChoice delete =  UISelectChoice.make(grouprow, "group-select", deleteselect.getFullID(), (deletable.size()-1));
				delete.decorators = new DecoratorList(new UITooltipDecorator(UIMessage.make("delete_group_tooltip", new String[] {group.getTitle()})));
				UIMessage message = UIMessage.make(grouprow,"delete-label","delete_group_tooltip", new String[] {group.getTitle()});
				UILabelTargetDecorator.targetLabel(message,delete);
				M_log.debug(this + ".fillComponent: this group can be deleted");
				renderDelete = true;
    		}

    		deleteselect.optionlist.setValue(deletable.toStringArray());
    		UICommand.make(deleteForm, "delete-groups",  UIMessage.make("editgroup.update"), "#{SiteManageGroupHandler.confirmGroupDelete}");
    }

    public List reportNavigationCases() {
        List togo = new ArrayList();
        

        return togo;
    }
}
