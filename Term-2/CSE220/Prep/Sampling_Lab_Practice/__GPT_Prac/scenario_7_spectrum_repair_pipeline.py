"""
PROBLEM STATEMENT: Scenario 7 — "Spectrum Repair" Pipeline

A program stores an approximation to X(j*omega) on a dense frequency grid (array, NOT a function).
You must simulate:
1. Impulse-train sampling in the frequency domain
2. Ideal reconstruction filtering
3. Reconstruction success/failure detection

You receive omega = np.linspace(-6000, 6000, 120001) and X as array samples.

This is TRICKIER than Scenario 2 because you can't call original_spectrum(omega - shift) directly.
You only have array samples, so to evaluate X(j*(omega - k*omega_s)), you must use np.interp to
resample/interpolate the stored spectrum.

Detailed Mathematical Theory:
-----------------------------
In ideal impulse-train sampling, the sampled signal's spectrum is:
X_p(j*omega) = (1/T) * sum_{k=-inf}^{inf} X(j*(omega - k*omega_s))

Unlike when we have a mathematical function X(j*omega) that we can evaluate at arbitrary points,
here we only have samples of X at specific frequencies in the `omega` array.
To compute X(j*(omega - k*omega_s)), we need to evaluate the shifted spectrum at our grid points.
We achieve this by interpolating the original array `X` using `np.interp`.

Why np.interp with left=0, right=0 is correct:
The original continuous-time signal is assumed to be bandlimited. Outside of the frequency range
captured in the array `X`, its spectrum is zero. When we shift the spectrum, the values that
fall outside our observed frequency window should evaluate to zero, which `left=0` and `right=0` handles.

Handling Complex Arrays:
`np.interp` does not directly support complex values. To interpolate a complex spectrum, we must
interpolate the real and imaginary parts separately, and then recombine them:
    real_part = np.interp(new_x, old_x, X.real)
    imag_part = np.interp(new_x, old_x, X.imag)
    X_shifted = real_part + 1j * imag_part

The Low-Pass Filter (LPF) Gain:
The ideal reconstruction filter is a brick-wall low-pass filter H(j*omega) with a cutoff frequency
at pi/T (the Nyquist frequency of the sampling process). The gain of this filter must be exactly T.
Why? Because the sampling process scales the replicas by a factor of 1/T. The filter gain of T cancels
this 1/T scaling from spectral replication, restoring the original amplitude: X(j*omega) = X_p(j*omega) * H(j*omega).

The Complete Pipeline:
1. Replicate the spectrum (shift it by multiples of omega_s).
2. Add up replicas (with scaling 1/T).
3. Multiply by a brick-wall filter (LPF with gain T).
4. Get back the original (or corrupted) spectrum.

Real-life Analogy:
Imagine you have a photo (the spectrum as pixels). Sampling tiles the photo repeatedly across an infinite canvas.
The LPF is like cropping back down to just the center tile. If the tiles overlap (aliasing due to under-sampling),
the crop captures mixed content from multiple tiles, resulting in a distorted photo. If the tiles don't overlap,
the center crop perfectly matches the original photo.
"""

import numpy as np
import matplotlib.pyplot as plt

def shifted_spectrum(omega, X, shift):
    # Want X(omega - shift)
    return np.interp(
        omega - shift,
        omega,
        X,
        left=0.0,
        right=0.0
    )


def simulate_sampling_spectrum(omega, X, T, copies):
    omega = np.asarray(omega)
    X = np.asarray(X)

    omega_s = 2 * np.pi / T

    Xp = np.zeros_like(
        X,
        dtype=np.result_type(X, complex)
    )

    for k in range(-copies, copies + 1):
        shift = k * omega_s

        if np.iscomplexobj(X):
            real_part = np.interp(
                omega - shift,
                omega,
                X.real,
                left=0,
                right=0
            )

            imag_part = np.interp(
                omega - shift,
                omega,
                X.imag,
                left=0,
                right=0
            )

            copy = real_part + 1j * imag_part

        else:
            copy = np.interp(
                omega - shift,
                omega,
                X,
                left=0,
                right=0
            )

        Xp += copy

    return Xp / T


def ideal_reconstruction_filter(omega, T):
    cutoff = np.pi / T

    H = np.zeros_like(
        omega,
        dtype=float
    )

    H[np.abs(omega) < cutoff] = T

    return H


def reconstructed_spectrum(omega, X, T, copies):
    Xp = simulate_sampling_spectrum(
        omega,
        X,
        T,
        copies
    )

    H = ideal_reconstruction_filter(
        omega,
        T
    )

    return Xp * H

# ============================================================================
# EXTENSION FUNCTIONS
# ============================================================================

