package jo.d2k.data.logic.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jo.d2k.data.data.DeletionBean;
import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.util.beans.Bean;
import jo.util.beans.CSVLogic;

public class ZIPImporter implements IImporter
{
    /* (non-Javadoc)
     * @see jo.d2k.data.logic.imp.IImporter#getFileTypes()
     */
    @Override
    public String[] getFileTypes()
    {
        return new String[] { "ZIP File", "*.zip" };
    }
    
    /* (non-Javadoc)
     * @see jo.d2k.data.logic.imp.IImporter#importFile(java.io.File, jo.d2k.data.logic.imp.IImportCallback)
     */
    @Override
    public void importFile(File f, IImportCallback cb) throws IOException
    {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
        while (!cb.isCanceled())
        {
            ZipEntry entry = zis.getNextEntry();
            if (entry == null)
                break;
            if (entry.getName().equals("stars.csv"))
                importStarData(zis, cb);
            else if (entry.getName().equals("routes.csv"))
                importRouteData(zis, cb);
            else if (entry.getName().equals("metadata.csv"))
                importMetadataData(zis, cb);
            else if (entry.getName().equals("deletions.csv"))
                importDeletionData(zis, cb);
        }
        cb.importDone();
        zis.close();
    }

    private void importStarData(InputStream is, IImportCallback cb)
            throws IOException
    {
        InputStreamReader rdr = new InputStreamReader(is, "utf-8");
        try
        {
            Collection<Bean> beans = CSVLogic.fromCVS(rdr, StarBean.class);
            cb.importStart(beans.size());
            for (Bean b : beans)
            {
                cb.importStar((StarBean)b);
                if (cb.isCanceled())
                    break;
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e); // should never happen
        }
    }

    private void importRouteData(InputStream is, IImportCallback cb)
            throws IOException
    {
        InputStreamReader rdr = new InputStreamReader(is, "utf-8");
        try
        {
            Collection<Bean> beans = CSVLogic.fromCVS(rdr, StarRouteBean.class);
            for (Bean b : beans)
            {
                cb.importRoute((StarRouteBean)b);
                if (cb.isCanceled())
                    break;
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e); // should never happen
        }
    }

    private void importMetadataData(InputStream is, IImportCallback cb)
            throws IOException
    {
        InputStreamReader rdr = new InputStreamReader(is, "utf-8");
        try
        {
            Collection<Bean> beans = CSVLogic.fromCVS(rdr, MetadataBean.class);
            for (Bean b : beans)
            {
                cb.importMetadata((MetadataBean)b);
                if (cb.isCanceled())
                    break;
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e); // should never happen
        }
    }

    private void importDeletionData(InputStream is, IImportCallback cb)
            throws IOException
    {
        InputStreamReader rdr = new InputStreamReader(is, "utf-8");
        try
        {
            Collection<Bean> beans = CSVLogic.fromCVS(rdr, DeletionBean.class);
            for (Bean b : beans)
            {
                cb.importDeletion((DeletionBean)b);
                if (cb.isCanceled())
                    break;
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e); // should never happen
        }
    }
}
