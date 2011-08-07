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
package net.sourceforge.squirrel_sql.fw.gui.action;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.ExportDataException;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.IExportData;
import net.sourceforge.squirrel_sql.fw.gui.action.exportData.ResultSetExportData;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Command for exporting a result set to a file.
 * 
 * @author Stefan Willinger
 * 
 */
public class ResultSetExportCommand extends AbstractExportCommand {
	static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(ResultSetExportCommand.class);

	static ILogger log = LoggerController.createLogger(ResultSetExportCommand.class);

	static interface i18n {
		// i18n[ResultSetExportCommand.errorExecuteStatement="Could not create the data for exporting."]
		String ERROR_EXECUTE_STATEMENT = s_stringMgr
				.getString("ResultSetExportCommand.errorExecuteStatement");

	}

	private ResultSet resultSet;

	private DialectType dialect;

	private String sql;

	private Statement stmt;

	public ResultSetExportCommand(Statement stmt, String sql, DialectType dialect,
			ProgressAbortCallback progressController) {
		super(progressController);
		this.sql = sql;
		this.stmt = stmt;
		this.dialect = dialect;
	}

	/**
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.AbstractExportCommand#checkMissingData(java.lang.String)
	 */
	@Override
	protected boolean checkMissingData(String sepChar) {
		return false;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.AbstractExportCommand#createExportData()
	 */
	@Override
	protected IExportData createExportData(TableExportCsvController ctrl) throws ExportDataException{
		try {
			super.progress("Running the query");
			this.resultSet = stmt.executeQuery(sql);
			return new ResultSetExportData(this.resultSet, dialect);
		} catch (SQLException e) {
			log.error(i18n.ERROR_EXECUTE_STATEMENT, e);
			throw new ExportDataException(i18n.ERROR_EXECUTE_STATEMENT, e);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.gui.action.AbstractExportCommand#createTableExportController()
	 */
	@Override
	protected TableExportCsvController createTableExportController() {
		return new ResultSetExportCsvController();
	}

}
