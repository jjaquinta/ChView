package jo.d2k.data.logic.stargen.data;

public class Verbose
{
    public boolean x0001; // Earthlike count
    public boolean x0002; // Trace Min/max
    public boolean x0004; // List habitable
    public boolean x0008; // List Earth-like (and Sphinx-line)

    public boolean x0010; // List Gases
    public boolean x0020; // Trace temp iterations
    public boolean x0040; // Gas lifetimes
    public boolean x0080; // List loss of accreted gas mass

    public boolean x0100; // Injecting, collision
    public boolean x0200; // Checking..., Failed...
    public boolean x0400; // List binary info
    public boolean x0800; // List Gas Dwarfs etc.

    public boolean x1000; // Moons
    public boolean x2000; // Oxygen poisoned
    public boolean x4000; // Trace gas %ages (whoops)
    public boolean x8000; // Jovians in habitable zone

    public boolean x10000; // List type diversity
    public boolean x20000; // Trace Surface temp interations
    public boolean x40000; // Lunar orbits
    
    public Verbose()
    {
        //x0100 = true;
    }
    
    public void parse(String substring)
    {
        // TODO Auto-generated method stub
    }
}
