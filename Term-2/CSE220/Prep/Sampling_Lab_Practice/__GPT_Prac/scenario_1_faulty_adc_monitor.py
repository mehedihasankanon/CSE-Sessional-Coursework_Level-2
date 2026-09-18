"""
PROBLEM STATEMENT
=================
Scenario 1 — The Faulty ADC Monitor

A vibration monitoring system measures a signal:
  x(t) = 1.5*cos(2*pi*120*t) + 0.8*sin(2*pi*310*t) + 0.3*cos(2*pi*420*t)

A provided program simulates an ADC. The code currently samples the signal and plots the sample points, but the laboratory now wants a reconstruction mode.

Tasks:
1. Implement ideal sinc reconstruction (the `reconstruct()` function does NOT receive fs — must infer T from sample_times).
2. Determine the sampling period from `sample_times`.
3. Add `sampling_status(fs)` returning 'safe' if Nyquist satisfied, 'aliased' otherwise.
4. For fs=1200 Hz and fs=700 Hz, predict what happens.
5. The code works for complex-valued samples too (NumPy handles this natively).
6. Explain why "smooth reconstruction ≠ no aliasing" — the engineer's conceptual error.

KEY THEORY
==========
Highest frequency f_M = 420 Hz, so Nyquist requires f_s > 2*f_M = 840 Hz.
For fs = 1200 Hz: 1200 > 840 -> safe.
For fs = 700 Hz: 700 < 840 -> aliased.

At fs = 700 Hz, the 420 Hz tone aliases to |420 - 700| = 280 Hz. The result is perfectly smooth but WRONG frequency.

Ideal Sinc Reconstruction:
  x_r(t) = sum_n x[n] * sinc((t - n*T) / T)

Note: np.sinc(x) computes sin(pi*x)/(pi*x). 

WHY dividing by T inside sinc is critical:
The sinc function must have its zero crossings exactly at multiples of the sampling period T. By evaluating np.sinc((t - n*T)/T), when t = m*T, the argument is (m-n). The np.sinc(m-n) equals 1 when m=n and 0 for all other integers, ensuring the reconstructed signal passes exactly through the original samples!

WHY the kernel matrix approach works:
We can define a kernel matrix K where K[i,j] = sinc((t_output[i] - t_sample[j]) / T). 
Then x_r = K @ samples. This is equivalent to summing shifted sinc pulses weighted by the sample values. It vectorizes the computation over output times and sample points efficiently.

WHY smooth reconstruction doesn't mean correct reconstruction:
An aliased signal is perfectly bandlimited to the new (lower) Nyquist band. The sinc reconstruction will always yield a perfectly smooth continuous-time curve. However, aliasing produces the wrong frequencies. 
Real-life analogy: Aliasing is like a spinning wheel that appears to go backwards under strobe lights. It looks perfectly smooth and physically valid, but it represents a wrong speed/direction.

The alias frequency formula:
  f_alias = ((f + fs/2) % fs) - fs/2
"""

import numpy as np
import matplotlib.pyplot as plt

def vibration_signal(t):
    return (
        1.5 * np.cos(2*np.pi*120*t)
        + 0.8 * np.sin(2*np.pi*310*t)
        + 0.3 * np.cos(2*np.pi*420*t)
    )

def sample_signal(signal, fs, duration):
    ts = np.arange(0, duration, 1/fs)
    xs = signal(ts)
    return ts, xs

def reconstruct(samples, sample_times, output_times):
    samples = np.asarray(samples)
    sample_times = np.asarray(sample_times)
    output_times = np.asarray(output_times)

    if len(sample_times) < 2:
        raise ValueError("Need at least two samples")

    T = sample_times[1] - sample_times[0]

    kernel = np.sinc(
        (output_times[:, None] - sample_times[None, :]) / T
    )

    return kernel @ samples


def sampling_status(fs):
    f_max = 420.0

    if fs > 2 * f_max:
        return "safe"

    return "aliased"

def run_monitor(fs):
    duration = 0.05

    ts, xs = sample_signal(vibration_signal, fs, duration)

    tdense = np.linspace(0, duration, 6000)
    xr = reconstruct(xs, ts, tdense)

    return tdense, xr


