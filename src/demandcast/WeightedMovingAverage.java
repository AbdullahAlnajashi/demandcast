package demandcast;

public class WeightedMovingAverage {
	 public static double[] forecast(double[] actual, int period, double[] weights, double totalWeight) {

	        double[] forecast = new double[actual.length];

	        for (int i = period; i < actual.length; i++) {

	            double sum = 0;

	            int k = 0;

	            for (int j = i - 1; j >= i - period; j--) {

	                sum = sum + (actual[j] * weights[k]);

	                k++;

	            }

	            forecast[i] = sum / totalWeight;

	        }

	        return forecast;
	    }

}
