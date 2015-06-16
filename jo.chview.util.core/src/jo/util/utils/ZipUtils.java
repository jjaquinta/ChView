/*
 * Created on Aug 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jo.util.utils.io.StreamUtils;

public class ZipUtils
{
    public static void unzip(File dir, InputStream is) throws IOException
    {
        ZipInputStream zis = new ZipInputStream(is);
        for (;;)
        {
            ZipEntry entry = zis.getNextEntry();
            if (entry == null)
                break;
            DebugUtils.trace("  "+entry.getName());
            File entryFile = new File(dir, entry.getName());
            if (entry.isDirectory())
                entryFile.mkdir();
            else
            {
                if (!entryFile.getParentFile().exists())
                    entryFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(entryFile);
                StreamUtils.copy(zis, fos);
                fos.close();
            }
        }        
    }

    public static void zip(File targetDir, FileOutputStream os) throws IOException
    {
        ZipOutputStream zos = new ZipOutputStream(os);
        createEntry(targetDir, targetDir, zos);
        zos.finish();
    }
    
    private static void createEntry(File baseDir, File target, ZipOutputStream zos) throws IOException
    {
        String name = target.toString().substring(baseDir.toString().length());
        if (name.startsWith("/") || name.startsWith("\\"))
            name = name.substring(1);
        name = name.replace('\\', '/');
        DebugUtils.trace(name);
        if (target.isFile())
        {
            ZipEntry ze = new ZipEntry(name);
            zos.putNextEntry(ze);
            FileInputStream fis = new FileInputStream(target);
            StreamUtils.copy(fis, zos);
            fis.close();
            zos.closeEntry();
        }
        else
        {
            //ZipEntry ze = new ZipEntry(name+"/");
            //zos.putNextEntry(ze);
            //zos.closeEntry();
            File[] children = target.listFiles();
            if (children != null)
                for (File child : children)
                    createEntry(baseDir, child, zos);
        }
    }
}
