/*
 * Created on Jul 27, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.ctrl;

/*
 * Copyright (c) 2003 Advanced Systems Concepts, Inc. All rights reserved. This
 * file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

/**
 * CButton is a custom Button object that allows both an Image and Text at the
 * same time.
 * 
 * @author daveo
 */
public class CButton extends Composite
{

    /**
     * Constructs a new instance of this class given its parent and a style
     * value describing its behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in class
     * <code>SWT</code> which is applicable to instances of this class, or
     * must be built by <em>bitwise OR</em> 'ing together (that is, using the
     * <code>int</code> "|" operator) two or more of those <code>SWT</code>
     * style constants. The class description lists the style constants that are
     * applicable to the class. Style bits are also inherited from superclasses.
     * </p>
     * 
     * @param parent
     *            a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style
     *            the style of control to construct. CButton ignores the style
     *            and always creates a Button of type SWT.PUSH.
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the parent</li>
     *                <li>ERROR_INVALID_SUBCLASS - if this class is not an
     *                allowed subclass</li>
     *                </ul>
     * 
     */
    public CButton(Composite parent, int style)
    {
        super(parent, SWT.NULL);
        setLayout(new FillLayout());
        button = new Button(this, style);
    }

    private Button button;
    private Image  rawImage = null; // I didn't create it; I don't dispose it.
    private Image  iconPlusText;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose()
    {
        if (iconPlusText != null)
        {
            if (!iconPlusText.isDisposed())
                iconPlusText.dispose();
        }
        super.dispose();
    }

    /*
     * Converts the depth of mask to 1 bpp.
     */
    private ImageData convertDepth(ImageData mask)
    {
        // if (true) return mask;
        if (mask.depth == 1)
            return mask;
        PaletteData palette = new PaletteData(new RGB[] { new RGB(0, 0, 0),
                new RGB(255, 255, 255) });
        ImageData tempMask = new ImageData(mask.width, mask.height, 1, palette);
        /* Find index of black in mask palette */
        int blackIndex = 0;
        RGB[] rgbs = mask.getRGBs();
        if (rgbs != null)
        {
            while (blackIndex < rgbs.length)
            {
                if (rgbs[blackIndex].equals(palette.colors[0]))
                    break;
                blackIndex++;
            }
            if (blackIndex == rgbs.length)
                SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        int[] pixels = new int[mask.width];
        for (int y = 0; y < mask.height; y++)
        {
            mask.getPixels(0, y, mask.width, pixels, 0);
            for (int i = 0; i < pixels.length; i++)
            {
                if (pixels[i] == blackIndex)
                {
                    pixels[i] = 0;
                }
                else
                {
                    pixels[i] = 1;
                }
            }
            tempMask.setPixels(0, y, mask.width, pixels, 0);
        }
        return tempMask;
    }

    /*
     * Updates the Image in the actual Button to include both the Text and the
     * Image.
     */
    private void updateImage(String text, Image icon)
    {
        if (icon != null)
        {
            /*
             * FEATURE IN SWT: Button can't display both an image and a text
             * caption at the same time. The workaround is to make your own
             * image containing the icon and the caption and use that instead.
             */

            // We still have to set the text in order for accelerator keys to
            // work correctly. The text just won't be displayed once the
            // image is set.
            button.setText(text);

            // Figure out how big everything has to be
            Rectangle iconSize = icon.getBounds();

            GC gc = new GC(button);
            Point captionSize = new Point(0, 0);
            if (text != null)
                captionSize = gc.textExtent(text, SWT.DRAW_MNEMONIC);

            Rectangle iconTotalSize = icon.getBounds();
            iconTotalSize.width += 4;
            iconTotalSize.width += captionSize.x;
            gc.dispose();

            // Draw the icon
            Image image = new Image(Display.getDefault(), iconTotalSize.width,
                    iconTotalSize.height);
            gc = new GC(image);
            gc.setBackground(Display.getDefault().getSystemColor(
                    SWT.COLOR_WHITE));
            gc.setForeground(button.getForeground());
            gc.fillRectangle(iconTotalSize);
            if (iconSize.height > captionSize.y)
            {
                gc.drawImage(icon, 0, 0);
                gc.setFont(button.getFont());
                gc.drawText(text, iconSize.width + 2,
                        iconSize.height - captionSize.y
                                - (iconSize.height - captionSize.y) / 2,
                        SWT.DRAW_MNEMONIC | SWT.DRAW_TRANSPARENT);
            }
            else
            {
                gc.drawImage(icon, 0, captionSize.y - iconSize.height
                        - (captionSize.y - iconSize.height) / 2);
                gc.setFont(button.getFont());
                gc.drawText(text, iconSize.width + 2, 0, SWT.DRAW_MNEMONIC
                        | SWT.DRAW_TRANSPARENT);
            }
            gc.dispose();

            // Draw the transparancy mask
            ImageData iconTransparancy = icon.getImageData()
                    .getTransparencyMask();
            Image iconTransparancyMask = new Image(Display.getDefault(),
                    iconTransparancy);
            PaletteData palette = new PaletteData(new RGB[] { new RGB(0, 0, 0), // transparant
                                                                                // pixels
                                                                                // are
                                                                                // white
                    new RGB(0xFF, 0xFF, 0xFF), // opaque pixels are black
            });
            ImageData maskData = new ImageData(iconTotalSize.width,
                    iconTotalSize.height, 1, palette);
            Image mask = new Image(Display.getDefault(), maskData);
            gc = new GC(mask);
            gc.setBackground(Display.getDefault().getSystemColor(
                    SWT.COLOR_BLACK));
            gc.fillRectangle(0, 0, iconTotalSize.width, iconTotalSize.height);
            if (iconSize.height > captionSize.y)
            {
                gc.setBackground(Display.getDefault().getSystemColor(
                        SWT.COLOR_WHITE));
                gc.drawImage(iconTransparancyMask, 0, 0);

                gc.setForeground(Display.getDefault().getSystemColor(
                        SWT.COLOR_WHITE));
                gc.drawText(text, iconSize.width + 2,
                        iconSize.height - captionSize.y
                                - (iconSize.height - captionSize.y) / 2,
                        SWT.DRAW_MNEMONIC | SWT.DRAW_TRANSPARENT);
            }
            else
            {
                gc.setBackground(Display.getDefault().getSystemColor(
                        SWT.COLOR_WHITE));
                gc.drawImage(iconTransparancyMask, 0, captionSize.y
                        - iconSize.height - (captionSize.y - iconSize.height)
                        / 2);

                gc.setForeground(Display.getDefault().getSystemColor(
                        SWT.COLOR_WHITE));
                gc.drawText(text, iconSize.width + 2, 0, SWT.DRAW_MNEMONIC
                        | SWT.DRAW_TRANSPARENT);
            }
            gc.dispose();

            // Get the data for the image and mask so we can compose them into
            // the final icon...
            maskData = mask.getImageData();
            mask.dispose();

            ImageData imageData = image.getImageData();
            image.dispose();

            /*
             * Feature in SWT: On Linux, the depth winds up getting set to
             * something other than 1 and there's an assert in the code to make
             * sure it's 1. The workaround is to manually convert the bit-depth
             * to 1 each time. See bug #64266.
             */
            // Make the final image (including transparancy)
            iconPlusText = new Image(Display.getDefault(), imageData,
                    convertDepth(maskData));
            button.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e)
                {
                    Button b = (Button)e.widget;
                    Image image = b.getImage();
                    if (image != null)
                        image.dispose();
                }
            });

