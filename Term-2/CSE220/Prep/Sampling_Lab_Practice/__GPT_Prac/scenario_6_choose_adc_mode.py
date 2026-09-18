"""
PROBLEM STATEMENT (Scenario 6 — Choose the Correct ADC Mode)
============================================================
An acquisition device offers these sampling modes: AVAILABLE_FS = [250, 500, 800, 1000, 1600, 2400]

A signal is described as a list of sinusoidal components:
  components = [(amplitude, frequency_hz, phase), ...]
Example:
  components = [(1.0, 75, 0.0), (0.4, 180, 0.3), (0.2, 390, -0.7)]

We need to implement:
1. valid_modes(components) — returns all ADC rates satisfying Nyquist (fs > 2*f_max)
2. sample_and_reconstruct(components, fs, duration, render_fs) — generates signal, samples, sinc-reconstructs on dense grid

DETAILED MATHEMATICAL THEORY
============================
- The maximum frequency f_max in a signal is the maximum of the absolute values of the frequencies of its components. We use abs() because negative frequency components represent the same physical rate of oscillation (bandwidth).
- Nyquist criterion: We require a strict inequality fs > 2*f_max. This strictly follows the lecture's convention to avoid the ambiguity of sampling exactly at zero crossings of a 2*f_max component.
- Signal generation: A composite signal is generated as:
  x(t) = sum_i A_i * cos(2*pi*f_i*t + phi_i)
- Sampling: We sample on a uniform grid t = np.arange(0, duration, 1/fs)
- Reconstruction: The ideal interpolation uses a sinc kernel:
  x_r(t) = sum_n x(n*T) * sinc((t - n*T) / T)
  In matrix form: K[i,j] = sinc((t_render[i] - t_sample[j]) / T)
- Analogy: Choosing the right sampling rate is like choosing the right frame rate for a camera. If the frame rate is too slow, fast motion blurs or appears to move backwards (aliasing). The minimum safe speed depends on the fastest moving object in the scene.
"""

import numpy as np
import matplotlib.pyplot as plt

AVAILABLE_FS = [
    250,
    500,
    800,
    1000,
    1600,
    2400
]


def max_frequency(components):
    if not components:
        return 0.0

    return max(
        abs(freq)
        for _, freq, _ in components
    )


def valid_modes(components):
    fmax = max_frequency(components)

    return [
        fs
        for fs in AVAILABLE_FS
        if fs > 2 * fmax
    ]


def make_signal(components):
    def signal(t):
        t = np.asarray(t)

        y = np.zeros_like(t, dtype=float)

        for amplitude, frequency, phase in components:
            y += amplitude * np.cos(
                2*np.pi*frequency*t + phase
            )

        return y

    return signal


def sample_and_reconstruct(
    components,
    fs,
    duration,
    render_fs
):
    signal = make_signal(components)

    T = 1 / fs

    sample_times = np.arange(
        0,
        duration,
        T
    )

    samples = signal(sample_times)

    render_times = np.arange(
        0,
        duration,
        1/render_fs
    )

    kernel = np.sinc(
        (
            render_times[:, None]
            - sample_times[None, :]
        ) / T
    )

    reconstructed = kernel @ samples

    return (
        sample_times,
        samples,
        render_times,
        reconstructed
    )


# --- EXTENSION FUNCTIONS ---

def optimal_mode(components):
    """
    Returns the lowest valid sampling rate (most efficient).
    If no valid modes are found, returns None.
    """
    modes = valid_modes(components)
    if modes:
        return min(modes)
    return None


def reconstruction_error(components, fs, duration, render_fs):
    """
    Computes and returns the RMS error between original and reconstructed signals.
    """
    _, _, render_times, reconstructed = sample_and_reconstruct(components, fs, duration, render_fs)
    signal = make_signal(components)
    original = signal(render_times)
    error = np.sqrt(np.mean((original - reconstructed)**2))
    return error


def compare_modes(components, duration, render_fs):
    """
    Runs reconstruction at all available modes and plots results with RMS error.
    """
    signal = make_signal(components)
    render_times = np.arange(0, duration, 1/render_fs)
    original = signal(render_times)
    
    num_modes = len(AVAILABLE_FS)
    fig, axes = plt.subplots(num_modes, 1, figsize=(10, 2*num_modes), sharex=True)
    if num_modes == 1:
        axes = [axes]
        
    for ax, fs in zip(axes, AVAILABLE_FS):
        try:
            st, s, rt, recon = sample_and_reconstruct(components, fs, duration, render_fs)
            error = np.sqrt(np.mean((original - recon)**2))
            ax.plot(rt, original, 'k--', alpha=0.5, label="Original")
            ax.plot(rt, recon, 'b-', alpha=0.7, label=f"Reconstructed (fs={fs})")
            ax.stem(st, s, linefmt='r-', markerfmt='ro', basefmt='r-')
            ax.set_title(f"fs = {fs} Hz | RMS Error = {error:.4f}")
            ax.legend()
        except Exception as e:
            ax.set_title(f"fs = {fs} Hz | Failed: {e}")
            
    plt.tight_layout()
    plt.show()


def handle_negative_frequencies(components):
    """
    Demonstrates that negative freq in component list is handled by abs()
    """
    print("--- Handling Negative Frequencies ---")
    print(f"Original Components: {components}")
    
    neg_components = [(amp, -freq, phase) for amp, freq, phase in components]
    print(f"Components with Negative Frequencies: {neg_components}")
    
    fmax1 = max_frequency(components)
    fmax2 = max_frequency(neg_components)
    print(f"Max Frequency (Original): {fmax1} Hz")
    print(f"Max Frequency (Negative): {fmax2} Hz")
    print(f"Valid modes are identical: {valid_modes(components) == valid_modes(neg_components)}")
    print("-------------------------------------")


if __name__ == "__main__":
    components = [(1.0, 75, 0.0), (0.4, 180, 0.3), (0.2, 390, -0.7)]
    
    print("--- ADC Mode Selection ---")
    f_max = max_frequency(components)
    print(f"Max Frequency: {f_max} Hz")
    print(f"Nyquist Threshold (> 2*f_max): > {2*f_max} Hz")
    
    modes = valid_modes(components)
    print(f"Valid ADC Modes: {modes}")
    
    opt_mode = optimal_mode(components)
    print(f"Optimal ADC Mode: {opt_mode} Hz")
    
    handle_negative_frequencies(components)
    
    if opt_mode:
        duration = 0.05
        render_fs = 10000
        print(f"Reconstruction Error (fs={opt_mode}): {reconstruction_error(components, opt_mode, duration, render_fs):.6f}")
        
        # Uncomment to view the plot comparisons
        # compare_modes(components, duration, render_fs)
