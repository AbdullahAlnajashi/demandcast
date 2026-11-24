package demandcast;

public class LastPeriod {
	
	 public static double[] forecast(double[] actual) {

	        double[] forecast = new double[actual.length];

	        for (int i = 1; i < actual.length; i++) {

	            forecast[i] = actual[i - 1];

	        }

	        return forecast;
	    }

}
