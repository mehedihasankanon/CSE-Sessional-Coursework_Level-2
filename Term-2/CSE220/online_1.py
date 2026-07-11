import numpy as np

# =============================================================================
# 1. ENVELOPE GENERATORS (The Symmetric Grid Method)
# =============================================================================

def get_ct_envelope(t_min: float, t_max: float, N: int = 4001) -> np.ndarray:
    """Creates a symmetric continuous-time grid safely encompassing the bounds."""
    M = max(abs(t_min), abs(t_max))
    return np.linspace(-M, M, N)

def get_dt_envelope(n_min: int, n_max: int) -> np.ndarray:
    """Creates a symmetric discrete-time integer grid."""
    M = int(max(abs(n_min), abs(n_max)))
    return np.arange(-M, M + 1)


# =============================================================================
# 2. UNIVERSAL TRANSFORMATIONS (Works for both CT and DT grids)
# =============================================================================

def time_reverse(x: np.ndarray, method: str = 'slice') -> np.ndarray:
    """
    Time Reversal: x(-t) or x[-n]
    Requires the underlying time grid to be symmetric around 0.
    
    Methods:
    - 'slice': Standard Python array slicing (Fastest).
    - 'flip': Uses NumPy's built-in np.flip (From solve2.py).
    - 'two_pointer': Brute-force while loop swapping elements (From solve3.py).
    """
    if method == 'slice':
        return x[::-1]
        
    elif method == 'flip':
        # From solve2.py: Built in numpy wrapper for flipping arrays
        return np.flip(x)
        
    elif method == 'two_pointer':
        # From solve3.py & solve_C.py: Manual element assignment
        x_rev = np.empty(len(x))
        i, j = 0, len(x) - 1
        while j >= 0:
            x_rev[i] = x[j]
            i += 1
            j -= 1
        return x_rev


def even_odd_decompose(x: np.ndarray) -> tuple[np.ndarray, np.ndarray]:
    """
    Even-Odd Decomposition.
    Mathematically identical for CT and DT, provided the grid is symmetric.
    """
    x_rev = time_reverse(x, method='slice')
    x_e = 0.5 * (x + x_rev)
    x_o = 0.5 * (x - x_rev)
    return x_e, x_o


# =============================================================================
# 3. DISCRETE-TIME (DT) TRANSFORMATIONS
# =============================================================================

def dt_shift(x: np.ndarray, n0: int, method: str = 'roll') -> np.ndarray:
    """
    Discrete Time Shift: x[n - n0]
    
    Methods:
    - 'roll': Uses np.roll and explicitly zeroes out wrapped edges (From solve.py).
    - 'loop': Manually checks bounds before assignment (From solve.py).
    """
    N = len(x)
    y = np.zeros_like(x)
    
    if method == 'roll':
        # From solve.py: np.roll shifts the array, but wraps elements around.
        # We must zero out the wrapped elements manually to simulate true shifting.
        y = np.roll(x, n0)
        if n0 > 0:  # Right shift
            y[:n0] = 0
        elif n0 < 0: # Left shift
            y[n0:] = 0
            
    elif method == 'loop':
        # From solve.py (original implementation): Bounds checking
        for i in range(N):
            if i - n0 < 0 or i - n0 >= N:
                y[i] = 0
            else:
                y[i] = x[i - n0]
                
    return y


def dt_scale_decimate(x: np.ndarray, k: int, center_idx: int = None, method: str = 'is_integer') -> np.ndarray:
    """
    Discrete Time Scale (Compression): x[k * n]
    
    Methods:
    - 'is_integer': Computes source index and checks if it's a whole number (From solve1.py).
    - 'slice': Explicitly slices positive and negative sides from the center (From solve.py).
    """
    N = len(x)
    y = np.zeros_like(x)
    
    # If center isn't provided, assume it's the exact middle of the array
    if center_idx is None:
        center_idx = N // 2
        
    if method == 'is_integer':
        # From solve1.py: Works backward from output n to check if source n exists
        for i in range(N):
            n = i - center_idx
            source_n = n / k
            if source_n.is_integer():
                source_index = int(source_n) + center_idx
                if 0 <= source_index < N:
                    y[i] = x[source_index]
                    
    elif method == 'slice':
        # From solve.py: Manually slices outward from the origin
        y[center_idx] = x[center_idx]
        
        # Positive side
        pos = x[center_idx + k :: k]
        y[center_idx + 1 : center_idx + 1 + len(pos)] = pos
        
        # Negative side
        neg = x[center_idx - k :: -k]
        y[center_idx - len(neg) : center_idx] = neg[::-1]
        
    return y


