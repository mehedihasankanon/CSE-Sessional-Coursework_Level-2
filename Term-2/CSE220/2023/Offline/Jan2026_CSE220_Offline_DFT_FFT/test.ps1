# ==============================================================================
# PART 1: TASK A (Standard Runs & Benchmark)
# ==============================================================================

Write-Host "=== TASK A: Standard Runs ===" -ForegroundColor Cyan

# Inputs 1 & 2 (DFT as per expected_outputs)
python bigmul.py inputs/1.txt --engine dft --out-dir outputs/task_a/1
python bigmul.py inputs/2.txt --engine dft --out-dir outputs/task_a/2

# Inputs 3 & 4 (FFT as per expected_outputs)
python bigmul.py inputs/3.txt --engine fft --out-dir outputs/task_a/3
python bigmul.py inputs/4.txt --engine fft --out-dir outputs/task_a/4

# Optional: verify inputs 1 & 2 on FFT as well
python bigmul.py inputs/1.txt --engine fft --out-dir outputs/task_a/1_fft
python bigmul.py inputs/2.txt --engine fft --out-dir outputs/task_a/2_fft

# Task A Benchmark
Write-Host "`n=== TASK A: Benchmark ===" -ForegroundColor Cyan
python bigmul.py --benchmark --out-dir outputs/task_a/benchmark


# ==============================================================================
# PART 2: TASK B (Standard Image Convolutions & Benchmark)
# ==============================================================================

Write-Host "`n=== TASK B: Standard Runs ===" -ForegroundColor Cyan

# 1. Color skyline with bokeh blur (FFT)
python image_conv.py images/skyline512.png --kernel bokeh --param 9 --engine fft --out-dir outputs/task_b/skyline_bokeh

# 2. Grayscale sunset with motion blur (FFT)
python image_conv.py images/sunset512.png --gray --kernel motion --param 41 --engine fft --out-dir outputs/task_b/sunset_motion

# 3. Grayscale skyline256 with gaussian blur (DFT)
python image_conv.py images/skyline256.png --gray --kernel gaussian --param 21 --engine dft --out-dir outputs/task_b/skyline256_gaussian_dft

# 4. Color nebula with bokeh blur (FFT extra example from expected_outputs)
python image_conv.py images/nebula512.png --kernel bokeh --param 13 --engine fft --out-dir outputs/task_b/nebula_bokeh

# Task B Benchmark
Write-Host "`n=== TASK B: Benchmark ===" -ForegroundColor Cyan
python image_conv.py images/skyline512.png --benchmark --out-dir outputs/task_b/benchmark


# ==============================================================================
# PART 3: BONUS (Arbitrary-Length FFT on Non-Power-of-Two Sizes)
# ==============================================================================

Write-Host "`n=== BONUS: Arbitrary-Length FFT Runs ===" -ForegroundColor Yellow

# Task A: Runs without padding to power of two (transforms at exact N = len_a + len_b - 1)
python bigmul.py inputs/1.txt --engine arbitrary --out-dir outputs/bonus/task_a/1
python bigmul.py inputs/2.txt --engine arbitrary --out-dir outputs/bonus/task_a/2
python bigmul.py inputs/3.txt --engine arbitrary --out-dir outputs/bonus/task_a/3

# Task B: Linear convolution without padding to power of two (transforms at exact H+kh-1 x W+kw-1)
# skyline256 (256x256) + gaussian (21x21) -> transforms at exactly 276x276 instead of 512x512
python image_conv.py images/skyline256.png --gray --kernel gaussian --param 21 --engine arbitrary --out-dir outputs/bonus/task_b/skyline256_gaussian

# skyline512 (512x512) + bokeh 9 (19x19) -> transforms at exactly 530x530 instead of 1024x1024
python image_conv.py images/skyline512.png --kernel bokeh --param 9 --engine arbitrary --out-dir outputs/bonus/task_b/skyline_bokeh

Write-Host "`n>>> All tests, benchmarks, and bonus runs completed successfully! <<<" -ForegroundColor Green