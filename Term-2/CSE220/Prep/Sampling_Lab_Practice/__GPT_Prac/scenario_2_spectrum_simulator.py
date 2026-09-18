"""
PROBLEM STATEMENT:
Scenario 2 — Implement the Sampling-Theorem Spectrum Simulator

Your lab has a signal-spectrum visualization tool. The original signal has a triangular continuous-time spectrum:
  X(j*omega) = 1 - |omega|/omega_M  for |omega| <= omega_M, else 0

The existing program plots X(j*omega). Your task is to add a sampling preview that displays the spectrum after impulse-train sampling.

You must implement the expression directly from sampling theory (NO FFT):
  X_p(j*omega) = (1/T) * sum_{k=-K}^{K} X(j*(omega - k*omega_s))
where omega_s = 2*pi/T, K = num_copies.

Also implement guard_band(omega_M, T) returning the gap between baseband right edge and k=1 copy left edge. Negative means overlap.

DETAILED MATHEMATICAL THEORY:
- WHY the spectrum becomes periodic: 
  Sampling in the time domain involves multiplying the continuous-time signal x(t) by an impulse train p(t). 
  Multiplication in time corresponds to convolution in the frequency domain. 
  Since the Fourier transform of an impulse train is also an impulse train (spaced by omega_s), convolving the original spectrum X(j*omega) with this impulse train creates infinite periodic replicas of X(j*omega) centered at multiples of omega_s.

- WHY the 1/T factor appears: 
  The impulse train p(t) = sum_{n=-infty}^{infty} delta(t - n*T) can be represented as a Fourier series. 
  The Fourier series coefficients a_k of the impulse train are all equal to 1/T. 
  Therefore, when represented in the frequency domain, each shifted replica of the spectrum is scaled by this 1/T factor.

- Real-life analogy: 
  Imagine photocopying a document and tiling the copies side by side. 
  If the copies overlap (aliasing), the text gets mixed up and you cannot read it properly. 
  If there is a gap (guard band) between the copies, each copy is cleanly readable, meaning you can perfectly reconstruct the original document by looking at just one copy.

- Guard band formula derivation: 
  The right edge of the baseband (k=0) copy is at omega_M.
  The left edge of the first (k=1) copy is at omega_s - omega_M.
  The gap (guard band) = (omega_s - omega_M) - omega_M = omega_s - 2*omega_M.

Formulas:
- X_p(j*omega) = (1/T) * sum_{k=-K}^{K} X(j*(omega - k*omega_s))
- omega_s = 2*pi/T
- Guard Band = omega_s - 2*omega_M
- Nyquist criterion for no aliasing: omega_s > 2*omega_M, so T < pi/omega_M
"""

import numpy as np
import matplotlib.pyplot as plt

def original_spectrum(omega, omega_M):
    omega = np.asarray(omega, dtype=float)
    val = 1.0 - np.abs(omega) / omega_M
    return np.maximum(0.0, val)

def guard_band(omega_M, T):
    return 2 * np.pi / T - 2 * omega_M

def sampled_spectrum(omega, omega_M, T, num_copies):
    K = num_copies
    
    X_p = np.zeros_like(omega, dtype=np.float64)
    
    omega_s = 2 * np.pi / T
    
    k = np.arange(-K, K + 1)
    
    for k_val in k:
        shifted_omega = omega - k_val * omega_s
        X_p += original_spectrum(shifted_omega, omega_M)
        
    return X_p / T


