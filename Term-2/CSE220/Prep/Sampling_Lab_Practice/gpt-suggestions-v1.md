Yes. Given the point your class has reached, I would prepare for an online centered on **implementing the sampling theorem rather than merely plotting a few samples**.

The lecture has already established the entire computational chain

$$
x(t)\rightarrow x[n]=x(nT)
\rightarrow X_p(j\omega)
\rightarrow \text{aliasing/non-aliasing}
\rightarrow \text{ideal LPF}
\rightarrow \text{sinc reconstruction}.
$$

The deck explicitly derives the replicated spectrum, Nyquist condition, aliasing, ideal reconstruction filter, and sinc interpolation before moving on to ZOH.    

So if you have only been taught through our Section 5.9, **I would not prioritize ZOH/FOH yet**. The slide deck starts ZOH only afterward. 

What follows is partly based on the lecture and partly my inference from the online style you described.

---

# 1. What I would expect the coding test to actually look like

I would not expect:

```python
def nyquist_rate(fmax):
    return 2 * fmax
```

and that's the entire problem.

That is too trivial for the evaluation philosophy you quoted.

A more plausible structure is:

```text
Large provided codebase
        │
        ├── signal generation
        ├── plotting
        ├── maybe Fourier/DFT helpers from previous work
        ├── sampling code
        └── reconstruction/analysis pipeline

You receive:
        ↓
one new scenario / requirement
        ↓
modify 1–3 functions
        ↓
the mathematical change itself is small,
but understanding where and how to implement it is the test.
```

In other words, **the hard part will probably be translating a verbal sampling requirement into the correct mathematical operation**.

The highest-value skills to prepare are:

1. Sampling a callable continuous signal correctly.
2. Computing/using \(T\), \(f_s\), \(\omega_s\), \(f_M\), \(\omega_M\) without mixing units.
3. Determining whether aliasing occurs.
4. Understanding what frequency aliases into what frequency.
5. Implementing

$$
x_r(t)
=
\sum_nx[n]\operatorname{sinc}
\left(\frac{t-nT}{T}\right).
$$

6. Producing shifted spectral replicas

$$
X_p(j\omega)
=
\frac1T
\sum_kX(j(\omega-k\omega_s)).
$$

7. Implementing the ideal reconstruction filter

$$
H(j\omega)=
\begin{cases}
T,&|\omega|<\pi/T,\\
0,&\text{otherwise}.
\end{cases}
$$

8. Diagnosing why reconstruction failed instead of blindly changing code.

The lecture's entire sampling theorem is built around the fact that the sampled spectrum contains \(1/T\)-scaled replicas spaced by \(\omega_s\), and they become unrecoverably mixed if those replicas overlap. 

---

# 2. Python facts you should know cold

There is one extremely useful NumPy fact:

```python
np.sinc(x)
```

means

$$
\frac{\sin(\pi x)}{\pi x}.
$$

That is **exactly the sinc convention used in your lecture**:

$$
\operatorname{sinc}(x)=\frac{\sin(\pi x)}{\pi x}.
$$

So the ideal interpolation kernel is simply

```python
np.sinc((t - n*T) / T)
```

not

```python
np.sinc(np.pi * (t - n*T) / T)   # WRONG
```

The lecture derives this kernel directly from the inverse Fourier transform of the ideal LPF. 

A basic implementation you should be able to write from memory is:

```python
import numpy as np

def sinc_reconstruct(samples, sample_times, query_times, T):
    samples = np.asarray(samples)
    sample_times = np.asarray(sample_times)
    query_times = np.asarray(query_times)

    # shape: (#query_times, #samples)
    kernels = np.sinc(
        (query_times[:, None] - sample_times[None, :]) / T
    )

    return kernels @ samples
```

Mathematically this matrix contains

$$
S_{mn}
=
\operatorname{sinc}
\left(
\frac{t_m-nT}{T}
\right),
$$

and then

$$
\mathbf{x}_r=S\mathbf{x}.
$$

That vectorized form is worth understanding.

---

## Another thing: Hz versus rad/s

This will absolutely ruin an otherwise correct implementation.

If

$$
f_s=\frac1T
$$

in Hz, then

$$
\omega_s=2\pi f_s=\frac{2\pi}{T}.
$$

Likewise,

$$
\omega_M=2\pi f_M.
$$

The Nyquist conditions are equivalent:

$$
f_s>2f_M
$$

and

$$
\omega_s>2\omega_M.
$$

Do not accidentally compare

```python
fs > 2 * omega_M
```

because those quantities have different units.

