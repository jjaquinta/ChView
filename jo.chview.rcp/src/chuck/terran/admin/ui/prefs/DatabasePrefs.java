package chuck.terran.admin.ui.prefs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jo.d2k.data.data.StarGenParams;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarGenParamLogic;
import jo.util.ui.act.GenericAction;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.MapUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DatabasePrefs extends PreferencePage implements IWorkbenchPreferencePage
{
    private static final StarGenParams mDefaults = new StarGenParams();
    
    private Button  mGenerate;
    private Button  mExport;
    private Button  mImport;
    
    @Override
    protected Control createContents(Composite parent)
    {
        boolean ro = RuntimeLogic.getInstance().getDataSource().isReadOnly();
        Composite client = new Composite(parent, SWT.NULL);
        client.setLayout(new GridLayout(8, true));
        if (ro)
            GridUtils.makeLabel(client, "Settings may not be changed in read-only database.", "8x1 fill=h");
        GridUtils.makeLabel(client, "These settings apply only to the current data store loaded.", "8x1");

        GridUtils.makeLabel(client, "", "");
        mGenerate = GridUtils.makeCheck(client, "Generate Stars", "7x1 fill=h");
        GridUtils.makeLabel(client, "All star generations settings may be changed at once:", "8x1");
        mExport = GridUtils.makeButton(client, "Export...", "");
        mImport = GridUtils.makeButton(client, "Import...", "");
        GridUtils.makeLabel(client, "", "6x1 fill=h");

        ControlUtils.setEnabled(client, !ro);
        mExport.setEnabled(true);
        
        mImport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doImport();
            }
        });
        mExport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doExport();
            }
        });

        load(StarGenLogic.PARAMS);
        return client;
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
        mGenerate.setSelection(params.GENERATE);
    }

    private void store(StarGenParams params)
    {
        params.GENERATE = mGenerate.getSelection();
        StarGenParamLogic.saveToMetadata(params);
    }
    
    private void doExport()
    {
        String file = GenericAction.getSaveFile(DatabasePrefs.class, "stargen.properties", "Properties file", ".properties");
        if (StringUtils.isTrivial(file))
            return;
        Map<String, String> map = new HashMap<String, String>();
        StarGenParamLogic.toMap(map, StarGenLogic.PARAMS);
        Properties props = MapUtils.load(map, "");
        try
        {
            FileWriter wtr = new FileWriter(file);
            props.store(wtr, "ChView Star Generation Properties");
            wtr.close();
        }
        catch (IOException e)
        {
            GenericAction.openError("Export Star Generation Properties", "Error writing file", e);
        }
    }
    
    private void doImport()
    {
        String file = GenericAction.getOpenFile(DatabasePrefs.class, "stargen.properties", "Properties file", ".properties");
        if (StringUtils.isTrivial(file))
            return;
        Properties props = new Properties();
        try
        {
            FileReader rdr = new FileReader(file);
            props.load(rdr);
            rdr.close();
        }
        catch (IOException e)
        {
            GenericAction.openError("Import Star Generation Properties", "Error reading file", e);
            return;
        }
        Map<String,String> map = new HashMap<>();
        MapUtils.copy(map, props);
        StarGenParamLogic.fromMap(StarGenLogic.PARAMS, map);
        load(StarGenLogic.PARAMS);
        store(StarGenLogic.PARAMS);
        StarGenLogic.updatedParams();
    }
}
