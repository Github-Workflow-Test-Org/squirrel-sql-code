package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeDndTransfer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.util.TooManyListenersException;
import java.util.Vector;


public class GraphDesktopController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphDesktopController.class);


   private GraphDesktopPane _desktopPane;
   private ConstraintView _lastPressedConstraintView;

   private JPopupMenu _popUp;
   private JMenuItem _mnuSaveGraph;
   private JMenuItem _mnuRenameGraph;
   private JMenuItem _mnuRemoveGraph;
   private JMenuItem _mnuRefreshAllTables;
   private JMenuItem _mnuScriptAllTables;
   private JMenuItem _mnuSelectAllTables;
   private JMenuItem _mnuSelectTablesByName;
   private JCheckBoxMenuItem _mnuShowConstraintNames;
   private JCheckBoxMenuItem _mnuShowQualifiedTableNames;
   private JMenuItem _mnuToggleWindowTab;
   private GraphDesktopListener _listener;
   private ISession _session;
   private GraphPlugin _plugin;
   private ModeManager _modeManager;

   private JMenuItem _mnuAllTablesDbOrder;
   private JMenuItem _mnuAllTablesByNameOrder;
   private JMenuItem _mnuAllTablesPkConstOrder;
   private JMenuItem _mnuAllFilteredSelectedOrder;
   private GraphPluginResources _graphPluginResources;
   private GraphControllerPopupListener _currentGraphControllerPopupListener;


   public GraphDesktopController(GraphDesktopListener listener, ISession session, GraphPlugin plugin, ModeManager modeManager, boolean showDndDesktopImageAtStartup)
   {
      _listener = listener;
      _session = session;
      _plugin = plugin;
      _graphPluginResources = new GraphPluginResources(_plugin);

      ImageIcon startUpImage = null;

      if (showDndDesktopImageAtStartup)
      {
         startUpImage = _graphPluginResources.getIcon(GraphPluginResources.IKeys.DND);
      }

      _desktopPane = new GraphDesktopPane(_session.getApplication(), startUpImage);
      _desktopPane.setBackground(Color.white);

      _modeManager = modeManager;


      DropTarget dt = new DropTarget();

      try
      {
         dt.addDropTargetListener(new DropTargetAdapter()
         {
            public void drop(DropTargetDropEvent dtde)
            {
               onTablesDroped(dtde);
            }
         });
      }
      catch (TooManyListenersException e)
      {
         throw new RuntimeException(e);
      }

      _desktopPane.setDropTarget(dt);

      _desktopPane.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            onMouseClicked(e);
         }

         public void mousePressed(MouseEvent e)
         {
            onMousePressed(e);
         }

         public void mouseReleased(MouseEvent e)
         {
            onMouseReleased(e);
         }
      });

      _desktopPane.addMouseMotionListener(new MouseMotionAdapter()
      {
         public void mouseDragged(MouseEvent e)
         {
            onMouseDragged(e);
         }
      });

      createPopUp();

   }

   private void onTablesDroped(DropTargetDropEvent dtde)
   {
      try
      {
         Object transferData = dtde.getTransferable().getTransferData(dtde.getTransferable().getTransferDataFlavors()[0]);

         if(transferData instanceof ObjectTreeDndTransfer)
         {
            ObjectTreeDndTransfer objectTreeDndTransfer = (ObjectTreeDndTransfer) transferData;

            if(false == objectTreeDndTransfer.getSessionIdentifier().equals(_session.getIdentifier()))
            {
               JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(_desktopPane), s_stringMgr.getString("GraphDesktopController.tableDropedFormOtherSession"));
               return;
            }




            _listener.tablesDropped(objectTreeDndTransfer.getSelectedTables(), dtde.getLocation());
         }

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   EdgesListener createEdgesListener()
   {
      return new EdgesListener()
      {
         public void edgesGraphComponentChanged(EdgesGraphComponent edgesGraphComponent, boolean put)
         {
            onEdgesGraphComponentChanged(edgesGraphComponent, put);
         }
      };
   }

   private void onEdgesGraphComponentChanged(EdgesGraphComponent edgesGraphComponent, boolean put)
   {
      if(put)
      {
         _desktopPane.putGraphComponents(new GraphComponent[]{edgesGraphComponent});
      }
      else
      {
         _desktopPane.removeGraphComponents(new GraphComponent[]{edgesGraphComponent});
      }
      _desktopPane.repaint();
   }

   private void createPopUp()
   {
      _popUp = new JPopupMenu();

		// i18n[graph.saveGraph=Save graph]
		_mnuSaveGraph = new JMenuItem(s_stringMgr.getString("graph.saveGraph"));
      _mnuSaveGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onSaveGraph();
         }
      });


		// i18n[graph.renameGraph=Rename graph]
		_mnuRenameGraph= new JMenuItem(s_stringMgr.getString("graph.renameGraph"));
      _mnuRenameGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRenameGraph();
         }
      });

		// i18n[graph.removeGraph=Remove graph]
		_mnuRemoveGraph= new JMenuItem(s_stringMgr.getString("graph.removeGraph"));
      _mnuRemoveGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRemoveGraph();
         }
      });


		// i18n[graph.refreshAllTables=Refresh all tables]
		_mnuRefreshAllTables = new JMenuItem(s_stringMgr.getString("graph.refreshAllTables"));
      _mnuRefreshAllTables.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRefreshAllTables();
         }
      });

		// i18n[graph.scriptAllTables=Script all tables]
		_mnuScriptAllTables = new JMenuItem(s_stringMgr.getString("graph.scriptAllTables"));
      _mnuScriptAllTables.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onScriptAllTables();
         }
      });

      /////////////////////////////////////////////////////////
      // Tablegroups
		// i18n[graph.scriptAllTables=Script all tables]
		_mnuSelectAllTables = new JMenuItem(s_stringMgr.getString("graph.selectAllTables"));
		_mnuSelectAllTables.addActionListener(new ActionListener()
	    {
	       public void actionPerformed(ActionEvent e)
	       {
	          onSelectAllTables();
	       }
	    });

      // i18n[graph.scriptAllTables=Script all tables]
      _mnuSelectTablesByName = new JMenuItem(s_stringMgr.getString("graph.selectTablesByName"));
      _mnuSelectTablesByName.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onSelectTablesByName();
         }
      });
      /////////////////////////////////////////////////////////

		// i18n[graph.showConstr=Show constraint names]
		_mnuShowConstraintNames = new JCheckBoxMenuItem(s_stringMgr.getString("graph.showConstr"));
      _mnuShowConstraintNames.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            _desktopPane.repaint();
         }
      });

		// i18n[graph.showQualifiedTableNames=Show qualified table names]
		_mnuShowQualifiedTableNames = new JCheckBoxMenuItem(s_stringMgr.getString("graph.showQualifiedTableNames"));
      _mnuShowQualifiedTableNames.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onShowQualifiedTableNames();
         }
      });

      ImageIcon toWInIcon = _graphPluginResources.getIcon(GraphPluginResources.IKeys.TO_WINDOW);
      _mnuToggleWindowTab = new JMenuItem(s_stringMgr.getString("graph.toggleWindowTab"), toWInIcon);
      _mnuToggleWindowTab.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onToggleWindowTab();
         }
      });


		_mnuAllTablesDbOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesDbOrderRequested"));
      _mnuAllTablesDbOrder.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAllTablesDbOrder();
         }
      });

		_mnuAllTablesByNameOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesByNameOrderRequested"));
      _mnuAllTablesByNameOrder.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAllTablesByNameOrder();
         }
      });

		_mnuAllTablesPkConstOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesPkConstOrderRequested"));
      _mnuAllTablesPkConstOrder.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAllTablesPkConstOrder();
         }
      });

		_mnuAllFilteredSelectedOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesFilteredSelectedOrderRequested"));
      _mnuAllFilteredSelectedOrder.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAllTablesFilteredSelectedOrder();
         }
      });

      _popUp.add(_mnuSaveGraph);
      _popUp.add(_mnuRenameGraph);
      _popUp.add(_mnuRemoveGraph);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuRefreshAllTables);
      _popUp.add(_mnuScriptAllTables);
      _popUp.add(new JSeparator());
      /////////////////////////////////////////////////////////
      // Tablegroups
      _popUp.add(_mnuSelectAllTables);
      _popUp.add(_mnuSelectTablesByName);
      _popUp.add(new JSeparator());
      /////////////////////////////////////////////////////////
      _popUp.add(_mnuAllTablesDbOrder);
      _popUp.add(_mnuAllTablesByNameOrder);
      _popUp.add(_mnuAllTablesPkConstOrder);
      _popUp.add(_mnuAllFilteredSelectedOrder);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuShowConstraintNames);
      _popUp.add(_mnuShowQualifiedTableNames);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuToggleWindowTab);
      _popUp.add(new JSeparator());
      _popUp.add(_modeManager.getModeMenuItem());

      _modeManager.addModeManagerListener(new ModeManagerListener()
      {
         @Override
         public void modeChanged(Mode newMode)
         {
            _popUp.setVisible(false);
         }
      });

      _popUp.addPopupMenuListener(new PopupMenuListener()
      {
         @Override
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
         {
            onPopupMenuWillBecomeInvisible();
         }

         @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
         @Override public void popupMenuCanceled(PopupMenuEvent e) {}
      });

   }

   private void onPopupMenuWillBecomeInvisible()
   {
      if(null != _currentGraphControllerPopupListener)
      {
         _currentGraphControllerPopupListener.hiding();
         _currentGraphControllerPopupListener = null;
      }
   }

   private void onShowQualifiedTableNames()
   {
      _listener.showQualifiedTableNamesRequested();
   }

   private void onAllTablesPkConstOrder()
   {
      _listener.allTablesPkConstOrderRequested();
   }

   private void onAllTablesByNameOrder()
   {
      _listener.allTablesByNameOrderRequested();
   }

   private void onAllTablesDbOrder()
   {
      _listener.allTablesDbOrderRequested();
   }

   private void onAllTablesFilteredSelectedOrder()
   {
      _listener.allTablesFilteredSelectedOrderRequested();
   }

   private void onToggleWindowTab()
   {
      _listener.toggleWindowTab();
   }



   private void onScriptAllTables()
   {
      _listener.scriptAllTablesRequested();
   }

   /////////////////////////////////////////////////////////
   // Tablegroups
   private void onSelectAllTables()
   {
      for(JInternalFrame f:_desktopPane.getAllFrames()) {
    	  if(f instanceof TableFrame) {
    		  _desktopPane.addGroupFrame((TableFrame)f);
    	  }
      }
   }

   private void onSelectTablesByName()
   {
      String namePattern=JOptionPane.showInputDialog(_desktopPane, s_stringMgr.getString("graph.selectTablesByName.message"), s_stringMgr.getString("graph.selectTablesByName.title"), JOptionPane.QUESTION_MESSAGE);

      if(null == namePattern || 0 == namePattern.trim().length())
      {
         return;
      }

      _desktopPane.clearGroupFrames();
      for (JInternalFrame f : _desktopPane.getAllFrames())
      {
         if (f instanceof TableFrame)
         {
            TableFrame tf = (TableFrame) f;
            if (tf.getTitle().matches(namePattern.replace('?', '.').replace("*", ".*")))
            {
               _desktopPane.addGroupFrame(tf);
            }
         }
      }
   }
   /////////////////////////////////////////////////////////

   private void onRefreshAllTables()
   {
      _listener.refreshAllTablesRequested();
   }

   private void onRemoveGraph()
   {
		// i18n[graph.delGraph=Do you really wish to delete this graph?]
      Window parent = SwingUtilities.windowForComponent(_desktopPane);
      int res = JOptionPane.showConfirmDialog(parent, s_stringMgr.getString("graph.delGraph"));
      if(res == JOptionPane.YES_OPTION)
      {
         _listener.removeRequest();
      }
   }

   private void onRenameGraph()
   {

		// i18n[graph.newName=Please enter a new name]
      Window parent = SwingUtilities.windowForComponent(_desktopPane);
		String newName = JOptionPane.showInputDialog(parent, s_stringMgr.getString("graph.newName"));
      if(null != newName && 0 != newName.trim().length())
      {
         _listener.renameRequest(newName);
      }
   }

   private void onSaveGraph()
   {
      _listener.saveGraphRequested();
   }

   private void maybeShowPopup(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {
         _mnuAllFilteredSelectedOrder.setEnabled(_modeManager.getMode().isQueryBuilder());

         _popUp.show(e.getComponent(), e.getX(), e.getY());
      }
   }



   /**
    * It's called put because it adds unique, like a Hashtable.
    */
   public void putConstraintViews(ConstraintView[] constraintViews)
   {
      _desktopPane.putGraphComponents(constraintViews);
   }

   public void removeConstraintViews(ConstraintView[] constraintViews, boolean keepFoldingPoints)
   {
      _desktopPane.removeGraphComponents(constraintViews);

      if(false == keepFoldingPoints)
      {
         for (int i = 0; i < constraintViews.length; i++)
         {
            constraintViews[i].removeAllFoldingPoints();
         }
      }
   }


   private void refreshSelection(ConstraintView hitOne, boolean allowDeselect)
   {
      if(allowDeselect)
      {
         hitOne.setSelected(!hitOne.isSelected());
      }
      else if(false == hitOne.isSelected())
      {
         hitOne.setSelected(true);
      }

      Vector<GraphComponent> graphComponents = _desktopPane.getGraphComponents();

      for (int i = 0; i < graphComponents.size(); i++)
      {
         if(false == graphComponents.elementAt(i) instanceof ConstraintView)
         {
            continue;
         }

         ConstraintView constraintView = (ConstraintView) graphComponents.elementAt(i);
         if(false == constraintView.equals(hitOne))
         {
            constraintView.setSelected(false);
         }
      }
   }

   public void onMouseReleased(final MouseEvent e)
   {
      _lastPressedConstraintView = null;

      ConstraintHitData hitData = findHit(e);
      if(ConstraintHit.LINE == hitData.getConstraintHit())
      {
         hitData.getConstraintView().mouseReleased(e);
      }
      else if(ConstraintHit.NONE == hitData.getConstraintHit())
      {
         maybeShowPopup(e);
      }
   }

   public void onMousePressed(final MouseEvent e)
   {
      final ConstraintHitData hitData = findHit(e);
      if(ConstraintHit.LINE == hitData.getConstraintHit())
      {
         _lastPressedConstraintView = hitData.getConstraintView();

         if(InputEvent.BUTTON3_MASK == e.getModifiers())
         {
            refreshSelection(hitData.getConstraintView(), false);
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  hitData.getConstraintView().mousePressed(e);
               }
            });
         }
         else
         {
            hitData.getConstraintView().mousePressed(e);
         }
      }
      else if(ConstraintHit.NONE == hitData.getConstraintHit())
      {
         maybeShowPopup(e);
      }
   }

   public void onMouseClicked(final MouseEvent e)
   {
      final ConstraintHitData hitData = findHit(e);

      if(ConstraintHit.LINE == hitData.getConstraintHit())
      {
         refreshSelection(hitData.getConstraintView(), InputEvent.BUTTON1_MASK == e.getModifiers() );
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               hitData.getConstraintView().mouseClicked(e);
            }
         });
      }
   }

   private void onMouseDragged(MouseEvent e)
   {
      if(null != _lastPressedConstraintView)
      {
         _lastPressedConstraintView.mouseDragged(e);
      }
   }

   private ConstraintHitData findHit(MouseEvent e)
   {
      Vector<GraphComponent> graphComponents = _desktopPane.getGraphComponents();


      for (int i = 0; i < graphComponents.size(); i++)
      {
         GraphComponent graphComponent = graphComponents.elementAt(i);

         if(graphComponent instanceof ConstraintView)
         {
            ConstraintView constraintView = (ConstraintView)graphComponents.elementAt(i);
            ConstraintHit constraintHit = constraintView.hitMe(e);
            if(ConstraintHit.NONE != constraintHit)
            {
               return new ConstraintHitData(constraintView, constraintHit);
            }
         }
      }
      return new ConstraintHitData(null, ConstraintHit.NONE);
   }


   public void repaint()
   {
      _desktopPane.repaint();
   }

   public void addFrame(JInternalFrame frame)
   {
      _desktopPane.hideStartupImage();
      _desktopPane.add(frame);
   }

   public GraphDesktopPane getDesktopPane()
   {
      return _desktopPane;
   }

   public boolean isShowConstraintNames()
   {
      return _mnuShowConstraintNames.isSelected();
   }

   public void setShowConstraintNames(boolean showConstraintNames)
   {
      _mnuShowConstraintNames.setSelected(showConstraintNames);
   }

   public Zoomer getZoomer()
   {
      return _modeManager.getZoomer();
   }

   public ZoomPrintController getZoomPrintController()
   {
      return _modeManager.getZoomPrintController();
   }

   public void sessionEnding()
   {
      _modeManager.sessionEnding();
   }

   public void setShowQualifiedTableNames(boolean showQualifiedTableNames)
   {
      _mnuShowQualifiedTableNames.setSelected(showQualifiedTableNames);
   }


   public boolean isShowQualifiedTableNames()
   {
      return _mnuShowQualifiedTableNames.isSelected();
   }

   public ModeManager getModeManager()
   {
      return _modeManager;
   }

   public GraphPluginResources getResource()
   {
      return _graphPluginResources;
   }

   public void removeGraph()
   {
      onRemoveGraph();
   }

   public void showPopupAbove(Point loc, GraphControllerPopupListener graphControllerPopupListener)
   {
      if (_popUp.isVisible())
      {
         _popUp.setVisible(false);
      }
      else
      {
         _popUp.show(_desktopPane,0,0);
         _popUp.setLocation(loc.x, loc.y - _popUp.getHeight());
      }
      _currentGraphControllerPopupListener = graphControllerPopupListener;
   }

   public void hidePopup()
   {
      _popUp.setVisible(false);
   }

   public TableFramesModel getTableFramesModel()
   {
      return _modeManager.getTableFramesModel();
   }
}
