# Compiles src + tests into a temp folder and runs the tests.
$ErrorActionPreference = "Stop"
$out = Join-Path $env:TEMP "demandcast-test-out"
if (Test-Path $out) { Remove-Item -Recurse -Force $out }
$files = @(Get-ChildItem "$PSScriptRoot\src\demandcast\*.java") + @(Get-ChildItem "$PSScriptRoot\test\demandcast\*.java")
javac -Xlint:all -d $out ($files | ForEach-Object { $_.FullName })
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp $out demandcast.ForecastTest
exit $LASTEXITCODE
