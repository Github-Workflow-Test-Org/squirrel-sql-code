package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.EntryPanelManager;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects.ObjectResultController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;

public class HqlResultPanelManager extends EntryPanelManager
{
   private static final String PREF_KEY_VIEW_LIMIT_OBJECT_COUNT = "SquirrelSQL.hibernate.limitObjectsCount";
   private static final String PREF_KEY_VIEW_LIMIT_OBJECT_COUNT_VAL = "SquirrelSQL.hibernate.limitObjectsCountVal";
   private static final String PREF_KEY_USE_CONNECTION_OF = "SquirrelSQL.hibernate.useConnectionOf";

   private HqlResultPanel _hqlResultPanel;
   private ObjectResultController _objectResultController;


   public HqlResultPanelManager(final ISession session, HibernatePluginResources resource)
   {
      super(session);
      init(null, null);

      _objectResultController = new ObjectResultController(session, resource);
      _hqlResultPanel = new HqlResultPanel(_objectResultController.getPanel(), resource);


      session.getApplication().getSessionManager().addSessionListener(
         new SessionAdapter()
         {

            public void sessionClosing(SessionEvent evt)
            {
               onSessionClosing();
               session.getApplication().getSessionManager().removeSessionListener(this);
            }
         }
      );

      _hqlResultPanel.chkLimitObjectCount.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT, false));
      _hqlResultPanel.nbrLimitRows.setInt(Preferences.userRoot().getInt(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT_VAL, 100));
      _hqlResultPanel.nbrLimitRows.setEnabled(_hqlResultPanel.chkLimitObjectCount.isSelected());


      int ordinalToSelect = Preferences.userRoot().getInt(PREF_KEY_USE_CONNECTION_OF, HqlResultPanel.UseConnectionOf.OF_HIBBERNAT_CONFIG.ordinal());
      _hqlResultPanel.cboUseConnectionOf.setSelectedItem(HqlResultPanel.UseConnectionOf.getByOrdinal(ordinalToSelect));


      _hqlResultPanel.chkLimitObjectCount.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onLimitRowsChanged();
         }
      });

      _hqlResultPanel.cboUseConnectionOf.addItemListener(new ItemListener()
      {
         @Override
         public void itemStateChanged(ItemEvent e)
         {
            onUseConnectionOfChanged(e);
         }
      });
   }

   private void onUseConnectionOfChanged(ItemEvent e)
   {
      if(ItemEvent.DESELECTED == e.getStateChange())
      {
         return;
      }

      HqlResultPanel.UseConnectionOf seleted = (HqlResultPanel.UseConnectionOf) _hqlResultPanel.cboUseConnectionOf.getSelectedItem();
      Preferences.userRoot().putInt(PREF_KEY_USE_CONNECTION_OF, seleted.ordinal());
   }

   private void onLimitRowsChanged()
   {
      if(_hqlResultPanel.chkLimitObjectCount.isSelected())
      {
         LimitObjectCountDialog locc = new LimitObjectCountDialog(getSession().getApplication().getMainFrame());
         _hqlResultPanel.chkLimitObjectCount.setSelected(locc.check());

         if(locc.checkAndRemember())
         {
            Preferences.userRoot().putBoolean(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT, true);
         }
      }
      else
      {
         Preferences.userRoot().putBoolean(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT, false);
      }

     _hqlResultPanel.nbrLimitRows.setEnabled(_hqlResultPanel.chkLimitObjectCount.isSelected());
   }


   private void onSessionClosing()
   {
      // Omitted intentionally
      //Preferences.userRoot().putBoolean(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT, _hibernateSQLPanel.chkLimitObjectCount.isSelected());

      Preferences.userRoot().putInt(PREF_KEY_VIEW_LIMIT_OBJECT_COUNT_VAL, _hqlResultPanel.nbrLimitRows.getInt());
   }


   public JComponent getComponent()
   {
      return _hqlResultPanel;
   }

   public void displayObjects(HibernateConnection con, String hqlQuery)
   {
      boolean limitObjectCount = _hqlResultPanel.chkLimitObjectCount.isSelected();
      int limitObjectCountVal = _hqlResultPanel.nbrLimitRows.getInt();

      boolean useSessionConnection =
            _hqlResultPanel.cboUseConnectionOf.getSelectedItem() == HqlResultPanel.UseConnectionOf.OF_SESSION;


      _objectResultController.displayObjects(con, hqlQuery, limitObjectCount, limitObjectCountVal, useSessionConnection);
   }
}
