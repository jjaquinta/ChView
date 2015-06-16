package jo.util.astar;

import java.util.LinkedList;
import java.util.List;

import jo.util.utils.DebugUtils;

/**
 * The AStarSearch class, along with the AStarNode class, implements a generic
 * A* search algorithm. The AStarNode class should be subclassed to provide
 * searching capability.
 */
public class AStarSearch
{
    /**
     * A simple priority list, also called a priority queue. Objects in the list
     * are ordered by their priority, determined by the object's Comparable
     * interface. The highest priority item is first in the list.
     */
    public static class PriorityList extends LinkedList<AStarNode>
    {
        /**
         * 
         */
        private static final long serialVersionUID = -4739200255850518179L;

        public void addPriority(AStarNode object)
        {
            for (int i = 0; i < size(); i++)
            {
                if (object.compareTo(get(i)) <= 0)
                {
                    add(i, object);
                    return;
                }
            }
            addLast(object);
        }
    }

    /**
     * Construct the path, not including the start node.
     */
    protected List<AStarNode> constructPath(AStarNode node)
    {
        LinkedList<AStarNode> path = new LinkedList<AStarNode>();
        while (node.mPathParent != null)
        {
            path.addFirst(node);
            node = node.mPathParent;
        }
        return path;
    }

    /**
     * Find the path from the start node to the end node. A list of AStarNodes
     * is returned, or null if the path is not found.
     */
    public List<AStarNode> findPath(AStarNode startNode, AStarNode goalNode)
    {
        DebugUtils.debug("Searching from "+startNode+" to "+goalNode);
        PriorityList openList = new PriorityList();
        LinkedList<AStarNode> closedList = new LinkedList<AStarNode>();

        startNode.mCostFromStart = 0;
        startNode.mEstimatedCostToGoal = startNode.getEstimatedCost(goalNode);
        startNode.mPathParent = null;
        openList.add(startNode);

        while (!openList.isEmpty())
        {
            StringBuffer debug = new StringBuffer("In list:");
            for (int i = 0; i < Math.min(6, openList.size()); i++)
                debug.append("["+openList.get(i).toString()+"]");
            DebugUtils.debug(debug.toString());
            AStarNode node = (AStarNode)openList.removeFirst();
            DebugUtils.debug("Considering "+node.toString());
            if (node == goalNode)
            {
                DebugUtils.debug("  We're here!");
                // construct the path from start to goal
                return constructPath(goalNode);
            }

            List<AStarNode> neighbors = node.getNeighbors();
            for (int i = 0; i < neighbors.size(); i++)
            {
                AStarNode neighborNode = (AStarNode)neighbors.get(i);
                DebugUtils.debug("  neighbor "+neighborNode.toString());
                boolean isOpen = openList.contains(neighborNode);
                boolean isClosed = closedList.contains(neighborNode);
                float costFromStart = node.mCostFromStart
                        + node.getCost(neighborNode);
                DebugUtils.debug("    open="+isOpen+", closed="+isClosed+", cost="+costFromStart);

                // check if the neighbor node has not been
                // traversed or if a shorter path to this
                // neighbor node is found.
                if ((!isOpen && !isClosed)
                        || costFromStart < neighborNode.mCostFromStart)
                {
                    neighborNode.mPathParent = node;
                    neighborNode.mCostFromStart = costFromStart;
                    neighborNode.mEstimatedCostToGoal = neighborNode
                            .getEstimatedCost(goalNode);
                    if (isClosed)
                    {
                        closedList.remove(neighborNode);
                    }
                    if (isOpen)
                        openList.remove(neighborNode);
                    openList.add(neighborNode);
                    DebugUtils.debug("    added "+neighborNode);
                }
            }
            closedList.add(node);
        }

        // no path found
        return null;
    }
}