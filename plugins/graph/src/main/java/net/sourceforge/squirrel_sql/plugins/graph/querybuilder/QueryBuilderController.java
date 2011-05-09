package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.*;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.sqlgen.QueryBuilderSQLGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

public class QueryBuilderController
{
   private static final String PREF_KEY_SQL_DOCK_HEIGHT = "Squirrel.graph.sqldock.height";
   private static final String PREF_KEY_RESULT_DOCK_HEIGHT = "Squirrel.graph.resultdock.height";
   private static final String PREF_KEY_ORDER_DOCK_HEIGHT = "Squirrel.graph.orderdock.height";
   private static final String PREF_KEY_WHERE_DOCK_HEIGHT = "Squirrel.graph.wheredock.height";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryBuilderController.class);

   private JPanel _panel;
   private JToggleButton _btnSQL;
   private JToggleButton _btnResult;
   private JToggleButton _btnOrder;
   private JToggleButton _btnWhere;
   private TrippleStateCheckBox _chkHideNoJoins;
   private GraphDockHandle _sqlDockHandle;
   private GraphDockHandle _resultDockHandle;
   private GraphDockHandle _orderDockHandle;
   private GraphDockHandle _whereDockHandle;
   private TableFramesModel _tableFramesModel;
   private GraphControllerFacade _graphControllerFacade;
   private ISession _session;
   private GraphQuerySQLPanelCtrl _graphQuerySQLPanelCtrl;
   private GraphQueryResultPanelCtrl _graphQueryResultPanelCtrl;
   private GraphQueryOrderPanelCtrl _graphQueryOrderPanelCtrl;
   private GraphQueryWherePanelCtrl _graphQueryWherePanelCtrl;
   private SessionAdapter _sessionAdapter;
   private GraphDockHandleAdmin _graphDockHandleAdmin;

   public QueryBuilderController(TableFramesModel tableFramesModel, GraphControllerFacade graphControllerFacade, boolean queryHideNoJoins, WhereTreeNodeStructure whereTreeNodeStructure, ISession session, GraphPlugin plugin, StartButtonHandler startButtonHandler)
   {
      _tableFramesModel = tableFramesModel;
      _graphControllerFacade = graphControllerFacade;
      _session = session;
      _panel = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _panel.add(startButtonHandler.getButton(), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,0,5),0,0);
      _btnSQL = new JToggleButton(s_stringMgr.getString("QueryBuilderController.SQL"));
      _panel.add(_btnSQL, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _btnResult = new JToggleButton(s_stringMgr.getString("QueryBuilderController.Result"));
      _panel.add(_btnResult, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _btnOrder = new JToggleButton(s_stringMgr.getString("QueryBuilderController.Order"));
      _panel.add(_btnOrder, gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _btnWhere = new JToggleButton(s_stringMgr.getString("QueryBuilderController.Where"));
      _panel.add(_btnWhere, gbc);

      gbc = new GridBagConstraints(5,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _chkHideNoJoins = new TrippleStateCheckBox(s_stringMgr.getString("QueryBuilderController.HideNoJoins"));
      _panel.add(_chkHideNoJoins, gbc);
      _chkHideNoJoins.setSelected(queryHideNoJoins);

      gbc = new GridBagConstraints(6,0,1,1,1,1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _panel.add(new JPanel(), gbc);

      GraphPluginResources rsrc = new GraphPluginResources(plugin);
      _graphQuerySQLPanelCtrl = new GraphQuerySQLPanelCtrl(_session, new HideDockButtonHandler(_btnSQL, rsrc), createSQLSyncListener());
      _graphQueryResultPanelCtrl = new GraphQueryResultPanelCtrl(_session, new HideDockButtonHandler(_btnResult, rsrc), createResultSyncListener());
      _graphQueryOrderPanelCtrl = new GraphQueryOrderPanelCtrl();
      _graphQueryWherePanelCtrl = new GraphQueryWherePanelCtrl(_session, new HideDockButtonHandler(_btnWhere, rsrc), rsrc, whereTreeNodeStructure);

      initHandels();

      _sessionAdapter = new SessionAdapter()
      {
         @Override
         public void sessionClosing(SessionEvent evt)
         {
            onSessionClosing();
         }
      };

      _session.getApplication().getSessionManager().addSessionListener(_sessionAdapter);

      _tableFramesModel.addTableFramesModelListener(new TableFramesModelListener()
      {
         @Override
         public void modelChanged(TableFramesModelChangeType changeType)
         {
            onModelChanged(changeType);
         }
      });

      _chkHideNoJoins.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onNoJoin();
         }
      });
   }



   private void onNoJoin()
   {
      _tableFramesModel.hideNoJoins(_chkHideNoJoins.isSelected());
      _graphControllerFacade.repaint();
   }

   private SyncListener createResultSyncListener()
   {
      return new SyncListener()
      {
         @Override
         public void synRequested()
         {
            WhereTreeNodeStructure wts = _graphQueryWherePanelCtrl.syncWhereCols(_tableFramesModel);
            _graphQueryResultPanelCtrl.execSQL(new QueryBuilderSQLGenerator(_session).generateSQL(_tableFramesModel, wts));
         }
      };
   }

   private SyncListener createSQLSyncListener()
   {
      return new SyncListener()
      {
         @Override
         public void synRequested()
         {
            WhereTreeNodeStructure wts = _graphQueryWherePanelCtrl.syncWhereCols(_tableFramesModel);
            _graphQuerySQLPanelCtrl.setSQL(new QueryBuilderSQLGenerator(_session).generateSQL(_tableFramesModel, wts));
         }
      };

   }


   private void onModelChanged(TableFramesModelChangeType changeType)
   {
      if (_sqlDockHandle.isShowing() && _graphQuerySQLPanelCtrl.isAutoSync())
      {
         WhereTreeNodeStructure wts = _graphQueryWherePanelCtrl.syncWhereCols(_tableFramesModel);
         _graphQuerySQLPanelCtrl.setSQL(new QueryBuilderSQLGenerator(_session).generateSQL(_tableFramesModel, wts));
      }
      else if(_resultDockHandle.isShowing()  && _graphQueryResultPanelCtrl.isAutoSync())
      {
         WhereTreeNodeStructure wts = _graphQueryWherePanelCtrl.syncWhereCols(_tableFramesModel);
         _graphQueryResultPanelCtrl.execSQL(new QueryBuilderSQLGenerator(_session).generateSQL(_tableFramesModel, wts));
      }
      else if(_whereDockHandle.isShowing())
      {
         _graphQueryWherePanelCtrl.syncWhereCols(_tableFramesModel);
      }

      if(null != changeType && changeType == TableFramesModelChangeType.CONSTRAINT && _chkHideNoJoins.isSelected() && _tableFramesModel.containsUniddenNoJoins())
      {
         _chkHideNoJoins.setUndefined(true);
      }

      if(null != changeType && changeType == TableFramesModelChangeType.TABLE && false == _chkHideNoJoins.isUndefined())
      {
         onNoJoin();
      }
   }

   private void initHandels()
   {
      _graphDockHandleAdmin = new GraphDockHandleAdmin(new GraphDockHandleAdminListerner()
      {
         @Override
         public void newDockOpened()
         {
            onModelChanged(null);
         }
      });

      int sqlHeight = Preferences.userRoot().getInt(PREF_KEY_SQL_DOCK_HEIGHT, 250);
      _sqlDockHandle = new GraphDockHandle(_graphControllerFacade, _graphQuerySQLPanelCtrl.getGraphQuerySQLPanel(), sqlHeight);
      _graphDockHandleAdmin.add(_sqlDockHandle, _btnSQL);

      int resHeight = Preferences.userRoot().getInt(PREF_KEY_RESULT_DOCK_HEIGHT, 250);
      _resultDockHandle = new GraphDockHandle(_graphControllerFacade, _graphQueryResultPanelCtrl.getGraphQueryResultPanel(), resHeight);
      _graphDockHandleAdmin.add(_resultDockHandle, _btnResult);

      int orderHeight = Preferences.userRoot().getInt(PREF_KEY_ORDER_DOCK_HEIGHT, 250);
      _orderDockHandle = new GraphDockHandle(_graphControllerFacade, _graphQueryOrderPanelCtrl.getGraphQueryOrderPanel(), orderHeight);
      _graphDockHandleAdmin.add(_orderDockHandle, _btnOrder);

      int whereHeight = Preferences.userRoot().getInt(PREF_KEY_WHERE_DOCK_HEIGHT, 250);
      _whereDockHandle = new GraphDockHandle(_graphControllerFacade, _graphQueryWherePanelCtrl.getGraphQueryWherePanel(), whereHeight);
      _graphDockHandleAdmin.add(_whereDockHandle, _btnWhere);
   }

   private void onSessionClosing()
   {
      Preferences.userRoot().putInt(PREF_KEY_SQL_DOCK_HEIGHT, _sqlDockHandle.getLastHeigth());
      Preferences.userRoot().putInt(PREF_KEY_RESULT_DOCK_HEIGHT, _resultDockHandle.getLastHeigth());

      // To prevent memory leaks
      _session.getApplication().getSessionManager().removeSessionListener(_sessionAdapter);

   }


   public JPanel getBottomPanel()
   {
      return _panel;
   }

   public void activate(boolean b)
   {
      if(false == b)
      {
         _graphDockHandleAdmin.deselectAllButtons();
      }
   }

   public boolean isHideNoJoins()
   {
      return _chkHideNoJoins.isSelected();
   }

   public WhereTreeNodeStructure getWhereTreeNodeStructure()
   {
      return _graphQueryWherePanelCtrl.getWhereTreeNodeStructure();
   }
}
