package chuck.terran.admin.ui;

import java.util.List;

import jo.d2k.data.data.StarColumn;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DlgPickStarColumns extends GenericDialog
{
    private List<StarColumn>    mColumns;
    
    private StarColumnsPanel    mClient;
    
    public DlgPickStarColumns(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Select Display Columns");
        mClient = new StarColumnsPanel(parent, SWT.NULL);
        GridUtils.setLayoutData(mClient, "fill=hv");
        mClient.setColumns(mColumns);
        return mClient;
    }
    
    @Override
    protected void okPressed()
    {
        mColumns = mClient.getColumns();
        super.okPressed();
    }

    public List<StarColumn> getColumns()
    {
        return mColumns;
    }

    public void setColumns(List<StarColumn> columns)
    {
        mColumns = columns;
    }
}
