package constants;

public final class SVMConstants {

    // Bandpass Filter
    public final static double SAMPLE_RATE = 256; // Sample Frequency
//    public final static double SAMPLE_RATE = 100; // Sample Frequency

    public final static double[] preFilterA = {1, -5.15782851817200, 11.5608198955593, -14.9658099132349, 12.4693538123194, -6.90189476483231, 2.44985418058679, -0.502508975737991, 0.0480142849697873};
    public final static double[] preFilterB = {0.0302684886055911, 0, -0.121073954422364, 0, 0.181610931633547, 0, -0.121073954422364, 0, 0.0302684886055911};
    public final static int NUM_EEG_CH = 4;
    public final static int[] kCompMat = {22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 48, 52};
    public final static int[] kCompMat2 = {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 24, 26};
    public final static double nComp = kCompMat.length;



    public final static double WINDOW_LENGTH = 2;
    public final static double overLap = 50; //percentage
    public final static double maFeat = 1; //averaging frames
    public final static double maStep = 1; //shifting frames

    public final static double[] BAND = {1, 4, 8, 12, 18, 30, 45}; //TODO theta is 3-8hz?
    public final static int NUM_BAND = BAND.length - 1;
    public final static int N_START_BAND = 1; //start from theta band - removing delta band which is noisy
    public static final double[][] BPCoe1 =

            {
                    {1, -1.9996288113898675, 1, 1, -1.970678492670487, 0.98124185659790164},
                    {1, -1.9843941083593173, 1, 1, -1.99518449737547, 0.99572722173265393},
                    {1, -1.9997356280506082, 1, 1, -1.9233139676096491, 0.93444264473183458},
                    {1, -1.9781226190200072, 1, 1, -1.9853950121012676, 0.98589542003065278},
                    {1, -1.9999052056533044, 1, 1, -1.9709370314512096, 0.97140652310675601},
                    {1, -1.939574831982845, 1, 1, -1.8522189535287943, 0.86356064390918641},
                    {1, 0, -1, 1, -1.8808566006003127, 0.88312593358244484}
            };
    public static final double[] BPGain1 =
            {
                    0.75701130410533457,
                    0.75701130410533457,
                    0.6722666683349231,
                    0.6722666683349231,
                    0.44613561218524111,
                    0.44613561218524111,
                    0.058437033208777578
            };
    public static final double[][] BPCoe2 =

            {
                    {1, -1.9925674091830181, 1, 1, -1.9405173640379598, 0.97993709410966412},
                    {1, -1.9503203155643714, 1, 1, -1.9809739348262407, 0.99022045660679081},
                    {1, -1.9939969977121625, 1, 1, -1.8913735185704463, 0.93051313671209113},
                    {1, -1.9386490914989534, 1, 1, -1.9573255061168042, 0.96629530554045484},
                    {1, -1.9971645235613529, 1, 1, -1.9155218688526485, 0.92510885715268643},
                    {1, -1.8722035917039273, 1, 1, -1.8260726699095802, 0.86065030956083566},
                    {1, 0, -1, 1, -1.8331156793968122, 0.85094125551483446}
            };
    public static final double[] BPGain2 =
            {
                    0.75603116184194208,
                    0.75603116184194208,
                    0.66891043737961398,
                    0.66891043737961398,
                    0.44256863950801417,
                    0.44256863950801417,
                    0.07452937224258277
            };
    public static final double[][] BPCoe3 =

            {
                    {1, -1.9677522180861722, 1, 1, -1.893065005426708, 0.98135086448067899},
                    {1, -1.8976386571578105, 1, 1, -1.9509493763306323, 0.98784442084305135},
                    {1, -1.9719168829690188, 1, 1, -1.8486231966927784, 0.935485921351915},
                    {1, -1.8827837689414955, 1, 1, -1.9218252384116852, 0.95790145970379692},
                    {1, -1.9833777911107042, 1, 1, -1.7917458441636678, 0.87007831997814478},
                    {1, -1.8053531266809124, 1, 1, -1.8707163960165012, 0.90842482680662517},
                    {1, 0, -1, 1, -1.7933456449569654, 0.84652347611435252}
            };
    public static final double[] BPGain3 =
            {
                    0.75590093580401652,
                    0.75590093580401652,
                    0.66845747134216793,
                    0.66845747134216793,
                    0.44211605294016998,
                    0.44211605294016998,
                    0.076738261942823766
            };
    public static final double[][] BPCoe4 =

