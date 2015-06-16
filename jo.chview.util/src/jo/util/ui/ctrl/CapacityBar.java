package jo.util.ui.ctrl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class CapacityBar extends Canvas implements PaintListener
{
    private static final int INSET = 2;
    private double  mMinimum;
    private double  mMaximum;
    private double  mValue;
    private boolean mInvert;
    
    public CapacityBar(Composite parent, int style)
    {
        super(parent, style);
        mMinimum = 0.0;
        mMaximum = 100.0;
        mValue = 0.0;
        addPaintListener(this);
    }
    
    public Point computeSize(int wHint, int hHint, boolean changed)
    {
        return new Point(wHint, 24);
    }



    public void paintControl(PaintEvent e)
    {
        double pc = 0;
        if (mMaximum != mMinimum)
            pc = (mValue - mMinimum)/mMaximum;
        if (pc < 0)
            pc = 0;
        else if (pc > 2.0)
            pc = 2.0;
        String pcText = ((int)(pc*100.0))+"%";
        Point size = getSize();
        int barWidth = (int)((size.x - INSET*2)/2*pc); 
        // draw bar
        e.gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        if (!mInvert)
        {
            if (pc < .8)
                e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
            else if (pc <= 1.0)
                e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
            else
                e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
        }
        else
        {
            if (pc > 1.2)
                e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
            else if (pc >= 1.0)
                e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
            else
                e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
        }
        e.gc.fillRectangle(INSET, INSET, barWidth, size.y - INSET*2);
        // draw box
        e.gc.drawRectangle(INSET, INSET, size.x - INSET*2, size.y - INSET*2);
        // draw mid
        e.gc.drawLine(size.x/2, 0, size.x/2, size.y);
        // draw text
        Point textExtents = e.gc.stringExtent(pcText);
        e.gc.drawText(pcText, size.x/2 - textExtents.x/2, size.y/2 - textExtents.y/2);
    }

    public double getMaximum()
    {
        return mMaximum;
    }

    public void setMaximum(double maximun)
    {
        mMaximum = maximun;
        redraw();
    }

    public double getMinimum()
    {
        return mMinimum;
    }

    public void setMinimum(double minimum)
    {
        mMinimum = minimum;
        redraw();
    }

    public double getValue()
    {
        return mValue;
    }

    public void setValue(double value)
    {
        mValue = value;
        redraw();
    }

    public boolean isInvert()
    {
        return mInvert;
    }

    public void setInvert(boolean invert)
    {
        mInvert = invert;
    }
}