---

# 3. One subtle numerical issue that can easily become an online trap

The ideal formula is

$$
x_r(t)
=
\sum_{n=-\infty}^{\infty}
x[n]\operatorname{sinc}
\left(
\frac{t-nT}{T}
\right).
$$

But your computer only has a finite number of samples.

Therefore in code you actually calculate

$$
\hat{x}_r(t)
=
\sum_{n=n_0}^{n_1}
x[n]\operatorname{sinc}
\left(
\frac{t-nT}{T}
\right).
$$

That is a **truncated sinc reconstruction**.

Consequences:

* near the middle of a sufficiently large captured interval, reconstruction can be excellent;
* near boundaries, missing sinc contributions from unavailable samples can create noticeable error;
* outside the sampled interval, you should not expect magical exact recovery.

This is not a contradiction of Shannon reconstruction. The mathematical theorem assumes the required infinite sample sequence.

---

# 4. Scenario 1 — The Faulty ADC Monitor

This is the scenario I think is closest to a conventional online.

---

## Problem statement

A vibration monitoring system measures

$$
x(t)
=
1.5\cos(2\pi\cdot120t)
+
0.8\sin(2\pi\cdot310t)
+
0.3\cos(2\pi\cdot420t).
$$

A provided program simulates an ADC.

The code currently samples the signal and plots the sample points, but the laboratory now wants a **reconstruction mode**.

You are given:

```python
import numpy as np
import matplotlib.pyplot as plt

def vibration_signal(t):
    return (
        1.5 * np.cos(2*np.pi*120*t)
        + 0.8 * np.sin(2*np.pi*310*t)
        + 0.3 * np.cos(2*np.pi*420*t)
    )

def sample_signal(signal, fs, duration):
    # already implemented
    ...

def reconstruct(samples, sample_times, output_times):
    # TODO
    pass

def run_monitor(fs):
    duration = 0.05

    ts, xs = sample_signal(vibration_signal, fs, duration)

    tdense = np.linspace(0, duration, 6000)
    xr = reconstruct(xs, ts, tdense)

    return tdense, xr
```

The function `reconstruct()` does not receive `fs`.

### Tasks

1. Implement ideal sinc reconstruction.
2. Determine the sampling period from `sample_times`.
3. Add a function

```python
def sampling_status(fs):
    ...
```

that returns `"safe"` if Nyquist is satisfied and `"aliased"` otherwise.
4. For

```text
fs = 1200 Hz
fs = 700 Hz
```

predict what will happen.
5. Modify the reconstruction code so that it works for complex-valued samples too.
6. The engineer says:

> “At \(700\) Hz the reconstructed curve still looks smooth, therefore there is no aliasing.”

Explain the exact conceptual error.

---

# Solution

The highest frequency is

$$
f_M=420\text{ Hz}.
$$

Therefore the lecture's strict Nyquist criterion requires

$$
f_s>840\text{ Hz}.
$$

So

$$
1200>840
$$

is safe, whereas

$$
700<840
$$

aliases. This is exactly the non-overlap versus overlap distinction in the slides.  

Implementation:

```python
def reconstruct(samples, sample_times, output_times):
    samples = np.asarray(samples)
    sample_times = np.asarray(sample_times)
    output_times = np.asarray(output_times)

    if len(sample_times) < 2:
        raise ValueError("Need at least two samples")

    T = sample_times[1] - sample_times[0]

    kernel = np.sinc(
        (output_times[:, None] - sample_times[None, :]) / T
    )

    return kernel @ samples


def sampling_status(fs):
    f_max = 420.0

    if fs > 2 * f_max:
        return "safe"

    return "aliased"
```

No special modification is required for complex samples: NumPy matrix multiplication already preserves complex arithmetic.

---

## What aliases at \(700\) Hz?

The \(420\) Hz tone exceeds

$$
f_s/2=350\text{ Hz}.
$$

A frequency can be mapped into the Nyquist band by subtracting integer multiples of \(f_s\).

$$
420-700=-280.
$$

For a real cosine,

$$
\cos(-2\pi280t)=\cos(2\pi280t).
$$

So the sampled \(420\) Hz cosine is indistinguishable from a \(280\) Hz cosine.

You therefore reconstruct the **wrong continuous-time frequency**, not random garbage.

That is a crucial point.

Aliasing often produces something perfectly smooth and plausible.

---

## Generic alias-frequency utility

You may want to memorize this:

```python
def wrap_frequency(f, fs):
    """
    Map continuous frequency f into [-fs/2, fs/2).
    """
    return ((f + fs/2) % fs) - fs/2
```

