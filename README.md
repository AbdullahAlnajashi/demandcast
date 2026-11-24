# DemandCast

A Java console application that forecasts demand from past data with four classic methods, measures how accurate each one is, and picks the best one.

## Forecasting methods

| Method | Forecast for period *t* |
|---|---|
| Last Period (Naive) | `F(t) = A(t-1)` |
| Moving Average | average of the last *n* actual values |
| Weighted Moving Average | weighted average of the last *n* values (weight 1 = most recent period) |
| Exponential Smoothing | `F(t) = F(t-1) + α · (A(t-1) − F(t-1))`, with `F(2) = A(1)` |

## Accuracy measures

For each method the program prints a period-by-period table (actual, forecast, error) and:

- **MSE**: Mean Squared Error, used to choose the best method
- **MAD**: Mean Absolute Deviation
- **MAPE**: Mean Absolute Percentage Error (shown as "undefined" if an actual value is 0)
- **LAD**: Largest Absolute Deviation

All methods are compared over the same periods, starting after the moving-average window, so the comparison is fair.

## Input validation

Invalid input is rejected and asked again instead of crashing:

- at least 2 periods
- moving-average period from 1 to (number of periods − 1)
- weights greater than 0
- α between 0 and 1
- non-numeric input

Decimals use a dot (`0.5`) on any system language.

## Run

Requires Java 21 or newer.

```powershell
javac -d out src\demandcast\*.java
java -cp out demandcast.DemandCast
```

Example session:

```text
Enter number of periods: 6
Enter actual values:
Period 1: 100
...
Enter moving average period: 3
Enter weights:
Weight 1 (most recent period): 3
Weight 2: 2
Weight 3: 1
Enter alpha: 0.5
...
Best forecasting method is: Last Period
```

The project also opens directly in Eclipse.

## Tests

A plain-Java test suite (no external libraries) covers the forecasting math, the accuracy measures, and the input validation:

```powershell
powershell -ExecutionPolicy Bypass -File .\run-tests.ps1
```
