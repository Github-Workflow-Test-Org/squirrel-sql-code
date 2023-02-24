package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.client.preferences.NewSessionPropertiesSheet;

import java.util.List;
import java.util.TreeMap;

public class PrefsFindInfo
{
   private SessionPropertiesDialogFindInfo _sessionPropertiesDialogFindInfo;
   private GlobalPreferencesDialogFindInfo _globalPreferencesDialogFindInfo;
   private TreeMap<List<String>, List<PrefComponentInfo>> _componentInfoByPath;

   /**
    * E.g the shortcut table changes when the preference window is opened.
    * That is why we refresh the FindInfo when {@link #openDialogAndShowComponentAtPath(List)} is called.
    */
   private PrefsFindInfo _prefsFindInfoUpdate;

   public PrefsFindInfo(GlobalPreferencesDialogFindInfo globalPreferencesDialogFindInfo,
                        SessionPropertiesDialogFindInfo sessionPropertiesDialogFindInfo,
                        TreeMap<List<String>, List<PrefComponentInfo>> componentInfoByPath)
   {
      _globalPreferencesDialogFindInfo = globalPreferencesDialogFindInfo;
      _sessionPropertiesDialogFindInfo = sessionPropertiesDialogFindInfo;
      _componentInfoByPath = componentInfoByPath;
   }

   public TreeMap<List<String>, List<PrefComponentInfo>> getComponentInfoByPath()
   {
      return _componentInfoByPath;
   }

   /**
    *
    * @param The first path entry (path.subList(0,1)) is the dialog. The second if present is the tab components path.
    * @return The tab component If the path contained a tab component else null.
    */
   public PrefComponentInfo openDialogAndShowComponentAtPath(List<String> path)
   {
      final DialogFindInfo dialogFindInfo = getPrefComponentInfoByPath(path.subList(0,1)).getDialogFindInfo();

      PrefComponentInfo showingPrarentsComponentInfo = null;

      if(dialogFindInfo instanceof GlobalPreferencesDialogFindInfo )
      {
         GlobalPreferencesSheet.showSheet(null);

         // After the GlobalPreferencesSheet we create an FindInfo-update
         _prefsFindInfoUpdate = ComponentInfoByPathUtil.createPrefsFindInfo();

         if(1 < path.size())
         {
            showingPrarentsComponentInfo = getPrefComponentInfoByPath(path.subList(0, 2));
            showingPrarentsComponentInfo = _prefsFindInfoUpdate.getPrefComponentInfoByPath(showingPrarentsComponentInfo.getPath());
            _prefsFindInfoUpdate._globalPreferencesDialogFindInfo.selectTabOfPathComponent(showingPrarentsComponentInfo.getComponent());
         }
      }
      else if(dialogFindInfo instanceof SessionPropertiesDialogFindInfo)
      {
         NewSessionPropertiesSheet.showSheet();
         _prefsFindInfoUpdate = ComponentInfoByPathUtil.createPrefsFindInfo();

         if(1 < path.size())
         {
            showingPrarentsComponentInfo = getPrefComponentInfoByPath(path.subList(0,2));
            showingPrarentsComponentInfo = _prefsFindInfoUpdate.getPrefComponentInfoByPath(showingPrarentsComponentInfo.getPath());
            _prefsFindInfoUpdate._sessionPropertiesDialogFindInfo.selectTabOfPathComponent(showingPrarentsComponentInfo.getComponent());
         }
      }
      else
      {
         throw new IllegalStateException("Unknown DialogFindInfo class: " + dialogFindInfo.getClass().getName());
      }

      return showingPrarentsComponentInfo;
   }

   public PrefsFindInfo getPrefsFindInfoUpdate()
   {
      if(null != _prefsFindInfoUpdate)
      {
         return _prefsFindInfoUpdate;
      }

      return this;
   }

   public PrefComponentInfo getComponentInfoByPath(List<String> path)
   {
      PrefComponentInfo prefComponentInfo = getPrefComponentInfoByPath(path);
      return prefComponentInfo;
   }

   public PrefComponentInfo getPrefComponentInfoByPath(List<String> path)
   {
      final List<PrefComponentInfo> componentInfoList = getPrefComponentInfoListByPath(path);
      if(componentInfoList == null)
      {
         return null;
      }

      PrefComponentInfo prefComponentInfo = componentInfoList.get(0);
      return prefComponentInfo;
   }

   public List<PrefComponentInfo> getPrefComponentInfoListByPath(List<String> path)
   {
      return _componentInfoByPath.get(path);
   }

}
