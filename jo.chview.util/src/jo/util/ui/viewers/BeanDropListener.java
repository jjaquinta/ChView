/*
 * Created on Jul 15, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.viewers;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;

public class BeanDropListener implements DropTargetListener
{
    protected StructuredViewer mViewer;
    private TextTransfer     textTransfer;
    private BeanTransfer     beanTransfer;
    protected DropTargetEvent mEvent;

    public BeanDropListener(StructuredViewer viewer)
    {
        mViewer = viewer;
        textTransfer = TextTransfer.getInstance();
        beanTransfer = BeanTransfer.getInstance();
    }

    public void dragEnter(DropTargetEvent event)
    {
        mEvent = event;
        if (event.detail == DND.DROP_DEFAULT)
        {
            if ((event.operations & DND.DROP_COPY) != 0)
            {
                event.detail = DND.DROP_COPY;
            }
            else
            {
                event.detail = DND.DROP_NONE;
            }
        }
        // will accept text but prefer to have beans dropped
        for (int i = 0; i < event.dataTypes.length; i++)
        {
            if (beanTransfer.isSupportedType(event.dataTypes[i]))
            {
                event.currentDataType = event.dataTypes[i];
                break;
            }
        }
    }

    public void dragLeave(DropTargetEvent event)
    {
        mEvent = null;
    }

    public void dragOperationChanged(DropTargetEvent event)
    {
        mEvent = event;
        if (event.detail == DND.DROP_DEFAULT)
        {
            if ((event.operations & DND.DROP_COPY) != 0)
            {
                event.detail = DND.DROP_COPY;
            }
            else
            {
                event.detail = DND.DROP_NONE;
            }
        }
    }

    public void dragOver(DropTargetEvent event)
    {
        mEvent = event;
        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
    }

    public void drop(DropTargetEvent event)
    {
        mEvent = event;
        if (beanTransfer.isSupportedType(event.currentDataType))
        {
            Object[] beans = (Object[])event.data;
            drop(beans);
        }
        else if (textTransfer.isSupportedType(event.currentDataType))
        {
            String text = (String)event.data;
            drop(text);
        }
    }

    public void dropAccept(DropTargetEvent event)
    {
        mEvent = event;
    }

    public void drop(Object[] beans)
    {
        // to be overridden
    }

    public void drop(String txt)
    {
        // to be overridden
    }
}
