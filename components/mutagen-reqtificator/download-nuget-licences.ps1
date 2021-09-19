# Run in Package Manager Console with `./download-packages-license.ps1`.
# If access denied, execute `Set-ExecutionPolicy -Scope Process -ExecutionPolicy RemoteSigned`.
# Code copied from: https://softwareengineering.stackexchange.com/a/286981/537 under CC BY-SA 3.0
#
# 1) Download all licences
# 2) Delete anything that's only used for tests / will not be distributed
# 3) Change extension of licences that are actually html

Split-Path -parent $dte.Solution.FileName | cd; New-Item -ItemType Directory -Force -Path ".\licenses";
@( Get-Project -All | ? { $_.ProjectName } | % {
    Get-Package -ProjectName $_.ProjectName | ? { $_.LicenseUrl }
} ) | Sort-Object Id -Unique | % {
    $pkg = $_;
    Try {
        if ($pkg.Id -notlike 'microsoft*' -and $pkg.LicenseUrl.StartsWith('http')) {
            Write-Host ("Download license for package " + $pkg.Id + " from " + $pkg.LicenseUrl);
            #Write-Host (ConvertTo-Json ($pkg));

            $licenseUrl = $pkg.LicenseUrl
            if ($licenseUrl.contains('github.com')) {
                $licenseUrl = $licenseUrl.replace("/blob/", "/raw/")
            }

            $extension = ".txt"
            if ($licenseUrl.EndsWith(".md")) {
                $extension = ".md"
            }

            (New-Object System.Net.WebClient).DownloadFile($licenseUrl, (Join-Path (pwd) 'licenses\') + $pkg.Id + $extension);
        }
    }
    Catch [system.exception] {
        Write-Host ("Could not download license for " + $pkg.Id)
    }
}