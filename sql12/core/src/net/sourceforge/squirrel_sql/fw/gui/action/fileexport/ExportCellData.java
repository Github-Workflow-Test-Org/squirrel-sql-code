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
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * @author Stefan Willinger
 */
public class ExportCellData
{
   private ColumnDisplayDefinition columnDisplayDefinition;
   private Object object;
   private int rowIndex;
   private int columnIndex;

   public ExportCellData(ColumnDisplayDefinition columnDisplayDefinition, Object object, int rowIndex, int columnIndex)
   {
      setColumnDisplayDefinition(columnDisplayDefinition);
      setObject(object);
      setRowIndex(rowIndex);
      setColumnIndex(columnIndex);
   }

   /**
    * @return the columnDisplayDefinition
    * @see ExportCellData#getColumnDisplayDefinition()
    */
   public ColumnDisplayDefinition getColumnDisplayDefinition()
   {
      return columnDisplayDefinition;
   }

   /**
    * @param columnDisplayDefinition the columnDisplayDefinition to set
    */
   public void setColumnDisplayDefinition(ColumnDisplayDefinition columnDisplayDefinition)
   {
      this.columnDisplayDefinition = columnDisplayDefinition;
   }

   /**
    * @return the object
    * @see ExportCellData#getObject()
    */
   public Object getObject()
   {
      return object;
   }

   /**
    * @param object the object to set
    */
   public void setObject(Object object)
   {
      this.object = object;
   }

   /**
    * @return the rowIndex
    * @see ExportCellData#getRowIndex()
    */
   public int getRowIndex()
   {
      return rowIndex;
   }

   /**
    * @param rowIndex the rowIndex to set
    */
   public void setRowIndex(int rowIndex)
   {
      this.rowIndex = rowIndex;
   }

   /**
    * @return the columnIndex
    * @see ExportCellData#getColumnIndex()
    */
   public int getColumnIndex()
   {
      return columnIndex;
   }

   /**
    * @param columnIndex the columnIndex to set
    */
   public void setColumnIndex(int columnIndex)
   {
      this.columnIndex = columnIndex;
   }


}
