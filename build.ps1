function Write-Line($Object) {
	$width = $Host.UI.RawUI.WindowSize.Width
	Write-Host "$Object".PadRight($width) @args
}

function Write-Failed($Object) {
	Write-Line $Object -BackgroundColor Red -ForegroundColor White
}

function Write-Successful($Object) {
	Write-Line $Object -BackgroundColor Green -ForegroundColor Black
}

$arguments = $args

function build {

Write-Host
Write-Host
Write-Host "Run Tasks: build" -ForegroundColor Green
./gradlew --build-cache --parallel --daemon build
if ($LASTEXITCODE -ne 0) {
	Write-Failed "BUILD FAILED"
	return
}

Write-Successful "BUILD SUCCESSFUL"

}


$elapsed = Measure-Command { build | Out-Default }

Write-Host -ForegroundColor Cyan ("{0} days {1:d2}:{2:d2}:{3:d2}:{4:d3}" -f
		$elapsed.Days, $elapsed.Hours, $elapsed.Minutes, $elapsed.Seconds, $elapsed.Milliseconds)