# =============================================================================
# 4. CONTINUOUS-TIME (CT) INTERPOLATION & SCALING
# =============================================================================

def ct_interpolate(t_orig: np.ndarray, x_orig: np.ndarray, t_query: np.ndarray, method: str = 'dt_index') -> np.ndarray:
    """
    Core interpolation engine for CT scaling.
    
    Methods:
    - 'dt_index': Calculates index mathematically using uniform dt spacing (From solve_A.py).
    - 'searchsorted': Advanced NumPy search with exact match handling (From interpolate.py).
    - 'vectorized_avg': Maps indices using np.floor/ceil and averages them (From solve1.py).
    - 'interp': Standard np.interp (Safest shortcut).
    """
    N = len(x_orig)
    
    if method == 'dt_index':
        # From solve_A.py: Assumes t_orig is perfectly uniform.
        # It calculates the physical array index mathematically without searching.
        dt = t_orig[1] - t_orig[0]
        idx_float = (t_query - t_orig[0]) / dt 
        
        idx_left = np.floor(idx_float).astype(int)
        idx_right = np.ceil(idx_float).astype(int)
        
        # Prevent out of bounds
        idx_left = np.clip(idx_left, 0, N - 1)
        idx_right = np.clip(idx_right, 0, N - 1)
        
        return 0.5 * (x_orig[idx_left] + x_orig[idx_right])

    elif method == 'searchsorted':
        # From interpolate.py: Robust method that works even if t_orig is NOT uniform.
        idx = np.searchsorted(t_orig, t_query, side='left')
        idx_clipped = np.clip(idx, 0, N - 1)
        
        # Handle exact matches directly
        exact_match = (idx < N) & np.isclose(t_orig[idx_clipped], t_query)
        
        left_idx = np.clip(idx - 1, 0, N - 1)
        right_idx = np.clip(idx, 0, N - 1)
        
        averaged = 0.5 * (x_orig[left_idx] + x_orig[right_idx])
        result = np.where(exact_match, x_orig[idx_clipped], averaged)
        
        # NaN out anything strictly out of range
        out_of_range = (t_query < t_orig[0]) | (t_query > t_orig[-1])
        return np.where(out_of_range, 0.0, result) # Changed NaN to 0.0 for DSP safety

    elif method == 'vectorized_avg':
        # From solve1.py (Adapted for general grids)
        center_idx = N // 2
        # Transform the target queries back into index-space assuming uniform spacing
        n_query = np.arange(len(t_query)) - center_idx
        # Calculate the expansion factor implicitly
        k = t_orig[1] / (t_query[1] - t_query[0]) if len(t_query) > 1 else 1
        source_n = n_query / k
        
        left = np.floor(source_n).astype(int) + center_idx
        right = np.ceil(source_n).astype(int) + center_idx
        
        left = np.clip(left, 0, N - 1)
        right = np.clip(right, 0, N - 1)
        
        y = 0.5 * (x_orig[left] + x_orig[right])
        valid = (source_n >= -center_idx) & (source_n <= N - center_idx - 1)
        y[~valid] = 0
        return y
        
    elif method == 'interp':
        # Standard shortcut
        return np.interp(t_query, t_orig, x_orig, left=0.0, right=0.0)


def ct_scale(t: np.ndarray, x: np.ndarray, k: float, method: str = 'dt_index') -> np.ndarray:
    """
    Continuous Time Scaling: y(t) = x(t / k)
    Routes the query timeline (t / k) into the selected interpolation engine.
    """
    t_query = t / k
    return ct_interpolate(t, x, t_query, method=method)


def ct_affine(t: np.ndarray, x: np.ndarray, a: float, b: float, method: str = 'dt_index') -> np.ndarray:
    """
    Continuous General Affine: x(at + b)
    """
    t_query = a * t + b
    return ct_interpolate(t, x, t_query, method=method)


# =============================================================================
# 5. SINUSOIDAL GENERATORS (From solve_B.py / solve4.py)
# =============================================================================

def sinusoid(n: np.ndarray, A: float, Omega0: float, phi: float) -> np.ndarray:
    """Generates standard discrete sinusoid."""
    return A * np.cos(Omega0 * n + phi)

def time_shift_sinusoid(n: np.ndarray, A: float, Omega0: float, phi: float, n0: int) -> np.ndarray:
    """Calculates x[n + n0] or x[n - n0] mathematically inside the cosine."""
    return A * np.cos(Omega0 * (n + n0) + phi)

def phase_change_sinusoid(n: np.ndarray, A: float, Omega0: float, phi: float, phi0: float) -> np.ndarray:
    """Calculates phase offset mathematically inside the cosine."""
    return A * np.cos(Omega0 * n + phi + phi0)