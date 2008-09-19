package org.sakaiproject.site.tool.helper.participant.rsf;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.tool.helper.participant.impl.SiteAddParticipantHandler;
import org.sakaiproject.site.util.Participant;
import org.sakaiproject.site.util.SiteComparator;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import uk.ac.cam.caret.sakai.rsf.producers.FrameAdjustingProducer;
import uk.ac.cam.caret.sakai.rsf.util.SakaiURLUtil;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.evolvers.FormatAwareDateInputEvolver;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.RawViewParameters;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import uk.org.ponder.stringutil.StringList;

/**
 * 
 * @author
 *
 */
public class ConfirmProducer implements ViewComponentProducer, NavigationCaseReporter{

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(ConfirmProducer.class);
	
    public SiteAddParticipantHandler handler;
    public static final String VIEW_ID = "Confirm";
    public MessageLocator messageLocator;
    public FrameAdjustingProducer frameAdjustingProducer;
    public SiteService siteService = null;
    public SessionManager sessionManager;
    
	private TextInputEvolver richTextEvolver;
	public void setRichTextEvolver(TextInputEvolver richTextEvolver) {
				this.richTextEvolver = richTextEvolver;
	}

    public String getViewID() {
        return VIEW_ID;
    }
    
    private TargettedMessageList tml;
	public void setTargettedMessageList(TargettedMessageList tml) {
		this.tml = tml;
	}
	
	public UserDirectoryService userDirectoryService;
	public void setUserDiretoryService(UserDirectoryService userDirectoryService)
	{
		this.userDirectoryService = userDirectoryService;
	}

    public void fillComponents(UIContainer arg0, ViewParameters arg1, ComponentChecker arg2) {
    	
    	String state="";
    	
    	UIBranchContainer content = UIBranchContainer.make(arg0, "content:");
        
    	UIForm confirmForm = UIForm.make(content, "confirm-form");
    	
    	Hashtable<User, String> userRoleTable = handler.getUserRoleTable();
    	
    	for (Enumeration<User> users=userRoleTable.keys(); users.hasMoreElements(); ) {
        	User user = users.nextElement();
            UIBranchContainer userrow = UIBranchContainer.make(confirmForm, "user-row:", user.getId());
            
            UIOutput.make(userrow,"user-name",user.getSortName());
			UIOutput.make(userrow,"user-id",user.getId());
			UIOutput.make(userrow, "user-role", userRoleTable.get(user));	
        }
    	
    	UICommand.make(confirmForm, "continue", messageLocator.getMessage("gen.finish"), "#{siteAddParticipantHandler.processConfirmContinue}");
    	UICommand.make(confirmForm, "back", messageLocator.getMessage("gen.back"), "#{siteAddParticipantHandler.processConfirmBack}");
    	UICommand.make(confirmForm, "cancel", messageLocator.getMessage("gen.cancel"), "#{siteAddParticipantHandler.processCancel}");
   
    	
         frameAdjustingProducer.fillComponents(arg0, "resize", "resetFrame");
    }
    
    public List reportNavigationCases() {
        List togo = new ArrayList();
        togo.add(new NavigationCase("back", new SimpleViewParameters(EmailNotiProducer.VIEW_ID)));
        return togo;
    }
    
    public void interceptActionResult(ARIResult result, ViewParameters incoming,
            Object actionReturn) 
    {
        if ("done".equals(actionReturn) || "finish".equals(actionReturn)) {
          Tool tool = handler.getCurrentTool();
           result.resultingView = new RawViewParameters(SakaiURLUtil.getHelperDoneURL(tool, sessionManager));
        }
    }

}
