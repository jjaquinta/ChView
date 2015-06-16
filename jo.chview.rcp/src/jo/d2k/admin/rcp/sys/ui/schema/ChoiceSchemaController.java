package jo.d2k.admin.rcp.sys.ui.schema;

import java.util.ArrayList;
import java.util.Map;

import jo.d2k.data.data.StarSchemaBean;
import jo.util.ui.utils.ControlUtils;
import jo.util.ui.utils.GridUtils;
import jo.util.utils.obj.StringUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class ChoiceSchemaController implements ISchemaController
{

    @Override
    public Control addUI(Composite parent, StarSchemaBean schema,
            boolean readOnly)
    {
        GridUtils.makeLabel(parent, schema.getTitle()+":", "");
        if (readOnly)
        {
            Text ctrl = GridUtils.makeText(parent, SWT.READ_ONLY, "6x1 fill=h");
            return ctrl;
        }
        else
        {
            String[] choices = schema.getSubType();
            Combo ctrl = GridUtils.makeCombo(parent, choices, "6x1 fill=h");
            ctrl.setData("choices", choices);
            return ctrl;
        }
    }

    @Override
    public void loadFromMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        String val = metadata.get(StarSchemaUIController.schemaToID(schema));
        if (ctrl instanceof Combo)
        {
            String[] choices = (String[])ctrl.getData("choices");
            for (int i = 0; i < choices.length; i++)
                if (choices[i].equals(val))
                {
                    ((Combo)ctrl).select(i);
                    return;
                }
            ((Combo)ctrl).select(-1);
        }
        else
            ControlUtils.setText(ctrl, val);
    }

    @Override
    public void storeToMetadata(Control ctrl, StarSchemaBean schema,
            Map<String, String> metadata)
    {
        if (ctrl instanceof Combo)
        {
            int idx = ((Combo)ctrl).getSelectionIndex();
            if (idx < 0)
            {
                metadata.remove(StarSchemaUIController.schemaToID(schema));
                return;
            }
            String[] choices = (String[])ctrl.getData("choices");
            metadata.put(StarSchemaUIController.schemaToID(schema), choices[idx]);
        }
        else
        {
            metadata.put(StarSchemaUIController.schemaToID(schema), ControlUtils.getText(ctrl));
        }
    }

    @Override
    public String getName()
    {
        return "Choice";
    }

    @Override
    public void addDefUI(Composite parent)
    {
        parent.setLayout(new GridLayout(2, false));
        List choiceChoices = GridUtils.makeList(parent, new String[0], "1x3 fill=hv");
        Button mChoiceAdd = GridUtils.makeButton(parent, "Add", "");
        mChoiceAdd.setData("list", choiceChoices);
        Button mChoiceRemove = GridUtils.makeButton(parent, "Del", "");
        mChoiceRemove.setData("list", choiceChoices);
        GridUtils.makeLabel(parent, "", "fill=v");
        mChoiceAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doChoiceAdd((List)e.widget.getData("list"));
            }
        });
        mChoiceRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doChoiceDel((List)e.widget.getData("list"));
            }
        });
        parent.setData("choiceChoices", choiceChoices);
    }
    
    private void doChoiceAdd(List choiceChoices)
    {
        InputDialog dlg = new InputDialog(choiceChoices.getShell(), "Add Choice", "Enter text for this choice", "", null);
        if (dlg.open() != Dialog.OK)
            return;
        String choice = dlg.getValue();
        if (StringUtils.isTrivial(choice))
            return;
        choiceChoices.add(choice);
    }
    
    private void doChoiceDel(List choiceChoices)
    {
        int sel = choiceChoices.getSelectionIndex();
        if (sel < 0)
            return;
        choiceChoices.remove(sel);
        if (choiceChoices.getItemCount() > 0)
            if (sel < choiceChoices.getItemCount())
                choiceChoices.select(sel);
            else
                choiceChoices.select(sel-1);
    }

    @Override
    public void loadFromDefPanel(StarSchemaBean schema, Composite parent)
    {
        List choiceChoices = (List)parent.getData("choiceChoices");
        java.util.List<String> choices = new ArrayList<String>();
        for (int i = 0; i < choiceChoices.getItemCount(); i++)
            choices.add(choiceChoices.getItem(i));
        schema.setSubType(choices.toArray(new String[0]));
    }

    @Override
    public void storeToDefPanel(StarSchemaBean schema, Composite parent)
    {
        List choiceChoices = (List)parent.getData("choiceChoices");
        for (String choice : schema.getSubType())
            choiceChoices.add(choice);
    }
}
