package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
 * 
 * Based on initial work from Colin Bell
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;

/**
 * This dialog allows the user to review and maintain connection properties for an alias.
 */
public class ConnectionPropertiesController implements IAliasPropertiesPanelController
{
	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ConnectionPropertiesController.class);

	private ConnectionPropertiesPanel _propsPnl;

	private SQLAlias _alias;

	public static interface i18n {
		// i18n[ConnectionPropertiesController.title=Connection]
		String TITLE = s_stringMgr.getString("ConnectionPropertiesController.title");
		
		// i18n[ConnectionPropertiesController.hint=Set session connection properties for this Alias]
		String HINT = s_stringMgr.getString("ConnectionPropertiesController.hint");
	}
	
	public ConnectionPropertiesController(SQLAlias alias)
	{
		_alias = alias;
		_propsPnl = new ConnectionPropertiesPanel(alias.getConnectionProperties());
	}

	public Component getPanelComponent()
	{
		return _propsPnl;
	}

	public void applyChanges()
	{
		_alias.setConnectionProperties(_propsPnl.getSQLAliasConnectionProperties());
	}

	public String getTitle()
	{
		return i18n.TITLE;
	}

	public String getHint()
	{
		return i18n.HINT;
	}

}
