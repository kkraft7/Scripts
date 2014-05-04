
import java.lang.Math;

class TestJavaLog {

    public static double logWithBase(double number, double base) {
        return Math.log(number)/Math.log(base);
    }

    public static void main(String[] args) {
        int base = 1, power = 4;
        if ( args.length == 0 ) {
            System.out.println("Must enter base for logWithBase()");
            System.exit(0);
        }
        try {
            base = Integer.parseInt(args[0]);
            if ( args.length > 1 )
                power = Integer.parseInt(args[1]);
        }
        catch(Exception e) { }

        if ( base < 2 ) {
            System.out.println("logWithBase(): Illegal base: " + base);
            System.exit(0);
        }
        int nMax = (int)Math.pow(base, power);
        System.out.format("BASE : %d\nPOWER: %d\nN-MAX: %d\n",
            base, power, nMax);
        for ( int n = 1; n < nMax; n++ )
            System.out.format("%03d LOG BASE %d == %f\n", n, base,
                logWithBase(n, base));
    }
}

