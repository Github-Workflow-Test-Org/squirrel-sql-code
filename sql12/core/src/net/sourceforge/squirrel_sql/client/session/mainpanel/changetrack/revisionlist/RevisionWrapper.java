package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class RevisionWrapper
{
   private String _revisionDateString;
   private String _branchesListString;
   private String _committerName;
   private String _revisionId;
   private String _commitMsgBegin;

   public RevisionWrapper(RevCommit revCommit, Git git)
   {
      try
      {
         _revisionDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(revCommit.getAuthorIdent().getWhen());
         _branchesListString = String.join("; ", git.branchList().setContains(revCommit.getId().getName()).call().stream().map(b -> b.getName()).collect(Collectors.toList()));
         _committerName = revCommit.getAuthorIdent().getName()  + "; Mail: " + revCommit.getAuthorIdent().getEmailAddress();
         _revisionId = revCommit.getId().getName();
         _commitMsgBegin = firstTwoLines(revCommit.getFullMessage());
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }

   }

   private String firstTwoLines(String fullMessage)
   {
      String ret = "";

      String[] splits = fullMessage.split("\n");
      for (int i = 0; i < splits.length; i++)
      {
         if (i == 0)
         {
            ret = splits[i];
         }
         else
         {
            ret = "\n" + splits[i];
         }

         if(i >= 1)
         {
            return ret;
         }
      }

      return ret;
   }

   public String getRevisionDateString()
   {
      return _revisionDateString;
   }

   public String getBranchesListString()
   {
      return _branchesListString;
   }

   public String getCommitterName()
   {
      return _committerName;
   }

   public String getRevisionId()
   {
      return _revisionId;
   }

   public String getCommitMsgBegin()
   {
      return _commitMsgBegin;
   }



}