            button.setImage(iconPlusText);
        }
        else
        {
            button.setText(text);
        }
    }

    /**
     * Sets the receiver's text.
     * <p>
     * This method sets the button label. The label may include the mnemonic
     * character but must not contain line delimiters.
     * </p>
     * <p>
     * Mnemonics are indicated by an '&amp' that causes the next character to be
     * the mnemonic. When the user presses a key sequence that matches the
     * mnemonic, a selection event occurs. On most platforms, the mnemonic
     * appears underlined but may be emphasised in a platform specific manner.
     * The mnemonic indicator character '&amp' can be escaped by doubling it in
     * the string, causing a single '&amp' to be displayed.
     * </p>
     * 
     * @param string
     *            the new text
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the text is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public void setText(String string)
    {
        button.setText(string);

        // The text is actually drawn as a Image if an Image has been set.
        if (rawImage != null)
            updateImage(string, rawImage);
    }

    /**
     * Returns the receiver's text, which will be an empty string if it has
     * never been set.
     * 
     * @return the receiver's text
     * 
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public String getText()
    {
        return button.getText();
    }

    /**
     * Sets the receiver's image to the argument, which may be null indicating
     * that no image should be displayed.
     * 
     * @param image
     *            the image to display on the receiver (may be null)
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_INVALID_ARGUMENT - if the image has been
     *                disposed</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public void setImage(Image image)
    {
        rawImage = image;
        String text = button.getText();
        if (image != null)
            if (text == null || text.equals(""))
                button.setImage(image);
            else
                updateImage(text, image);
        else
        {
            button.setImage(null);
            button.setText(text);
        }
    }

    /**
     * Returns the receiver's image if it has one, or null if it does not.
     * 
     * @return the receiver's image
     * 
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public Image getImage()
    {
        return rawImage;
    }

    /**
     * Returns the underlying Button control.
     * 
     * @return the underlying Button control.
     */
    public Button getControl()
    {
        return button;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addControlListener(org.eclipse.swt.events.ControlListener)
     */
    public void addControlListener(ControlListener listener)
    {
        button.addControlListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addFocusListener(org.eclipse.swt.events.FocusListener)
     */
    public void addFocusListener(FocusListener listener)
    {
        button.addFocusListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addHelpListener(org.eclipse.swt.events.HelpListener)
     */
    public void addHelpListener(HelpListener listener)
    {
        button.addHelpListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addKeyListener(org.eclipse.swt.events.KeyListener)
     */
    public void addKeyListener(KeyListener listener)
    {
        button.addKeyListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Widget#addListener(int,
     *      org.eclipse.swt.widgets.Listener)
     */
    public void addListener(int eventType, Listener listener)
    {
        button.addListener(eventType, listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addMouseListener(org.eclipse.swt.events.MouseListener)
     */
    public void addMouseListener(MouseListener listener)
    {
        button.addMouseListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addMouseMoveListener(org.eclipse.swt.events.MouseMoveListener)
     */
    public void addMouseMoveListener(MouseMoveListener listener)
    {
        button.addMouseMoveListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addMouseTrackListener(org.eclipse.swt.events.MouseTrackListener)
     */
    public void addMouseTrackListener(MouseTrackListener listener)
    {
        button.addMouseTrackListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addPaintListener(org.eclipse.swt.events.PaintListener)
     */
    public void addPaintListener(PaintListener listener)
    {
        button.addPaintListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#addTraverseListener(org.eclipse.swt.events.TraverseListener)
     */
    public void addTraverseListener(TraverseListener listener)
    {
        button.addTraverseListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#getAccessible()
     */
    public Accessible getAccessible()
    {
        return button.getAccessible();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#forceFocus()
     */
    public boolean forceFocus()
    {
        return button.forceFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#getBackground()
     */
    public Color getBackground()
    {
        return button.getBackground();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#getFont()
     */
    public Font getFont()
    {
        return button.getFont();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#getForeground()
     */
    public Color getForeground()
    {
        return button.getForeground();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#getToolTipText()
     */
    public String getToolTipText()
    {
        return button.getToolTipText();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#isFocusControl()
     */
    public boolean isFocusControl()
    {
        return button.isFocusControl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Widget#notifyListeners(int,
     *      org.eclipse.swt.widgets.Event)
     */
    public void notifyListeners(int eventType, Event event)
    {
        button.notifyListeners(eventType, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removeControlListener(org.eclipse.swt.events.ControlListener)
     */
    public void removeControlListener(ControlListener listener)
    {
        button.removeControlListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Widget#removeDisposeListener(org.eclipse.swt.events.DisposeListener)
     */
    public void removeDisposeListener(DisposeListener listener)
    {
        button.removeDisposeListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removeFocusListener(org.eclipse.swt.events.FocusListener)
     */
    public void removeFocusListener(FocusListener listener)
    {
        button.removeFocusListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removeHelpListener(org.eclipse.swt.events.HelpListener)
     */
    public void removeHelpListener(HelpListener listener)
    {
        button.removeHelpListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removeKeyListener(org.eclipse.swt.events.KeyListener)
     */
    public void removeKeyListener(KeyListener listener)
    {
        button.removeKeyListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Widget#removeListener(int,
     *      org.eclipse.swt.widgets.Listener)
     */
    public void removeListener(int eventType, Listener listener)
    {
        button.removeListener(eventType, listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removeMouseListener(org.eclipse.swt.events.MouseListener)
     */
    public void removeMouseListener(MouseListener listener)
    {
        button.removeMouseListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removeMouseMoveListener(org.eclipse.swt.events.MouseMoveListener)
     */
    public void removeMouseMoveListener(MouseMoveListener listener)
    {
        button.removeMouseMoveListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removeMouseTrackListener(org.eclipse.swt.events.MouseTrackListener)
     */
    public void removeMouseTrackListener(MouseTrackListener listener)
    {
        button.removeMouseTrackListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removePaintListener(org.eclipse.swt.events.PaintListener)
     */
    public void removePaintListener(PaintListener listener)
    {
        button.removePaintListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#removeTraverseListener(org.eclipse.swt.events.TraverseListener)
     */
    public void removeTraverseListener(TraverseListener listener)
    {
        button.removeTraverseListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
     */
    public void setBackground(Color color)
    {
        button.setBackground(color);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#setCapture(boolean)
     */
    public void setCapture(boolean capture)
    {
        button.setCapture(capture);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#setCursor(org.eclipse.swt.graphics.Cursor)
     */
    public void setCursor(Cursor cursor)
    {
        button.setCursor(cursor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Composite#setFocus()
     */
    public boolean setFocus()
    {
        return button.setFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
     */
    public void setFont(Font font)
    {
        button.setFont(font);
    }

    /* (non-Javadoc)
     * @see
     org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics.Color)
     */
    public void setForeground(Color color)
    {
        button.setForeground(color);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Control#setMenu(org.eclipse.swt.widgets.Menu)
     */
    public void setMenu(Menu menu)
    {
        button.setMenu(menu);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
     */
    public void setToolTipText(String string)
    {
        button.setToolTipText(string);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Control#traverse(int)
     */
    public boolean traverse(int traversal)
    {
        return button.traverse(traversal);
    }

    public void addSelectionListener(SelectionListener listener)
    {
        button.addSelectionListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener)
    {
        button.removeSelectionListener(listener);
    }
}