Example:

```python
print(wrap_frequency(420, 700))
```

gives

```text
-280
```

For a real cosine you could report

```python
abs(...)
```

as \(280\) Hz.

---

## Main implementation pitfalls

Do not do:

```python
T = 1 / len(samples)
```

The sample period is not determined by the number of samples.

Do not do:

```python
np.sinc(output_times - sample_times)
```

because the argument must be normalized by \(T\):

$$
\frac{t-nT}{T}.
$$

Do not multiply each sinc by \(T\). The interpolation formula already is

$$
x[n]\operatorname{sinc}(\cdots),
$$

not

$$
Tx[n]\operatorname{sinc}(\cdots).
$$

The factor \(T\) belongs to the frequency response of the ideal reconstruction LPF; after the convolution derivation, it has already produced the normalized sinc kernel.  

---

# 5. Scenario 2 — Implement the Sampling-Theorem Spectrum Simulator

This one is more mathematically faithful to the actual lecture and would be a very reasonable “extend the existing codebase” online.

---

## Problem statement

Your lab has a signal-spectrum visualization tool.

The original signal has a triangular continuous-time spectrum

$$
X(j\omega)=
\begin{cases}
1-\dfrac{|\omega|}{\omega_M},
&
|\omega|\leq\omega_M,\\[6pt]
0,&\text{otherwise}.
\end{cases}
$$

The existing program plots \(X(j\omega)\).

Your task is to add a **sampling preview** that displays the spectrum after impulse-train sampling.

You are given:

```python
import numpy as np

def original_spectrum(omega, omega_M):
    # provided
    ...

def sampled_spectrum(
    omega,
    omega_M,
    T,
    num_copies
):
    # TODO
    pass
```

You are prohibited from FFT-transforming a generated time-domain signal.

You must implement the expression directly from sampling theory.

### Requirements

`sampled_spectrum()` must approximate

$$
X_p(j\omega)
=
\frac1T
\sum_{k=-\infty}^{\infty}
X(j(\omega-k\omega_s)).
$$

Use

$$
k=-K,\ldots,K
$$

where

```python
K = num_copies
```

and

$$
\omega_s=\frac{2\pi}{T}.
$$

Additionally implement

```python
def guard_band(omega_M, T):
    ...
```

which returns the distance between the right edge of the baseband copy and the left edge of the \(k=1\) copy.

If spectra overlap, return a negative number.

---

# Solution

From the lecture,

$$
X_p(j\omega)
=
\frac1T
\sum_k
X(j(\omega-k\omega_s)),
$$

and the copy centered at \(k\omega_s\) occupies

$$
[k\omega_s-\omega_M,\,
k\omega_s+\omega_M].
$$

 

Implementation:

```python
def original_spectrum(omega, omega_M):
    omega = np.asarray(omega)

    X = np.zeros_like(omega, dtype=float)

    mask = np.abs(omega) <= omega_M
    X[mask] = 1 - np.abs(omega[mask]) / omega_M

    return X


def sampled_spectrum(omega, omega_M, T, num_copies):
    omega = np.asarray(omega)

    omega_s = 2 * np.pi / T

    Xp = np.zeros_like(omega, dtype=float)

    for k in range(-num_copies, num_copies + 1):
        Xp += original_spectrum(
            omega - k * omega_s,
            omega_M
        )

    return Xp / T


def guard_band(omega_M, T):
    omega_s = 2 * np.pi / T

    return (
        (omega_s - omega_M)
        - omega_M
    )
```

Simplify:

$$
\boxed{
G=\omega_s-2\omega_M.
}
$$

So another implementation is

```python
def guard_band(omega_M, T):
    omega_s = 2 * np.pi / T
    return omega_s - 2 * omega_M
```

---

## Why negative guard band is meaningful

Suppose

$$
G=-100.
$$

That means

$$
\omega_s-2\omega_M=-100
$$

or

$$
2\omega_M-\omega_s=100.
$$

So neighboring spectra overlap by an angular-frequency interval of width \(100\).

That is a convenient way to quantify aliasing.

---

## A likely extension question

The instructor might suddenly add:

> Add a mode that returns only the contribution of the \(k\)-th replica.

Then the implementation is simply

```python
def spectrum_copy(omega, omega_M, T, k):
    omega_s = 2 * np.pi / T

    return (
        original_spectrum(
            omega - k * omega_s,
            omega_M
        )
        / T
    )
```

If you understand

$$
X(j(\omega-k\omega_s)),
$$

this is trivial.

