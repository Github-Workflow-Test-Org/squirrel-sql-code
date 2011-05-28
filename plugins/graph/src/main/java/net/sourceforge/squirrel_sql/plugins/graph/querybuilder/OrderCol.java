package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.plugins.graph.AggregateFunctions;
import net.sourceforge.squirrel_sql.plugins.graph.ColumnInfo;

public class OrderCol
{
   private String _qualifiedCol;
   private boolean _ascending;
   private boolean _aggregated;


   /**
    * Needed for XML deserialization.
    */
   public OrderCol()
   {
   }

   public OrderCol(String simpleTableName, ColumnInfo columnInfo)
   {
      _qualifiedCol = simpleTableName + "." + columnInfo.getColumnName();
      _ascending = columnInfo.getQueryData().isSortedAsc();
      _aggregated = (AggregateFunctions.NONE != columnInfo.getQueryData().getAggregateFunction());

   }

   public String getQualifiedCol()
   {
      return _qualifiedCol;
   }


   /**
    * Needed for XML deserialization.
    */
   public void setQualifiedCol(String qualifiedCol)
   {
      _qualifiedCol = qualifiedCol;
   }

   public boolean isAscending()
   {
      return _ascending;
   }

   public void setAscending(boolean b)
   {
      _ascending = b;
   }

   public boolean isAggregated()
   {
      return _aggregated;
   }

   public void setAggregated(boolean aggregated)
   {
      _aggregated = aggregated;
   }

   @Override
   public int hashCode()
   {
      return _qualifiedCol.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof OrderCol )
      {
         return false;
      }

      return _qualifiedCol.equals(((OrderCol)obj)._qualifiedCol);
   }
}
