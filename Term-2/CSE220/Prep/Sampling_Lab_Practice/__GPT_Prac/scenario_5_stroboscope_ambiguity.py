"""
PROBLEM STATEMENT: Scenario 5 — Rotating Wheel / Stroboscope Ambiguity

A camera monitors a rotating machine. A marker generates brightness x(t) = cos(2*pi*f_0*t).
Camera samples at fs = 30 frames/s. Observed apparent frequency: f_a = 7 Hz.
True frequency is only known to satisfy 0 <= f_0 <= 100 Hz.

Implement possible_frequencies(apparent_frequency, fs, max_frequency) returning all continuous frequencies compatible with the observed sampled cosine.
Explain why imposing a band limit (Nyquist) removes the ambiguity.

KEY THEORY:
- Mathematical Derivation: Sampling cannot distinguish frequencies differing by integer multiples of fs.
  A continuous signal x(t) = exp(j*2*pi*f_0*t) sampled at t = n*T yields x[n] = exp(j*2*pi*f_0*n*T).
  If we consider a frequency f = f_0 + k*fs, then:
  x_new[n] = exp(j*2*pi*(f_0 + k*fs)*n*T) = exp(j*2*pi*f_0*n*T) * exp(j*2*pi*k*fs*n*T).
  Since fs*T = 1, exp(j*2*pi*k*n) = 1 for any integer k and n, meaning the samples are exactly identical! Multiple frequencies produce identical samples due to this periodicity in f.
- Logical Reasoning (Cosine Symmetry): For a real cosine signal x(t) = cos(2*pi*f_0*t), there is an additional sign symmetry because cos(-theta) = cos(theta).
  Therefore, the apparent frequency f_a is the absolute value of the aliased frequency.
  This doubles the ambiguity count vs complex exponential, meaning candidates satisfy: f_0 = |k*fs ± f_a| for all integers k.
- Example: For fs=30, f_a=7, the candidates are 7, 23, 37, 53, 67, 83, 97 Hz (all <= 100).
- Real-Life Analogy: The "Wagon Wheel Effect" in western movies. A wagon wheel appears to spin backward or stand still because the camera frame rate creates aliases of the true rotation speed. The camera acts as a stroboscope sampling the continuous motion, and our brain interprets the lowest apparent frequency (f_a).
- Why Nyquist removes ambiguity: Imposing a strict band limit restricts our possible frequencies to 0 <= f_0 < fs/2. In this range [0, fs/2), only exactly ONE candidate survives from the infinite family of |k*fs ± f_a|. This is the Sampling Theorem functioning as an AMBIGUITY ELIMINATION theorem. By guaranteeing f_0 < fs/2, we ensure a unique solution for the true frequency.
"""

import numpy as np
import matplotlib.pyplot as plt

def possible_frequencies(
    apparent_frequency,
    fs,
    max_frequency
):
    candidates = set()

    k = 0

    while k * fs - apparent_frequency <= max_frequency:
        a = abs(k * fs + apparent_frequency)
        b = abs(k * fs - apparent_frequency)

        if a <= max_frequency:
            candidates.add(a)

        if b <= max_frequency:
            candidates.add(b)

        k += 1

    return sorted(candidates)


def unique_under_nyquist(apparent_frequency, fs):
    """
    Extension 1: Returns the single unique frequency if Nyquist is satisfied.
    If we enforce the Nyquist condition (f < fs/2), the apparent frequency 
    must be the unique true frequency.
    
    Parameters:
    - apparent_frequency: The observed frequency f_a.
    - fs: The sampling rate.
    
    Returns:
    - The unique true frequency.
    """
    if apparent_frequency >= fs / 2.0:
        raise ValueError("Apparent frequency violates Nyquist condition.")
    return apparent_frequency


def stroboscopic_demo(f_true, fs, duration):
    """
    Extension 2: Simulates the stroboscopic effect by calculating the apparent 
    frequency when sampling cos(2*pi*f_true*t) at fs, and lists all ambiguous 
    candidates up to 2*fs.
    
    Parameters:
    - f_true: The actual rotation frequency.
    - fs: The camera frame rate (sampling frequency).
    - duration: duration parameter (unused here but kept for signature).
    
    Returns:
    - Tuple of (apparent_frequency, list of ambiguous candidates).
    """
    # Calculate apparent frequency using modulo arithmetic taking into account cosine symmetry
    f_a = abs(f_true - round(f_true / fs) * fs)
    print(f"[Demo] True frequency: {f_true} Hz, fs: {fs} Hz -> Apparent frequency: {f_a} Hz")
    
    candidates = possible_frequencies(f_a, fs, max_frequency=2*fs)
    print(f"[Demo] Ambiguous candidates up to {2*fs} Hz: {candidates}")
    return f_a, candidates


def count_ambiguities(f_a, fs, f_max):
    """
    Extension 3: Returns how many ambiguous frequencies exist for a given setup.
    
    Parameters:
    - f_a: Apparent frequency.
    - fs: Sampling frequency.
    - f_max: Maximum possible true frequency limit.
    
    Returns:
    - Number of valid candidate frequencies.
    """
    return len(possible_frequencies(f_a, fs, f_max))


def find_minimum_fs_for_unique(f_max):
    """
    Extension 4: Returns the minimum sampling frequency (fs) that guarantees 
    unique frequency identification for all frequencies in [0, f_max].
    
    According to Nyquist, we need fs > 2*f_max to ensure no aliasing 
    occurs for the highest possible frequency in the band.
    
    Parameters:
    - f_max: The maximum possible frequency of the signal.
    
    Returns:
    - Minimum fs to avoid ambiguity.
    """
    return 2.0 * f_max


if __name__ == "__main__":
    fs_system = 30
    f_apparent = 7
    max_f = 100
    
    print("=====================================================")
    print("      Scenario 5: Stroboscope Ambiguity Test         ")
    print("=====================================================")
    
    candidates = possible_frequencies(f_apparent, fs_system, max_f)
    print(f"\nGiven fs = {fs_system} Hz, apparent f_a = {f_apparent} Hz")
    print(f"Possible true frequencies up to {max_f} Hz:")
    print(candidates)
    
    print("\n>>> Demonstrating that band-limiting removes ambiguity:")
    print(f"If we enforce Nyquist limit f_0 < fs/2 ({fs_system/2} Hz):")
    band_limited_candidates = [f for f in candidates if f < fs_system/2]
    print(f"Surviving candidates: {band_limited_candidates}")
    if len(band_limited_candidates) == 1:
        print("-> Ambiguity eliminated! The unique true frequency is found.")
        
    print("\n=====================================================")
    print("                Testing Extensions                   ")
    print("=====================================================")
    
    print(f"1. Unique under Nyquist (f_a=7, fs=30): {unique_under_nyquist(7, 30)} Hz")
    
    print("\n2. Stroboscopic Demo:")
    stroboscopic_demo(f_true=67, fs=30, duration=1.0)
    
    print("\n3. Ambiguity Count:")
    count = count_ambiguities(f_apparent, fs_system, max_f)
    print(f"Number of ambiguous frequencies for f_a={f_apparent}, fs={fs_system}, max_f={max_f}: {count}")
    
    print("\n4. Minimum fs for unique ID:")
    min_fs = find_minimum_fs_for_unique(max_f)
    print(f"Minimum fs to guarantee unique ID up to {max_f} Hz: > {min_fs} Hz")

