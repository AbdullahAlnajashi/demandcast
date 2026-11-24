package demandcast;

public class ExponentialSmoothing {
	 public static double[] forecast(double[] actual, double alpha) {

	        double[] forecast = new double[actual.length];

	        if (actual.length > 1) {
	            forecast[1] = actual[0];
	        }

	        for (int i = 2; i < actual.length; i++) {

	            forecast[i] = forecast[i - 1] + alpha * (actual[i - 1] - forecast[i - 1]);

	        }

	        return forecast;
	    }

}
