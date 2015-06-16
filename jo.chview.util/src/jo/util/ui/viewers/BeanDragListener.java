/*
 * Created on Jul 15, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.viewers;

import jo.util.beans.Bean;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.TreeItem;

public class BeanDragListener implements DragSourceListener
{
    protected StructuredViewer    mViewer;
    
    public BeanDragListener(StructuredViewer viewer)
    {
        mViewer = viewer;
    }
    
    public void dragStart(DragSourceEvent event)
    {
        // Only start the drag if there are selected beans
        if (mViewer.getSelection().isEmpty())
            event.doit = false;
    }

    public void dragSetData(DragSourceEvent event)
    {
        // provide the data of the suggested type
        if (TextTransfer.getInstance().isSupportedType(event.dataType))
        {
            StringBuffer data = new StringBuffer();
            Object[] beans = getSelection();
            for (int i = 0; i < beans.length; i++)
            {
                if (beans[i] == null)
                    continue;
                Bean b = (Bean)beans[i];
                if (data.length() > 0)
                    data.append(";");
                data.append(b.getClass().getName());
                data.append(",");
                if ((b.getOID() == 0) || (b.getOID() == -1))
                    data.append(b.hashCode());
                else
                    data.append(b.getOID());
            }
            event.data = data.toString();
        }
    }
    
    private Object[] getSelection()
    {
        if (mViewer instanceof TreeViewer)
        {
            TreeItem[] sel = ((TreeViewer)mViewer).getTree().getSelection();
            Object[] ret = new Object[sel.length];
            for (int i = 0; i < sel.length; i++)
                ret[i] = sel[i].getData();
            return ret;
        }
        IStructuredSelection sel = (IStructuredSelection)mViewer.getSelection();
        return sel.toArray();
    }

    public void dragFinished(DragSourceEvent event)
    {
    }
}
