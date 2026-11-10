param([string]$File, [string]$OutFile, [int]$MinLen = 4)

$bytes = [System.IO.File]::ReadAllBytes($File)
$sb = [System.Text.StringBuilder]::new()
$results = New-Object System.Collections.Generic.List[string]

for ($i = 0; $i -lt $bytes.Length; $i++) {
    $c = [char]$bytes[$i]
    if ($c -match '[ -~]') {
        [void]$sb.Append($c)
    } else {
        if ($sb.Length -ge $MinLen) {
            $results.Add($sb.ToString())
        }
        $sb.Clear()
    }
}
if ($sb.Length -ge $MinLen) {
    $results.Add($sb.ToString())
}

# Filter for class-like names (contain /) or app-related keywords
$filtered = $results | Where-Object { $_ -match '/' -or $_ -match 'cosmic|flow|Flow|Cosmic|com\.|org\.|net\.' } | Sort-Object -Unique

$filtered | Out-File -FilePath $OutFile -Encoding utf8
Write-Host "Wrote $($filtered.Count) strings to $OutFile"
