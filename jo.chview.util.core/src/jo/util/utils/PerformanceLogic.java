package jo.util.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PerformanceLogic
{
    private static final Map<String, Long> mStartTimes = new HashMap<String, Long>();
    private static final Map<String, Long> mTotalTimes = new HashMap<String, Long>();
    private static final Map<String, Integer> mTotalInstances = new HashMap<String, Integer>();
    
    public static void clear()
    {
        mStartTimes.clear();
        mTotalTimes.clear();
        mTotalInstances.clear();
    }
    
    public static void start(String id)
    {
        mStartTimes.put(id, System.nanoTime());
    }
    
    public static void stop(String id)
    {
        long stop = System.nanoTime();
        if (!mStartTimes.containsKey(id))
            return;
        long start = mStartTimes.get(id);
        long elapsed = stop - start;
        if (mTotalInstances.containsKey(id))
            mTotalInstances.put(id, mTotalInstances.get(id) + 1);
        else
            mTotalInstances.put(id, 1);
        if (mTotalTimes.containsKey(id))
            mTotalTimes.put(id, mTotalTimes.get(id) + elapsed);
        else
            mTotalTimes.put(id, elapsed);
        mStartTimes.remove(id);
    }
    
    public static void dump()
    {
        String[] keys = mTotalInstances.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (int i = 0; i < keys.length; i++)
        {
            String prefix = keys[i];
            if (prefix.indexOf('.') >= 0)
                continue;
            long average = mTotalTimes.get(prefix)/mTotalInstances.get(prefix);
            DebugUtils.info(String.format("%5d %s", average, prefix));
            dump("      ", average, keys, i);
        }
    }
    private static void dump(String indent, long overall, String[] keys, int idx)
    {
        String prefix = keys[idx]+".";
        for (int i = idx + 1; i < keys.length; i++)
        {
            String key = keys[i];
            if (!key.startsWith(prefix))
                break;
            if (key.indexOf('.', prefix.length()) > 0)
                continue;
            long average = mTotalTimes.get(key)/mTotalInstances.get(key);
            int pc = (int)(average*100/overall);
            DebugUtils.info(indent+String.format("%5d %3d%% %s", average, pc, key.substring(prefix.length())));
            dump(indent+"           ", average, keys, i);
        }
    }
}
