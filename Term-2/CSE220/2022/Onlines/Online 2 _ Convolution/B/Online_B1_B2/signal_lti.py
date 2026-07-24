import numpy as np
   

def readable_time_ticks(time_values, max_labels=18):
    if len(time_values) <= max_labels:
        return time_values

    step = int(np.ceil(len(time_values) / max_labels))
    ticks = time_values[::step]

    if ticks[-1] != time_values[-1]:
        ticks.append(time_values[-1])

    return ticks


class DiscreteSignal:
    """Finite discrete-time signal with integer indices."""

    # Arguments: start_time and end_time are integers with start_time <= end_time.
    # Output: None; initialize start_time, end_time, and zero-valued stored samples.
    # Example: DiscreteSignal(-2, 3) represents samples for n = -2, -1, ..., 3.
    def __init__(self, start_time, end_time):
        self.start_time = start_time
        self.end_time = end_time
        self.size = end_time - start_time + 1
        
        self.values = np.zeros(self.size)
        
    # Arguments: none.
    # Returns: int, the number of stored samples in this finite signal.
    # Example: len(DiscreteSignal(-2, 3)) should be 6.
    def __len__(self):
        return self.size

    # Arguments: none.
    # Returns: range of integer time indices covered by the signal.
    # Example: DiscreteSignal(-1, 2).times() should cover -1, 0, 1, 2.
    def times(self):
        return np.arange(self.start_time, self.end_time + 1, 1)

    # Arguments: t is an integer time index.
    # Returns: float, the signal value at t; return 0.0 if t is outside the range.
    # Example: if x[2] = 5, then x.get_value_at_time(2) should return 5.0.
    def get_value_at_time(self, t):
        return 0.0 if (t < self.start_time or t > self.end_time) else self.values[t - self.start_time]

    # Arguments: t is an integer time index, value is the sample value to store.
    # Output: None; update the stored sample at t, or raise an error if t is outside.
    # Example: x.set_value_at_time(2, 5) makes x[2] equal to 5.
    def set_value_at_time(self, t, value):
        if t >= self.start_time and t <= self.end_time:
            self.values[t - self.start_time] = value

    # Arguments: k is an integer shift amount.
    # Returns: DiscreteSignal, a copy with indices shifted so y[n] = x[n - k].
    # Example: shifting a signal over 0..2 by 3 returns a signal over 3..5.
    def shift(self, k):
        new_signal = DiscreteSignal(self.start_time + k, self.end_time + k)
        
        new_signal.values = self.values.copy()
        
        return new_signal

    # Arguments: other is another DiscreteSignal.
    # Returns: DiscreteSignal over the combined range with sample-wise sums.
    # Example: if x[0] = 2 and z[0] = 3, then x.add(z)[0] should be 5.
    def add(self, other):
        # np.min() or np.max() not to be used here
        new_start = np.minimum(self.start_time, other.start_time)
        new_end = np.maximum(self.end_time, other.end_time)
        
        added = DiscreteSignal(new_start, new_end)
        
        # mode='constant' does padding with zero by default, constant_values=0 is optional here
        added.values = np.pad(self.values, (self.start_time - new_start, new_end - self.end_time), mode='constant', constant_values=0)
        added.values += np.pad(other.values, (other.start_time - new_start, new_end - other.end_time), mode='constant', constant_values=0)
        
        return added 
        
        
        
        

    # Arguments: scalar is a number used to multiply every stored sample.
    # Returns: DiscreteSignal with the same time range and scaled sample values.
    # Example: if x[1] = 4, then x.multiply(0.5)[1] should be 2.
    def multiply(self, scalar):
        scaled_signal = DiscreteSignal(self.start_time, self.end_time)
        
        scaled_signal.values = (self.values * scalar).copy()
        
        return scaled_signal

    # Arguments: tolerance is the threshold below which values are treated as zero.
    # Returns: list of (time_index, value) tuples for samples with abs(value) > tolerance.
    # Example: values [1, 0, 3] starting at n = 0 should return [(0, 1), (2, 3)].
    def nonzero_samples(self, tolerance=1e-12):
        mask = np.abs(self.values) > np.abs(tolerance)
        
        times = self.times()[mask]
        values = self.values[mask]
        
        return list(zip(times, values))
        
        

    def plot(self, title, save_path=None, ax=None):
        import matplotlib.pyplot as plt

        if ax is None:
            _, ax = plt.subplots()

        time_values = list(self.times())
        markerline, stemlines, baseline = ax.stem(time_values, self.values)
        markerline.set_markersize(6)
        baseline.set_color("black")
        baseline.set_linewidth(1)

        ax.axhline(0, color="black", linewidth=0.8)
        ax.set_title(title)
        ax.set_xlabel("n")
        ax.set_ylabel("value")
        ax.grid(True, alpha=0.35)
        ax.set_xticks(readable_time_ticks(time_values))
        ax.tick_params(axis="x", labelsize=9)

        if save_path is not None:
            plt.savefig(save_path, bbox_inches="tight", dpi=150)

        return ax


