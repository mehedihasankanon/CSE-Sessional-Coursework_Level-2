import numpy as np
import matplotlib.pyplot as plt

from signal_lti import DiscreteSignal, LTISystem


if __name__ == "__main__":
    # ---- 1. Create Input Signal x[n] (spans n = 0 to 2) ----
    x = DiscreteSignal(0, 2)
    x.set_value_at_time(0, 1.0)
    x.set_value_at_time(2, -1.0)
    x.plot("Input x(n)")

    # ---- 2. Create Impulse Responses ----
    # h1[n] = 1 at n = 0
    h1 = DiscreteSignal(0, 0)
    h1.set_value_at_time(0, 1.0)

    # h2[n] = 0.5 at n = 1
    h2 = DiscreteSignal(1, 1)
    h2.set_value_at_time(1, 0.5)

    # h3[n] = 1 at n = 0, 1 at n = 1
    h3 = DiscreteSignal(0, 1)
    h3.set_value_at_time(0, 1.0)
    h3.set_value_at_time(1, 1.0)

    # ---- 3. Create LTI Systems ----
    sys1 = LTISystem(h1)
    sys2 = LTISystem(h2)
    sys3 = LTISystem(h3)

    # ---- TODO 1: Determine output block by block ----
    # Hint: Pass x through sys1 -> y1, then y1 through sys2 -> y2, then y2 through sys3 -> y_final_1
    
    y1 = sys1.output(x)
    y2 = sys2.output(x)
    y_final_1 = sys3.output(y2.add(y1))
    
    
    y_final_1.plot("Output via block-by-block system")

    # ---- TODO 2: Determine h_combined ----
    # Hint: Convolve h1 with h2 to get h12 (using sys2.output(h1)), 
    # then convolve h12 with h3 (using sys3.output(h12)).
    
    h_combined = sys3.output(h1.add(h2))
    
    sys_combined = LTISystem(h_combined)

    y_final_2 = sys_combined.output(x)
    y_final_2.plot("Output via combined impulse response")

    # ---- 4. Check Verification ----
    print("Outputs are equal:", np.allclose(y_final_1.values, y_final_2.values))