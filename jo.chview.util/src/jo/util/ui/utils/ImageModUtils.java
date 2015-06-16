package jo.util.ui.utils;


import jo.util.utils.obj.ByteUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

public class ImageModUtils
{
    public static Image crop(Image img, int x, int y, int width, int height)
    {
        Rectangle bounds = img.getBounds();
        ImageData oldData = img.getImageData();
        if (oldData.depth == 32)
        {
            ImageData newData = new ImageData(width, height, oldData.depth, oldData.palette);
            newData.maskPad = oldData.maskPad;
            int oldBytesPerScanLine = ((bounds.width + 7) / 8 + (oldData.maskPad - 1)) / oldData.maskPad * oldData.maskPad;
            int newBytesPerScanLine = ((width + 7) / 8 + (newData.maskPad - 1)) / newData.maskPad * newData.maskPad;
            byte[] newMaskData = new byte[newBytesPerScanLine*height];
            for (int yy = 0; yy < height; yy++)
                ByteUtils.copyBits(oldData.maskData, yy*oldBytesPerScanLine, 0, newMaskData, yy*newBytesPerScanLine, 0, width);
            newData.maskData = newMaskData;
            int[] pixels = new int[width];
            for (int yy = 0; yy < height; yy++)
            {
                oldData.getPixels(x, y + yy, width, pixels, 0);
                newData.setPixels(0, yy, width, pixels, 0);
            }
            return new Image(img.getDevice(), newData);
        }
        return null;
    }
    public static Image flipLeftToRight(Image img)
    {
        Rectangle bounds = img.getBounds();
        ImageData oldData = img.getImageData();
        if (oldData.depth == 32)
        {
            ImageData newData = new ImageData(bounds.width, bounds.height, oldData.depth, oldData.palette);
            byte[] newMaskData = new byte[oldData.maskData.length];
            System.arraycopy(oldData.maskData, 0, newMaskData, 0, oldData.maskData.length);
            int bytesPerScanLine = ((bounds.width + 7) / 8 + (oldData.maskPad - 1)) / oldData.maskPad * oldData.maskPad;
            for (int i = 0; i < newMaskData.length; i += bytesPerScanLine)
            {
                ByteUtils.flipBits(newMaskData, i, bounds.width);
            }
            newData.maskData = newMaskData;
            newData.maskPad = oldData.maskPad;
            int[] pixels = new int[bounds.width];
            for (int y = 0; y < bounds.height; y++)
            {
                oldData.getPixels(0, y, bounds.width, pixels, 0);
                for (int x = 0; x < bounds.width/2; x++)
                {
                    int tmp = pixels[x];
                    pixels[x] = pixels[bounds.width-1-x];
                    pixels[bounds.width-1-x] = tmp;
                }
                newData.setPixels(0, y, bounds.width, pixels, 0);
            }
            return new Image(img.getDevice(), newData);
        }
        return null;
    }
    public static Image rot90(Image img)
    {
        ImageData dataRot = rotate(img.getImageData(), SWT.RIGHT);
        Image imgRot = new Image(img.getDevice(), dataRot);
        return imgRot;
        /*
        Rectangle bounds = img.getBounds();
        ImageData oldData = img.getImageData();
        if (oldData.depth == 32)
        {
            ImageData newData = new ImageData(bounds.height, bounds.width, oldData.depth, oldData.palette);
            byte[] newMaskData = null;
            if (oldData.maskData != null)
            {
                newMaskData = new byte[oldData.maskData.length];
                System.arraycopy(oldData.maskData, 0, newMaskData, 0, oldData.maskData.length);
                int bytesPerScanLine = ((bounds.height + 7) / 8 + (oldData.maskPad - 1)) / oldData.maskPad * oldData.maskPad;
                for (int i = 0; i < newMaskData.length; i += bytesPerScanLine)
                {
                    ByteUtils.flipBits(newMaskData, i, bounds.height);
                }
            }
            newData.maskData = newMaskData;
            newData.maskPad = oldData.maskPad;
            for (int y = 0; y < bounds.height; y++)
                for (int x = 0; x < bounds.width/2; x++)
                    newData.setPixel(y, x, oldData.getPixel(x, y));
            return new Image(img.getDevice(), newData);
        }
        else if (oldData.depth == 8)
        {
            ImageData newData = new ImageData(bounds.height, bounds.width, oldData.depth, oldData.palette);
            newData.maskData = oldData.maskData;
            newData.maskPad = oldData.maskPad;
            for (int y = 0; y < bounds.height; y++)
                for (int x = 0; x < bounds.width; x++)
                    newData.setPixel(y, x, oldData.getPixel(x, y));
            return new Image(img.getDevice(), newData);
        }
        return null;
        */
    }
    
    // taken from http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet139.java
    public static ImageData rotate(ImageData srcData, int direction) {
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = (direction == SWT.DOWN)? srcData.width * bytesPerPixel : srcData.height * bytesPerPixel;
        byte[] newData = new byte[(direction == SWT.DOWN)? srcData.height * destBytesPerLine : srcData.width * destBytesPerLine];
        int width = 0, height = 0;
        for (int srcY = 0; srcY < srcData.height; srcY++) {
            for (int srcX = 0; srcX < srcData.width; srcX++) {
                int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
                switch (direction){
                    case SWT.LEFT: // left 90 degrees
                        destX = srcY;
                        destY = srcData.width - srcX - 1;
                        width = srcData.height;
                        height = srcData.width; 
                        break;
                    case SWT.RIGHT: // right 90 degrees
                        destX = srcData.height - srcY - 1;
                        destY = srcX;
                        width = srcData.height;
                        height = srcData.width; 
                        break;
                    case SWT.DOWN: // 180 degrees
                        destX = srcData.width - srcX - 1;
                        destY = srcData.height - srcY - 1;
                        width = srcData.width;
                        height = srcData.height; 
                        break;
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        // destBytesPerLine is used as scanlinePad to ensure that no padding is required
        ImageData dstData = new ImageData(width, height, srcData.depth, srcData.palette, srcData.scanlinePad, newData);
        dstData.transparentPixel = srcData.transparentPixel;
        return dstData;
    }
    // taken from http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet139.java
    public static ImageData flip(ImageData srcData, boolean vertical) {
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = srcData.width * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
        for (int srcY = 0; srcY < srcData.height; srcY++) {
            for (int srcX = 0; srcX < srcData.width; srcX++) {
                int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
                if (vertical){
                    destX = srcX;
                    destY = srcData.height - srcY - 1;
                } else {
                    destX = srcData.width - srcX - 1;
                    destY = srcY; 
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        // destBytesPerLine is used as scanlinePad to ensure that no padding is required
        return new ImageData(srcData.width, srcData.height, srcData.depth, srcData.palette, srcData.scanlinePad, newData);
    }
}
