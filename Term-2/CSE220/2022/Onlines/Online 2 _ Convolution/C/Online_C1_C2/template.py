import numpy as np
import matplotlib.pyplot as plt

from signal_lti import DiscreteSignal, LTISystem


# Helper class to represent a linear combination of signals
class SuperSignal:
    def __init__(self):
        self.components = []  # Stores tuples of (coefficient, DiscreteSignal)

    def add(self, signal: DiscreteSignal, coefficient=1.0):
        self.components.append((coefficient, signal))


if __name__ == "__main__":
    # ---- 1. Component Signals ----
    # x1[n] = 1 at n = 0
    x1 = DiscreteSignal(0, 0)
    x1.set_value_at_time(0, 1.0)

    # x2[n] = 1 at n = 2
    x2 = DiscreteSignal(2, 2)
    x2.set_value_at_time(2, 1.0)

    # ---- TODO 1: Create SuperSignal for x[n] = 2*x1[n] - x2[n] ----
    super_x = SuperSignal()
    # Hint: Use super_x.add(x1, 2.0) and super_x.add(x2, -1.0)
    super_x.add(x1, 2.0)
    super_x.add(x1, -1.0)

    # ---- 2. Impulse Response h[n] ----
    # h[n] = 1 at n = 0, 0.5 at n = 1
    h = DiscreteSignal(0, 1)
    h.set_value_at_time(0, 1.0)
    h.set_value_at_time(1, 0.5)

    system = LTISystem(h)

    # ---- TODO 2: Compute Output using Superposition ----
    # By Linearity of LTI systems:
    # If y_i[n] is the system output for input x_i[n], 
    # then total output y[n] = sum( c_i * system.output(x_i) )
    
    # y_superposition = ...
    
    
    # ---- TODO 3: Verify against Direct Convolution Output ----
    # Hint: Reconstruct full input signal x = x1.multiply(2.0).add(x2.multiply(-1.0))
    # y_direct = system.output(x)
    
    # print("Superposition and Direct outputs match:", np.allclose(y_superposition.values, y_direct.values))