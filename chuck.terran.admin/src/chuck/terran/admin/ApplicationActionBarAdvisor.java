package chuck.terran.admin;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import chuck.terran.admin.ui.DataSourceStatusContribution;
import chuck.terran.admin.ui.NetworkStatusContribution;
import chuck.terran.admin.ui.ViewContextStatusContribution;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void fillStatusLine(IStatusLineManager statusLine)
	{
        statusLine.appendToGroup("END_GROUP", new DataSourceStatusContribution());
        statusLine.appendToGroup("END_GROUP", new NetworkStatusContribution());
        statusLine.appendToGroup("END_GROUP", new ViewContextStatusContribution());
	}
}
