# AI Generated. Claude Haiku 4.5

# Test script for Max Flow Problems
# This script allows interactive testing of problem solutions

Write-Host "====== Max Flow Problem Tester ======" -ForegroundColor Green
Write-Host ""

# Get problem number
$problem = Read-Host "Enter problem number (1 or 2)"

# Validate problem number
if ($problem -notmatch "^[12]$") {
    Write-Host "Error: Problem number must be 1 or 2" -ForegroundColor Red
    exit
}

# Get input number
$inputNum = Read-Host "Enter input number (1-5)"

# Validate input number
if ($inputNum -notmatch "^[1-5]$") {
    Write-Host "Error: Input number must be 1-5" -ForegroundColor Red
    exit
}

# Build file paths
$inputFile = ".\Test_Cases\Problem $problem\Inputs\$inputNum.txt"
$expectedOutputFile = ".\Test_Cases\Problem $problem\Outputs\$inputNum.txt"
$executable = ".\2305052_$problem.exe"
$outputFile = ".\output.txt"

# Check if files exist
if (-not (Test-Path $inputFile)) {
    Write-Host "Error: Input file not found: $inputFile" -ForegroundColor Red
    exit
}

if (-not (Test-Path $expectedOutputFile)) {
    Write-Host "Warning: Expected output file not found: $expectedOutputFile" -ForegroundColor Yellow
    $hasExpectedOutput = $false
} else {
    $hasExpectedOutput = $true
}

if (-not (Test-Path $executable)) {
    Write-Host "Error: Executable not found: $executable" -ForegroundColor Red
    Write-Host "Compiling first..." -ForegroundColor Yellow
    
    $sourceFile = ".\2305052_$problem.cpp"
    if (Test-Path $sourceFile) {
        g++ $sourceFile -o $executable
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Error: Compilation failed" -ForegroundColor Red
            exit
        }
        Write-Host "Compilation successful!" -ForegroundColor Green
    } else {
        Write-Host "Error: Source file not found: $sourceFile" -ForegroundColor Red
        exit
    }
}

# Run the test
Write-Host ""
Write-Host "Running Problem $problem with input $inputNum..." -ForegroundColor Cyan
Write-Host ""

Get-Content $inputFile | & $executable | Out-File $outputFile

# Display results
Write-Host "=== INPUT ===" -ForegroundColor Yellow
Get-Content $inputFile

if ($hasExpectedOutput) {
    Write-Host ""
    Write-Host "=== EXPECTED OUTPUT ===" -ForegroundColor Cyan
    Get-Content $expectedOutputFile
}

Write-Host ""
Write-Host "=== ACTUAL OUTPUT ===" -ForegroundColor Yellow
Get-Content $outputFile

Write-Host ""
Write-Host "[SUCCESS] Output saved to: $outputFile" -ForegroundColor Green
