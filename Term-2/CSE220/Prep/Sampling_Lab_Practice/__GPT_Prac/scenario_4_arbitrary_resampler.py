"""
PROBLEM STATEMENT (Scenario 4 - Arbitrary-Rate Resampler)
---------------------------------------------------------
You have recorded a signal at fs_in = 1000 Hz. The signal is properly band-limited. A downstream program requires samples at fs_out = 1375 Hz.

You may NOT use np.interp or SciPy interpolation. You must use ideal sinc reconstruction from class.
Conceptually: discrete samples -> reconstruct continuous x(t) -> evaluate at new sample times.

Old sample times: t_n = n / fs_in
New sample times: t_m' = m / fs_out
Then: x_out[m] = sum_n x_in[n] * sinc((t_m' - t_n) / T_in)

DETAILED MATHEMATICAL THEORY
----------------------------
Resampling is essentially reconstruction evaluated at non-original time points. By using the ideal interpolation formula, we completely reconstruct the continuous-time signal from its samples, and then re-sample it at arbitrary new time points. T_in = 1/fs_in is the ORIGINAL sampling period (used for sinc normalization).

- WHY np.interp is conceptually wrong: `np.interp` performs piecewise LINEAR interpolation. In the frequency domain, this corresponds to a non-ideal low-pass filter (a sinc^2 function, derived from convolving rectangular pulses), which fails to perfectly remove all alias components and attenuates some high frequencies in the baseband. Ideal reconstruction requires an ideal low-pass filter (a pure rect function in frequency), which corresponds to convolution with a sinc function in time.
- WHY the sinc kernel uses T_in not T_out: We are reconstructing the signal from the ORIGINAL samples. The original samples were spaced by T_in. The sinc kernel sinc((t - n*T_in) / T_in) represents the impulse response of the ideal low-pass filter with cutoff frequency fs_in/2. It normalizes by the ORIGINAL sampling grid spacing.
- WHY this works for arbitrary rate ratios (even irrational ones like 1000->1375): Because we analytically reconstruct the continuous-time mathematical function x(t). Once we have the formula for x(t) (via the sum of weighted sincs), we can evaluate it at literally any real value t, regardless of whether the new sample times align with the old ones rationally or irrationally.
- Analogy: Imagine knowing the exact shape of a curve from sample points (like a connect-the-dots with infinite precision). If the signal is band-limited and sampled properly, the sinc function acts as the ultimate smooth "curve fitting" that perfectly recovers the original physical phenomenon. Once that perfect curve is mathematically formed, you can then read off any point on the curve, at any x-coordinate you want.

Key Formulas:
x_out[m] = sum_n x_in[n] * sinc((t_m' - t_n) / T_in)
K[i,j] = sinc((t_out[i] - t_in[j]) / T_in)
"""

import numpy as np
import matplotlib.pyplot as plt

# ==========================================
# EXACT SOLUTION CODE (preserve exactly)
# ==========================================

def resample(samples, fs_in, fs_out):
    samples = np.asarray(samples)

    T_in = 1.0 / fs_in

    t_in = np.arange(len(samples)) / fs_in

    duration = t_in[-1]

    t_out = np.arange(
        0,
        duration + 1e-15,
        1.0 / fs_out
    )

    kernel = np.sinc(
        (t_out[:, None] - t_in[None, :])
        / T_in
    )

    output = kernel @ samples

    return t_out, output

# ==========================================
# EXTENSION FUNCTIONS
# ==========================================

def resample_at(samples, fs_in, query_times):
    """
    Resample at arbitrary (possibly non-uniform) query times.
    
    Parameters:
    samples (array): Original sampled values.
    fs_in (float): Original sampling frequency in Hz.
    query_times (array): Array of arbitrary time points to evaluate the reconstructed signal.
    
    Returns:
    array: Resampled values at the specified query times.
    """
    samples = np.asarray(samples)
    T_in = 1.0 / fs_in
    t_in = np.arange(len(samples)) / fs_in
    
    query_times = np.asarray(query_times)
    
    kernel = np.sinc((query_times[:, None] - t_in[None, :]) / T_in)
    output = kernel @ samples
    
    return output

