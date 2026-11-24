package demandcast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Plain-Java tests (no JUnit needed). Run: see run-tests.ps1
 */
public class ForecastTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        // --- correctness of the forecasting math ---
        test("LastPeriod: F(t) = A(t-1)", () -> {
            double[] f = LastPeriod.forecast(new double[] {10, 20, 30});
            assertArray(new double[] {0, 10, 20}, f);
        });
        test("MovingAverage: 2-period average", () -> {
            double[] f = MovingAverage.forecast(new double[] {10, 20, 30, 40}, 2);
            assertArray(new double[] {0, 0, 15, 25}, f);
        });
        test("WeightedMovingAverage: weight 1 = most recent period", () -> {
            double[] f = WeightedMovingAverage.forecast(new double[] {100, 110, 120, 130}, 3,
                    new double[] {3, 2, 1}, 6);
            assertEquals((3 * 120 + 2 * 110 + 1 * 100) / 6.0, f[3]);
        });
        test("ExponentialSmoothing: F2 = A1, F(t) = F + a(A - F)", () -> {
            double[] f = ExponentialSmoothing.forecast(new double[] {100, 110, 120, 130}, 0.5);
            assertArray(new double[] {0, 100, 105, 112.5}, f);
        });
        test("Evaluator: returns MSE over periods from start", () -> {
            String out = captureOut(() -> {
                double mse = Evaluator.evaluate("X", new double[] {10, 20, 30}, new double[] {0, 18, 33}, 1);
                assertEquals((4 + 9) / 2.0, mse);
            });
            assertContains(out, "MAD = 2.5");
            assertContains(out, "LAD = 3.0");
        });

        // --- bugs ---
        test("ExponentialSmoothing: single period does not crash", () -> {
            ExponentialSmoothing.forecast(new double[] {5}, 0.5);
        });
        test("Evaluator: MAPE is not Infinity when an actual value is 0", () -> {
            String out = captureOut(() -> Evaluator.evaluate("X", new double[] {5, 0, 4}, new double[] {0, 5, 0}, 1));
            assertNotContains(out, "Infinity");
            assertNotContains(out, "NaN");
        });
        test("Evaluator: MAPE uses |actual| (never negative)", () -> {
            String out = captureOut(() -> Evaluator.evaluate("X", new double[] {0, -10}, new double[] {0, -8}, 1));
            assertContains(out, "MAPE = 20.0%");
        });
        test("Main: weight prompt says weight 1 is the most recent period", () -> {
            String out = runMain("3\n10 20 30\n2\n1 1\n0.5\n");
            assertContains(out, "Weight 1 (most recent period): ");
        });
        test("Main: decimal alpha 0.5 works on Arabic (ar_SA) locale", () -> {
            String out = runMainWithLocale(Locale.of("ar", "SA"), "6\n100 110 120 130 140 150\n3\n3 2 1\n0.5\n");
            assertContains(out, "Best forecasting method is: Last Period");
        });
        test("Main: period >= number of periods is rejected and re-asked", () -> {
            String out = runMain("3\n10 20 30\n5\n2\n1 1\n0.5\n");
            assertNotContains(out, "NaN");
            assertContains(out, "Best forecasting method is:");
        });
        test("Main: period 0 is rejected and re-asked", () -> {
            String out = runMain("3\n10 20 30\n0\n2\n1 1\n0.5\n");
            assertNotContains(out, "NaN");
            assertContains(out, "Best forecasting method is:");
        });
        test("Main: negative period does not crash", () -> {
            String out = runMain("3\n10 20 30\n-1\n2\n1 1\n0.5\n");
            assertContains(out, "Best forecasting method is:");
        });
        test("Main: fewer than 2 periods is rejected and re-asked", () -> {
            String out = runMain("1\n3\n10 20 30\n2\n1 1\n0.5\n");
            assertContains(out, "Best forecasting method is:");
        });
        test("Main: weight 0 is rejected (no division by zero)", () -> {
            String out = runMain("3\n10 20 30\n2\n0\n0\n1\n1\n0.5\n");
            assertNotContains(out, "NaN");
            assertNotContains(out, "Infinity");
            assertContains(out, "Best forecasting method is:");
        });
        test("Main: alpha outside [0,1] is rejected and re-asked", () -> {
            String out = runMain("3\n10 20 30\n2\n1 1\n5\n0.5\n");
            // with alpha 0.5: F2 = 10, F3 = 10 + 0.5 * (20 - 10) = 15
            assertContains(out, "Exponential Smoothing\nPeriod\tActual\tForecast\tError\n3\t30.0\t15.0");
        });
        test("Main: non-numeric input is re-asked instead of crashing", () -> {
            String out = runMain("abc\n3\n10 20 30\n2\n1 1\n0.5\n");
            assertContains(out, "Best forecasting method is:");
        });

        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---------- tiny test framework ----------

    interface Body {
        void run() throws Exception;
    }

    static void test(String name, Body body) {
        try {
            body.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("FAIL  " + name + "\n      -> " + t);
        }
    }

    static void assertEquals(double expected, double actual) {
        if (Math.abs(expected - actual) > 1e-9) {
            throw new AssertionError("expected " + expected + " but was " + actual);
        }
    }

    static void assertArray(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new AssertionError("length " + expected.length + " vs " + actual.length);
        }
        for (int i = 0; i < expected.length; i++) {
            if (Math.abs(expected[i] - actual[i]) > 1e-9) {
                throw new AssertionError("index " + i + ": expected " + expected[i] + " but was " + actual[i]);
            }
        }
    }

    static void assertContains(String text, String part) {
        if (!text.contains(part)) {
            throw new AssertionError("output does not contain \"" + part + "\"\n" + text);
        }
    }

    static void assertNotContains(String text, String part) {
        if (text.contains(part)) {
            throw new AssertionError("output should not contain \"" + part + "\"\n" + text);
        }
    }

    static String captureOut(Body body) throws Exception {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true));
        try {
            body.run();
        } finally {
            System.setOut(oldOut);
        }
        return buffer.toString().replace("\r\n", "\n");
    }

    static String runMain(String stdin) throws Exception {
        return runMainWithLocale(Locale.getDefault(), stdin);
    }

    static String runMainWithLocale(Locale locale, String stdin) throws Exception {
        Locale oldLocale = Locale.getDefault();
        InputStream oldIn = System.in;
        Locale.setDefault(locale);
        System.setIn(new ByteArrayInputStream(stdin.getBytes()));
        try {
            return captureOut(() -> DemandCast.main(new String[0]));
        } finally {
            System.setIn(oldIn);
            Locale.setDefault(oldLocale);
        }
    }
}
