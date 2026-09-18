"""Problem 09: When several tones become one.
Hard; 30-minute practice. Read statement.pdf first.
Run this file directly to print results and display the figure.
"""

"""
Background
Consider x(t)=sum A[i] cos(2 pi f[i] t), with zero phase. Under sampling, each cosine folds to an
equivalent frequency in [0,fs/2]. Components landing at the same frequency add their signed
amplitudes. Opposite amplitudes can cancel. A zero-frequency component is a constant (DC).

Your task
Return the folded multitone model after merging collisions and removing groups whose total amplitude
is exactly zero. Then evaluate the original and folded models at the same sample instants and report
their maximum difference. The provided plot displays both continuous models and their samples.

Function contract and assumptions
collapse_tones(freqs, amps, fs) returns (alias_freqs, alias_amps), sorted by ascending frequency.
Use integer frequencies, integer signed amplitudes, and positive even integer fs; duplicate
frequencies are allowed. If all groups cancel, return two empty arrays. evaluate_tones(t, freqs, amps)
returns the signal values at t; an empty model returns zeros. A folding helper is provided.

Example / correctness check
freqs=[3,17,23,7,13,20], amps=[2,1,-1,3,-3,4], fs=20 gives alias_freqs=[0,3], alias_amps=[4,2]. The 7
Hz group cancels; the 3 Hz group sums to 2. With N=40, sample error should be below 1e-10.

Exam instructions
Complete only the TODO functions in template.py and preserve their signatures. The supplied driver
handles printing and plotting. Your functions must work for all valid inputs described above, not just
the example. No external files or packages beyond NumPy and Matplotlib are needed. Small
floating-point differences are acceptable.
"""

import numpy as np
import matplotlib.pyplot as plt
from collections import defaultdict

def given_alias(f, fs):
    """Provided zero-phase cosine folding rule."""
    r = int(f) % fs
    return min(r, fs-r)

def collapse_tones(freqs, amps, fs):
    # Accumulate signed amplitudes for every folded alias frequency
    alias_map = defaultdict(int)
    for f, a in zip(freqs, amps):
        alias_f = given_alias(f, fs)
        alias_map[alias_f] += int(a)
    
    # Filter out groups whose total amplitude canceled to 0, sorted by frequency
    surviving = sorted((f, a) for f, a in alias_map.items() if a != 0)
    
    if not surviving:
        return np.array([], dtype=int), np.array([], dtype=int)
    
    alias_freqs, alias_amps = zip(*surviving)
    return np.array(alias_freqs, dtype=int), np.array(alias_amps, dtype=int)

def evaluate_tones(t, freqs, amps):
    t = np.asarray(t)
    signal = np.zeros_like(t, dtype=float)
    
    # Empty model returns all zeros
    if len(freqs) == 0:
        return signal
        
    for f, a in zip(freqs, amps):
        signal += a * np.cos(2 * np.pi * f * t)
        
    return signal

if __name__ == "__main__":
    freqs = np.array([3, 17, 23, 7, 13, 20])
    amps = np.array([2, 1, -1, 3, -3, 4])
    fs, N = 20, 40
    alias_freqs, alias_amps = collapse_tones(freqs, amps, fs)
    print("Merged frequencies:", alias_freqs)
    print("Merged amplitudes:", alias_amps)
    t_samples = np.arange(N) / fs
    original_samples = evaluate_tones(t_samples, freqs, amps)
    alias_samples = evaluate_tones(t_samples, alias_freqs, alias_amps)
    print("Maximum sample error:", np.max(np.abs(original_samples-alias_samples)))
    t = np.linspace(0, 0.5, 2001)
    plt.plot(t, evaluate_tones(t, freqs, amps), label="Original")
    plt.plot(t, evaluate_tones(t, alias_freqs, alias_amps), "--", label="Folded model")
    plt.plot(t_samples, original_samples, "ko", label="Shared samples")
    plt.xlim(0, 0.5); plt.xlabel("Time (s)"); plt.ylabel("Amplitude")
    plt.title("Alias collisions and cancellation"); plt.legend(); plt.grid(True)
    plt.tight_layout(); plt.show()
