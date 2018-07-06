package net.sourceforge.squirrel_sql.plugins.userscript;


import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.plugins.userscript.kernel.ScriptListController;
import net.sourceforge.squirrel_sql.plugins.userscript.kernel.UserScriptAdmin;

public class UserScriptAction extends SquirrelAction implements ISessionAction
{

	/** Current session. */
	protected ISession _session;

	/** Current plugin. */
	protected final UserScriptPlugin _plugin;

	public UserScriptAction(IApplication app, IResources resources, UserScriptPlugin plugin)
	{
		super(app, resources);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			UserScriptAdmin adm = _plugin.getUserScriptAdmin(_session);

			if(0 == adm.getTargets(getTargetType()).getAll().length)
			{
				return;
			}

			new ScriptListController(_session.getApplication().getMainFrame(), _plugin.getUserScriptAdmin(_session), getTargetType());
		}
	}

	/**
	 * Is redefined in UserScriptSQLAction.
	 */
	protected boolean getTargetType()
	{
		return UserScriptAdmin.TARGET_TYPE_DB_OBJECT;
	}



	/**
	 * Set the current session.
	 *
	 * @param	session		The current session.
	 */
	public void setSession(ISession session)
	{
		_session = session;
	}
}
