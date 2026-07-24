"""
CSE220 Online 2 Practice Problem: Cascaded LTI Systems & Equivalences

** changed because 2023 Online uses a different structure **

Instructions:
- Use your DiscreteSignal and LTISystem classes imported from signal_lti.py.
- Implement the TODO functions below.
- Do NOT use numpy.convolve / scipy.signal / built-in convolution.
"""

import os
import numpy as np
import matplotlib.pyplot as plt

from signal_lti import DiscreteSignal, LTISystem


# Helper: Reads a signal from file
def read_signal_from_file(filename: str) -> DiscreteSignal:
    """
    File format:
    Line 1: nstart nend
    Line 2: sample_0 sample_1 ... sample_N
    """
    with open(filename, "r", encoding="utf-8") as f:
        lines = [line.strip() for line in f if line.strip() and not line.startswith("#")]
        nstart, nend = map(int, lines[0].split())
        vals = list(map(float, lines[1].split()))

    assert len(vals) == (nend - nstart + 1), "Sample count does not match nstart..nend range"
    
    sig = DiscreteSignal(nstart, nend)
    for i, v in enumerate(vals):
        sig.set_value_at_time(nstart + i, v)
    return sig


# TODO 1: Compute First Difference of a signal
def first_difference(sig: DiscreteSignal) -> DiscreteSignal:
    """
    Computes Δsig[n] = sig[n] - sig[n-1].
    MUST use only DiscreteSignal operations (shift, multiply, add).
    """
    
    return sig.add(sig.shift(1).multiply(-1))


# TODO 2: Compute Cascaded Output
def compute_cascade_output(x: DiscreteSignal, sys1: LTISystem, sys2: LTISystem) -> DiscreteSignal:
    """
    Computes output y_cascade[n] by passing x[n] through sys1 first,
    and then passing the intermediate result through sys2.
    
    w[n] = sys1.output(x)
    y[n] = sys2.output(w)
    """
    return sys2.output(sys1.output(x))


# TODO 3: Compute Equivalent Combined Impulse Response
def compute_equivalent_impulse_response(sys1: LTISystem, sys2: LTISystem) -> DiscreteSignal:
    """
    Computes equivalent impulse response h_eq[n] = (h1 * h2)[n].
    Re-use sys1 or sys2 output machinery!
    """
    # TODO: Implement h_eq calculation
    return sys2.output(sys1.h)


# Helper function to generate mock test files if they don't exist
def create_mock_files():
    if not os.path.exists("input_x.txt"):
        with open("input_x.txt", "w") as f:
            f.write("0 4\n1.0 2.0 3.0 2.0 1.0\n")

    if not os.path.exists("impulse_h1.txt"):
        with open("impulse_h1.txt", "w") as f:
            f.write("0 2\n0.5 0.3 0.2\n")

    if not os.path.exists("impulse_h2.txt"):
        with open("impulse_h2.txt", "w") as f:
            f.write("0 1\n1.0 -1.0\n")


if __name__ == "__main__":
    # Generate mock files for testing locally
    create_mock_files()

    # ---- Step 1: Load Signals ----
    x = read_signal_from_file("input_x.txt")
    h1 = read_signal_from_file("impulse_h1.txt")
    h2 = read_signal_from_file("impulse_h2.txt")

    sys1 = LTISystem(h1)
    sys2 = LTISystem(h2)

    # ---- Step 2: Compute First Difference of Input ----
    dx = first_difference(x)

    # ---- Step 3: Compute Cascaded Output ----
    y_cascade = compute_cascade_output(x, sys1, sys2)

    # ---- Step 4: Compute Equivalent Impulse Response and Output ----
    h_eq = compute_equivalent_impulse_response(sys1, sys2)
    sys_eq = LTISystem(h_eq)
    y_eq = sys_eq.output(x)

    # ---- Step 5: Verification ----
    print("y_cascade values:", y_cascade.values)
    print("y_eq values:     ", y_eq.values)

    if np.allclose(y_cascade.values, y_eq.values, atol=1e-6):
        print("✅ SUCCESS: Cascaded output matches Equivalent System output!")
    else:
        print("❌ ERROR: Outputs differ!")