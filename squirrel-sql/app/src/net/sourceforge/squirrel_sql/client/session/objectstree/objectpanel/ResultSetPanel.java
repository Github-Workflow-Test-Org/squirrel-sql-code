package net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JScrollPane;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ResultSetPanel extends JScrollPane {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(ResultSetPanel.class);

	private boolean _fullyCreated = false;
	private IDataSetViewer _viewer;

	public void load(final ISession session, final ResultSet rs, final int[] cols,
						final String destClassName)
	{
		try
		{
			// Lazily create the user interface.
			if (!_fullyCreated) {
				createUserInterface(session, destClassName);
				_fullyCreated = true;
			}
			final ResultSetDataSet ds = new ResultSetDataSet();
			SessionProperties props = session.getProperties();
			ds.setLargeResultSetObjectInfo(props.getLargeResultSetObjectInfo());
			ds.setResultSet(rs, cols);
			Runnable run = new Runnable()
			{
				public void run()
				{
					try
					{
						_viewer.show(ds);
					} catch(DataSetException dse)
					{
						s_log.error("Error", dse);
					}

				}
			};
			SwingUtilities.invokeLater(run);
		}
		catch(Exception ex)
		{
			s_log.error("Error", ex);
		}

	}

	public void clear()
	{
		if(_viewer != null)
		{
			_viewer.clear();
		}
	}
	private void createUserInterface(ISession session, String destClassName)
		throws DataSetException
	{
		_viewer = BaseDataSetViewerDestination.getInstance(destClassName);
		Runnable run = new Runnable()
		{
			public void run()
			{
				setViewportView(_viewer.getComponent());
			}
		};
		SwingUtilities.invokeLater(run);
	}
}

