"""
PROBLEM STATEMENT:
Scenario 3 — The "Perfect" Signal Recovery System That Is Secretly Wrong

A previous student has written a buggy reconstruction function:
def reconstruct(samples, T, t):
    y = np.zeros_like(t)
    for n in range(len(samples)):
        y += T * samples[n] * np.sinc(t - n*T)
    return y

This code contains three major errors:
1. ERROR: np.sinc(t - n*T) should be np.sinc((t - n*T) / T)
   Why: The sinc function np.sinc(x) has zero crossings at non-zero integers. We want zero 
   crossings at multiples of T, so we must divide by T.
2. ERROR: The formula incorrectly multiplies by T.
   Why: The ideal interpolation formula has no extra T factor. The ideal low-pass filter gain T
   cancels exactly with the 1/T factor from the sampled signal's spectrum.
3. ERROR: Assumes sample indices start at 0.
   Why: It fails for negative indices like n = -5, -4, ..., 5. A general reconstruction function
   must accept explicit sample indices.

DETAILED MATHEMATICAL THEORY:
The correct formula for ideal reconstruction is:
x_r(t) = sum_n x(n*T) * sinc((t - n*T) / T)

Why does this perfectly reproduce the original sample values?
At t = m*T (where m is an integer), the argument of the sinc function becomes:
((m*T) - (n*T)) / T = m - n

So, x_r(m*T) = sum_n x(n*T) * sinc(m - n)
Since sinc(0) = 1 and sinc(nonzero integer) = 0:
x_r(m*T) = x(m*T)

This is WHY sinc reconstruction perfectly reproduces sample values at the sampling instants, 
regardless of whether aliasing occurred!

ANALOGY:
Think of each sample as placing a sinc-shaped "tent" at that sample location. At the exact 
sample point, only the tent for that sample has a height equal to the sample value, and all 
other tents cross exactly at 0. When we add them all up, the sum at each sampling point is 
exactly the original sample value.

Vectorized Kernel Matrix vs Loop:
The vectorized approach `np.sinc((query_times[:, None] - sample_times[None, :]) / T)` builds a 
2D matrix (kernel) where each row is a time point and each column is a sample index. Multiplying 
this matrix by the samples vector computes the exact same sum as the loop approach, but much faster.
"""

import numpy as np
import matplotlib.pyplot as plt

# THE BUGGY VERSION (preserved for reference)
def reconstruct_buggy(samples, T, t):
    y = np.zeros_like(t)
    for n in range(len(samples)):
        y += T * samples[n] * np.sinc(t - n*T)
    return y

# THE CORRECT VERSION
def ideal_reconstruct(samples, indices, T, query_times):
    samples = np.asarray(samples)
    indices = np.asarray(indices)
    query_times = np.asarray(query_times)

    sample_times = indices * T

    kernel = np.sinc(
        (query_times[:, None] - sample_times[None, :]) / T
    )

    return kernel @ samples

# EXTENSION FUNCTIONS

def ideal_reconstruct_loop(samples, indices, T, query_times):
    """
    The readable loop-based version of ideal reconstruction.
    Slower than vectorized but easier to understand under exam pressure.
    """
    y = np.zeros_like(query_times, dtype=np.float64)
    for n, val in zip(indices, samples):
        y += val * np.sinc((query_times - n * T) / T)
    return y

def diagnose_errors(samples, indices, T, query_times):
    """
    Runs the buggy version (assuming indices start at 0) and the correct version
    side-by-side and prints a diagnostic report.
    """
    print("--- DIAGNOSTIC REPORT ---")
    correct_y = ideal_reconstruct(samples, indices, T, query_times)
    buggy_y = reconstruct_buggy(samples, T, query_times)
    
    print(f"Correct reconstruction max amplitude: {np.max(np.abs(correct_y)):.4f}")
    print(f"Buggy reconstruction max amplitude: {np.max(np.abs(buggy_y)):.4f}")
    print(f"Difference (MSE): {np.mean((correct_y - buggy_y)**2):.4f}")
    print("Why they differ:")
    print("1. Buggy version scales by T.")
    print("2. Buggy version's sinc is not scaled by 1/T inside the argument.")
    print("3. Buggy version shifts by n*T where n is 0,1,2... not the actual indices.")

def verify_sample_reproduction(samples, indices, T):
    """
    Verifies that x_r(n*T) = x[n] for all sample indices.
    Proves the sinc(0)=1 and sinc(nonzero integer)=0 property.
    """
    sample_times = indices * T
    reconstructed_at_samples = ideal_reconstruct(samples, indices, T, sample_times)
    
    max_error = np.max(np.abs(reconstructed_at_samples - samples))
    print("\n--- VERIFYING SAMPLE REPRODUCTION ---")
    print(f"Maximum reconstruction error at sample times: {max_error:.2e}")
    if max_error < 1e-10:
        print("Success: Reconstruction perfectly reproduced sample values!")
    else:
        print("Failed: Sample values not exactly reproduced.")

def reconstruct_with_dtype(samples, indices, T, query_times):
    """
    Handles mixed real/complex samples using np.result_type to ensure
    the output array can hold the correct type.
    """
    samples = np.asarray(samples)
    query_times = np.asarray(query_times)
    indices = np.asarray(indices)
    
    # Determine the result type based on inputs
    out_dtype = np.result_type(samples, query_times)
    
    y = np.zeros_like(query_times, dtype=out_dtype)
    for n, val in zip(indices, samples):
        y += val * np.sinc((query_times - n * T) / T)
    return y

if __name__ == "__main__":
    # Example usage
    T = 0.1
    t = np.linspace(-1, 1, 500)
    
    # Let's sample a simple sine wave
    indices = np.arange(-5, 6)
    samples = np.sin(2 * np.pi * 1.5 * indices * T)
    
    y_correct = ideal_reconstruct(samples, indices, T, t)
    
    plt.figure(figsize=(10, 6))
    plt.plot(t, y_correct, label="Correct Reconstruction")
    plt.stem(indices * T, samples, 'r', label="Samples")
    plt.title("Scenario 3: Debugging Ideal Reconstruction")
    plt.xlabel("Time (s)")
    plt.ylabel("Amplitude")
    plt.legend()
    plt.grid(True)
    plt.show()
    
    diagnose_errors(samples, indices, T, t)
    verify_sample_reproduction(samples, indices, T)
