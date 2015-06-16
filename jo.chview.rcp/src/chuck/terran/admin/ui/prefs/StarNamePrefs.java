package chuck.terran.admin.ui.prefs;

import jo.d2k.data.data.StarGenParams;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarGenParamLogic;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
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

public class StarNamePrefs extends PreferencePage implements IWorkbenchPreferencePage
{
    private static final StarGenParams mDefaults = new StarGenParams();
    
    private Text    mPrefix;
    private List    mGreekNames;
    private Button  mAdd;
    private Button  mRemove;
    private Button  mUp;
    private Button  mDown;

    @Override
    protected Control createContents(Composite parent)
    {
        boolean ro = RuntimeLogic.getInstance().getDataSource().isReadOnly();
        Composite client = new Composite(parent, SWT.NULL);
        client.setLayout(new GridLayout(3, true));
        if (ro)
            GridUtils.makeLabel(client, "Settings may not be changed in read-only database.", "3x1 fill=h");
        GridUtils.makeLabel(client, "Prefix:", "");
        mPrefix = GridUtils.makeText(client, "", "2x1 fill=h");
        GridUtils.makeLabel(client, "Suffixes:", "3x1 fill=h");
        mGreekNames = new List(client, SWT.NULL);
        GridUtils.setLayoutData(mGreekNames, "2x5 fill=hv");
        mAdd = GridUtils.makeButton(client, "Add", "");
        mRemove = GridUtils.makeButton(client, "Remove", "");
        mUp = GridUtils.makeButton(client, "Up", "");
        mDown = GridUtils.makeButton(client, "Down", "");
        GridUtils.makeLabel(client, "", "fill=v");

        mGreekNames.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateEnablement();
            }
        });
        mAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doAdd();
            }
        });
        mRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doRemove();
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

        ControlUtils.setEnabled(client, !ro);

        load(StarGenLogic.PARAMS);
        updateEnablement();
        return client;
    }
    
    private void updateEnablement()
    {
        int sel = mGreekNames.getSelectionIndex();
        mRemove.setEnabled(sel >= 0);
        mDown.setEnabled((sel >= 0) && (sel + 1 < mGreekNames.getItemCount()));
        mUp.setEnabled(sel > 0);
    }
    
    private void doAdd()
    {
        InputDialog dlg = new InputDialog(getShell(), "Add Star Name", "Enter name to be used for star suffix", "", null);
        if (dlg.open() != Dialog.OK)
            return;
        String name = dlg.getValue();
        if (StringUtils.isTrivial(name))
            return;
        int sel = mGreekNames.getSelectionIndex();
        if (sel < 0)
            mGreekNames.add(name, mGreekNames.getItemCount());
        else
            mGreekNames.add(name, sel);
        updateEnablement();
    }
    
    private void doRemove()
    {
        int sel = mGreekNames.getSelectionIndex();
        if (sel < 0)
            return;
        mGreekNames.remove(sel);
        if (sel < mGreekNames.getItemCount())
            mGreekNames.setSelection(sel);
        else
            mGreekNames.setSelection(sel - 1);
        updateEnablement();
    }
    
    private void doUp()
    {
        int sel = mGreekNames.getSelectionIndex();
        if (sel <= 0)
            return;
        String name = mGreekNames.getItem(sel);
        mGreekNames.remove(sel);
        sel--;
        mGreekNames.add(name, sel);
        mGreekNames.setSelection(sel);
        updateEnablement();
    }
    
    private void doDown()
    {
        int sel = mGreekNames.getSelectionIndex();
        if (sel < 0)
            return;
        if (sel > mGreekNames.getItemCount() - 1)
            return;
        String name = mGreekNames.getItem(sel);
        mGreekNames.remove(sel);
        sel++;
        mGreekNames.add(name, sel);
        mGreekNames.setSelection(sel);
        updateEnablement();
    }
    
    @Override
    public boolean performOk()
    {
        store(StarGenLogic.PARAMS);
        StarGenLogic.updatedParams();
        return true;
    }
    
    @Override
    protected void performApply()
    {
        store(StarGenLogic.PARAMS);
        load(StarGenLogic.PARAMS);
        StarGenLogic.updatedParams();
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

    private void load(StarGenParams params)
    {
        mPrefix.setText(params.PREFIX);
        mGreekNames.setItems(params.GREEK_NAMES);       
    }

    private void store(StarGenParams params)
    {
        params.PREFIX = mPrefix.getText();
        params.GREEK_NAMES = mGreekNames.getItems();
        StarGenParamLogic.saveToMetadata(params);
    }
}
