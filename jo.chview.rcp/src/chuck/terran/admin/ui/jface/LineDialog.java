package chuck.terran.admin.ui.jface;

import jo.util.ui.ctrl.LineAttributesPanel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class LineDialog extends Dialog
{
    private LineAttributes  mAttributes;
    
    private LineAttributesPanel mClient;
    
    public LineDialog(Shell shell)
    {
        super(shell);
    }

    public LineAttributes openDialog()
    {
        if (super.open() != OK)
            return null;
        return getAttributes();
    }
    
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite area = (Composite)super.createDialogArea(parent);
        area.setLayout(new FillLayout());
        mClient = new LineAttributesPanel(area, SWT.NULL);
        if (mAttributes != null)
            mClient.setAttributes(mAttributes);
        return area;
    }

    @Override
    protected void okPressed()
    {
        mAttributes = mClient.getAttributes();
        super.okPressed();
    }
    
    public LineAttributes getAttributes()
    {
        return mAttributes;
    }

    public void setAttributes(LineAttributes attributes)
    {
        mAttributes = attributes;
        if (mClient != null)
            mClient.setAttributes(mAttributes);
    }
}