# -----------------------------------------------------------------------------
# EXTENSION FUNCTIONS & SCENARIOS
# -----------------------------------------------------------------------------

def wrap_frequency(f, fs):
    """
    Maps a continuous frequency into the fundamental Nyquist range [-fs/2, fs/2).
    This helps in finding the exact alias frequency when undersampling occurs.
    """
    return ((f + fs/2) % fs) - fs/2

def find_aliased_components(components_hz, fs):
    """
    Given a list of component frequencies, returns a dictionary mapping original
    frequencies to their aliased/reconstructed (positive) frequencies.
    """
    result = {}
    for f in components_hz:
        f_alias = wrap_frequency(f, fs)
        # Convert to positive frequency since cos(-wt) = cos(wt) and we care about the apparent frequency
        result[f] = abs(f_alias)
    return result

def reconstruct_complex(samples, sample_times, output_times):
    """
    Explicitly shows that the same sinc code works for complex samples.
    NumPy handles the complex arithmetic natively in the matrix multiplication.
    This works because linear combinations of complex numbers seamlessly parallel
    independent reconstruction of real and imaginary parts.
    """
    # The kernel matrix is real. Reconstructing complex samples simply 
    # weighs the real sinc kernels by complex coefficients.
    return reconstruct(samples, sample_times, output_times)

if __name__ == "__main__":
    # Demonstration comparing reconstruction at 1200 Hz vs 700 Hz
    duration = 0.05
    t_true = np.linspace(0, duration, 6000)
    x_true = vibration_signal(t_true)

    print("Analyzing Frequency Components for fs = 700 Hz...")
    components = [120, 310, 420]
    aliases = find_aliased_components(components, 700)
    for f in components:
        if abs(aliases[f] - f) > 1e-6:
            print(f"  Component {f} Hz ALIASES to {aliases[f]:.1f} Hz")
        else:
            print(f"  Component {f} Hz is correctly represented.")
            
    print("\nRunning reconstruction simulation...")

    # 1. 1200 Hz - Safe
    fs1 = 1200
    tdense1, xr1 = run_monitor(fs1)
    ts1, xs1 = sample_signal(vibration_signal, fs1, duration)

    # 2. 700 Hz - Aliased
    fs2 = 700
    tdense2, xr2 = run_monitor(fs2)
    ts2, xs2 = sample_signal(vibration_signal, fs2, duration)
    
    # Plotting
    plt.figure(figsize=(12, 8))
    
    # Plot 1200 Hz
    plt.subplot(2, 1, 1)
    plt.plot(t_true, x_true, 'k--', label='True Signal')
    plt.plot(tdense1, xr1, 'b-', alpha=0.7, label='Reconstructed (1200Hz)')
    plt.scatter(ts1, xs1, color='red', s=20, zorder=5, label='Samples')
    plt.title(f"Reconstruction at fs = 1200 Hz (Status: {sampling_status(1200)})")
    plt.legend(loc='upper right')
    plt.grid(True)
    
    # Plot 700 Hz
    plt.subplot(2, 1, 2)
    plt.plot(t_true, x_true, 'k--', label='True Signal')
    plt.plot(tdense2, xr2, 'm-', alpha=0.7, label='Reconstructed (700Hz)')
    plt.scatter(ts2, xs2, color='red', s=20, zorder=5, label='Samples')
    plt.title(f"Reconstruction at fs = 700 Hz (Status: {sampling_status(700)})\nNote: The reconstruction is perfectly smooth, but completely misses the true signal shape!")
    plt.legend(loc='upper right')
    plt.grid(True)
    
    plt.tight_layout()
    plt.show()

    # Complex signal demonstration
    print("\nTesting complex signal reconstruction...")
    t_c = np.arange(0, duration, 1/fs1)
    x_c = np.exp(1j * 2 * np.pi * 120 * t_c) # 120 Hz complex exponential
    x_c_r = reconstruct_complex(x_c, t_c, tdense1)
    print(f"  Original complex samples: {len(x_c)}")
    print(f"  Reconstructed complex signal length: {len(x_c_r)}")
    print(f"  Is reconstructed signal complex? {np.iscomplexobj(x_c_r)}")
    print("  Success: Sinc interpolation supports complex signals.")