If you memorized only “sampling causes aliasing,” it is surprisingly difficult.

---

# 6. Scenario 3 — The “Perfect” Signal Recovery System That Is Secretly Wrong

This is the kind of debugging question I would specifically prepare for.

---

## Problem statement

A previous student has written:

```python
def reconstruct(samples, T, t):
    y = np.zeros_like(t)

    for n in range(len(samples)):
        y += (
            T
            * samples[n]
            * np.sinc(t - n*T)
        )

    return y
```

The student claims this implements the ideal low-pass reconstruction formula.

Your job is to repair the implementation.

The test framework verifies:

1. reconstructed values at sample locations,
2. reconstruction of a 70 Hz sinusoid sampled at 500 Hz,
3. correctness when \(T\neq1\),
4. handling sample indices beginning at negative \(n\),
5. output on arbitrary query locations.

You are supplied both `samples` and the integer sample indices `n`.

Implement:

```python
def ideal_reconstruct(samples, indices, T, query_times):
    ...
```

---

# Diagnose every error

The correct formula is

$$
x_r(t)
=
\sum_n
x(nT)
\operatorname{sinc}
\left(
\frac{t-nT}{T}
\right).
$$

The lecture explicitly derives this form. 

The faulty implementation has three important problems.

### Error 1: incorrect sinc scaling

It uses

```python
np.sinc(t - n*T)
```

but must use

```python
np.sinc((t - n*T) / T)
```

because zero crossings must occur at

$$
t=nT\pm T,\,
nT\pm2T,\ldots
$$

not at offsets of \(1,2,\ldots\) seconds.

### Error 2: incorrect factor \(T\)

It multiplies by `T`, but the interpolation equation does not.

### Error 3: assumes sample index starts at zero

This:

```python
n = range(len(samples))
```

implicitly means the samples occurred at

$$
0,T,2T,\ldots
$$

which fails if the actual indices are

$$
-5,-4,\ldots,5.
$$

---

# Correct implementation

```python
def ideal_reconstruct(samples, indices, T, query_times):
    samples = np.asarray(samples)
    indices = np.asarray(indices)
    query_times = np.asarray(query_times)

    sample_times = indices * T

    kernel = np.sinc(
        (query_times[:, None] - sample_times[None, :]) / T
    )

    return kernel @ samples
```

---

# Why does this reproduce sample values?

At

$$
t=mT,
$$

$$
x_r(mT)
=
\sum_nx(nT)\operatorname{sinc}(m-n).
$$

Now

$$
\operatorname{sinc}(0)=1
$$

while

$$
\operatorname{sinc}(q)=0
$$

for every nonzero integer \(q\).

Therefore only \(n=m\) remains:

$$
x_r(mT)=x(mT).
$$

This is precisely the structural reason sinc reconstruction works.

---

## An alternate implementation

Readable but slower:

```python
def ideal_reconstruct(samples, indices, T, query_times):
    result = np.zeros(
        len(query_times),
        dtype=np.result_type(samples, float)
    )

    for sample, n in zip(samples, indices):
        result += (
            sample
            * np.sinc(
                (query_times - n*T) / T
            )
        )

    return result
```

For an online, this is perfectly defensible unless efficiency is explicitly tested.

The vectorized version is faster but easier to get shape errors in under time pressure.

---

# 7. Scenario 4 — Arbitrary-Rate Resampler

This is one of the most plausible “extension” tasks because it uses ideal interpolation without asking you to derive anything new.

---

## Scenario

You have recorded a signal with sampling rate

$$
f_{s,\text{in}}=1000\text{ Hz}.
$$

The original signal is known to be properly band-limited for this sampling rate.

A downstream program requires samples at

$$
f_{s,\text{out}}=1375\text{ Hz}.
$$

You may **not** use

```python
np.interp
```

or SciPy interpolation.

You must use the ideal reconstruction concept taught in class.

Given:

```python
def resample(samples, fs_in, fs_out):
    # TODO
    pass
```

Return the new uniformly sampled sequence covering the same time interval.

---

# Underlying idea

This is conceptually:

$$
\text{discrete samples}
\rightarrow
\text{reconstruct continuous }x(t)
\rightarrow
\text{evaluate at new times}.
$$

If old sample times are

$$
t_n=\frac{n}{f_{s,\text{in}}},
$$

and new sample times are

$$
t_m'=\frac{m}{f_{s,\text{out}}},
$$

then

