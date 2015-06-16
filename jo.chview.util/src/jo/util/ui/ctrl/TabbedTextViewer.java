package jo.util.ui.ctrl;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class TabbedTextViewer extends Canvas implements PaintListener, MouseListener, KeyListener
{
    private ITabbedTextContentProvider  mContentProvider;
    private Point                       mInsets;
    private String                      mSelection;
    private Rectangle[]                 mListItemExtents;
    private int                         mSelectionIndex;
    private int                         mListWidth;

    public TabbedTextViewer(Composite parent, int style)
    {
        super(parent, style);
        addPaintListener(this);
        addMouseListener(this);
        addKeyListener(this);
        mInsets = new Point(4, 4);
    }

    public ITabbedTextContentProvider getContentProvider()
    {
        return mContentProvider;
    }

    public void setContentProvider(ITabbedTextContentProvider contentProvider)
    {
        mContentProvider = contentProvider;
        if (mContentProvider != null)
        {
            String[] labels = mContentProvider.getPrimaryLabels();
            if ((labels != null) && (labels.length > 0))
                mSelection = labels[0];
        }
        redraw();
    }

    public void paintControl(PaintEvent e)
    {
        String[] labels;
        if (mContentProvider == null)
            labels = new String[0];
        else
            labels = mContentProvider.getPrimaryLabels();
        Point size = getSize();
        paintListText(e.gc, labels);
        paintListBox(e.gc, size);
        paintSelectionText(e.gc, size);
        paintSelectionBox(e.gc, size);
        paintConnector(e.gc);
    }
    
    private void paintConnector(GC gc)
    {
        if (mSelectionIndex == -1)
            return;
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        int left = mListWidth + mInsets.x*3;
        gc.drawLine(left, mListItemExtents[mSelectionIndex].y, left + mInsets.x, mListItemExtents[mSelectionIndex].y);
        gc.drawLine(left, mListItemExtents[mSelectionIndex].y + mListItemExtents[mSelectionIndex].height, left + mInsets.x, mListItemExtents[mSelectionIndex].y + mListItemExtents[mSelectionIndex].height);
    }
    
    private void paintSelectionBox(GC gc, Point size)
    {
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        int left = mListWidth + mInsets.x*4;
        gc.drawLine(left, mInsets.y, size.x - mInsets.x, mInsets.y);
        gc.drawLine(left, size.y - mInsets.y, size.x - mInsets.x, size.y - mInsets.y);
        gc.drawLine(size.x - mInsets.x, mInsets.y, size.x - mInsets.x, size.y - mInsets.y);
        if (mSelectionIndex == -1)
            gc.drawLine(left, mInsets.y, left, size.y - mInsets.y);
        else
        {
            gc.drawLine(left, mInsets.y, left, mListItemExtents[mSelectionIndex].y);
            gc.drawLine(left, mListItemExtents[mSelectionIndex].y + mListItemExtents[mSelectionIndex].height, left, size.y - mInsets.y);
        }
    }
    
    private void paintSelectionText(GC gc, Point size)
    {
        if (mSelection == null)
            return;
        if (mContentProvider == null)
            return;
        String txt = mContentProvider.getSecondaryText(mSelection);
        if ((txt == null) || (txt.length() == 0))
            return;
        gc.setForeground(getForeground());
        gc.setBackground(getBackground());
        int left = mListWidth + mInsets.x*5;
        int right = size.x - mInsets.x*2;
        Point gap = gc.textExtent("m");
        int x = left;
        int y = mInsets.y*2;
        boolean fixedWith = false;
        for (StringTokenizer st = new StringTokenizer(txt, " \n\t\u0002\u0003", true); st.hasMoreTokens(); )
        {
            String word = st.nextToken();
            if (word.equals(" "))
            {
                x += gap.x;
            }
            else if (word.equals("\t"))
            {
                x += gap.x*4;                
            }
            else if (word.equals("\n"))
            {
                x = left;
                y += gap.y;
            }
            else if (word.equals("\u0002"))
            {
                fixedWith = true;
            }
            else if (word.equals("\u0003"))
            {
                fixedWith = false;
            }
            else
            {
                Point extent;
                if (fixedWith)
                    extent = new Point(word.length()*gap.x, gap.y);
                else
                    extent = gc.stringExtent(word);
                if (x + extent.x >= right)
                {
                    x = left;
                    y += gap.y;
                }
                if (fixedWith)
                {
                    for (int i = 0; i < word.length(); i++)
                        gc.drawString(word.substring(i, i + 1), x + i*gap.x, y);                    
                }
                else
                    gc.drawString(word, x, y);
                x += extent.x;
            }
        }
    }

    private void paintListText(GC gc, String[] labels)
    {
        Point cursor = new Point(mInsets.x*2, mInsets.y*2);
        mListItemExtents = new Rectangle[labels.length];
        mSelectionIndex = -1;
        Point extent;
        gc.setForeground(getForeground());
        gc.setBackground(getBackground());
        mListWidth = 0;
        for (int i = 0; i < labels.length; i++)
        {
            if (labels[i] == null)
                labels[i] = "";
            extent = gc.textExtent(labels[i]);
            if (labels[i].equals(mSelection))
                mSelectionIndex = i;
            else
                gc.drawString(labels[i], cursor.x, cursor.y);
            mListItemExtents[i] = new Rectangle(mInsets.x*2, cursor.y, extent.x, extent.y);
            mListWidth = Math.max(mListWidth, extent.x);
            cursor.y += extent.y;
        }        
        if (mSelectionIndex >= 0)
        {
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
            gc.fillRectangle(mListItemExtents[mSelectionIndex].x, mListItemExtents[mSelectionIndex].y, 
                    mListWidth, mListItemExtents[mSelectionIndex].height);
            gc.drawString(mSelection, mListItemExtents[mSelectionIndex].x, mListItemExtents[mSelectionIndex].y);
        }
    }
    
    private void paintListBox(GC gc, Point size)
    {
        int right = mListWidth + mInsets.x*3;
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        gc.drawLine(mInsets.x, mInsets.y, right, mInsets.y);
        gc.drawLine(mInsets.x, mInsets.y, mInsets.x, size.y - mInsets.y);
        gc.drawLine(mInsets.x, size.y - mInsets.y, right, size.y - mInsets.y);
        if (mSelectionIndex == -1)
            gc.drawLine(right, mInsets.y, right, size.y - mInsets.y);
        else
        {
            gc.drawLine(right, mInsets.y, right, mListItemExtents[mSelectionIndex].y);
            gc.drawLine(right, mListItemExtents[mSelectionIndex].y + mListItemExtents[mSelectionIndex].height, right, size.y - mInsets.y);
        }
    }

    public Point getInsets()
    {
        return mInsets;
    }

    public void setInsets(Point insets)
    {
        mInsets = insets;
    }

    public String getSelection()
    {
        return mSelection;
    }

    public void setSelection(String selection)
    {
        mSelection = selection;
        redraw();
    }
    
    public void setSelection(int i)
    {
        if (mContentProvider == null)
            return;
        String[] items = mContentProvider.getPrimaryLabels();
        if (items == null)
            return;
        setSelection(items[i]);        
    }

    public void mouseDoubleClick(MouseEvent e)
    {
    }

    public void mouseDown(MouseEvent e)
    {
        for (int i = 0; i < mListItemExtents.length; i++)
            if (mListItemExtents[i].contains(e.x, e.y))
            {
                setSelection(i);
                break;
            }
    }

    public void mouseUp(MouseEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        if (e.keyCode == SWT.ARROW_DOWN)
        {
            if (mSelectionIndex < mListItemExtents.length - 1)
                setSelection(mSelectionIndex+1);
        }
        else if (e.keyCode == SWT.ARROW_UP)
        {
            if (mSelectionIndex > 0)
                setSelection(mSelectionIndex-1);
        }
    }

    public void keyReleased(KeyEvent e)
    {
    }
}
