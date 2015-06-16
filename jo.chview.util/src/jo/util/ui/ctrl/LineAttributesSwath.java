package jo.util.ui.ctrl;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class LineAttributesSwath extends Canvas implements PaintListener
{
    private LineAttributes  mAttributes;

    public LineAttributesSwath(Composite parent, int style) 
    {
        super(parent, style);
        addPaintListener(this);
    }
    
    public void paintControl(PaintEvent e) 
    {
        paintSwath(e.gc, getBounds(), getForeground(), getBackground(), getAttributes());
    }
    
    public static void paintSwath(GC gc, Rectangle bounds, Color fg, Color bg, LineAttributes attributes) 
    {
        gc.setBackground(bg);
        gc.fillRectangle(bounds);
        if (attributes != null)
        {
            gc.setForeground(fg);
            gc.setLineAttributes(attributes);
            int xLeft = bounds.x + 0;
            int xOneQ = bounds.x + bounds.width/4;
            int xTwoQ = bounds.x + bounds.width/2;
            int xThreeQ = bounds.x + bounds.width*3/4;
            int xRight = bounds.x + bounds.width;
            int yTop = bounds.y + bounds.height/4;
            int yBot = bounds.y + bounds.height*3/4;
            gc.drawLine(xLeft, yTop, xOneQ, yBot);
            gc.drawLine(xOneQ, yBot, xTwoQ, yTop);
            gc.drawLine(xTwoQ, yTop, xThreeQ, yBot);
            gc.drawLine(xThreeQ, yBot, xRight, yTop);
        }
    }

    public LineAttributes getAttributes()
    {
        return mAttributes;
    }

    public void setAttributes(LineAttributes attributes)
    {
        mAttributes = attributes;
        redraw();
    }

}