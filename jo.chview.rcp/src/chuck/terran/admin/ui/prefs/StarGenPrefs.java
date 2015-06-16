package chuck.terran.admin.ui.prefs;

import jo.d2k.data.data.StarGenParams;
import jo.d2k.data.logic.RuntimeLogic;
import jo.d2k.data.logic.StarGenLogic;
import jo.d2k.data.logic.StarGenParamLogic;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.DoubleUtils;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class StarGenPrefs extends PreferencePage implements IWorkbenchPreferencePage
{
    private static final StarGenParams mDefaults = new StarGenParams();
    
    private Text mExclusionZone;
    private Spinner[] mSpectrumFrequency = new Spinner[7];
    private Spinner[][] mSpectrumClassFrequency = new Spinner[7][7];

    @Override
    protected Control createContents(Composite parent)
    {
        boolean ro = RuntimeLogic.getInstance().getDataSource().isReadOnly();
        Composite client = new Composite(parent, SWT.NULL);
        client.setLayout(new GridLayout(8, true));
        if (ro)
            GridUtils.makeLabel(client, "Settings may not be changed in read-only database.", "8x1 fill=h");
        Composite exBar = new Composite(client, SWT.NULL);
        GridUtils.setLayoutData(exBar, "8x1 fill=h");
        exBar.setLayout(new GridLayout(2, false));
        GridUtils.makeLabel(exBar, "Exclusion Zone:", "");
        mExclusionZone = GridUtils.makeText(exBar, SWT.NULL, "fill=h");
        mExclusionZone.setToolTipText("Radius in light years not to generate stars for");
        
        GridUtils.makeLabel(client, "Spectrum Frequency Distribution:", "8x1");
        addSpectraLabels(client);
        GridUtils.makeLabel(client, "", "");
        makeSpinners(client, mSpectrumFrequency);

        GridUtils.makeLabel(client, "Spectrum Class Distribution:", "8x1");
        addSpectraLabels(client);
        GridUtils.makeLabel(client, "I", "");
        makeSpinners(client, mSpectrumClassFrequency[0]);
        GridUtils.makeLabel(client, "II", "");
        makeSpinners(client, mSpectrumClassFrequency[1]);
        GridUtils.makeLabel(client, "III", "");
        makeSpinners(client, mSpectrumClassFrequency[2]);
        GridUtils.makeLabel(client, "IV", "");
        makeSpinners(client, mSpectrumClassFrequency[3]);
        GridUtils.makeLabel(client, "V", "");
        makeSpinners(client, mSpectrumClassFrequency[4]);
        GridUtils.makeLabel(client, "VI", "");
        makeSpinners(client, mSpectrumClassFrequency[5]);
        GridUtils.makeLabel(client, "D", "");
        makeSpinners(client, mSpectrumClassFrequency[6]);

        ControlUtils.setEnabled(client, !ro);

        load(StarGenLogic.PARAMS);
        return client;
    }

    private void makeSpinners(Composite client, Spinner[] spinners)
    {
        for (int i = 0; i < spinners.length; i++)
            spinners[i] = makeSpinner(client);
    }

    private void addSpectraLabels(Composite client)
    {
        GridUtils.makeLabel(client, "", "");
        GridUtils.makeLabel(client, "O", "");
        GridUtils.makeLabel(client, "B", "");
        GridUtils.makeLabel(client, "A", "");
        GridUtils.makeLabel(client, "F", "");
        GridUtils.makeLabel(client, "G", "");
        GridUtils.makeLabel(client, "K", "");
        GridUtils.makeLabel(client, "M", "");
    }

    private Spinner makeSpinner(Composite parent)
    {
        Spinner s = new Spinner(parent, SWT.NULL);
        s.setMinimum(0);
        s.setMaximum(1000);
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
        mExclusionZone.setText(String.valueOf(params.EXCLUSION_ZONE));
        load(mSpectrumFrequency, params.SPECTRUM_FREQ);
        for (int i = 0; i < mSpectrumClassFrequency.length; i++)
            load(mSpectrumClassFrequency[i], params.SPECTRUM_CLASS_FREQ[i]);
    }
    
    private void load(Spinner[] ctrls, int[] values)
    {
        for (int i = 0; i < ctrls.length; i++)
            ctrls[i].setSelection(values[i]);
    }

    private void store(StarGenParams params)
    {
        params.EXCLUSION_ZONE = DoubleUtils.parseDouble(mExclusionZone.getText());
        store(params.SPECTRUM_FREQ, mSpectrumFrequency, 1000);
        for (int i = 0; i < mSpectrumClassFrequency.length; i++)
            store(params.SPECTRUM_CLASS_FREQ[i], mSpectrumClassFrequency[i], 1000);
        StarGenParamLogic.saveToMetadata(params);
    }
    
    private void store(int[] values, Spinner[] ctrls, int norm)
    {
        int tot = 0;
        for (int i = 0; i < ctrls.length; i++)
        {
            values[i] = ctrls[i].getSelection();
            tot += values[i];
        }
        if ((norm > 0) && (tot != norm))
        {
            int newtot = 0;
            for (int i = 0; i < values.length; i++)
            {
                double pc = (double)values[i]/(double)tot;
                values[i] = (int)(norm*pc);
                newtot += values[i];
            }
            int delta = newtot - norm;
            values[0] += delta;
        }
    }
}
