# task a

python bigmul.py inputs/1.txt --engine dft --out-dir outputs/task_a/1
python bigmul.py inputs/2.txt --engine fft --out-dir outputs/task_a/2
python bigmul.py inputs/3.txt --engine fft --out-dir outputs/task_a/3

# benchmark

python3 bigmul.py --benchmark --out-dir outputs/benchmark_a

# task b

# Color image, bokeh blur, FFT engine
python3 image_conv.py images/skyline512.png --kernel bokeh --param 9 --engine fft --out-dir outputs/task_b/skyline_bokeh

# Grayscale image, motion blur, FFT engine
python3 image_conv.py images/sunset512.png --gray --kernel motion --param 41 --engine fft --out-dir outputs/task_b/sunset_motion

# Gray image, gaussian blur, naive DFT engine (will take slightly longer)
python3 image_conv.py images/skyline256.png --gray --kernel gaussian --param 21 --engine dft --out-dir outputs/task_b/skyline256_gaussian_dft


# benchmark
python3 image_conv.py images/skyline512.png --benchmark --out-dir outputs/benchmark_b