def resample_with_verification(signal_func, fs_in, fs_out, duration):
    """
    Resample and compare with ground truth continuous signal.
    
    Parameters:
    signal_func (callable): Function representing the continuous signal x(t).
    fs_in (float): Original sampling frequency.
    fs_out (float): Target sampling frequency.
    duration (float): Signal duration to generate.
    
    Returns:
    tuple: (t_out, resampled_signal, ground_truth)
    """
    t_in = np.arange(0, duration, 1.0 / fs_in)
    samples = signal_func(t_in)
    
    t_out, resampled_signal = resample(samples, fs_in, fs_out)
    ground_truth = signal_func(t_out)
    
    return t_out, resampled_signal, ground_truth

def downsample_safe(samples, fs_in, fs_out, signal_bw=None):
    """
    Downsamples a signal, warning if downsampling violates Nyquist for the original signal's bandwidth.
    
    Parameters:
    samples (array): Original sampled values.
    fs_in (float): Original sampling frequency.
    fs_out (float): Target sampling frequency.
    signal_bw (float): Estimated bandwidth of the original signal. Defaults to fs_in / 2 if unknown.
    
    Returns:
    tuple: (t_out, resampled_signal)
    """
    if signal_bw is None:
        signal_bw = fs_in / 2.0
        
    if fs_out < 2 * signal_bw:
        print(f"WARNING: Target sampling frequency fs_out={fs_out} Hz is less than 2*signal_bw ({2*signal_bw} Hz). "
              "Aliasing will occur!")
              
    return resample(samples, fs_in, fs_out)

# ==========================================
# DEMO BLOCK
# ==========================================

if __name__ == "__main__":
    # Create a multi-tone signal
    def my_signal(t):
        # 10 Hz and 40 Hz components
        return np.sin(2 * np.pi * 10 * t) + 0.5 * np.cos(2 * np.pi * 40 * t)

    fs_in = 100  # Original sampling rate
    fs_out = 250 # Upsampling to 250 Hz
    duration = 0.5
    
    # 1. Original samples
    t_in = np.arange(0, duration, 1.0 / fs_in)
    samples_in = my_signal(t_in)
    
    # 2. Sinc Resampling
    t_out_sinc, samples_sinc = resample(samples_in, fs_in, fs_out)
    
    # 3. Linear Interpolation (np.interp)
    samples_interp = np.interp(t_out_sinc, t_in, samples_in)
    
    # 4. Ground Truth
    ground_truth = my_signal(t_out_sinc)
    
    # Calculate errors
    error_sinc = np.abs(samples_sinc - ground_truth)
    error_interp = np.abs(samples_interp - ground_truth)
    
    print(f"Max Sinc Interpolation Error: {np.max(error_sinc):.6f}")
    print(f"Max Linear Interpolation Error: {np.max(error_interp):.6f}")
    
    # Plotting
    plt.figure(figsize=(12, 8))
    
    plt.subplot(2, 1, 1)
    plt.plot(t_out_sinc, ground_truth, 'k-', alpha=0.5, label='Ground Truth x(t)', linewidth=2)
    plt.plot(t_in, samples_in, 'bo', label=f'Original Samples (fs={fs_in}Hz)')
    plt.plot(t_out_sinc, samples_sinc, 'r.', label=f'Sinc Resampled (fs={fs_out}Hz)')
    plt.plot(t_out_sinc, samples_interp, 'g--', alpha=0.7, label='np.interp (Linear)')
    plt.title("Arbitrary Rate Resampling: Sinc vs Linear Interpolation")
    plt.xlabel("Time (s)")
    plt.ylabel("Amplitude")
    plt.legend()
    plt.grid(True)
    
    plt.subplot(2, 1, 2)
    plt.plot(t_out_sinc, error_sinc, 'r-', label='Sinc Error')
    plt.plot(t_out_sinc, error_interp, 'g-', label='Linear Error (np.interp)')
    plt.title("Absolute Interpolation Errors")
    plt.xlabel("Time (s)")
    plt.ylabel("Absolute Error")
    plt.legend()
    plt.grid(True)
    
    plt.tight_layout()
    plt.show()