$$
x_{\text{out}}[m]
=
\sum_n
x_{\text{in}}[n]
\operatorname{sinc}
\left(
\frac{t_m'-t_n}{T_{\text{in}}}
\right).
$$

---

# Solution

```python
def resample(samples, fs_in, fs_out):
    samples = np.asarray(samples)

    T_in = 1.0 / fs_in

    t_in = np.arange(len(samples)) / fs_in

    duration = t_in[-1]

    t_out = np.arange(
        0,
        duration + 1e-15,
        1.0 / fs_out
    )

    kernel = np.sinc(
        (t_out[:, None] - t_in[None, :])
        / T_in
    )

    output = kernel @ samples

    return t_out, output
```

---

## Why `np.interp` is conceptually wrong here

`np.interp` performs piecewise linear interpolation.

But linear interpolation is a different reconstruction kernel.

Your currently taught reconstruction method is sinc interpolation derived from the ideal LPF.

So even though linear interpolation might look reasonable, it is not implementing the required system.

---

## Surprise extension

The question may add:

> The required output timestamps are not uniform. Instead you receive a list `query_times`.

Then practically nothing changes:

```python
def resample_at(samples, fs_in, query_times):
    samples = np.asarray(samples)
    query_times = np.asarray(query_times)

    T = 1 / fs_in
    t_samples = np.arange(len(samples)) * T

    kernel = np.sinc(
        (
            query_times[:, None]
            - t_samples[None, :]
        ) / T
    )

    return kernel @ samples
```

This is a very good example of what your faculty meant by “the extension is simple if you understood the original code.”

---

# 8. Scenario 5 — Rotating Wheel / Stroboscope Ambiguity

This is less predictable and tests whether you truly understand aliasing.

---

## Problem statement

A camera monitors a rotating machine.

A marker on the wheel generates a sinusoidal brightness measurement

$$
x(t)=\cos(2\pi f_0t).
$$

The camera samples at

$$
f_s=30\text{ frames/s}.
$$

The program estimates that the discrete-time observations correspond to an apparent frequency of

$$
f_a=7\text{ Hz}.
$$

The true wheel frequency is known only to satisfy

$$
0\leq f_0\leq100\text{ Hz}.
$$

Implement:

```python
def possible_frequencies(
    apparent_frequency,
    fs,
    max_frequency
):
    ...
```

which returns every continuous frequency compatible with the observed sampled cosine.

Then determine why imposing a sufficiently small band limit removes the ambiguity.

---

# Mathematical reasoning

Sampling cannot distinguish frequencies differing by integer multiples of \(f_s\):

$$
e^{j2\pi(f+kf_s)nT}
=
e^{j2\pi fnT}.
$$

For cosine there is an additional sign equivalence because

$$
\cos(-\theta)=\cos(\theta).
$$

Hence candidates satisfy

$$
f_0
=
|k f_s\pm f_a|.
$$

Here

$$
f_s=30,\qquad f_a=7.
$$

Candidates include

$$
7,
$$

$$
30-7=23,
$$

$$
30+7=37,
$$

$$
60-7=53,
$$

$$
60+7=67,
$$

$$
90-7=83,
$$

$$
90+7=97.
$$

All are at most \(100\) Hz.

So the same sampled cosine can correspond to

$$
\boxed{
7,23,37,53,67,83,97\text{ Hz}.
}
$$

This is the computational form of the ambiguity shown at the beginning of the lecture: many continuous-time signals can produce the same samples. 

---

# Implementation

```python
def possible_frequencies(
    apparent_frequency,
    fs,
    max_frequency
):
    candidates = set()

    k = 0

    while k * fs - apparent_frequency <= max_frequency:
        a = abs(k * fs + apparent_frequency)
        b = abs(k * fs - apparent_frequency)

        if a <= max_frequency:
            candidates.add(a)

        if b <= max_frequency:
            candidates.add(b)

        k += 1

    return sorted(candidates)
```

---

# Why Nyquist removes ambiguity

If the signal is guaranteed to satisfy

$$
f_0<\frac{f_s}{2},
$$

then only frequencies in the baseband are admissible.

Here

$$
f_s/2=15\text{ Hz}.
$$

Of all the possibilities,

$$
7,23,37,\ldots,
$$

only

$$
7
$$

lies below \(15\) Hz.

The band-limit assumption makes the solution unique.

That is the Sampling Theorem viewed as an **ambiguity elimination theorem**, rather than merely a formula saying “sample at twice the frequency.”

---

# 9. Scenario 6 — Choose the Correct ADC Mode

This one combines control logic and signal processing and could easily be embedded in a larger codebase.

---

## Problem statement

An acquisition device offers these sampling modes:

```python
AVAILABLE_FS = [
    250,
    500,
    800,
    1000,
    1600,
    2400
]
```

A signal is described as a list of sinusoidal components:

```python
components = [
    (amplitude, frequency_hz, phase),
    ...
]
```

For example:

```python
components = [
    (1.0, 75, 0.0),
    (0.4, 180, 0.3),
    (0.2, 390, -0.7)
]
```

Implement

```python
def valid_modes(components):
    ...
```

returning all ADC rates satisfying the sampling theorem.

Then implement

```python
def sample_and_reconstruct(
    components,
    fs,
    duration,
    render_fs
):
    ...
```

which

1. generates the continuous signal,
2. samples it,
3. reconstructs it using ideal sinc interpolation,
4. returns the reconstructed values on a dense rendering grid.

---

# Solution

```python
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
```

For the example,

$$
f_M=390\text{ Hz}
$$

so

$$
2f_M=780\text{ Hz}.
$$

Therefore valid rates are

```text
800, 1000, 1600, 2400
```

using the lecture's strict inequality.

Signal generation:

```python
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
```

Complete function:

```python
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
```

---

# A nasty variant

Suppose the input contains

```python
[
    (1, -150, 0),
    (2, 275, 0)
]
```

Frequency bandwidth depends on

$$
|f|.
$$

Therefore use

```python
abs(freq)
```

when finding \(f_M\).

For a real cosine, negative and positive frequency parameters might produce equivalent signals depending on phase, but the safe bandwidth computation is still based on magnitude.

---

# 10. Scenario 7 — “Spectrum Repair” Pipeline

This is probably the hardest one here.

---

## Problem statement

A program stores an approximation to \(X(j\omega)\) on a dense frequency grid.

You must simulate:

1. impulse-train sampling,
2. ideal reconstruction filtering,
3. reconstruction success/failure.

You receive:

```python
omega = np.linspace(-6000, 6000, 120001)
X = ...
```

The samples in `X` correspond to `omega`.

Implement:

```python
def simulate_sampling_spectrum(
    omega,
    X,
    T,
    copies
):
    ...
```

and

```python
def ideal_reconstruction_filter(
    omega,
    T
):
    ...
```

and finally

```python
def reconstructed_spectrum(
    omega,
    X,
    T,
    copies
):
    ...
```

The first function must produce

$$
X_p(j\omega)
\approx
\frac1T
\sum_{k=-K}^{K}
X(j(\omega-k\omega_s)).
$$

You are not given an analytic function for \(X\), only array samples.

---

# Why this is trickier

In Scenario 2, you could evaluate

```python
original_spectrum(omega - shift)
```

directly.

Now you only have array samples.

So to evaluate

$$
X(j(\omega-k\omega_s)),
$$

you must resample/interpolate the stored spectrum.

---

# Solution using interpolation

For a real-valued magnitude-style spectrum:

```python
def shifted_spectrum(
    omega,
    X,
    shift
):
    # Want X(omega - shift)
    return np.interp(
        omega - shift,
        omega,
        X,
        left=0.0,
        right=0.0
    )
```

Then:

```python
def simulate_sampling_spectrum(
    omega,
    X,
    T,
    copies
):
    omega = np.asarray(omega)
    X = np.asarray(X)

    omega_s = 2 * np.pi / T

    Xp = np.zeros_like(
        X,
        dtype=np.result_type(X, complex)
    )

    for k in range(-copies, copies + 1):
        shift = k * omega_s

        if np.iscomplexobj(X):
            real_part = np.interp(
                omega - shift,
                omega,
                X.real,
                left=0,
                right=0
            )

            imag_part = np.interp(
                omega - shift,
                omega,
                X.imag,
                left=0,
                right=0
            )

            copy = real_part + 1j * imag_part

        else:
            copy = np.interp(
                omega - shift,
                omega,
                X,
                left=0,
                right=0
            )

        Xp += copy

    return Xp / T
```

Ideal LPF:

```python
def ideal_reconstruction_filter(
    omega,
    T
):
    cutoff = np.pi / T

    H = np.zeros_like(
        omega,
        dtype=float
    )

    H[np.abs(omega) < cutoff] = T

    return H
```

Complete reconstruction:

```python
def reconstructed_spectrum(
    omega,
    X,
    T,
    copies
):
    Xp = simulate_sampling_spectrum(
        omega,
        X,
        T,
        copies
    )

    H = ideal_reconstruction_filter(
        omega,
        T
    )

    return Xp * H
```

The LPF gain must be \(T\), because the central replicated spectrum has amplitude \(X/T\). 

---

## Tricky complex-data pitfall

`np.interp` does not always fit arbitrary complex interpolation workflows the way you expect.

A robust approach is to interpolate real and imaginary parts separately.

If the codebase only uses real nonnegative magnitude spectra, this issue disappears.

---

# 11. Scenario 8 — The Boundary Case That Exposes Memorization

Suppose the signal bandwidth is

$$
f_M=100\text{ Hz}.
$$

The test asks:

```python
def is_recoverable(fs):
    ...
```

and tests

```text
199.999
200
200.001
```

What should you do?

Your lecture states the condition as

$$
\boxed{f_s>2f_M}
$$

rather than \(f_s\geq2f_M\). 

Therefore, if you are implementing **according to the course material**, write:

```python
def is_recoverable(fs):
    return fs > 200
```

not

```python
return fs >= 200
```

There are theoretical subtleties concerning equality and components precisely at the Nyquist edge, but that is exactly the kind of peripheral detail I would **not** spend revision time on unless your teacher raises it.

For the online, follow the convention taught.

---

# 12. A “mega-online” mock test

If you want one realistic 30-minute simulation, I would use this.

Set a timer for **30 minutes**, do not look at the solution, and implement the TODOs.

---

## Mock Online: Satellite Telemetry Recovery

A satellite emits the telemetry signal

$$
x(t)
=
A_1\cos(2\pi f_1t+\phi_1)
+
A_2\cos(2\pi f_2t+\phi_2)
+\cdots
$$

The receiver supports multiple ADC sampling rates.

A previous developer created the following code:

```python
import numpy as np


def build_signal(components):
    def x(t):
        ans = np.zeros_like(
            np.asarray(t),
            dtype=float
        )

        for A, f, phi in components:
            ans += A * np.cos(
                2*np.pi*f*t + phi
            )

        return ans

    return x


def acquire(signal, fs, duration):
    T = 1 / fs

    t = np.arange(0, duration, T)

    return t, signal(t)


def is_sampling_safe(components, fs):
    # TODO A
    pass


def reconstruct(
    samples,
    sample_times,
    query_times
):
    # TODO B
    pass


def alias_frequency(f, fs):
    # TODO C
    pass


def choose_modes(
    components,
    available_rates
):
    # TODO D
    pass


def estimate_error(
    signal,
    samples,
    sample_times,
    query_times
):
    # TODO E
    pass
```

### Requirements

`TODO A`
Return whether the sampling theorem's condition is satisfied.

`TODO B`
Perform ideal sinc interpolation. Do not assume the first sample occurs at \(0\).

`TODO C`
Return an equivalent frequency inside

$$
[-f_s/2,f_s/2).
$$

`TODO D`
Return all ADC rates satisfying Nyquist.

`TODO E`
Reconstruct at `query_times` and return RMS error relative to the known continuous signal.

The main program uses:

```python
components = [
    (1.2, 80, 0.1),
    (0.8, 230, -0.4),
    (0.3, 410, 1.0)
]

rates = [
    500,
    700,
    850,
    1000,
    1600
]
```

It additionally asks:

1. Which modes are theoretically safe?
2. At \(f_s=700\) Hz, what baseband frequency aliases from the 410 Hz component?
3. Why can sinc interpolation still pass exactly through every sampled point even when aliasing occurred?
4. Why does that not imply the reconstruction equals the original signal?

---

# Full solution

```python
def is_sampling_safe(components, fs):
    if not components:
        return True

    f_max = max(
        abs(f)
        for _, f, _ in components
    )

    return fs > 2 * f_max


def reconstruct(
    samples,
    sample_times,
    query_times
):
    samples = np.asarray(samples)
    sample_times = np.asarray(sample_times)
    query_times = np.asarray(query_times)

    if len(sample_times) < 2:
        raise ValueError(
            "Need at least two samples"
        )

    T = sample_times[1] - sample_times[0]

    kernel = np.sinc(
        (
            query_times[:, None]
            - sample_times[None, :]
        ) / T
    )

    return kernel @ samples


def alias_frequency(f, fs):
    return (
        (f + fs/2) % fs
    ) - fs/2


def choose_modes(
    components,
    available_rates
):
    return [
        fs
        for fs in available_rates
        if is_sampling_safe(
            components,
            fs
        )
    ]


def estimate_error(
    signal,
    samples,
    sample_times,
    query_times
):
    reconstructed = reconstruct(
        samples,
        sample_times,
        query_times
    )

    truth = signal(query_times)

    error = reconstructed - truth

    return np.sqrt(
        np.mean(np.abs(error)**2)
    )
```

The highest frequency is

$$
410\text{ Hz}.
$$

Therefore

$$
2f_M=820\text{ Hz}.
$$

So the safe modes are

$$
\boxed{
850,\ 1000,\ 1600\text{ Hz}.
}
$$

For

$$
f_s=700,
$$

$$
410-700=-290.
$$

Thus the complex/baseband representation aliases to

$$
-290\text{ Hz},
$$

or an apparent \(290\) Hz cosine for a real cosine signal.

---

# The very important conceptual question

> Why can aliased sinc reconstruction still pass through every sample?

Because interpolation guarantees

$$
x_r(nT)=x[n].
$$

That fact depends on

$$
\operatorname{sinc}(m-n)
=
\begin{cases}
1,&m=n,\\
0,&m\neq n.
\end{cases}
$$

It says:

> “The reconstructed continuous function agrees with the sample sequence.”

It does **not** say:

> “The reconstructed continuous function is necessarily the original continuous-time signal.”

When aliasing has happened, multiple continuous-time signals are compatible with the same sequence.

Sinc reconstruction chooses the band-limited interpretation in the reconstruction band.

That distinction is extremely testable.

---

# 13. Questions I would expect in viva immediately after the code

If the online has a viva component, make sure you can answer these without thinking too long:

### “Why did you divide by \(T\) inside `np.sinc`?”

Because the theoretical kernel is

$$
\operatorname{sinc}
\left(
\frac{t-nT}{T}
\right),
$$

which places its zeros one sampling interval apart.

### “Why didn't you multiply by \(T\)?”

The final time-domain interpolation formula contains no extra \(T\):

$$
x_r(t)
=
\sum_nx[n]
\operatorname{sinc}
\left(
\frac{t-nT}{T}
\right).
$$

### “Where did the \(T\) go?”

The sampled spectrum's central copy is scaled by \(1/T\), while the ideal LPF has passband gain \(T\); they cancel.

### “Why is ideal reconstruction impossible physically?”

Its impulse response

$$
h(t)=\operatorname{sinc}(t/T)
$$

extends to both \(t<0\) and \(t>0\), so it is noncausal and infinitely long. The slides make exactly this point.  

### “If the reconstructed waveform passes through all samples, why can it still be wrong?”

Because samples are not unique representations of arbitrary continuous-time signals. Uniqueness requires the band-limit/Nyquist assumption. 

### “What does aliasing mean mathematically?”

Neighboring terms of

$$
\frac1T
\sum_kX(j(\omega-k\omega_s))
$$

overlap and add.

### “Why can't the LPF undo aliasing?”

Because once spectral components add in the same frequency interval, the filter only sees their sum; it has no information telling it how to separate the original contributors. 

---

# 14. What I would personally practice before the test

Given the 25–35 minute constraint, don't spend tonight implementing huge DSP libraries.

I would be able to write these six functions without reference:

```python
sample_signal(...)
sinc_reconstruct(...)
nyquist_ok(...)
alias_frequency(...)
sampled_spectrum(...)
ideal_lpf(...)
```

The conceptual templates are:

```python
# uniform sampling
T = 1 / fs
t_samples = np.arange(start, end, T)
samples = x(t_samples)
```

```python
# Nyquist
safe = fs > 2 * fmax
```

```python
# alias wrapping
f_alias = ((f + fs/2) % fs) - fs/2
```

```python
# sinc interpolation
K = np.sinc(
    (t_query[:, None] - t_samples[None, :]) / T
)
x_reconstructed = K @ samples
```

```python
# physical sampling angular frequency
omega_s = 2 * np.pi / T
```

```python
# ideal LPF
H = np.where(
    np.abs(omega) < np.pi/T,
    T,
    0.0
)
```

and conceptually:

```python
Xp = 0
for k in replica_indices:
    Xp += shifted_X(k * omega_s)
Xp /= T
```

If those operations are automatic for you, most plausible “extend this provided sampling codebase” questions reduce to understanding the story wrapped around them.

The single most useful mental model to carry into the room is therefore not “Nyquist means sample twice as fast.” It is:

$$
\boxed{
\text{Sampling creates translated spectrum copies;}
\quad
\text{Nyquist keeps them separated;}
\quad
\text{sinc/ideal LPF extracts the original copy.}
}
$$

That is also exactly the progression of your lecture deck: sampling as impulse-train multiplication, spectrum copying, Nyquist/aliasing, then ideal LPF and sinc reconstruction. 
