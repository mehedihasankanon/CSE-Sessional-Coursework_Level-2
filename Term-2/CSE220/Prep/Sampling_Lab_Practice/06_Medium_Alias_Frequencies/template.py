"""Problem 06: Where do cosine frequencies fold?.
Medium; 30-minute practice. Read statement.pdf first.
Run this file directly to print results and display the figure.
"""
import numpy as np
import matplotlib.pyplot as plt

def fold_frequencies(freqs, fs):
    # TODO: Return the equivalent cosine frequencies in [0, fs/2].
    freq_new = np.asarray(freqs, dtype=np.float64) % fs
    
    ans = []
    
    for i in range(len(freqs)):
        ans.append(np.min(freqs[i], freq_new[i]))
        
    return ans

def verify_aliases(freqs, aliases, fs, N):
    t = np.arange(N) / fs
    
    err = []
    
    for i in range(len(freqs)):
        err.append(np.max(np.cos(2 * np.pi * freqs[i] * t), np.cos(aliases[i] * 2 * np.pi * t)))
    
    return err
        

if __name__ == "__main__":
    freqs = np.array([0, 3, 13, 17, 23, 40])
    fs, N = 20.0, 20
    aliases = fold_frequencies(freqs, fs)
    errors = verify_aliases(freqs, aliases, fs, N)
    for f, a, e in zip(freqs, aliases, errors):
        print(f"{f:g} Hz -> {a:g} Hz; sample error={e:.3e}")
    pos = np.arange(len(freqs))
    plt.bar(pos-0.18, freqs, 0.36, label="Original")
    plt.bar(pos+0.18, aliases, 0.36, label="Alias")
    plt.axhline(fs/2, linestyle="--", color="red", label="fs/2")
    plt.xticks(pos, [str(f) for f in freqs]); plt.xlabel("Original component (Hz)")
    plt.ylabel("Frequency (Hz)"); plt.legend(); plt.title("Cosine folding")
    plt.tight_layout(); plt.show()
