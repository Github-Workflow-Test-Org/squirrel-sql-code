package net.sourceforge.squirrel_sql.plugins.example;

import net.sourceforge.squirrel_sql.client.session.event.SQLExecutionAdapter;
import net.sourceforge.squirrel_sql.fw.sql.QueryHolder;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

/*
 * Copyright (C) 2010 Rob Manning
 * manningr@users.sourceforge.net
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

/**
 * A simple ISQLExecutionListener that displays the SQL as it is being executed in the main application 
 * message panel (at the bottom of the application).
 */
public class ExampleSqlExecutionListener extends SQLExecutionAdapter
{
	/** This is what gives the ability to print a message to the message panel */
	private final IMessageHandler _messageHandler;
	
	public ExampleSqlExecutionListener(IMessageHandler messageHandler) {
		_messageHandler = messageHandler;
	}

	@Override
	public void statementExecuted(QueryHolder sql)
	{
		_messageHandler.showMessage("statementExecuted: "+sql.getQuery());
	}

	@Override
	public String statementExecuting(String sql)
	{
		_messageHandler.showMessage("statementExecuting: "+sql);
		
		// We don't modify the SQL in this example.  in addition to modifying it, we could veto it's execution 
		// by returning null.
		return sql;
	}

   public void executionFinished()
   {
   }

}
