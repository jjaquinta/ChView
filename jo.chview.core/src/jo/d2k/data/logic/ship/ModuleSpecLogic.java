package jo.d2k.data.logic.ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jo.d2k.data.io.IOModuleSpecHandler;
import jo.d2k.data.logic.ApplicationLogic;
import jo.d2k.data.ship.ModuleBean;
import jo.d2k.data.ship.ModuleSpec;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.LongUtils;
import jo.util.utils.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ModuleSpecLogic
{
    private static boolean mInit = false;
    
    private static IOModuleSpecHandler getHandler()
    {
        IOModuleSpecHandler handler = (IOModuleSpecHandler)ApplicationLogic.getHandler(ApplicationLogic.MODULE_SPEC_HANDLER);
        if (!mInit)
        {
            Document doc = XMLUtils.readStream(ResourceUtils.loadSystemResourceStream("modules.xml", ModuleSpecLogic.class));
            for (Node s : XMLUtils.findNodes(doc, "modules/module"))
            {
                ModuleSpec spec = handler.newInstance();
                readBasicProperties(s, spec);
                readUnmappedProperties(s, spec);
                readMappedProperties(s, spec);
                handler.update(spec);
                System.out.println("Loaded "+spec+" as "+spec.getOID());
            }
            mInit = true;
        }
        return handler;
    }

    private static void readBasicProperties(Node s, ModuleSpec spec)
    {
        spec.setType(IntegerUtils.parseInt(XMLUtils.getAttribute(s, "type")));
        spec.setName(XMLUtils.getAttribute(s, "name"));
        spec.setGroup(XMLUtils.getAttribute(s, "group"));
        spec.setCost(LongUtils.parseLong(XMLUtils.getAttribute(s, "cost")));
        spec.setTier(IntegerUtils.parseInt(XMLUtils.getAttribute(s, "tier")));
        String placement = XMLUtils.getAttribute(s, "placement");
        if ("cold".equals(placement))
            spec.setPlacement(ModuleSpec.COLD);
        else if ("warm".equals(placement))
            spec.setPlacement(ModuleSpec.WARM);
        else
            spec.setPlacement(IntegerUtils.parseInt(placement));
    }

    private static void readUnmappedProperties(Node s, ModuleSpec spec)
    {
        for (int i = 0; ; i++)
        {
            String lParam = XMLUtils.getAttribute(s, "lparam"+i, null);
            if (lParam != null)
                spec.setLParam(i, LongUtils.parseLong(lParam));
            else
                break;
        }
        for (int i = 0; ; i++)
        {
            String dParam = XMLUtils.getAttribute(s, "dparam"+i, null);
            if (dParam != null)
                spec.setDParam(i, DoubleUtils.parseDouble(dParam));
            else
                break;
        }
    }

    private static void readMappedProperties(Node s, ModuleSpec spec)
    {
        Map<String,Integer> lParams = ModuleSpec.LONG_TAG_MAP.get(spec.getGroup());
        if (lParams != null)
            for (String key : lParams.keySet())
            {
                String sVal = XMLUtils.getAttribute(s, key);
                if (sVal != null)
                {
                    long lVal;
                    if ((ModuleSpec.LONG_VALUE_MAP.get(spec.getGroup()) != null)
                            && (ModuleSpec.LONG_VALUE_MAP.get(spec.getGroup()).get(key) != null)
                                    && (ModuleSpec.LONG_VALUE_MAP.get(spec.getGroup()).get(key).get(sVal) != null))
                                lVal = ModuleSpec.LONG_VALUE_MAP.get(spec.getGroup()).get(key).get(sVal);
                    else
                        lVal = LongUtils.parseLong(sVal);
                    spec.setLParam(lParams.get(key), lVal);
                }
            }
        Map<String,Integer> dParams = ModuleSpec.DOUBLE_TAG_MAP.get(spec.getGroup());
        if (dParams != null)
            for (String key : dParams.keySet())
            {
                String sVal = XMLUtils.getAttribute(s, key, null);
                if (sVal != null)
                {
                    double dVal = DoubleUtils.parseDouble(sVal);
                    spec.setDParam(dParams.get(key), dVal);
                }
            }
    }
    
    public static List<ModuleSpec> getSpecs()
    {
        return getHandler().findAll();
    }
    
    public static ModuleSpec getSpec(ModuleBean module)
    {
        return getSpec(module.getType());
    }
    
    public static ModuleSpec getSpec(int type)
    {
        return getHandler().find("Type", type);
    }

    public static List<ModuleSpec> getSpecsWithinTier(int tier)
    {
        List<ModuleSpec> specs = new ArrayList<ModuleSpec>();
        for (ModuleSpec spec : getHandler().findAll())
            if (spec.getTier() <= tier)
                specs.add(spec);
        return specs;
    }
}