def compute_reconstruction_error(omega, X_original, X_reconstructed):
    """
    Computes the error between the original spectrum and the reconstructed spectrum.
    
    Args:
        omega: Array of angular frequencies.
        X_original: Original spectrum array.
        X_reconstructed: Reconstructed spectrum array.
        
    Returns:
        tuple: (max_absolute_error, rms_error)
    """
    diff = np.abs(X_original - X_reconstructed)
    max_error = np.max(diff)
    rms_error = np.sqrt(np.mean(diff**2))
    return max_error, rms_error

def is_perfectly_recovered(omega, X_original, T, copies, tol=1e-10):
    """
    Checks if the spectrum is perfectly recovered after sampling and reconstruction.
    
    Args:
        omega: Array of angular frequencies.
        X_original: Original spectrum array.
        T: Sampling period.
        copies: Number of replicas to consider.
        tol: Tolerance for considering error as negligible.
        
    Returns:
        bool: True if perfectly recovered, False otherwise.
    """
    X_recon = reconstructed_spectrum(omega, X_original, T, copies)
    max_err, _ = compute_reconstruction_error(omega, X_original, X_recon)
    return max_err <= tol

def find_aliasing_regions(omega, X_original, T, copies, tol=1e-5):
    """
    Identifies the frequency bands where aliasing has caused distortion.
    
    Returns:
        list of tuples: [(omega_start, omega_end), ...] for regions where error > tol.
    """
    X_recon = reconstructed_spectrum(omega, X_original, T, copies)
    error = np.abs(X_original - X_recon)
    
    # Find contiguous regions where error > tol
    is_aliased = error > tol
    changes = np.diff(is_aliased.astype(int))
    starts = np.where(changes == 1)[0] + 1
    ends = np.where(changes == -1)[0] + 1
    
    if is_aliased[0]:
        starts = np.insert(starts, 0, 0)
    if is_aliased[-1]:
        ends = np.append(ends, len(is_aliased) - 1)
        
    regions = [(omega[s], omega[e]) for s, e in zip(starts, ends)]
    return regions

# ============================================================================
# MAIN DEMO
# ============================================================================

if __name__ == "__main__":
    # Create frequency grid
    omega = np.linspace(-6000, 6000, 120001)
    
    # Create a bandlimited original spectrum X(j*omega)
    # Let's say it's a triangle pulse in frequency domain (cutoff at W = 1000)
    W = 1000.0
    X = np.maximum(0, 1 - np.abs(omega)/W)
    
    # ---------------------------------------------------------
    # Scenario A: Nyquist Compliant (f_s > 2*f_m => omega_s > 2*W)
    # ---------------------------------------------------------
    T_compliant = np.pi / 1500  # omega_s = 3000 > 2000
    copies = 5
    
    X_recon_ok = reconstructed_spectrum(omega, X, T_compliant, copies)
    max_err_ok, rms_err_ok = compute_reconstruction_error(omega, X, X_recon_ok)
    recovered_ok = is_perfectly_recovered(omega, X, T_compliant, copies)
    
    # ---------------------------------------------------------
    # Scenario B: Nyquist Violating (Aliasing)
    # ---------------------------------------------------------
    T_violating = np.pi / 800  # omega_s = 1600 < 2000
    
    X_recon_bad = reconstructed_spectrum(omega, X, T_violating, copies)
    max_err_bad, rms_err_bad = compute_reconstruction_error(omega, X, X_recon_bad)
    recovered_bad = is_perfectly_recovered(omega, X, T_violating, copies)
    
    aliased_regions = find_aliasing_regions(omega, X, T_violating, copies)
    
    # Plotting
    plt.figure(figsize=(12, 8))
    
    plt.subplot(2, 1, 1)
    plt.plot(omega, np.abs(X), 'k-', label='Original |X|', linewidth=2)
    plt.plot(omega, np.abs(X_recon_ok), 'b--', label='Reconstructed (Compliant)', linewidth=2)
    plt.title(f'Nyquist Compliant: max error = {max_err_ok:.2e}, Perfect recovery: {recovered_ok}')
    plt.legend()
    plt.grid(True)
    plt.xlim(-4000, 4000)
    
    plt.subplot(2, 1, 2)
    plt.plot(omega, np.abs(X), 'k-', label='Original |X|', linewidth=2)
    plt.plot(omega, np.abs(X_recon_bad), 'r--', label='Reconstructed (Violating)', linewidth=2)
    plt.title(f'Nyquist Violating: max error = {max_err_bad:.2e}, Perfect recovery: {recovered_bad}')
    plt.legend()
    plt.grid(True)
    plt.xlim(-4000, 4000)
    
    print("=== Spectrum Repair Pipeline Demo ===")
    print(f"Compliant case (T={T_compliant:.4f}): Max Error = {max_err_ok:.2e}, Recovered = {recovered_ok}")
    print(f"Violating case (T={T_violating:.4f}): Max Error = {max_err_bad:.2e}, Recovered = {recovered_bad}")
    print(f"Aliased regions (Violating): {aliased_regions}")
    
    plt.tight_layout()
    plt.show()
