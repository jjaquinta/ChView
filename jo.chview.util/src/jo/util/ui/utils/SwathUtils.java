package jo.util.ui.utils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class SwathUtils
{
    public static Image getImage(Color c, int width, int height)
    {
        String name = "swath"+c.getRed()+"/"+c.getGreen()+"/"+c.getBlue()+"_"+width+"x"+height;
        Image swath = ImageUtils.getMappedImage(name);
        if (swath != null)
            return swath;
        swath = new Image(Display.getDefault(), width, height);
        GC gc = new GC(swath);
        gc.setBackground(c);
        gc.fillRectangle(0, 0, width, height);
        gc.dispose();
        ImageUtils.setMappedImage(name, swath);
        return swath;
    }
}
