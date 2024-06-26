package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.listholder.AliasListHolder;
import net.sourceforge.squirrel_sql.client.gui.db.listholder.DriverListHolder;
import net.sourceforge.squirrel_sql.client.gui.db.listholder.ListHolder;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoCacheSerializer;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AliasesAndDriversManager
{
   private final static StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasesAndDriversManager.class);

   private final static ILogger s_log = LoggerController.createLogger(AliasesAndDriversManager.class);

   /** Driver manager. */
   private final SQLDriverManager _driverMgr;


   private AliasListHolder _aliasListHolder = new AliasListHolder();

   private DriverListHolder _driverListHolder = new DriverListHolder();

   /**
    * Ctor. Loads drivers and aliases from the XML document.
    *
    * @param	driverMgr		Manages JDBC drivers.
    * @param	driversFile		<TT>File</TT> to load drivers from.
    * @param	aliasesFile		<TT>File</TT> to load aliases from.
    * @param	dftDriversURL	URL that the default rivers can be loaded from.
    * @param	msgHandler		Message handler to report on errors in this object.
    *
    * @throws	IllegalArgumentException
    *			Thrown if null <TT>SQLDriverManager</TT>, <TT>driversFile</TT>,
    *			<TT>aliasesFile</TT> or <TT>dftDriversURL</TT> passed.
    */
   public AliasesAndDriversManager(SQLDriverManager driverMgr, File driversFile, File aliasesFile, URL dftDriversURL)
   {
      if (driverMgr == null)
      {
         throw new IllegalArgumentException("SQLDriverManager == null");
      }
      if (driversFile == null)
      {
         throw new IllegalArgumentException("driversFile == null");
      }
      if (aliasesFile == null)
      {
         throw new IllegalArgumentException("aliasesFile == null");
      }
      if (dftDriversURL == null)
      {
         throw new IllegalArgumentException("dftDriversURL == null");
      }

      _driverMgr = driverMgr;

      loadDrivers(driversFile, dftDriversURL, NullMessageHandler.getInstance());
      loadAliases(aliasesFile, NullMessageHandler.getInstance());
   }

   /**
    * Save JDBC drivers to the passed file as XML.
    *
    * @param	file	File to save drivers to.
    *
    * @throws	IllegalArgumentException
    * 			Thrown if <TT>null</TT> <TT>File</TT> passed.
    * @throws	IOException
    * 			Thrown if an I/O error occurs saving.
    * @throws	XMLException
    * 			Thrown if an error occurs translating drivers to XML.
    */
   public void saveDrivers(File file) throws IOException, XMLException
   {
      if (file == null)
      {
         throw new IllegalArgumentException("File == null");
      }

        saveSecure(file, _driverListHolder);
   }

   /**
    * Save aliases to the passed file as XML.
    *
    * @param	file	File to save aliases to.
    *
    * @throws	IllegalArgumentException
    * 			Thrown if <TT>null</TT> <TT>File</TT> passed.
    * @throws	IOException
    * 			Thrown if an I/O error occurs saving.
    * @throws	XMLException
    * 			Thrown if an error occurs translating aliases to XML.
    */
   public void saveAliases(File file) throws IOException, XMLException
   {
      if (file == null)
      {
         throw new IllegalArgumentException("File == null");
      }
        saveSecure(file, _aliasListHolder);
    }

   private void saveSecure(File file, ListHolder listHolder)
   {
      File tempFile = new File(file.getPath() + "~");
      try
      {
         tempFile.delete();
      }
      catch (Exception e)
      {
      }


      listHolder.save(tempFile.getPath());
      if (false == tempFile.renameTo(file))
      {
         File doubleTemp = new File(file.getPath() + "~~");
         try
         {
            doubleTemp.delete();
         }
         catch (Exception e)
         {
         }
         File buf = new File(file.getPath());


         if (false == buf.renameTo(doubleTemp))
         {
            throw new IllegalStateException("Cannot rename file " + buf.getPath() + " to " + doubleTemp.getPath() + ". New File will not be saved.");
         }

         try
         {
            tempFile.renameTo(file);
            doubleTemp.delete();
         }
         catch (Exception e)
         {
            doubleTemp.renameTo(file);
         }
      }
   }

   /**
     * Retrieve the <TT>ISQLDriver</TT> for the passed identifier.
     *
     * @param	id	Identifier to retrieve driver for.
     *
     * @return	the <TT>ISQLDriver</TT> for the passed identifier.
     *
     * @throws	IllegalArgumentException
     * 			Thrown if <TT>null</TT> <TT>ISQLDriver</TT> passed.
     */
    public ISQLDriver getDriver(IIdentifier id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("ISQLDriver == null");
        }

        return _driverListHolder.get(id);
    }

   /**
    * Add a driver to the cache.
    *
    * @param	sqlDriver	The driver to add.
    *
    * @param messageHandler
    * @throws	IllegalArgumentException
    * 			Thrown if <TT>ISQLDriver</TT> is null.
    */
   public void addDriver(ISQLDriver sqlDriver, IMessageHandler messageHandler)
   {
      if (sqlDriver == null)
      {
         throw new IllegalArgumentException("ISQLDriver == null");
      }
      if (messageHandler != null)
      {
         registerDriver(sqlDriver, messageHandler, true);
      }
      _driverListHolder.add(sqlDriver);
   }

   public void removeDriver(ISQLDriver sqlDriver)
   {
      _driverListHolder.remove(sqlDriver.getIdentifier());
      _driverMgr.unregisterSQLDriver(sqlDriver);
   }

   public Iterator<SQLDriver> drivers()
   {
      return _driverListHolder.getAll().iterator();
   }

   public void addDriversListener(IObjectCacheChangeListener lis)
   {
      _driverListHolder.addChangesListener(lis);
   }

   public void removeDriversListener(IObjectCacheChangeListener lis)
   {
      _driverListHolder.removeChangesListener(lis);
   }

   public SQLAlias getAlias(IIdentifier id)
   {
      return _aliasListHolder.get(id);
   }

   public Iterator<? extends SQLAlias> aliases()
   {
      return _aliasListHolder.getAll().iterator();
   }

   public List<? extends SQLAlias> getAliasList()
   {
      return _aliasListHolder.getAll();
   }

   public List<SQLDriver> getDriverList()
   {
      return _driverListHolder.getAll();
   }


   public void addAlias(SQLAlias alias)
   {
      _aliasListHolder.add(alias);
   }

   public void removeAlias(SQLAlias alias)
   {
      SchemaInfoCacheSerializer.aliasRemoved(alias);
      Main.getApplication().getPluginManager().aliasRemoved(alias);
      _aliasListHolder.remove(alias.getIdentifier());
   }

   public Iterator<SQLAlias> getAliasesForDriver(ISQLDriver driver)
   {
      ArrayList<SQLAlias> data = new ArrayList<>();
      for (Iterator<? extends SQLAlias> it = aliases(); it.hasNext();)
      {
         SQLAlias alias = it.next();
         if (driver.equals(getDriver(alias.getDriverIdentifier())))
         {
            data.add(alias);
         }
      }
      return data.iterator();
   }

   public void addAliasesListener(IObjectCacheChangeListener lis)
   {
      _aliasListHolder.addChangesListener(lis);
   }

   public void removeAliasesListener(IObjectCacheChangeListener lis)
   {
      _aliasListHolder.removeChangesListener(lis);
   }

   /**
    * Load <TT>IISqlDriver</TT> objects from the XML file <TT>driversFile</TT>.
    * If file not found then load from the default drivers.
    *
    * @param	driversFile		<TT>File</TT> to load drivers from.
    * @param	dftDriversURL	<TT>URL</TT> to load default drivers from.
    * @param	msgHandler		Message handler to write any errors to.
    *
    *@throws	IllegalArgumentException
    *			Thrown if <TT>null</TT> <TT>driversFile</TT>,
    *			<TT>dftDriversURL</TT>, or <TT>msgHandler</TT> passed.
    */
   private void loadDrivers(File driversFile, URL dftDriversURL, IMessageHandler msgHandler)
   {
      if (driversFile == null)
      {
         throw new IllegalArgumentException("driversFile == null");
      }
      if (dftDriversURL == null)
      {
         throw new IllegalArgumentException("dftDriversURL == null");
      }
      if (msgHandler == null)
      {
         throw new IllegalArgumentException("msgHandler == null");
      }

      try
      {
         try
         {
            _driverListHolder.load(driversFile);
            if (!drivers().hasNext())
            {
               loadDefaultDrivers(dftDriversURL);
            }
            else
            {
               fixupDrivers();
               mergeDefaultWebsites(dftDriversURL);
            }
         }
         catch (FileNotFoundException ex)
         {
            loadDefaultDrivers(dftDriversURL); // first time user has run pgm.
         }
         catch (Exception ex)
         {
            String msg = s_stringMgr.getString("DataCache.error.loadingdrivers",
                                       driversFile.getPath());
            s_log.error(msg, ex);
            msgHandler.showErrorMessage(msg);
            msgHandler.showErrorMessage(ex, null);
            loadDefaultDrivers(dftDriversURL);
         }
      }
      catch (IOException ex)
      {
         s_log.error("Error loading drivers", ex);
      }

      for (Iterator<? extends ISQLDriver> it = drivers(); it.hasNext();)
      {
         registerDriver(it.next(), msgHandler, false);
      }
   }

   public SQLAlias createAlias(IIdentifier id)
   {
      return new SQLAlias(id);
   }

   public ISQLDriver createDriver(IIdentifier id)
   {
      return new SQLDriver(id);
   }

    /**
     * Tests the currently cached driver definitions to see if any default 
     * drivers are missing and returns an array of ISQLDrivers that represent
     * default drivers that were not found.
     * 
     * @param url the url of the file containing the default driver definitions.
     * @return a list of default ISQLDriver instances if any or found; null is
     *         returned otherwise.
     *         
     * @throws IOException
     */
    public ISQLDriver[] findMissingDefaultDrivers(URL url)
        throws IOException
    {
       try(InputStream is = url.openStream())
        {
           ISQLDriver[] result = null;
           ArrayList<ISQLDriver> missingDrivers = new ArrayList<>();

           DriverListHolder tmp = new DriverListHolder();
           tmp.load(is);

           for (Iterator<? extends ISQLDriver> iter = tmp.getAll().iterator(); iter.hasNext(); )
           {
              ISQLDriver defaultDriver = iter.next();
              if (!containsDriver(defaultDriver))
              {
                 missingDrivers.add(defaultDriver);
              }
           }

           if (missingDrivers.size() > 0)
           {
              result = missingDrivers.toArray(new ISQLDriver[missingDrivers.size()]);
           }
           return result;
        }
    }

    /**
     * Returns a boolean value indicating whether or not the specified driver
     * is contained in the cache.
     * 
     * @param driver the ISQLDriver to search for.
     * @return true if the specified driver was found; false otherwise.
     */
    public boolean containsDriver(ISQLDriver driver)
    {
       boolean result = false;
       for (Iterator<? extends ISQLDriver> iter = _driverListHolder.getAll().iterator(); iter.hasNext(); )
       {
          ISQLDriver cachedDriver = iter.next();
          if (cachedDriver.equals(driver))
          {
             result = true;
             break;
          }
       }
       return result;
    }

   public void loadDefaultDrivers(URL url) throws IOException
   {

      try(InputStream is = url.openStream())
      {
         _driverListHolder.load(is);
      }
   }

   private void registerDriver(ISQLDriver sqlDriver, IMessageHandler msgHandler, boolean extendedMessaging)
   {
      boolean registrationSucessfully = false;
      try
      {
         _driverMgr.registerSQLDriver(sqlDriver);
         registrationSucessfully = true;
      }
      catch (ClassNotFoundException cnfe)
      {
         if(extendedMessaging)
         {
            Object[] params  = new Object[]
               {
                  sqlDriver.getDriverClassName(),
                  sqlDriver.getName(),
                  cnfe
               };

            String msg = s_stringMgr.getString("DataCache.error.driverClassNotFound", params);
            // i18n[DataCache.msg.driverClassNotFound=Could not find class {0} in neither
            // the Java class path nor the Extra class path of the {1} driver definition:\n{2}]

            s_log.error(msg, cnfe);
            msgHandler.showErrorMessage(msg);
         }
      }
      catch (Throwable th)
      {
         String msg = s_stringMgr.getString("DataCache.error.registerdriver",
                                    sqlDriver.getName());
         s_log.error(msg, th);
         msgHandler.showErrorMessage(msg);
         msgHandler.showErrorMessage(th, null);
      }

      if(extendedMessaging && registrationSucessfully)
      {
         Object[] params  = new Object[]
            {
               sqlDriver.getDriverClassName(),
               sqlDriver.getName(),
            };


         String msg = s_stringMgr.getString("DataCache.msg.driverRegisteredSucessfully", params);
         // i18n[DataCache.msg.driverRegisteredSucessfully=Driver class {0} sucessfully registered
         // for driver definition: {1}]
         msgHandler.showMessage(msg);
      }
   }

   private void loadAliases(File aliasesFile, IMessageHandler msgHandler)
   {
      try(Java8CloseableFix java8Dum = Main.getApplication().getGlobalSQLAliasVersioner().switchOff())
      {
         _aliasListHolder.load(aliasesFile);
      }
      catch (Exception ex)
      {
         String msg = s_stringMgr.getString("DataCache.error.loadingaliases", aliasesFile.getPath());
         s_log.error(msg, ex);
         msgHandler.showErrorMessage(msg);
         msgHandler.showErrorMessage(ex, null);
      }
   }

   /**
    * In 1.1beta? the jar file for a driver was changed from only one allowed
    * to multiple ones allowed. This method changes the driver from the old
    * version to the new one to allow for loading old versions of the
    * SQLDrivers.xml file.
    */
   @SuppressWarnings("deprecation")
   private void fixupDrivers()
   {
      for (Iterator<? extends ISQLDriver> it = drivers(); it.hasNext();)
      {
         ISQLDriver driver = it.next();
         String[] fileNames = driver.getJarFileNames();
         if (fileNames == null || fileNames.length == 0)
         {
            String fileName = driver.getJarFileName();
            if (fileName != null && fileName.length() > 0)
            {
               driver.setJarFileNames(new String[] {fileName});
               try
               {
                  driver.setJarFileName(null);
               }
               catch (ValidationException ignore)
               {
                  // Ignore
               }
            }
         }
      }
   }

   /**
    * In 2.1 final(+), we introduced a new property for drivers which is the
    * website that hosts the driver and/or the relational database associated
    * with a driver.  To populate existing driver definitions in SQLDrivers.xml
    * with this value, it is necessary to read in the default defs, then scan
    * the currently loaded drivers and set the website url.  That is what this
    * method does.
    */
   private void mergeDefaultWebsites(URL defaultDriversUrl)
   {
      try(InputStream is = defaultDriversUrl.openStream())
      {
         DriverListHolder tmp = new DriverListHolder();
         tmp.load(is);

         for (Iterator<? extends ISQLDriver> iter = tmp.getAll().iterator(); iter.hasNext(); )
         {

            ISQLDriver defaultDriver = iter.next();
            ISQLDriver cachedDriver = getDriver(defaultDriver.getIdentifier());
            if (cachedDriver != null)
            {
               if (cachedDriver.getWebSiteUrl() == null || "".equals(cachedDriver.getWebSiteUrl()))
               {
                  if (defaultDriver.getWebSiteUrl() != null)
                  {
                     cachedDriver.setWebSiteUrl(defaultDriver.getWebSiteUrl());
                  }
               }
            }
         }
      }
      catch (Exception ex)
      {
         s_log.error("Received an unexpected Exception", ex);
      }
   }

   public void refreshDriver(ISQLDriver driver, IMessageHandler messageHandler)
   {
      registerDriver(driver, messageHandler, true);
   }
}
