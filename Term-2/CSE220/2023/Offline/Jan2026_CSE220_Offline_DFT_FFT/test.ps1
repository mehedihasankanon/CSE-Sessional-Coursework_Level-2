# task a

python bigmul.py inputs/1.txt --engine dft --out-dir outputs/task_a/1
python bigmul.py inputs/2.txt --engine fft --out-dir outputs/task_a/2
python bigmul.py inputs/3.txt --engine fft --out-dir outputs/task_a/3

# Test on Big Integer Multiplication
python bigmul.py inputs/1.txt --engine arbitrary --out-dir outputs/task_a/bonus_1
python bigmul.py inputs/2.txt --engine arbitrary --out-dir outputs/task_a/bonus_2
python bigmul.py inputs/3.txt --engine arbitrary --out-dir outputs/task_a/bonus_3


# benchmark

python bigmul.py --benchmark --out-dir outputs/benchmark_a

# task b

# Color image, bokeh blur, FFT engine
# python image_conv.py images/skyline512.png --kernel bokeh --param 9 --engine fft --out-dir outputs/task_b/skyline_bokeh

# Grayscale image, motion blur, FFT engine
# python image_conv.py images/sunset512.png --gray --kernel motion --param 41 --engine fft --out-dir outputs/task_b/sunset_motion

# Gray image, gaussian blur, naive DFT engine (will take slightly longer)
# python image_conv.py images/skyline256.png --gray --kernel gaussian --param 21 --engine dft --out-dir outputs/task_b/skyline256_gaussian_dft


# Test on Image Convolution
# python image_conv.py images/skyline256.png --gray --kernel gaussian --param 21 --engine arbitrary --out-dir outputs/bonus_1_b

# benchmark
# python image_conv.py images/skyline512.png --benchmark --out-dir outputs/benchmark_b
