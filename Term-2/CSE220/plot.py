import numpy as np
import matplotlib.pyplot as plt
from typing import Dict, List, Tuple

# =============================================================================
# GUIDELINE 1: THE DISCRETE STEM PLOTTER (Object-Oriented)
# Best for: Digital signals, arrays indexed by integers (n).
# Tricks used: Hiding the ugly default baseline, adding alpha grids.
# =============================================================================
def nice_stem_plot(ax: plt.Axes, n: np.ndarray, x: np.ndarray, label: str, color: str = 'b'):
    """
    Plots a highly readable discrete-time stem plot on a specific axis.
    """
    markerline, stemlines, baseline = ax.stem(n, x, label=label, linefmt=color)
    
    # TRICK: Hide the default solid red baseline which often distracts from the data
    baseline.set_visible(False)
    
    # TRICK: Use an alpha (transparency) on the grid so it doesn't overpower the signal
    ax.grid(True, alpha=0.3)
    ax.set_xlabel("n (Time Index)")
    ax.set_ylabel("Amplitude")


# =============================================================================
# GUIDELINE 2: THE DYNAMIC RANGE WRAPPER (State-Machine)
# Best for: Quick single plots where the signal amplitude might unexpectedly spike.
# Tricks used: Dynamic y-limiting and strict integer x-ticks.
# =============================================================================
def plot_single_discrete(
    n: np.ndarray, 
    signal: np.ndarray, 
    title: str = "Discrete Signal", 
    y_min: float = -1.0, 
    y_max_default: float = 3.0,
    save_path: str = None
):
    """
    Plots a single discrete signal and dynamically scales the Y-axis to prevent 
    the signal from clipping out of the frame.
    """
    plt.figure(figsize=(8, 4))
    
    # TRICK: Force X-ticks to be strict integers (no 1.5, 2.5 on a discrete plot)
    plt.xticks(np.arange(n[0], n[-1] + 1, 1))
    
    # TRICK: Dynamically calculate the top of the Y-axis. 
    # If the signal goes above our default max, expand the roof + 1 for padding.
    dynamic_y_max = max(np.max(signal), y_max_default) + 1.0
    plt.ylim(y_min, dynamic_y_max)
    
    plt.stem(n, signal, linefmt='b-')
    plt.title(title)
    plt.xlabel("n (Time Index)")
    plt.ylabel("x[n]")
    plt.grid(True, alpha=0.4)
    
    if save_path:
        plt.savefig(save_path)
        print(f"Saved plot to {save_path}")


# =============================================================================
# GUIDELINE 3: THE CONTINUOUS OVERLAY (Object-Oriented)
# Best for: Even/Odd Decompositions, comparing original vs. interpolated.
# Tricks used: Dictionary unpacking for infinite flexibility.
# =============================================================================
def plot_continuous_overlay(t: np.ndarray, signals: Dict[str, Tuple[np.ndarray, str]], title: str):
    """
    Plots multiple continuous signals on the same graph safely.
    Expects a dictionary where Key = Label, Value = (Signal Array, Color String)
    """
    plt.figure(figsize=(10, 5))
    
    for label, (sig_data, color) in signals.items():
        # TRICK: Explicitly set line width (lw) for better visibility
        plt.plot(t, sig_data, color=color, lw=2, label=label)
        
    plt.title(title)
    plt.xlabel("Time (t)")
    plt.ylabel("Amplitude")
    plt.grid(True, alpha=0.4)
    
    # TRICK: Adjust legend font size if layering many signals
    plt.legend(fontsize=10, loc='best')


# =============================================================================
# GUIDELINE 4: THE MULTI-PANEL DASHBOARD (Object-Oriented)
# Best for: Before & After comparisons (e.g., Original vs Shifted).
# Tricks used: Subplots array and tight_layout.
# =============================================================================
def plot_comparison_dashboard(
    t: np.ndarray, 
    x_original: np.ndarray, 
    x_transformed: np.ndarray, 
    title_main: str, 
    title_transformed: str
):
    """
    Creates a 1x2 grid of subplots for side-by-side continuous signal comparison.
    """
    # TRICK: fig is the window, axes is an array of the specific plotting boxes
    fig, axes = plt.subplots(nrows=1, ncols=2, figsize=(12, 5))
    
    # Left Panel (Original)
    axes[0].plot(t, x_original, c='blue', lw=2, label='Original')
    axes[0].set_title("Original Signal")
    axes[0].set_xlabel("Time (t)")
    axes[0].set_ylabel("Amplitude")
    axes[0].grid(True, alpha=0.4)
    axes[0].legend()
    
    # Right Panel (Transformed)
    axes[1].plot(t, x_transformed, c='red', lw=2, label='Transformed')
    axes[1].set_title(title_transformed)
    axes[1].set_xlabel("Time (t)")
    axes[1].set_ylabel("Amplitude")
    axes[1].grid(True, alpha=0.4)
    axes[1].legend()
    
    fig.suptitle(title_main, fontsize=14, fontweight='bold')
    
    # TRICK: Always call tight_layout at the very end to prevent text overlapping
    fig.tight_layout()


# =============================================================================
# DEMONSTRATION MAIN FUNCTION
# =============================================================================
def main():
    # --- Setup Fake Data ---
    n = np.arange(-5, 6)
    x_discrete = np.array([0, 0, 0, 0.5, 2.0, 1.0, 0.5, 1.0, 0, 0, 0])
    
    t = np.linspace(-4, 4, 1000)
    x_ct = np.sin(np.pi * t)
    x_ct_even = 0.5 * (x_ct + np.sin(np.pi * (-t))) # Fake even decomp
    x_ct_odd = 0.5 * (x_ct - np.sin(np.pi * (-t)))  # Fake odd decomp
    
    # --- Demo 1: Dynamic Range Single Plot (State-Machine) ---
    # Notice we don't need a plt.show() yet, it just builds the figure in memory
    plot_single_discrete(n, x_discrete, title="Dynamic Range Discrete Stem")
    
    # --- Demo 2: The Multi-Signal Overlay ---
    overlay_dict = {
        "x(t) Original": (x_ct, 'black'),
        "x_e(t) Even": (x_ct_even, 'orange'),
        "x_o(t) Odd": (x_ct_odd, 'green')
    }
    plot_continuous_overlay(t, overlay_dict, title="Even-Odd Overlay Pattern")
    
    # --- Demo 3: The Side-by-Side Dashboard ---
    plot_comparison_dashboard(
        t, 
        x_original=x_ct, 
        x_transformed=np.sin(np.pi * (t - 2)), 
        title_main="System Transformation Analysis",
        title_transformed="x(t - 2)"
    )
    
    # --- Demo 4: Object-Oriented Multi-Stem Plot ---
    fig, axes = plt.subplots(3, 1, figsize=(8, 8))
    nice_stem_plot(axes[0], n, x_discrete, "Original", 'b')
    nice_stem_plot(axes[1], n, np.roll(x_discrete, 2), "Shifted +2", 'r')
    nice_stem_plot(axes[2], n, x_discrete[::-1], "Reversed", 'g')
    
    # Setting titles for the individual axes
    axes[0].set_title("Base Signal")
    axes[1].set_title("Time Shifted")
    axes[2].set_title("Time Reversed")
    fig.tight_layout()
    
    # TRICK: plt.show() acts as a final render command for ALL figures created above.
    # It will open them all in separate windows simultaneously.
    plt.show()


if __name__ == "__main__":
    main()