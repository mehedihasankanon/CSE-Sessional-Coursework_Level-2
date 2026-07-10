import numpy as np
import matplotlib.pyplot as plt

INF = 8

def plot(
        signal, 
        title=None, 
        y_range=(-1, 3), 
        figsize = (8, 4),
        x_label='n (Time Index)',
        y_label='x[n]',
        saveTo=None
    ):
    plt.figure(figsize=figsize)
    plt.xticks(np.arange(-INF, INF + 1, 1))
    
    y_range = (y_range[0], max(np.max(signal), y_range[1]) + 1)
    # set y range of 
    plt.ylim(*y_range)
    plt.stem(np.arange(-INF, INF + 1, 1), signal)
    plt.title(title)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.grid(True)
    if saveTo is not None:
        plt.savefig(saveTo)
    # plt.show()
    

def plot_overlap(
        signal1,
        signal2, 
        title=None, 
        y_range=(-1, 3), 
        figsize = (8, 4),
        x_label='n (Time Index)',
        y_label='x[n]',
        saveTo=None
    ):
    plt.figure(figsize=figsize)
    plt.xticks(np.arange(-INF, INF + 1, 1))
    
    y_range = (y_range[0], max(np.max(signal1), np.max(signal2), y_range[1]) + 1)
    # set y range of 
    plt.ylim(*y_range)
    plt.stem(np.arange(-INF, INF + 1, 1), signal1, label='Signal 1')
    plt.stem(np.arange(-INF, INF + 1, 1), signal2, label='Signal 2', markerfmt='ro', basefmt='grey')
    plt.title(title)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.legend()
    plt.grid(True)
    if saveTo is not None:
        plt.savefig(saveTo)
    # plt.show()

def init_signal():
    return np.zeros(2 * INF + 1)


def time_shift_signal(x : np.ndarray, k : int, t=True) -> np.ndarray:
    if k == 0:
        return x.copy()
    
    zeros = np.zeros(abs(k), dtype=x.dtype)
    
    if k > 0:
        # Delay (Right Shift): Prepend zeros, discard trailing elements
        return np.concatenate((zeros, x[:-k]))
    else:
        # Advance (Left Shift): Append zeros, discard leading elements
        k_abs = abs(k)
        return np.concatenate((x[k_abs:], zeros))

# def time_scale_signal(x : np.ndarray, k : int, downsample : bool = True) -> np.ndarray:
#     """
#     Implements `y[n] = x[k*n]` on a fixed grid from `-INF` to `INF`.
#     """
#     temp = np.zeros_like(x)
    
#     # Generate the relative time indices from -INF to INF
#     n_out = np.arange(-INF, INF + 1)
    
#     # Calculate where these indices map to in the original signal
#     # (remember that n_out maps to n_in / k)
#     n_in = k * n_out
    
#     print(f'n_in: {n_in}')
#     print(f'n_out: {n_out}')
    
#     # Create a mask to only pick indices that stay within our fixed grid boundaries
#     bounds_mask = (n_in >= -INF) & (n_in <= INF)
#     # this is an array of boolean values, True where n_in is within bounds, False otherwise
#     # so this looks like: array([False, False, False, False,  True,  True,  True,  True,  True,  True, True, True, True, False, False, False, False])
    
#     # when we apply this to n_in and n_out, we only keep the values where the mask is True
#     # this is why even if some value in n_out is inside the bounds, they are truncated:
    
#     # n_in:                 [-16 -14 -12 -10  -8  -6  -4  -2   0   2   4   6   8  10  12  14  16]
#     # n_in[bounds_mask]:    [-8 -6 -4 -2  0  2  4  6  8]
    
#     # n_out:                [-8 -7 -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6  7  8]
#     # n_out[bounds_mask]:   [-4 -3 -2 -1  0  1  2  3  4]
    
#     print(f'n_in[bounds_mask]: {n_in[bounds_mask]}')
#     print(f'n_out[bounds_mask]: {n_out[bounds_mask]}')
    
#     # Map the valid scaled indices back to array positions (offsetting by INF)
#     # why do we offset by INF? 
#     temp[n_out[bounds_mask] + INF] = x[n_in[bounds_mask] + INF]
    
#     return temp


def time_scale_signal(x : np.ndarray, k : int, downsample : bool = True) -> np.ndarray:
    # Create the base time axis [-8, -7, ..., 8]
    n_out = np.arange(-INF, INF + 1) 
    
    if downsample:
        # --- SQUISHING (x[k*n]) ---
        n_in = k * n_out
        mask = (n_in <= INF) & (n_in >= -INF) 
        temp = np.zeros_like(x)
        temp[n_out[mask] + INF] = x[n_in[mask] + INF]
        return temp
    else:
        # --- STRETCHING WITH LINEAR INTERPOLATION (x[n/k]) ---
        # Calculate the fractional source coordinates we need
        # e.g., if k=2, t_query becomes [-4.0, -3.5, -3.0, ..., 3.5, 4.0]
        n_in = n_out / k
        
        # np.interp automatically calculates the values at these fractional positions
        temp = np.interp(n_in, n_out, x)
        return temp

def main():
    img_root_path = '.'
    signal = init_signal()
    signal[INF] = 1
    signal[INF+1] = .5
    signal[INF-1] = 2
    signal[INF + 2] = 1
    signal[INF - 2] = .5

    plot(signal, title='Original Signal(x[n])', saveTo=f'{img_root_path}/x[n].png')

    plot(time_shift_signal(signal, 2), title='x[n-2]', saveTo=f'{img_root_path}/x[n-2].png')
    
    plot(time_shift_signal(signal, -2), title='x[n+2]', saveTo=f'{img_root_path}/x[n+2].png')
    
    plot(time_shift_signal(signal, 0), title='x[n+0]', saveTo=f'{img_root_path}/x[n+0].png')
    
    plot(time_scale_signal(signal, 3, True), title='x[3n]', saveTo=f'{img_root_path}/x[3n].png')
    
    plot(time_scale_signal(signal, 2, True), title='x[2n]', saveTo=f'{img_root_path}/x[2n].png')
    
    plot(time_scale_signal(signal, 1, True), title='x[1n]', saveTo=f'{img_root_path}/x[1n].png')
    
    plot(time_scale_signal(signal, 3, False), title='x[n/3]', saveTo=f'{img_root_path}/x[ndiv3].png')
    
    plot_overlap(time_scale_signal(signal, 2, False), signal, title='x[n/2]', saveTo=f'{img_root_path}/x[ndiv2].png')
    
    plot(time_scale_signal(signal, 1, False), title='x[n/1]', saveTo=f'{img_root_path}/x[ndiv1].png')
    
        

main()

#############solve
# def time_shift_signal(x : np.ndarray, k : int) -> np.ndarray:
#     # implement this function
#     return np.roll(x,k)
#     None

# def time_scale_signal(x : np.ndarray, k : int) -> np.ndarray:
#     # implement this function
#     temp=np.zeros_like(x)
#     second_half=np.array(x[8::k])
#     x=np.flip(x)
#     x=np.array(x[8+k::k])
#     x=np.flip(x)
#     temp[8:8+np.size(second_half)]+=second_half
#     temp[8-np.size(x):8]+=x
#     return temp
#     None   todays solve using np