package jo.d2k.admin.rcp.sys.ui.schema;

import java.util.Map;
import java.util.StringTokenizer;

import jo.d2k.data.data.StarSchemaBean;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.ArrayUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class TagsSchemaController implements ISchemaController
{

    @Override
    public Control addUI(Composite parent, StarSchemaBean schema,
            boolean readOnly)
    {
        GridUtils.makeLabel(parent, schema.getTitle()+":", "1x2");
        if (readOnly)
        {
            Text ctrl = GridUtils.makeText(parent, SWT.READ_ONLY, "6x2 fill=h");
            return ctrl;
        }
        else
        {
            List ctrl = GridUtils.makeList(parent, SWT.MULTI, "5x2 fill=h");
            Button add = GridUtils.makeButton(parent, "+", "");
            add.setData("list", ctrl);
            add.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    doTagAdd((List)e.widget.getData("list"));
                }
            });
            Button del = GridUtils.makeButton(parent, "-", "");
            del.setData("list", ctrl);
            del.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    doTagDel((List)e.widget.getData("list"));
                }
            });
            return ctrl;
        }
    }

    @Override
    public void loadFromMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        String val = metadata.get(StarSchemaUIController.schemaToID(schema));
        if (val == null)
            return;
        if (ctrl instanceof List)
        {
            List l = (List)ctrl;
            l.removeAll();
            if (val != null)
                for (StringTokenizer st = new StringTokenizer(val, " "); st.hasMoreTokens(); )
                    l.add(st.nextToken());
        }
        else
        {
            StringBuffer txt = new StringBuffer();
            for (StringTokenizer st = new StringTokenizer(val, " "); st.hasMoreTokens(); )
            {
                if (txt.length() > 0)
                    txt.append(" ");
                txt.append(st.nextToken());
            }
            ControlUtils.setText(ctrl, txt.toString());
        }
    }

    @Override
    public void storeToMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        if (ctrl instanceof List)
        {
            List l = (List)ctrl;
            StringBuffer txt = new StringBuffer();
            for (int i = 0; i < l.getItemCount(); i++)
            {
                if (txt.length() > 0)
                    txt.append(" ");
                txt.append(l.getItem(i));
            }
            metadata.put(StarSchemaUIController.schemaToID(schema), txt.toString());
        }
        else
        {
            metadata.put(StarSchemaUIController.schemaToID(schema), ControlUtils.getText(ctrl));
        }
    }

    @Override
    public String getName()
    {
        return "Tags";
    }

    @Override
    public void addDefUI(Composite parent)
    {
    }
    
    private void doTagAdd(List tags)
    {
        InputDialog dlg = new InputDialog(tags.getShell(), "Add Tag", "Enter text for this tag", "", null);
        if (dlg.open() != Dialog.OK)
            return;
        String vals = dlg.getValue();
        if (StringUtils.isTrivial(vals))
            return;
        for (StringTokenizer st = new StringTokenizer(vals, " "); st.hasMoreTokens(); )
        {
            String val = st.nextToken();
            if (ArrayUtils.indexOf(tags.getItems(), val) < 0)
                tags.add(val);
        }
    }
    
    private void doTagDel(List tags)
    {
        int[] sel = tags.getSelectionIndices();
        if ((sel == null) || (sel.length == 0))
            return;
        tags.remove(sel);
        if (tags.getItemCount() > 0)
            if (sel[0] < tags.getItemCount())
                tags.select(sel);
            else
                tags.select(sel[0]-1);
    }

    @Override
    public void loadFromDefPanel(StarSchemaBean schema, Composite parent)
    {
        schema.setSubType(new String[0]);
    }

    @Override
    public void storeToDefPanel(StarSchemaBean schema, Composite parent)
    {
    }
}
