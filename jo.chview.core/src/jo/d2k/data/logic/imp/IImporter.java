package jo.d2k.data.logic.imp;

import java.io.File;
import java.io.IOException;

public interface IImporter
{

    public abstract String[] getFileTypes();

    public abstract void importFile(File f, IImportCallback cb)
            throws IOException;

}