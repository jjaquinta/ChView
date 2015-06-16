package jo.d2k.admin.rcp.sys.ui;

import java.util.ArrayList;

import jo.chview.rcp.logic.StarSchemaControllerLogic;
import jo.d2k.admin.rcp.sys.ui.schema.ISchemaController;
import jo.d2k.data.data.StarSchemaBean;
import jo.util.ui.utils.GridUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class StarSchemaPanel extends Composite
{
    private StarSchemaBean  mSchema;
    
    private Text            mName;
    private Text            mWidth;
    private Combo           mSortBy;
    private Combo           mType;
    private Composite       mSubType;

    private java.util.List<Group>     mSubTypePanels;

    public StarSchemaPanel(Composite parent, int style)
    {
        super(parent, style);
        setLayout(new GridLayout(2, false));
        GridUtils.makeLabel(this, "Title:", "");
        mName = GridUtils.makeText(this, SWT.NULL, "fill=h");
        GridUtils.makeLabel(this, "Display Width:", "");
        mWidth = GridUtils.makeText(this, SWT.NULL, "fill=h");
        GridUtils.makeLabel(this, "Sort By:", "");
        mSortBy = GridUtils.makeCombo(this, new String[] { "Alphabetic", "Numeric", "Alphabetic (case insensitive)" }, "fill=h");
        GridUtils.makeLabel(this, "Type:", "");
        mType = GridUtils.makeCombo(this, StarSchemaControllerLogic.getSchemaTypeLabels(), "fill=h");
        mSubType = new Composite(this, SWT.NULL);
        GridUtils.setLayoutData(mSubType, "2x1 fill=hv");
        mSubType.setLayout(new StackLayout());

        mSubTypePanels = new ArrayList<Group>();
        for (Integer i : StarSchemaControllerLogic.getSchemaTypes())
        {
            ISchemaController ctrl = StarSchemaControllerLogic.getController(i);
            Group panel = new Group(mSubType, SWT.NULL);
            panel.setText(ctrl.getName()+" Options");
            ctrl.addDefUI(panel);
            mSubTypePanels.add(panel);
        }

        mType.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateSubType();
            }
        });        
    }

    public void updateSubType()
    {
        ((StackLayout)mSubType.getLayout()).topControl = mSubTypePanels.get(mType.getSelectionIndex());
        mSubType.layout();
    }

    public StarSchemaBean getSchema()
    {
        mSchema.setTitle(mName.getText());
        mSchema.setWidth(Integer.parseInt(mWidth.getText()));
        mSchema.setSortBy(mSortBy.getSelectionIndex());
        mSchema.setType(mType.getSelectionIndex());
        ISchemaController ctrl = StarSchemaControllerLogic.getController(mSchema.getType());
        ctrl.loadFromDefPanel(mSchema, mSubTypePanels.get(mSchema.getType()));
        return mSchema;
    }

    public void setSchema(StarSchemaBean schema)
    {
        mSchema = schema;
        mName.setText(mSchema.getTitle());
        mWidth.setText(String.valueOf(mSchema.getWidth()));
        mSortBy.select(mSchema.getSortBy());
        mType.select(mSchema.getType());
        ISchemaController ctrl = StarSchemaControllerLogic.getController(mSchema.getType());
        ctrl.storeToDefPanel(mSchema, mSubTypePanels.get(mSchema.getType()));
        updateSubType();
    }

    public Text getTitle()
    {
        return mName;
    }
}
