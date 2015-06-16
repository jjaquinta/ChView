package jo.d2k.admin.rcp.viz.chview;

import jo.d2k.admin.rcp.viz.chview.logic.ChViewThemeLogic;
import jo.util.ui.dlg.GenericDialog;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class DlgEditThemes extends GenericDialog
{
    private static final int ADD = 0x1001;
    private static final int DEL = 0x1002;
    
    private List    mThemes;
    
    public DlgEditThemes(Shell parentShell)
    {
        super(parentShell);
    }
    
    protected Control createDialogArea(Composite parent)
    {
        getShell().setText("Star Filter");
        GridUtils.makeLabel(parent, "Visual Themes:", "");
        mThemes = new List(parent, SWT.NULL);
        GridUtils.setLayoutData(mThemes, "fill=hv");
        fillThemes();
        return mThemes;
    }
    
    private void fillThemes()
    {
        mThemes.removeAll();
        for (ChViewThemeBean theme : ChViewThemeLogic.getThemes())
            mThemes.add(theme.getName());
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, ADD, "Add", true);
        createButton(parent, DEL, "Remove", true);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
    }
    
    @Override
    protected void buttonPressed(int buttonId)
    {
        if (buttonId == ADD)
            addPressed();
        else if (buttonId == DEL)
            delPressed();
        else
            super.buttonPressed(buttonId);
    }
    
    private void addPressed()
    {
        InputDialog dlg = new InputDialog(getShell(), "New Theme", "Current visual settings will be saved a theme with this name", "Theme "+(mThemes.getItemCount()+1), null);
        if (dlg.open() != InputDialog.OK)
            return;
        String name = dlg.getValue();
        if (StringUtils.isTrivial(name))
            return;
        ChViewThemeLogic.makeTheme(name);
        fillThemes();
    }
    
    private void delPressed()
    {
        int idx = mThemes.getSelectionIndex();
        if (idx < 0)
            return;
        String name = mThemes.getItem(idx);
        ChViewThemeLogic.deleteTheme(name);
        fillThemes();
    }
}
