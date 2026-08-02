$ErrorActionPreference = "Stop"

$repositoryRoot = Split-Path -Parent $PSScriptRoot

function Assert-PathExists {
    param(
        [Parameter(Mandatory)]
        [string]$RelativePath
    )

    $absolutePath = Join-Path $repositoryRoot $RelativePath
    if (-not (Test-Path -LiteralPath $absolutePath)) {
        throw "Required baseline path is missing: $RelativePath"
    }
}

function Assert-FileContains {
    param(
        [Parameter(Mandatory)]
        [string]$RelativePath,
        [Parameter(Mandatory)]
        [string]$Pattern
    )

    $absolutePath = Join-Path $repositoryRoot $RelativePath
    Assert-PathExists $RelativePath
    if (-not (Select-String -LiteralPath $absolutePath -Pattern $Pattern -Quiet)) {
        throw "Required baseline pattern '$Pattern' is missing from $RelativePath"
    }
}

function Assert-FileExcludes {
    param(
        [Parameter(Mandatory)]
        [string]$RelativePath,
        [Parameter(Mandatory)]
        [string]$Pattern
    )

    $absolutePath = Join-Path $repositoryRoot $RelativePath
    Assert-PathExists $RelativePath
    if (Select-String -LiteralPath $absolutePath -Pattern $Pattern -Quiet) {
        throw "Forbidden baseline pattern '$Pattern' is present in $RelativePath"
    }
}

Assert-FileContains "backend/settings.gradle" "include 'marketplace-service'"
Assert-PathExists "backend/marketplace-service/build.gradle"
Assert-FileContains "backend/marketplace-service/build.gradle" "spring-boot-starter-actuator"
Assert-FileContains "backend/marketplace-service/build.gradle" "spring-modulith-starter-test"
Assert-PathExists "backend/marketplace-service/Dockerfile"
Assert-PathExists "backend/marketplace-service/src/main/java/com/example/marketplace/MarketplaceApplication.java"
Assert-PathExists "backend/marketplace-service/src/test/java/com/example/marketplace/architecture/ModularMonolithArchitectureTests.java"
Assert-FileExcludes "backend/marketplace-service/src/main/resources/application-prod.yml" "admin123|SYSTEM"
Assert-FileExcludes "compose.yaml" "LOCALSTACK_AUTH_TOKEN:-ls-"
Assert-FileContains "compose.yaml" "condition: service_healthy"
Assert-FileContains ".github/workflows/ci.yml" "./gradlew check"
Assert-FileContains ".github/workflows/images.yml" "backend/marketplace-service/Dockerfile"

Write-Output "Deployment baseline verification passed."
