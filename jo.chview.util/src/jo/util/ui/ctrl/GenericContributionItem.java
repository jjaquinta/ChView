/*
 * Created on Oct 25, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jo.util.ui.ctrl;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;

public class GenericContributionItem extends ContributionItem
{
    protected Composite   mParent;
    
        /* (non-Javadoc)
     * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
     */
    public void fill(Composite parent)
    {
        mParent = parent;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.ContributionItem#setVisible(boolean)
     */
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        mParent.setVisible(visible);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.CoolBar, int)
     */
    public void fill(CoolBar parent, int index)
    {
        CoolItem item = new CoolItem(parent, SWT.NULL, index);
        Composite container = new Composite(parent, SWT.NULL);
        fill(container);
        container.pack();
        Point ctrlSize = container.getSize();
        item.setControl(container);
        Point coolSize = item.computeSize(ctrlSize.x, ctrlSize.y);
        coolSize.x += 16;
        item.setMinimumSize(ctrlSize);
        item.setPreferredSize(coolSize);
        item.setSize(coolSize);
    }

}