class LTISystem:
    """Discrete-time LTI system described by a finite impulse response."""

    # Arguments: impulse_response is a DiscreteSignal representing h[n].
    # Output: None; store the impulse response that defines this LTI system.
    # Example: LTISystem(impulse_identity()) creates the identity system.
    def __init__(self, impulse_response):
        self.h = impulse_response

    # Arguments: input_signal is a DiscreteSignal representing x[n].
    # Returns: (start, end) tuple for the convolution output y[n].
    # Example: x over 0..4 and h over 0..2 produce output range (0, 6).
    def output_range(self, input_signal):
        return (self.h.start_time + input_signal.start_time, self.h.end_time + input_signal.end_time)  

    # Arguments: input_signal is a DiscreteSignal representing x[n].
    # Returns: list of (k, component_signal) for each nonzero input sample x[k].
    # Example: x[2] = 3 contributes the component 3*h[n - 2].
    def get_response_components(self, input_signal):
        # response components -> x[k]h[n - k]
        # get response components via nonzero_samples (since x[k] =  doesn't contribute to anything)
        # then for each k, shift self.h by k
        # multiply and store it in component_signal
        
        components = []
        
        for k, x_k in input_signal.nonzero_samples():
            components.append((k, self.h.shift(k).multiply(x_k)))
            
        return components
            
        

    # Arguments: input_signal is a DiscreteSignal representing x[n].
    # Returns: DiscreteSignal y[n], computed by adding all response components.
    # Example: for the identity impulse, the output should match the input signal.
    def output_by_superposition(self, input_signal):
        # output convoluted signal is the sum of all the component signals 
        # each component is x[k]h[n-k] 
        
        components = self.get_response_components(input_signal)
        
        output = DiscreteSignal(self.output_range(input_signal)[0],self.output_range(input_signal)[1])
        
        if len(components) == 0:
            return output
        
        for _, component in components:
            output = output.add(component)
            
        return output    

    # Arguments: input_signal is a DiscreteSignal and n is one output time index.
    # Returns: list of (k, x_k, h_n_minus_k, product) nonzero contribution tuples.
    # Example: a term may look like (2, 3.0, 0.5, 1.5) for x[2]h[n - 2].
    def get_contributions_at_time(self, input_signal, n):
        contributions = []
        
        for k, x_k in input_signal.nonzero_samples():
            h_n_minus_k = self.h.get_value_at_time(n - k)
            mul = x_k * h_n_minus_k
            if abs(mul) > 1e-12:
                contributions.append((k, x_k, h_n_minus_k, mul))
                
        return contributions

    # Arguments: input_signal is a DiscreteSignal and n is one output time index.
    # Returns: float, the convolution-sum value y[n].
    # Example: output_at_time(x, 4) returns the scalar sample y[4].
    def output_at_time(self, input_signal, n):
        return np.float64(sum(terms[3] for terms in self.get_contributions_at_time(input_signal,n)))

    # Arguments: input_signal is a DiscreteSignal representing x[n].
    # Returns: DiscreteSignal containing every output sample y[n].
    # Example: system.output(x) returns the full convolution result x[n] * h[n].
    def output(self, input_signal):
        
        op_range = self.output_range(input_signal)
        y = DiscreteSignal(op_range[0], op_range[1])
        
        for n in range(op_range[0], op_range[1] + 1): #inclusive iteration
            y_n = self.output_at_time(input_signal, n)
            y.set_value_at_time(n, y_n)
            
        return y
        
