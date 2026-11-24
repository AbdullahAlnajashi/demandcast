package demandcast;

public class MovingAverage {
	public static double[] forecast(double[] actual, int period) {

        double[] forecast = new double[actual.length];

        for (int i = period; i < actual.length; i++) {

            double sum = 0;

            for (int j = i - period; j < i; j++) {

                sum = sum + actual[j];

            }

            forecast[i] = sum / period;

        }

        return forecast;
    }
}
