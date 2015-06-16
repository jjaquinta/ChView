package jo.d2k.data.data;

import jo.util.beans.Bean;

public class StarGenParams extends Bean
{
    public boolean GENERATE = true;
    public double EXCLUSION_ZONE = 15;
    
    // chances out of 1000 for each spectral type
    public int[] SPECTRUM_FREQ = { 0,    0,   52,   20,   114,  114,  700  };
    public int[][] SPECTRUM_CLASS_FREQ = {
        /*O    1*/ {    0,    0,    0,    0, 1000,    0,    0 },
        /*B   48*/ {    0,   20,  187,  127,  666,    0,    0 },
        /*A  536*/ {    0,    3,  111,  187,  699,    0,    0 },
        /*F 2641*/ {    0,    2,   36,   99,  862,    1,    0 },
        /*G 3246*/ {    0,    1,   45,   77,  876,    1,    0 },
        /*K 1692*/ {    0,    0,  212,   78,  708,    2,    0 },
        /*M  431*/ {    0,    0,   34,    4,  944,    2,   16 },
    };
    public String[] CLASS_NAME = {
        "I", "II", "III", "IV", "V", "VI", "D",
    };
    
    public double[][] ABS_MAG_FREQ = {
        { -5.2, -3.45, -1.7,   0.05, 1.8,   3.55,  5.3,  7.06, 8.8,   },
        { -6.6, -1.4,  -0.6,  -0.0,  0.3,   0.7,   1.1,  2.2,  5.6 },
        { -8.7,  0.8,   1.2,   1.6,  1.8,   2.1,  2.3,   2.7, 10.2 },
        { -5.5,  2.5,   2.9,   3.1,  3.4,   3.6,  3.8,   4.2,  9.6 },
        { -1.5,  3.5,   3.9,   4.2,  4.5,   4.7,  5.0,   5.3, 11.2 },
        { -4.1,  1.7,   4.7,   5.6,  6.0,   6.4,  6.9,   7.6, 13.9 },
        { -5.2,  7.7,   8.5,   9.4, 10.4,  11.2, 12.0,  13.0, 19.1 },        
    };
    
    public int[][] SECONDARY_NUM_FREQ = {
        { 1000,    0,    0 },
        { 1000,    0,    0 },
        { 1000,    0,    0 },
        { 1000,    0,    0 },
        {  625,  375,    0 },
        {  619,  285,   96 },
        {  909,   90,    1 },    
        /*O (    0) :*/ { 1000,    0,    0 },
        /*B (    0) :*/ { 1000,    0,    0 },
        /*A (    6) :*/ { 1000,    0,    0 },
        /*F (    2) :*/ { 1000,    0,    0 },
        /*G (   13) :*/ {  692,  232,   76 },
        /*K (   20) :*/ {  800,   50,  150 },
        /*M (  105) :*/ {  839,  114,   47 },
    };
    
    public int[][] SECONDARY_TYPE_FREQ = {
        { 0,    0,    0,    0,    0,    0,    0 },
        { 0,    0,    0,    0,    0,    0,    0 },
        { 0,    0,    0,    0,    0,    0,    0 },
        { 0,    0,    0,    0,    0,    0,    0 },
        { 0,    0,    0,    0,  388,  387,  225 },
        { 0,    0,    0,    0,    0,  650,  350 },
        { 0,    0,    0,    0,    0,    0, 1000 },     
    };
    
    public double[] SECONDARY_DISTANCE_FREQ = {
        0.001, .005, 0.037, 0.082, 0.180, 0.225,    
    };

    public String[] GREEK_NAMES = {
        "Alpha",
        "Beta",
        "Gamma",
        "Delta",
        "Epsilon",
        "Zeta",
        "Eta",
        "Theta",
        "Iota",
        "Kappa",
        "Lambda",
        "Mu",
        "Nu",
        "Omicron",
    };

    public String PREFIX = "UN "; 
}
