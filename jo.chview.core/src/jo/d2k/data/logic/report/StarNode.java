package jo.d2k.data.logic.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.d2k.data.data.StarBean;
import jo.util.astar.AStarNode;
import jo.util.geom3d.Point3D;

public class StarNode extends AStarNode
{
    private static List<StarBean> mStars;
    private static double mJumpDist;
    private static Map<StarBean, StarNode> mCache = new HashMap<StarBean, StarNode>();
    
    public static void init(List<StarBean> stars, double jumpDist)
    {
        mStars = stars;
        mJumpDist = jumpDist;
        mCache.clear();
    }
    
    public static void term()
    {
        mCache.clear();
    }
    
    public static StarNode getInstance(StarBean star)
    {
        if (mCache.containsKey(star))
            return mCache.get(star);
        StarNode node = new StarNode(star);
        mCache.put(star, node);
        return node;
    }
    
    private StarBean mStar;
    private Point3D  mLocation;
    
    private StarNode(StarBean star)
    {
        mStar = star;
        mLocation = new Point3D(star.getX(), star.getY(), star.getZ());
    }

    @Override
    public float getCost(AStarNode node)
    {
        return (float)mLocation.dist(((StarNode)node).mLocation);
    }

    @Override
    public float getEstimatedCost(AStarNode node)
    {
        return (float)mLocation.dist(((StarNode)node).mLocation);
    }

    @Override
    public List<AStarNode> getNeighbors()
    {
        List<AStarNode> neighbors = new ArrayList<AStarNode>();
        for (StarBean star : mStars)
        {
            if (star == mStar)
                continue;
            if (star.getName().equals(mStar.getName()))
                continue;
            if (star.getParent() != 0)
                continue;
            StarNode node = getInstance(star);
            if (mLocation.dist(node.mLocation) < mJumpDist)
                neighbors.add(node);
        }
        return neighbors;
    }

    public StarBean getStar()
    {
        return mStar;
    }
    
    @Override
    public String toString()
    {
        return mStar.toString();
    }
}
