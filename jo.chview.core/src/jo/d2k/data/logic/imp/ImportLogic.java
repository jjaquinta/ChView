package jo.d2k.data.logic.imp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ImportLogic
{
    private static final IImporter[] IMPORTERS = new IImporter[] {
        new ZIPImporter(),
        new LSTImporter(),
        new CHVImporter(),
    };
    
    public static String[][] getFilters()
    {
        List<String> filterNames = new ArrayList<String>();
        List<String> filterExtensions = new ArrayList<String>();
        for (IImporter importer : IMPORTERS)
        {
            String[] filters = importer.getFileTypes();
            for (int i = 0; i < filters.length; i += 2)
                filterNames.add(filters[i]);
            for (int i = 1; i < filters.length; i += 2)
                filterExtensions.add(filters[i]);
        }
        return new String[][] { filterNames.toArray(new String[0]), filterExtensions.toArray(new String[0]) };
    }
    
    private static IImporter findImporterFor(String fname)
    {
        fname = fname.toLowerCase();
        for (IImporter importer : IMPORTERS)
        {
            String[] filters = importer.getFileTypes();
            for (int i = 1; i < filters.length; i += 2)
                for (StringTokenizer st = new StringTokenizer(filters[i].toLowerCase(), ","); st.hasMoreTokens(); )
                    if (fname.endsWith(st.nextToken().substring(1)))
                        return importer;
        }
        return null;
    }
    
    public static void importFile(File f, IImportCallback cb) throws IOException
    {
        IImporter importer = findImporterFor(f.getName());
        if (importer == null)
            throw new IllegalArgumentException("Unhandled file "+f.getName());
        importer.importFile(f, cb);
    }
}
