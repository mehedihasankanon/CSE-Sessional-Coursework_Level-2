"""Problem 01: Uniform sampling of a sinusoid.
Easy; 30-minute practice. Read statement.pdf first.
Run this file directly to print results and display the figure.
"""
import numpy as np
import matplotlib.pyplot as plt

def sample_sine(A, f, phi, fs, N):
    Ts = 1/fs
    
    # t = np.arange(0, N * Ts, Ts)
    
    t = np.arange(N) / fs
    
    return t, A * np.sin(2 * np.pi * f * t + phi)

if __name__ == "__main__":
    A, f, phi, fs, N = 2.0, 3.0, 0.0, 20.0, 20
    # A=2 
    # f=1
    # phi=0
    # fs=4
    # N=4
    t, x = sample_sine(A, f, phi, fs, N)
    print("First five times:", t[:5])
    print("First five samples:", x[:5])
    t_ref = np.linspace(0, t[-1], 2000)
    plt.plot(t_ref, A * np.sin(2 * np.pi * f * t_ref + phi), label="Reference")
    plt.stem(t, x, linefmt="C1-", markerfmt="C1o", basefmt=" ", label="Samples")
    plt.xlabel("Time (s)"); plt.ylabel("Amplitude"); plt.legend(); plt.grid(True)
    plt.title("Uniform sampling"); plt.tight_layout(); plt.show()
