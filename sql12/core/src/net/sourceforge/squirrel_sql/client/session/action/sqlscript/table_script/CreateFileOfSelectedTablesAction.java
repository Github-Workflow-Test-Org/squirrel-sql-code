/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.event.ActionEvent;

public class CreateFileOfSelectedTablesAction extends SquirrelAction implements IObjectTreeAction
{


   private IObjectTreeAPI _objectTreeAPI;

   public CreateFileOfSelectedTablesAction()
   {
      super(Main.getApplication(), Main.getApplication().getResources());
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      if(null == _objectTreeAPI)
      {
         return;
      }

      new CreateFileOfSQLCommand(_objectTreeAPI.getSession()).executeForSelectedTables(GUIUtils.getOwningWindow(_objectTreeAPI.getObjectTree()), _objectTreeAPI.getSelectedTables(), _objectTreeAPI);
   }

   @Override
   public void setObjectTree(IObjectTreeAPI objectTreeAPI)
   {
      _objectTreeAPI = objectTreeAPI;
      setEnabled(null != _objectTreeAPI);
   }
}
