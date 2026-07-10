import numpy as np
import matplotlib.pyplot as plt

# ----------------------------
# Time axis
# ----------------------------
T_MIN, T_MAX, N = -4.0, 4.0, 4001


def x_of_t(t: np.ndarray) -> np.ndarray:
    """
    Base signal x(t): sinusoidal signal
    """
    return (
        np.sin(2 * np.pi * 0.5 * t)
        + 0.5 * np.sin(2 * np.pi * 1.5 * t)
    )


# ==========================================================
# ANSWER IMPLEMENTATION
# ==========================================================

# def interpolate_signal(
#     t_original: np.ndarray,
#     x_original: np.ndarray,
#     t_query: np.ndarray
# ) -> np.ndarray:
#     """
#     Interpolate using average of two neighboring samples.
#     """
    
#     x_new = np.zeros_like(x_original)
    
#     t_out = t_query
#     t_in = t_original
    
#     mask = (t_out <= T_MAX) & (t_out >= T_MIN)
    
#     # Calculate the uniform spacing (dt) of the original timeline
#     dt = (T_MAX - T_MIN) / (len(t_in) - 1)
    
#     # Find the fractional index locations where the queried times land
#     float_idx = (t_out[mask] - T_MIN) / dt
    
#     # Round to the nearest 0.5 to eliminate floating-point precision noise
#     float_idx = np.round(float_idx * 2) / 2
    
#     # Identify the exact left and right neighbor indices
#     left_idx = np.floor(float_idx).astype(int)
#     right_idx = np.ceil(float_idx).astype(int)
    
#     # Calculate missing values using the average of the two neighbors
#     # (If it lands exactly on a sample, left_idx == right_idx, yielding the exact value)
#     x_new[mask] = 0.5 * (x_original[left_idx] + x_original[right_idx])
    
#     return x_new

def interpolate_signal(
    t_original: np.ndarray,
    x_original: np.ndarray,
    t_query: np.ndarray
) -> np.ndarray:
    """
    Interpolate using linear weighted combination of two neighboring samples.
    """
    # 1. Find the right neighbor index for every query point
    idx_right = np.searchsorted(t_original, t_query)
    
    # 2. Clip indices to prevent out-of-bounds array access at the edges
    idx_right = np.clip(idx_right, 1, len(t_original) - 1)
    idx_left = idx_right - 1
    
    # 3. Extract the surrounding anchor time values and signal values
    t_L = t_original[idx_left]
    t_R = t_original[idx_right]
    x_L = x_original[idx_left]
    x_R = x_original[idx_right]
    
    # 4. Calculate how far between the left and right neighbor each query sits
    # (dt is the spacing between original points, weight ranges from 0.0 to 1.0)
    dt = t_R - t_L
    dt = np.where(dt == 0, 1e-9, dt)  # Guard against division by zero
    weight = (t_query - t_L) / dt
    
    # 5. Apply the standard linear interpolation formula
    x_interp = x_L + weight * (x_R - x_L)
    
    return x_interp


def time_scale(
    t: np.ndarray,
    x: np.ndarray,
    k: int
) -> np.ndarray:
    """
    Time sub-scaling:
        y(t) = x(t / k)
    """
    # Changed from t * k to t / k to correctly achieve time sub-scaling (expansion)
    t_new = t / k
    
    return interpolate_signal(t, x, t_new)
    

def plot_pair(t: np.ndarray, x: np.ndarray, y: np.ndarray, title: str):
    """
    Plot graphs.
    """
    plt.figure(figsize=(10, 6))
    
    # Plot original signal
    plt.plot(t, x, label=r"$x(t)$ (Original)", color="blue", linewidth=1.5)
    
    # Plot scaled signal
    plt.plot(t, y, label=r"$y(t) = x(t/k)$ (Time Sub-scaled)", color="orange", linestyle="--", linewidth=1.5)
    
    # Layout and Labels
    plt.title(title, fontsize=14, fontweight='bold')
    plt.xlabel("Time (t)", fontsize=12)
    plt.ylabel("Amplitude", fontsize=12)
    plt.xlim(T_MIN, T_MAX)
    plt.grid(True, linestyle=":", alpha=0.6)
    plt.legend(fontsize=11, loc="upper right")
    plt.tight_layout()


# ----------------------------
# Main
# ----------------------------
def main():
    t = np.linspace(T_MIN, T_MAX, N)
    x = x_of_t(t)

    k = 2   # sub-scaling factor
    y = time_scale(t, x, k)

    plot_pair(
        t,
        x,
        y,
        title=f"Time Sub-scaling: y(t) = x(t / {k})"
    )
    plt.show()


if __name__ == "__main__":
    main()