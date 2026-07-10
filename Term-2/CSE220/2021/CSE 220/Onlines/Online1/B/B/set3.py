import numpy as np
import matplotlib.pyplot as plt
from typing import Tuple

INF = 8

def plot(
        signal, 
        title=None, 
        y_range=(-1, 3), 
        figsize = (8, 3),
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

def init_signal():
    return np.zeros(2 * INF + 1)


def time_scale_signal(x : np.ndarray, k : int) -> np.ndarray:
    """
    What we wanna do is stretch the time scale and put zeros in between
    """
    
    output = np.zeros_like(x) # output signal
    
    t_out = np.arange(-INF, INF + 1)
    
    mask = (t_out % k == 0)
    
    t_in = t_out//k 
    
    print(f't_out: {t_out}')
    print(f't_in: {t_in}')
    print(f't_in[mask]: {t_in[mask]}')
    
    output[t_out[mask] + INF] = x[t_in[mask] + INF]
    
    return output
    

def time_scale_signal_interpolate(x : np.ndarray, k : int) -> np.ndarray:

    """
    Stretches the signal by factor k, filling intermediate gaps 
    with the flat average of the surrounding anchor points.
    """
    output = np.zeros_like(x)
    t_out = np.arange(-INF, INF + 1)
    
    # Step 1: Place the original anchor points
    mask = (t_out % k == 0)
    t_in = t_out // k
    output[t_out[mask] + INF] = x[t_in[mask] + INF]
    
    # Step 2: Find the physical array indices where these anchors landed
    # For k=3 and INF=8, this returns slots like [2, 5, 8, 11, 14]
    anchor_indices = np.where(mask)[0]
    
    # Step 3: Loop through adjacent pairs of anchors and fill the gaps
    for i in range(len(anchor_indices) - 1):
        idx1 = anchor_indices[i]   # Left anchor index
        idx2 = anchor_indices[i+1] # Right anchor index
        
        # Calculate the flat average of the two anchor values
        avg_val = (output[idx1] + output[idx2]) / 2.0
        
        # Fill all the empty slots strictly BETWEEN these two indices
        output[idx1 + 1 : idx2] = avg_val
        
    return output

def main():
    img_root = '.'
    signal = init_signal()
    signal[INF] = 1
    signal[INF+1] = .5
    signal[INF-1] = 2
    signal[INF + 2] = 1
    signal[INF - 2] = .5

    plot(signal, title='Original Signal(x[n])', saveTo=f'{img_root}/x[n].png')
    plot(time_scale_signal(signal, 3), title='x[n/3]', saveTo=f'{img_root}/x[n divided by 3].png')
    plot(time_scale_signal(signal, 1), title='x[n/1]', saveTo=f'{img_root}/x[n divided by 1].png')
    plot(time_scale_signal_interpolate(signal, 3), title='x[n/3] with interpolation', saveTo=f'{img_root}/x[n divided by 3]_with_interpolation.png')
    plot(time_scale_signal_interpolate(signal, 1), title='x[n/1] with interpolation', saveTo=f'{img_root}/x[n divided by 1]_with_interpolation.png')

main()
