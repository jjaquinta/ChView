package chuck.terran.admin.ui;

import jo.d2k.data.logic.DataLogic;
import jo.d2k.data.logic.IDataSource;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class DlgPickDataSource extends GenericDialog
{
    private List   mDataSelect;
    
    public DlgPickDataSource(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        IDataSource[] sources = DataLogic.getDataSources();
        IDataSource current = RuntimeLogic.getInstance().getDataSource();
        String[] sourceNames = new String[sources.length];
        int sel = -1;
        for (int i = 0; i < sources.length; i++)
        {
            sourceNames[i] = sources[i].getName();
            if (sources[i] == current)
                sel = i;
        }
        
        getShell().setText("Pick Data Source");
        Composite client = new Composite(parent, SWT.NULL);
        GridUtils.setLayoutData(client, "fill=hv");
        client.setLayout(new GridLayout(2, false));
        
        GridUtils.makeLabel(client, "Source:", "");
        mDataSelect = GridUtils.makeList(client, sourceNames, "fill=h");
        mDataSelect.select(sel);
        
        return mDataSelect;
    }
    
    @Override
    protected void okPressed()
    {
        int idx = mDataSelect.getSelectionIndex();
        if (idx < 0)
            return;
        IDataSource sel = DataLogic.getDataSources()[idx];
        DataLogic.setDataSource(sel);
        super.okPressed();
    }
}
