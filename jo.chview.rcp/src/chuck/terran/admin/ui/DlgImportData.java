package chuck.terran.admin.ui;

import jo.util.ui.act.GenericAction;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import chuck.terran.admin.handlers.HandlerImport;

public class DlgImportData extends GenericDialog
{
    private String  mFile;
    private boolean mMerge;
    
    private Text    mCtrlFile;
    private Button  mCtrlLookup;
    private Button  mCtrlMerge;
    
    public DlgImportData(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        if (mFile == null)
            mFile = GenericAction.getMRUFile(DlgImportData.class);
        
        getShell().setText("Import Data");
        Composite client = new Composite(parent, SWT.NULL);
        GridUtils.setLayoutData(client, "fill=hv");
        client.setLayout(new GridLayout(3, false));
        
        GridUtils.makeLabel(client, "File:", "");
        mCtrlFile = GridUtils.makeText(client, mFile, "fill=h");
        mCtrlLookup = GridUtils.makeButton(client, "...", "");
        GridUtils.makeLabel(client, "", "");
        mCtrlMerge = new Button(client, SWT.CHECK);
        mCtrlMerge.setText("Merge");
        mCtrlMerge.setSelection(mMerge);
        GridUtils.setLayoutData(mCtrlMerge, "2x1");
        Text info = GridUtils.makeText(client, SWT.READ_ONLY|SWT.MULTI|SWT.WRAP, "3x1 fill=hv");
        info.setText(INFO);
        
        mCtrlLookup.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doLookup();
            }
        });
        
        return mCtrlFile;
    }
    
    private void doLookup()
    {
        String zipFile = GenericAction.getOpenFile(HandlerImport.class, mCtrlFile.getText(), 
                new String[] { "ZIP File", "ChView LST File", "ChView CHV File" }, 
                new String[] { "*.zip", "*.lst", "*.chv" });
        if (zipFile == null)
            return;
        mCtrlFile.setText(zipFile);
    }
    
    @Override
    protected void okPressed()
    {
        mMerge = mCtrlMerge.getSelection();
        mFile = mCtrlFile.getText();
        super.okPressed();
    }
    
    private static final String INFO = "Data imported will overwrite data already in the database.\n"
            + "If 'Merge' is selected, only overlapping data will be overwritten. If 'Merge' is not "
            + "selected, then ALL data will be overwritten\n"
            + "If a LST or CHV file is selected, Mass, Constellation, Group, and Comment will be stored in\n"
            + "extra fields if they have been defined with those specific names. Otherwise they will be discarded.";

    public String getFile()
    {
        return mFile;
    }

    public void setFile(String file)
    {
        mFile = file;
    }

    public boolean isMerge()
    {
        return mMerge;
    }

    public void setMerge(boolean merge)
    {
        mMerge = merge;
    }
}
