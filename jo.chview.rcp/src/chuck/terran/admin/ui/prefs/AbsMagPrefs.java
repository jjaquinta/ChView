package chuck.terran.admin.ui.prefs;

import jo.d2k.data.data.StarGenParams;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarGenParamLogic;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class AbsMagPrefs extends PreferencePage implements IWorkbenchPreferencePage
{
    private static final StarGenParams mDefaults = new StarGenParams();
    
    private Spinner[][] mAbsMagDist = new Spinner[7][7];

    @Override
    protected Control createContents(Composite parent)
    {
        boolean ro = RuntimeLogic.getInstance().getDataSource().isReadOnly();
        Composite client = new Composite(parent, SWT.NULL);
        client.setLayout(new GridLayout(8, true));
        if (ro)
            GridUtils.makeLabel(client, "Settings may not be changed in read-only database.", "8x1 fill=h");
        GridUtils.makeLabel(client, "Absolute Magnitude Distribution:", "8x1");
        GridUtils.makeLabel(client, "O", "");
        makeSpinners(client, mAbsMagDist[0]);
        GridUtils.makeLabel(client, "B", "");
        makeSpinners(client, mAbsMagDist[1]);
        GridUtils.makeLabel(client, "A", "");
        makeSpinners(client, mAbsMagDist[2]);
        GridUtils.makeLabel(client, "F", "");
        makeSpinners(client, mAbsMagDist[3]);
        GridUtils.makeLabel(client, "G", "");
        makeSpinners(client, mAbsMagDist[4]);
        GridUtils.makeLabel(client, "K", "");
        makeSpinners(client, mAbsMagDist[5]);
        GridUtils.makeLabel(client, "M", "");
        makeSpinners(client, mAbsMagDist[6]);

        ControlUtils.setEnabled(client, !ro);

        load(StarGenLogic.PARAMS);
        return client;
    }

    private void makeSpinners(Composite client, Spinner[] spinners)
    {
        for (int i = 0; i < spinners.length; i++)
            spinners[i] = makeSpinner(client);
    }

    private Spinner makeSpinner(Composite parent)
    {
        Spinner s = new Spinner(parent, SWT.NULL);
        s.setMinimum(-1000);
        s.setMaximum(2000);
        s.setDigits(2);
        GridUtils.setLayoutData(s, "");
        return s;
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
        StarGenLogic.updatedParams();
        load(StarGenLogic.PARAMS);
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
        for (int i = 0; i < mAbsMagDist.length; i++)
            load(mAbsMagDist[i], params.ABS_MAG_FREQ[i]);
    }
    
    private void load(Spinner[] ctrls, double[] values)
    {
        for (int i = 0; i < ctrls.length; i++)
            ctrls[i].setSelection((int)(values[i]*100));
    }

    private void store(StarGenParams params)
    {
        for (int i = 0; i < mAbsMagDist.length; i++)
            store(params.ABS_MAG_FREQ[i], mAbsMagDist[i]);
        StarGenParamLogic.saveToMetadata(params);
    }
    
    private void store(double[] values, Spinner[] ctrls)
    {
        for (int i = 0; i < ctrls.length; i++)
            values[i] = ctrls[i].getSelection()/100.0;
    }
}
