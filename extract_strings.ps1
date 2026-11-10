param([string]$File, [int]$MinLen = 4)

$bytes = [System.IO.File]::ReadAllBytes($File)
$sb = [System.Text.StringBuilder]::new()
$results = New-Object System.Collections.ArrayList

for ($i = 0; $i -lt $bytes.Length; $i++) {
    $c = [char]$bytes[$i]
    if ($c -match '[ -~]') {
        $null = $sb.Append($c)
    } else {
        if ($sb.Length -ge $MinLen) {
            $null = $results.Add($sb.ToString())
        }
        $sb.Clear()
    }
}
if ($sb.Length -ge $MinLen) {
    $null = $results.Add($sb.ToString())
}

$filtered = $results | Where-Object { $_ -match '/' -or $_ -match 'cosmic|flow|Flow|Cosmic|com\.|org\.' } | Sort-Object -Unique
$filtered
