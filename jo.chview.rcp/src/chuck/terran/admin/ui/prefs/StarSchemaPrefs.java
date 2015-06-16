package chuck.terran.admin.ui.prefs;

import java.util.ArrayList;

import jo.d2k.admin.rcp.sys.ui.DlgStarSchemaInput;
import jo.d2k.data.data.StarSchemaBean;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarSchemaLogic;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class StarSchemaPrefs extends PreferencePage implements IWorkbenchPreferencePage
{
    private static final java.util.List<StarSchemaBean> mDefaults = new ArrayList<>();
    
    private List    mSchemas;
    private Button  mAdd;
    private Button  mEdit;
    private Button  mDel;
    private Button  mUp;
    private Button  mDown;
    
    private java.util.List<StarSchemaBean> mSchemaList;
    
    @Override
    protected Control createContents(Composite parent)
    {
        boolean ro = RuntimeLogic.getInstance().getDataSource().isReadOnly();
        Composite client = new Composite(parent, SWT.NULL);
        client.setLayout(new GridLayout(2, false));
        if (ro)
            GridUtils.makeLabel(client, "Settings may not be changed in read-only database.", "2x1 fill=h");
        GridUtils.makeLabel(client, "Extra Star Fields:", "2x1");
        mSchemas = GridUtils.makeList(client, new String[0], "1x6 fill=hv");
        mAdd = GridUtils.makeButton(client, "Add", "");
        mEdit = GridUtils.makeButton(client, "Edit", "");
        mDel = GridUtils.makeButton(client, "Del", "");
        mUp = GridUtils.makeButton(client, "Up", "");
        mDown = GridUtils.makeButton(client, "Down", "");
        GridUtils.makeLabel(client, "", "fill=v");
        ControlUtils.setEnabled(client, !ro);
        Text warning = GridUtils.makeText(client, SWT.READ_ONLY|SWT.MULTI|SWT.WRAP, "2x1 fill=hv");
        warning.setText("Extra fields may not be available on certain tables until the window\n"
                + "containing them as be closed and open again. After making changes, please\n"
                + "close any window whose columns you want to adjust and open it again. Either\n"
                + "that or restart the application.");

        mAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doAdd();
            }
        });
        mEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doEdit();
            }
        });
        mDel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDel();
            }
        });
        mUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doUp();
            }
        });
        mDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doDown();
            }
        });

        load(StarSchemaLogic.getSchemas());
        return client;
    }
    
    private void doAdd()
    {
        DlgStarSchemaInput dlg = new DlgStarSchemaInput(getShell());
        dlg.setSchema(new StarSchemaBean());
        if (dlg.open() != Dialog.OK)
            return;
        StarSchemaBean schema = dlg.getSchema();
        StarSchemaLogic.makeID(schema);
        mSchemaList.add(schema);
        updateFromList();
    }
    
    private void doEdit()
    {
        int sel = mSchemas.getSelectionIndex();
        if (sel < 0)
            return;
        DlgStarSchemaInput dlg = new DlgStarSchemaInput(getShell());
        dlg.setSchema(mSchemaList.get(sel));
        if (dlg.open() != Dialog.OK)
            return;
        StarSchemaBean schema = dlg.getSchema();
        mSchemaList.remove(sel);
        mSchemaList.add(sel, schema);
        updateFromList();
    }
    
    private void doDel()
    {
        int sel = mSchemas.getSelectionIndex();
        if (sel < 0)
            return;
        mSchemas.remove(sel);
        mSchemaList.remove(sel);
        if (mSchemas.getItemCount() > 0)
            if (sel < mSchemas.getItemCount())
                mSchemas.select(sel);
            else
                mSchemas.select(sel-1);
    }
    
    private void doUp()
    {
        int sel = mSchemas.getSelectionIndex();
        if (sel < 1)
            return;
        StarSchemaBean s = mSchemaList.get(sel);
        mSchemaList.remove(sel);
        mSchemaList.add(sel - 1, s);
        updateFromList();
        mSchemas.select(sel - 1);
    }
    
    private void doDown()
    {
        int sel = mSchemas.getSelectionIndex();
        if (sel < 0)
            return;
        if (sel >= mSchemaList.size() - 1)
            return;
        StarSchemaBean s = mSchemaList.get(sel);
        mSchemaList.remove(sel);
        mSchemaList.add(sel + 1, s);
        updateFromList();
        mSchemas.select(sel + 1);
    }
        
    @Override
    public boolean performOk()
    {
        store();
        return true;
    }
    
    @Override
    protected void performApply()
    {
        store();
        load(StarSchemaLogic.getSchemas());
    }
    
    @Override
    protected void performDefaults()
    {
        load(mDefaults);
    }

    @Override
    public void init(IWorkbench wb)
    {
    }

    private void load(java.util.List<StarSchemaBean> schemas)
    {
        mSchemaList = schemas;
        updateFromList();
    }

    public void updateFromList()
    {
        mSchemas.removeAll();
        for (StarSchemaBean schema : mSchemaList)
            mSchemas.add(schema.getTitle());
    }

    private void store()
    {
        StarSchemaLogic.setSchemas(mSchemaList);
    }
}
