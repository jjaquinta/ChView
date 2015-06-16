package jo.util.ui.ctrl;

import jo.util.spline.CubicPolynomial;
import jo.util.spline.SplineLogic;
import jo.util.utils.MathUtils;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class SplinePanel extends Canvas implements PaintListener
{
    private double[][]          mData;
    private double              mMinValue;
    private double              mMaxValue;
    private double              mHighParam;
    private CubicPolynomial[][] mPolys;
    private Color[]             mDrawColor;
    private boolean[]           mDraw;

    public SplinePanel(Composite parent, int style) 
    {
        super(parent, style);
        addPaintListener(this);
    }
    
    public void paintControl(PaintEvent e) 
    {
        Point size = getSize();
        int[] newPoint = new int[2];
        int[] oldPoint = new int[2];
        for (int i = 0; i < mDraw.length; i++)
        {
            if (!mDraw[i])
                continue;
            if (mPolys[i] == null)
                continue;
            e.gc.setForeground(mDrawColor[i]);
            calcPoint(i, 0, size, oldPoint);
            for (int x = 5; x < size.x; x += 5)
            {
                calcPoint(i, x, size, newPoint);
                e.gc.drawLine(oldPoint[0], oldPoint[1], newPoint[0], newPoint[1]);
                oldPoint[0] = newPoint[0];
                oldPoint[1] = newPoint[1];
            }
        }

    }


    private void calcPoint(int i, int pixelX, Point size, int[] point)
    {
        double paramX = MathUtils.interpolate(pixelX, 0, size.x, 0, mHighParam);
        double paramY = SplineLogic.solveCubic(mPolys[i], paramX);
        int pixelY = (int)MathUtils.interpolate(paramY, mMinValue, mMaxValue, size.y, 0);
        point[0] = pixelX;
        point[1] = pixelY;
    }

    public double[][] getSplineData()
    {
        return mData;
    }


    public void setData(double[][] data)
    {
        mData = data;
        mMinValue = mData[0][0];
        mMaxValue = mData[0][0];
        mHighParam = mData[0].length;
        mPolys = new CubicPolynomial[mData.length][];
        for (int i = 0; i < mData.length; i++)
        {
            if (mData[i].length < 2)
                continue;
            for (int j = 0; j < mData[i].length; j++)
            {
                mMinValue = Math.min(mMinValue, mData[i][j]);
                mMaxValue = Math.max(mMaxValue, mData[i][j]);
            }
            mPolys[i] = SplineLogic.calcCubic(mData[i]);
        }
        redraw();
    }

    public Color[] getDrawColor()
    {
        return mDrawColor;
    }

    public void setDrawColor(Color[] colors)
    {
        mDrawColor = colors;
    }

    public boolean[] getDraw()
    {
        return mDraw;
    }

    public void setDraw(boolean[] draw)
    {
        mDraw = draw;
    }
}