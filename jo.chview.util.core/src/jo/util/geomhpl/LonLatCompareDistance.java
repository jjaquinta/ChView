package jo.util.geomhpl;

import java.util.Comparator;

public class LonLatCompareDistance implements Comparator<LonLat>
{
    private LonLat  mReference;
    
    public LonLatCompareDistance(LonLat ref)
    {
        mReference = ref;
    }
    
    @Override
    public int compare(LonLat o1, LonLat o2)
    {
        double d1 = LonLatLogic.dist(mReference, o1);
        double d2 = LonLatLogic.dist(mReference, o2);
        return (int)Math.signum(d1 - d2);
    }
}
