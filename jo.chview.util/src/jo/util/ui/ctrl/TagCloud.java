package jo.util.ui.ctrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jo.util.utils.DebugUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class TagCloud extends Canvas implements PaintListener
{
    private Map<String, Number>     mTags;
    private List<String>   mSortedTags;
    private int[]       mBoundaries;
    private Font[]      mFonts;

    public TagCloud(Composite parent, int style)
    {
        super(parent, style);
        mBoundaries = new int[4];
        mFonts = null;
        addPaintListener(this);
    }

    public void paintControl(PaintEvent e)
    {
        setupFonts();
        e.gc.setFont(mFonts[mFonts.length - 1]);
        Point delta = e.gc.stringExtent("X");
        int x = delta.x;
        int y = delta.y;
        Point screen = getSize();
        for (String key : mSortedTags)
        {
            Number val = mTags.get(key);
            Font font = getFont(val.intValue());
            e.gc.setFont(font);
            Point ext = e.gc.stringExtent(key);
            if (x + ext.x + delta.x >= screen.x)
            {   // wrap
                x = delta.x;
                y = y + delta.y*3/2;
            }
            e.gc.drawString(key, x, y + (delta.y - ext.y)/2);
            x += ext.x + delta.x;
        }
    }
    
    private Font getFont(int v)
    {
        for (int i = 0; i < mBoundaries.length; i++)
            if (v < mBoundaries[i])
                return mFonts[i];
        return mFonts[mFonts.length - 1];
    }
    
    private void setupFonts()
    {
        if (mFonts != null)
            return;
        mFonts = new Font[mBoundaries.length + 1];
        Font baseFont = getFont();
        FontData[] baseFontData = baseFont.getFontData();
        int min = baseFontData[0].getHeight()/2;
        int max = min*3;
        for (int i = 0; i < mFonts.length; i++)
        {
            int h = min + (max-min)*i/(mFonts.length - 1);
            int s = (i <= mFonts.length/2) ? SWT.NORMAL : SWT.BOLD;
            mFonts[i] = new Font(getDisplay(), baseFontData[0].getName(), h, s);
            DebugUtils.trace("Font #"+i+", h="+h);
        }
    }

    public Map<String,Number> getTags()
    {
        return mTags;
    }

    public void setTags(Map<String,Number> tags)
    {
        mTags = tags;
        calcBoundaries();
    }

    private void calcBoundaries()
    {
        boolean first = true;
        int min = 0;
        int max = 0;
        for (Number n : mTags.values())
        {
            int v = n.intValue();
            if (first || (v < min))
                min = v;
            if (first || (v > max))
                max = v;
            first = false;
        }
        int dist = (max - min)/(mBoundaries.length + 1);
        for (int i = 0; i < mBoundaries.length; i++)
            mBoundaries[i] = min + (dist*(i+1))/(mBoundaries.length + 1);
        mSortedTags = new ArrayList<String>();
        mSortedTags.addAll(mTags.keySet());
        Collections.sort(mSortedTags);
        redraw();
    }

}
