package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

public class AliasFolderState implements Comparable<AliasFolderState>, Serializable
{
   private AliasFolderState[] _kids = new AliasFolderState[0];
   private IIdentifier _aliasIdentifier;
   private String _folderName;
   private int _colorRGB = AliasFolder.NO_COLOR_RGB;
   private boolean _isSelected;
   private boolean _isExpanded;

   /**
    * DO NOT introduce setter/getter for this member these members should
    * not be treated by XMLBeanReader or XMLBeanWriter.
    */
   private DefaultMutableTreeNode _node;
   private String _aliasName;

   /**
    * To be used by XmlBeanReader only
    */
   public AliasFolderState()
   {
   }

   public AliasFolderState(DefaultMutableTreeNode dmtn, JTree tree)
   {
      TreePath selPath = tree.getSelectionPath();

      if(null != selPath && selPath.getLastPathComponent() == dmtn)
      {
         _isSelected = true;
      }

      _isExpanded = tree.isExpanded(new TreePath(dmtn.getPath()));


      if(dmtn.getUserObject() instanceof SQLAlias)
      {
         SQLAlias alias = (SQLAlias) dmtn.getUserObject();
         _aliasIdentifier = alias.getIdentifier();
         _aliasName = alias.getName();
      }
      else if(dmtn.getUserObject() instanceof AliasFolder)
      {
         _folderName = ((AliasFolder)dmtn.getUserObject()).getFolderName();

         _colorRGB = ((AliasFolder)dmtn.getUserObject()).getColorRGB();

         _kids = new AliasFolderState[dmtn.getChildCount()];
         for (int i = 0; i < dmtn.getChildCount(); i++)
         {
            AliasFolderState state = new AliasFolderState((DefaultMutableTreeNode) dmtn.getChildAt(i), tree);
            _kids[i] = state;
         }
      }
      else
      {
         AliasTreeUtil.throwUnknownUserObjectException(dmtn);
      }
   }

   public AliasFolderState[] getKids()
   {
      return _kids;
   }

   public void setKids(AliasFolderState[] kids)
   {
      _kids = kids;
   }

   public IIdentifier getAliasIdentifier()
   {
      return _aliasIdentifier;
   }

   public void setAliasIdentifier(IIdentifier aliasIdentifier)
   {
      _aliasIdentifier = aliasIdentifier;
   }

   public String getFolderName()
   {
      return _folderName;
   }

   public void setFolderName(String folderName)
   {
      _folderName = folderName;
   }

   public boolean isSelected()
   {
      return _isSelected;
   }

   public void setSelected(boolean selected)
   {
      _isSelected = selected;
   }

   public boolean isExpanded()
   {
      return _isExpanded;
   }

   public void setExpanded(boolean expanded)
   {
      _isExpanded = expanded;
   }

   public int getColorRGB()
   {
      return _colorRGB;
   }

   public void setColorRGB(int colorRGB)
   {
      _colorRGB = colorRGB;
   }

   public void applyNodes(DefaultMutableTreeNode parent, AliasesListModel aliasesListModel)
   {
      if(null != _folderName)
      {
         _node = GUIUtils.createFolderNode(new AliasFolder(_folderName, _colorRGB));
         parent.add(_node);
         for (AliasFolderState kid : _kids)
         {
            kid.applyNodes(_node, aliasesListModel);
         }
      }
      else
      {
         SQLAlias sqlAlias = aliasesListModel.getAlias(_aliasIdentifier);
         if(null != sqlAlias)
         {
            _node = new DefaultMutableTreeNode(sqlAlias);
            parent.add(_node);
         }
      }
   }

   private void applyExpansionAndSelectionToNode(JTree tree, DefaultMutableTreeNode node)
   {
      if(_isExpanded)
      {
         tree.expandPath(new TreePath(node.getPath()));
      }

      if(_isSelected)
      {
         tree.setSelectionPath(new TreePath(node.getPath()));
      }
   }

   public void applyExpansionAndSelection(JTree tree)
   {
      if (_node != null)
      {
         // _node is null when there is an _aliasIdentifier with no Alias
         // This has been seen some times and caused NullPointersHere  

         if (null != _folderName)
         {
            applyExpansionAndSelectionToNode(tree, _node);
            for (AliasFolderState kid : _kids)
            {
               kid.applyExpansionAndSelection(tree);
            }
         }
         else
         {
            applyExpansionAndSelectionToNode(tree, _node);
         }
      }
   }

   public void sort(boolean asc)
   {
      if (asc)
      {
         Arrays.sort(_kids);
      }
      else
      {
         Arrays.sort(_kids, Collections.reverseOrder());
      }

      for (AliasFolderState kid : _kids)
      {
         kid.sort(asc);
      }
   }

   public int compareTo(AliasFolderState other)
   {
      if(null == _aliasIdentifier && null != other._aliasIdentifier)
      {
         return -1;
      }
      else if(null != _aliasIdentifier && null == other._aliasIdentifier)
      {
         return 1;
      }

      return getCompString().compareTo(other.getCompString());
   }

   private String getCompString()
   {
      return null != _folderName ? _folderName : _aliasName;
   }
}
