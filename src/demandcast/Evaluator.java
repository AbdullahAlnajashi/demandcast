package demandcast;

public class Evaluator {
	public static double evaluate(String name, double[] actual, double[] forecast, int start) {

        double mse = 0;
        double mad = 0;
        double mape = 0;
        double lad = 0;
        boolean mapeDefined = true;

        int count = 0;

        System.out.println();
        System.out.println(name);

        System.out.println("Period\tActual\tForecast\tError");

        for (int i = start; i < actual.length; i++) {

            double error = actual[i] - forecast[i];

            double abs = Math.abs(error);

            mse = mse + (error * error);

            mad = mad + abs;

            if (actual[i] == 0) {
                mapeDefined = false; // MAPE divides by actual; undefined when actual is 0
            } else {
                mape = mape + (abs / Math.abs(actual[i]));
            }

            if (abs > lad) {

                lad = abs;

            }

            count++;

            System.out.println((i + 1) + "\t" + actual[i] + "\t" + forecast[i] + "\t\t" + error);

        }

        mse = mse / count;

        mad = mad / count;

        mape = (mape / count) * 100; // as a percentage

        System.out.println("MSE = " + mse);

        System.out.println("MAD = " + mad);

        if (mapeDefined) {
            System.out.println("MAPE = " + mape + "%");
        } else {
            System.out.println("MAPE = undefined (an actual value is 0)");
        }

        System.out.println("LAD = " + lad);
        
        return mse;

    }

}
