package net.sourceforge.squirrel_sql.plugins.hibernate.util;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;

import java.util.ArrayList;

public class HibernateSQLUtil
{
   public static String createAllSqlsString(ArrayList<String> sqls, ISession session)
   {
      String allSqls = "";

      String sep = session.getQueryTokenizer().getSQLStatementSeparator();

      for (String sql : sqls)
      {
         allSqls += sql;

         if (1 < sep.length())
         {
            allSqls += "\n";
         }
         allSqls += (sep + "\n\n");
      }
      return format(allSqls, session).trim();
   }

   private static String format(String sqls, ISession session)
   {
      CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(session));

      sqls = cr.reformat(sqls) + "\n";
      return sqls;
   }
}