            {
                    {1, -1.9278383192708048, 1, 1, -1.7762365358519356, 0.97227366644773561},
                    {1, -1.7721821057831599, 1, 1, -1.8993734083742657, 0.98171555366697361},
                    {1, -1.9371925983081704, 1, 1, -1.7144430198244971, 0.90516699636589426},
                    {1, -1.7398755341806338, 1, 1, -1.8572543639456116, 0.9371547472150028},
                    {1, -1.9629215514647698, 1, 1, -1.642235508869534, 0.81171122004939356},
                    {1, -1.5759355868752298, 1, 1, -1.7825314933666419, 0.86501307825713691},
                    {1, 0, -1, 1, -1.6630284864278941, 0.7776369129223254}
            };
    public static final double[] BPGain4 =
            {
                    0.75402582804427565,
                    0.75402582804427565,
                    0.66167409434966218,
                    0.66167409434966218,
                    0.43628446723718262,
                    0.43628446723718262,
                    0.11118154353883733

            };
    public static final double[][] BPCoe5 =

            {
                    {1, -1.8458324211269148, 1, 1, -1.4206279877600123, 0.94477946962104864},
                    {1, -1.3693434839013543, 1, 1, -1.785070067873364, 0.96577073798199309},
                    {1, -1.8701865519068841, 1, 1, -1.3223981956049711, 0.81727668634824946},
                    {1, -1.2687798066657936, 1, 1, -1.7136073597897052, 0.88490831941987791},
                    {1, -1.9311582640917071, 1, 1, -1.5855781877086952, 0.75861963637927643},
                    {1, -0.8002015368858616, 1, 1, -1.2331591624005218, 0.65145083450075947},
                    {1, 0, -1, 1, -1.3439863902709159, 0.59890361270906944},
            };
    public static final double[] BPGain5 =
            {
                    0.75104943241028055,
                    0.75104943241028055,
                    0.64745311414057083,
                    0.64745311414057083,
                    0.43316048813319791,
                    0.43316048813319791,
                    0.20054819364546525

            };
    public static final double[][] BPCoe6 =

            {
                    {1, -1.5569095112507523, 1, 1, -0.85470335571808409, 0.93899512656841388},
                    {1, -0.75194607529499335, 1, 1, -1.4577041289412254, 0.9543707383446639},
                    {1, -1.6134490290487853, 1, 1, -1.3820906527337939, 0.84824843464600108},
                    {1, -0.61760465965236255, 1, 1, -0.78707014431898736, 0.79983537414684514},
                    {1, -1.7717216016096049, 1, 1, -1.2360844647952356, 0.69169324628564866},
                    {1, -0.068848047065401485, 1, 1, -0.75615386842277976, 0.61811896087399676},
                    {1, 0, -1, 1, -0.94488036424578736, 0.53410426488946972}
            };
    public static final double[] BPGain6 =
            {
                    0.75092381018165899,
                    0.75092381018165899,
                    0.64396818825347213,
                    0.64396818825347213,
                    0.43673144372417028,
                    0.43673144372417028,
                    0.23294786755526517
            };
    public static final double[][][] BPCoe = {BPCoe1, BPCoe2, BPCoe3, BPCoe4, BPCoe5, BPCoe6};
    public static final double[][] BPGain = {BPGain1, BPGain2, BPGain3, BPGain4, BPGain5, BPGain6};
    public static int[] BPNumSec = {7, 7, 7, 7, 7, 7};

}
