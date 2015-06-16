/*
 * Created on May 7, 2005
 *
 */
package jo.util.ui.ctrl;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jo
 *
 */
public class ActionButton extends Composite implements SelectionListener, IPropertyChangeListener
{
    private Action	mAction;
    private Button	mImageButton;
    private Button	mLabelButton;

    public ActionButton(Composite parent, int style, Action action)
    {
        super(parent, SWT.NULL);
        mAction = action;
        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = false;
        rowLayout.pack = false;
        rowLayout.justify = true;
        if ((style&SWT.VERTICAL) != 0)
            rowLayout.type = SWT.VERTICAL;
        else
            rowLayout.type = SWT.HORIZONTAL;
        rowLayout.marginLeft = 0;
        rowLayout.marginTop = 0;
        rowLayout.marginRight = 0;
        rowLayout.marginBottom = 0;
        rowLayout.spacing = 2;
        setLayout(rowLayout);
        ImageDescriptor imageDescriptor = action.getImageDescriptor();
        if (imageDescriptor != null)
        {
            mImageButton = new Button(this, SWT.PUSH);
            mImageButton.setImage(imageDescriptor.createImage());
            mImageButton.setToolTipText(mAction.getToolTipText());
            mImageButton.addSelectionListener(this);
        }
        String text = action.getText();
        if (text != null)
        {
            mLabelButton = new Button(this, SWT.PUSH);
            mLabelButton.setText(text);
            mLabelButton.setToolTipText(mAction.getToolTipText());
            mLabelButton.addSelectionListener(this);
        }
        mAction.addPropertyChangeListener(this);
        updateEnabled();
        pack();
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent e)
    {
        mAction.run();        
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent e)
    {
        widgetSelected(e);        
    }

    public void propertyChange(PropertyChangeEvent event)
    {
        if (event.getProperty().equals("enabled"))
            updateEnabled();
    }
    
    private void updateEnabled()
    {
        if (isDisposed())
            return;
        if (mAction != null)
        {
            if (mImageButton != null)
                mImageButton.setEnabled(mAction.isEnabled());
            if (mLabelButton != null)
                mLabelButton.setEnabled(mAction.isEnabled());
        }
        else
        {
            if (mImageButton != null)
                mImageButton.setEnabled(false);
            if (mLabelButton != null)
                mLabelButton.setEnabled(false);
        }
    }
}
