package jo.d2k.admin.rcp.viz.chview.handlers.rep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.data.data.DeletionBean;
import jo.d2k.data.data.MetadataBean;
import jo.d2k.data.data.StarBean;
import jo.d2k.data.data.StarRouteBean;
import jo.d2k.data.logic.StarLogic;
import jo.d2k.data.logic.imp.IImportCallback;
import jo.util.utils.FormatUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.core.runtime.IProgressMonitor;

public class MergeCallback implements IImportCallback
{
    private IProgressMonitor    mPM;
    private int                 mSize;
    private int                 mTotalStars;
    private int                 mTotalMatchingStars;
    private int                 mTotalNewStars;
    private int                 mTotalMetadata;
    private int                 mTotalRoutes;
    private int                 mTotalDeletions;
    private List<String[]>      mNewStars;
    private List<String[]>      mMergeStars;
    
    private Map<String, Long>   mIndex;
    
    public MergeCallback(IProgressMonitor pm)
    {
        mPM = pm;
        mIndex = new HashMap<>();
        mNewStars = new ArrayList<String[]>();
        mMergeStars = new ArrayList<String[]>();
    }

    public String toHTML()
    {
        StringBuffer text = new StringBuffer();

        text.append("<html>");
        text.append("<head>");
        text.append("<title>MERGE REPORT</title>");
        text.append("</head>");
        text.append("<body>");
        text.append("<h1>MERGE REPORT</h1>");

        text.append("<h2>Totals</h2>");
        text.append("<table>");
        text.append("<tr><th>Stars</th><td>"+mTotalStars+"</td></tr>");
        text.append("<tr><td>(matching)</td><td>"+mTotalMatchingStars+"</td></tr>");
        text.append("<tr><td>(new)</td><td>"+mTotalNewStars+"</td></tr>");
        text.append("<tr><th>Metadata</th><td>"+mTotalMetadata+"</td></tr>");
        text.append("<tr><th>Routes</th><td>"+mTotalRoutes+"</td></tr>");
        text.append("<tr><th>Deletions</th><td>"+mTotalDeletions+"</td></tr>");
        text.append("</table>");

        text.append("<h2>New Stars</h2>");
        text.append("<table>");
        text.append("<tr><th>Stars</th><th>Location</th></tr>");
        for (String[] row : mNewStars)
            text.append("<tr><td>"+row[0]+"</td><td>"+row[1]+"</td></tr>");
        text.append("</table>");

        text.append("<h2>Merged Stars</h2>");
        text.append("<table>");
        text.append("<tr><th>Old Name</th><th>Old Location</th><th>New Name</th><th>New Location</th></tr>");
        for (String[] row : mMergeStars)
            text.append("<tr><td>"+row[0]+"</td><td>"+row[1]+"</td><td>"+row[2]+"</td><td>"+row[3]+"</td></tr>");
        text.append("</table>");

        text.append("</body>");
        text.append("</html>");
        return text.toString();
    }
    
    @Override
    public void importStart(int size)
    {
        mSize = size;
        if (size <= 0)
            size = 1;
        mPM.beginTask("Merge Report", size);
        indexExistingStars();
    }

    @Override
    public StarBean importStar(StarBean star)
    {
        if (mSize > 0)
            mPM.worked(1);
        mTotalStars++;
        long match = findMatch(star);
        if (match > 0)
        {
            mTotalMatchingStars++;
            StarBean old = StarLogic.getByID(match);
            String[] row = new String[] {
                    old.getName(),
                    getCoords(old),
                    star.getName(),
                    getCoords(star)
            };
            if (row[0].equals(row[2]))
                row[2] = "-";
            if (row[1].equals(row[3]))
                row[3] = "-";
            mMergeStars.add(row);
        }
        else
        {
            mTotalNewStars++;
            mNewStars.add(new String[]{
                    star.getName(),
                    getCoords(star)
            });
        }
        return star;
    }

    public String getCoords(StarBean star)
    {
        return FormatUtils.formatDouble(star.getX(), 1)+","
        +FormatUtils.formatDouble(star.getY(), 1)+","
        +FormatUtils.formatDouble(star.getZ(), 1);
    }

    @Override
    public void importDone()
    {
        mPM.done();
    }

    @Override
    public MetadataBean importMetadata(MetadataBean md)
    {
        mTotalMetadata++;
        return md;
    }

    @Override
    public StarRouteBean importRoute(StarRouteBean route)
    {
        mTotalRoutes++;
        return route;
    }

    @Override
    public DeletionBean importDeletion(DeletionBean del)
    {
        mTotalDeletions++;
        return del;
    }

    @Override
    public boolean isCanceled()
    {
        return mPM.isCanceled();
    }

    private void indexExistingStars()
    {
        for (int offset = 0; offset < 100000; offset += 1000)
        {
            List<StarBean> stars = StarLogic.getRange(offset, 1000);
            if (stars.size() == 0)
                break;
            for (StarBean star : stars)
                addToIndex(getNames(star), star);
        }
    }
    
    private List<String> getNames(StarBean star)
    {
        List<String> names = new ArrayList<String>();
        names.add(nerf(star.getName()));
        names.add(nerf(star.getCommonName()));
        names.add(nerf(star.getGJName()));
        names.add(nerf(star.getHDName()));
        names.add(nerf(star.getHIPName()));
        names.add(nerf(star.getHRName()));
        names.add(nerf(star.getSAOName()));
        names.add(nerf(star.getTwoMassName()));
        return names;
    }
    
    private String nerf(String name)
    {
        if (name == null)
            return "";
        name = name.toLowerCase();
        name = name.replace(" ", "");
        name = name.replace("?", "");
        return name;
    }
    
    private void addToIndex(List<String> names, StarBean star)
    {
        for (String name : names)
            if (!StringUtils.isTrivial(name))
                mIndex.put(name, star.getOID());
    }
    
    private long findMatch(StarBean star)
    {
        for (String name : getNames(star))
            if (!StringUtils.isTrivial(name))
                if (mIndex.containsKey(name))
                    return mIndex.get(name);
        return -1;
    }
}
