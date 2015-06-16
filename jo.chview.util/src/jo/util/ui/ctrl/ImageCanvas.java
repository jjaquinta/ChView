package jo.util.ui.ctrl;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ImageCanvas extends Canvas implements PaintListener
{
    public static final int ALIGN_CENTER = 0;
    public static final int ALIGN_NW = 1;
    public static final int ALIGN_N = 2;
    public static final int ALIGN_NE = 3;
    public static final int ALIGN_E = 4;
    public static final int ALIGN_SE = 5;
    public static final int ALIGN_S = 6;
    public static final int ALIGN_SW = 7;
    public static final int ALIGN_W = 8;
    
    private int mAlignment;
    private Image   mImage;

    public ImageCanvas(Composite parent, int style)
    {
        super(parent, style);
        addPaintListener(this);
    }

    public void paintControl(PaintEvent e)
    {
        if (mImage == null)
            return;
        Point controlSize = getSize();
        Rectangle imageSize = mImage.getBounds();
        switch (mAlignment)
        {
            case ALIGN_CENTER:
                e.gc.drawImage(mImage, (controlSize.x - imageSize.width)/2, (controlSize.y - imageSize.height)/2);
                break;
            case ALIGN_NW:
                e.gc.drawImage(mImage, 0, 0);
                break;
            case ALIGN_N:
                e.gc.drawImage(mImage, (controlSize.x - imageSize.width)/2, 0);
                break;
            case ALIGN_NE:
                e.gc.drawImage(mImage, controlSize.x - imageSize.width, 0);
                break;
            case ALIGN_E:
                e.gc.drawImage(mImage, controlSize.x - imageSize.width, (controlSize.y - imageSize.height)/2);
                break;
            case ALIGN_SE:
                e.gc.drawImage(mImage, controlSize.x - imageSize.width, controlSize.y - imageSize.height);
                break;
            case ALIGN_S:
                e.gc.drawImage(mImage, (controlSize.x - imageSize.width)/2, controlSize.y - imageSize.height);
                break;
            case ALIGN_SW:
                e.gc.drawImage(mImage, 0, controlSize.y - imageSize.height);
                break;
            case ALIGN_W:
                e.gc.drawImage(mImage, 0, (controlSize.y - imageSize.height)/2);
                break;
        }
    }

    public int getAlignment()
    {
        return mAlignment;
    }

    public void setAlignment(int alignment)
    {
        mAlignment = alignment;
    }

    public Image getImage()
    {
        return mImage;
    }

    public void setImage(Image image)
    {
        mImage = image;
    }

}