# ====================================================================
# MAIN DEMONSTRATION
# ====================================================================
if __name__ == "__main__":
    omega_M = 10.0
    # Nyquist threshold T < pi/10 ≈ 0.314
    T_good = 0.2  # omega_s = 2*pi/0.2 = 31.4 > 2*omega_M
    T_bad = 0.4   # omega_s = 2*pi/0.4 = 15.7 < 2*omega_M
    
    omega_range = np.linspace(-40, 40, 1000)
    
    X_orig = original_spectrum(omega_range, omega_M)
    X_samp_good = sampled_spectrum(omega_range, omega_M, T_good, 5)
    X_samp_bad = sampled_spectrum(omega_range, omega_M, T_bad, 5)
    
    print(f"Good T ({T_good}): Guard Band = {guard_band(omega_M, T_good):.2f}")
    print(f"Bad T ({T_bad}): Guard Band = {guard_band(omega_M, T_bad):.2f}")
    
    plt.figure(figsize=(12, 8))
    
    plt.subplot(3, 1, 1)
    plt.plot(omega_range, X_orig)
    plt.title("Original Spectrum")
    plt.grid(True)
    
    plt.subplot(3, 1, 2)
    plt.plot(omega_range, X_samp_good)
    plt.title(f"Sampled Spectrum (Good T = {T_good}) - No Aliasing")
    plt.grid(True)
    
    plt.subplot(3, 1, 3)
    plt.plot(omega_range, X_samp_bad, color='red')
    plt.title(f"Sampled Spectrum (Bad T = {T_bad}) - Aliasing Present")
    plt.grid(True)
    
    plt.tight_layout()
    plt.show()


# ====================================================================
# EXTENSION FUNCTIONS
# ====================================================================

def spectrum_copy(omega, omega_M, T, k):
    """
    Returns ONLY the contribution of the k-th spectral replica (scaled by 1/T).
    This function is useful to isolate individual aliases and observe how they overlap.
    """
    omega_s = 2 * np.pi / T
    shifted_omega = omega - k * omega_s
    return original_spectrum(shifted_omega, omega_M) / T


def find_minimum_safe_T(omega_M):
    """
    Returns the maximum T (minimum sampling rate) that avoids aliasing.
    According to the Nyquist criterion, omega_s > 2*omega_M.
    Since omega_s = 2*pi/T, this implies T < pi/omega_M.
    """
    return np.pi / omega_M


def plot_sampling_preview(omega_M, T, num_copies):
    """
    Visualization showing individual replicas, guard band annotation, and aliasing status.
    This illustrates the 'tiled copies' analogy from the theory section.
    """
    omega_s = 2 * np.pi / T
    omega_range = np.linspace(-omega_s * (num_copies + 1), omega_s * (num_copies + 1), 2000)
    
    plt.figure(figsize=(10, 6))
    
    # Plot individual copies
    total_spectrum = np.zeros_like(omega_range)
    for k in range(-num_copies, num_copies + 1):
        copy_k = spectrum_copy(omega_range, omega_M, T, k)
        total_spectrum += copy_k
        plt.plot(omega_range, copy_k, '--', label=f'k={k}' if abs(k) <= 2 else "")
    
    plt.plot(omega_range, total_spectrum, 'k-', linewidth=2, label='Total Spectrum')
    
    gb = guard_band(omega_M, T)
    status = "No Aliasing" if gb >= 0 else "Aliasing Present"
    plt.title(f"Sampling Preview | T = {T:.3f} | Guard Band = {gb:.2f} | {status}")
    plt.xlabel("Frequency (omega)")
    plt.ylabel("Magnitude")
    plt.legend(loc='upper right')
    plt.grid(True)
    plt.show()


def rectangular_spectrum_sampled(omega, omega_M, T, num_copies):
    """
    Same idea but with a rectangular (flat-top) spectrum instead of triangular,
    to show the concept generalizes to different spectrum shapes.
    The rectangular spectrum is 1 for |omega| <= omega_M and 0 elsewhere.
    """
    K = num_copies
    X_p = np.zeros_like(omega, dtype=np.float64)
    omega_s = 2 * np.pi / T
    
    for k in range(-K, K + 1):
        shifted_omega = omega - k * omega_s
        # Rectangular spectrum evaluation
        val = np.where(np.abs(shifted_omega) <= omega_M, 1.0, 0.0)
        X_p += val
        
    return X_p / T
