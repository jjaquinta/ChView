package jo.d2k.admin.rcp.viz.chview.actions;

import java.util.List;

import jo.d2k.admin.rcp.viz.chview.ChViewThemeBean;
import jo.d2k.admin.rcp.viz.chview.DlgEditThemes;
import jo.d2k.admin.rcp.viz.chview.logic.ChViewThemeLogic;
import jo.d2k.data.logic.RuntimeLogic;
import jo.util.ui.utils.ImageUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

public class ActionThemes extends Action 
{
    public ActionThemes()
    {
        super("Theme");
        setToolTipText("Set visual theme");
    }
    
    @Override
    public void runWithEvent(Event event)
    {
        Control parent = ((ToolItem)event.widget).getParent();        
        Menu m = getMenu(parent);
        m.setVisible(true);
    }

    public Menu getMenu(Control parent)
    {
        Menu menu = new Menu(parent);
        List<ChViewThemeBean> themes = ChViewThemeLogic.getThemes();
        for (ChViewThemeBean theme : themes)
        {
            Action a = new ActionUseTheme(theme);
            ActionContributionItem i = new ActionContributionItem(a);
            i.fill(menu, -1);
        }
        if (menu.getItemCount() > 0)
            new MenuItem(menu, SWT.SEPARATOR);
        (new ActionContributionItem(new ActionCopyTheme())).fill(menu, -1);
        (new ActionContributionItem(new ActionPasteTheme())).fill(menu, -1);
        (new ActionContributionItem(new ActionEditThemes())).fill(menu, -1);
        return menu;
    }
    
    class ActionUseTheme extends Action
    {
        private ChViewThemeBean mTheme;
        
        public ActionUseTheme(ChViewThemeBean theme)
        {
            mTheme = theme;
            setText(theme.getName());
        }
        
        @Override
        public void run()
        {
            ChViewThemeLogic.applyTheme(mTheme);
        }
    }
    
    class ActionCopyTheme extends Action
    {
        public ActionCopyTheme()
        {
            setText("Copy Theme");
            setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_copy"));
        }
        
        @Override
        public void run()
        {
            ChViewThemeLogic.copyTheme();
        }
    }
    
    class ActionPasteTheme extends Action
    {
        public ActionPasteTheme()
        {
            setText("Paste Theme");
            setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_paste"));
        }
        
        @Override
        public void run()
        {
            ChViewThemeLogic.pasteTheme();
        }
    }
    
    class ActionEditThemes extends Action
    {
        public ActionEditThemes()
        {
            setText("Edit Themes");
            setImageDescriptor(ImageUtils.getMappedImageDescriptor("tb_edit"));
            setEnabled(!RuntimeLogic.getInstance().getDataSource().isReadOnly());
        }
        
        @Override
        public void run()
        {
            DlgEditThemes dlg = new DlgEditThemes(Display.getDefault().getActiveShell());
            dlg.open();
        }
    }
}
