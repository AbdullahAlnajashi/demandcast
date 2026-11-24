package demandcast;
import java.util.Locale;
import java.util.Scanner;
public class DemandCast {

	public static void main(String[] args) {

		// Locale.US so decimals like 0.5 are accepted on any system language (e.g. Arabic)
		Scanner input = new Scanner(System.in).useLocale(Locale.US);

        int n = readInt(input, "Enter number of periods: ", 2, Integer.MAX_VALUE);

        double[] actual = new double[n];

        System.out.println("Enter actual values:");

        for (int i = 0; i < n; i++) {
            actual[i] = readDouble(input, "Period " + (i + 1) + ": ", -Double.MAX_VALUE, Double.MAX_VALUE);
        }

        int period = readInt(input, "Enter moving average period: ", 1, n - 1);

        double[] weights = new double[period];
        double totalWeight = 0;

        System.out.println("Enter weights:");

        for (int i = 0; i < period; i++) {
            String label = (i == 0) ? "Weight 1 (most recent period): " : "Weight " + (i + 1) + ": ";
            weights[i] = readDouble(input, label, Double.MIN_VALUE, Double.MAX_VALUE);
            totalWeight += weights[i];
        }

        double alpha = readDouble(input, "Enter alpha: ", 0, 1);

        int start = period;

        double[] lp = LastPeriod.forecast(actual);

        double[] ma = MovingAverage.forecast(actual, period);

        double[] wma = WeightedMovingAverage.forecast(actual, period, weights, totalWeight);

        double[] es = ExponentialSmoothing.forecast(actual, alpha);

        double mse1 = Evaluator.evaluate("Last Period", actual, lp, start);

        double mse2 = Evaluator.evaluate("Moving Average", actual, ma, start);

        double mse3 = Evaluator.evaluate("Weighted Moving Average", actual, wma, start);

        double mse4 = Evaluator.evaluate("Exponential Smoothing", actual, es, start);


        double best = mse1;
        String bestMethod = "Last Period";

        if (mse2 < best) {
            best = mse2;
            bestMethod = "Moving Average";
        }

        if (mse3 < best) {
            best = mse3;
            bestMethod = "Weighted Moving Average";
        }

        if (mse4 < best) {
            best = mse4;
            bestMethod = "Exponential Smoothing";
        }

        System.out.println();
        System.out.println("Best forecasting method is: " + bestMethod);



	}

	// Asks again until the user types a whole number in [min, max].
	private static int readInt(Scanner input, String prompt, int min, int max) {
		while (true) {
			System.out.print(prompt);
			if (input.hasNextInt()) {
				int value = input.nextInt();
				if (value >= min && value <= max) {
					return value;
				}
			} else {
				input.next();
			}
			System.out.println(max == Integer.MAX_VALUE
					? "Invalid input. Enter a whole number of " + min + " or more."
					: "Invalid input. Enter a whole number from " + min + " to " + max + ".");
		}
	}

	// Asks again until the user types a number in [min, max] (also rejects NaN/Infinity).
	private static double readDouble(Scanner input, String prompt, double min, double max) {
		while (true) {
			System.out.print(prompt);
			if (input.hasNextDouble()) {
				double value = input.nextDouble();
				if (value >= min && value <= max) {
					return value;
				}
			} else {
				input.next();
			}
			if (min == Double.MIN_VALUE) {
				System.out.println("Invalid input. Enter a number greater than 0.");
			} else if (min == -Double.MAX_VALUE) {
				System.out.println("Invalid input. Enter a number.");
			} else {
				System.out.println("Invalid input. Enter a number from " + min + " to " + max + ".");
			}
		}
	}

}
