"""
transforms.py  --  YOUR CODE GOES HERE.

The shared transform core used by BOTH tasks. Write it once; bigmul.py
(Task A) and image_conv.py (Task B) import it.

Nothing in this file may call numpy.fft, scipy.fft, numpy.convolve,
scipy.signal, or any other library routine that performs a Fourier
transform, a convolution or a correlation for you. NumPy is for array
arithmetic only.

A quick self-test you should run before touching either application:

    import numpy as np
    from transforms import DFTAnalyzer, FFTTransformer
    x = np.random.randn(64) + 1j * np.random.randn(64)
    d, f = DFTAnalyzer(), FFTTransformer()
    assert np.max(np.abs(d.transform(x) - f.transform(x))) < 1e-9
    assert np.max(np.abs(d.inverse(d.transform(x)) - x)) < 1e-9
"""

import numpy as np


def next_power_of_two(n):
    """
    Return the smallest power of two that is >= ``n`` (and at least 1).

    Both tasks need this to choose a transform length for the radix-2 FFT.
    """
    # TODO: implement this function
    
    return 1 << max(0, n - 1).bit_length()

class DFTAnalyzer:
    """
    The Discrete Fourier Transform, computed straight from its definition.

        Analysis:   X[k] = sum_{n=0}^{N-1} x[n] * exp(-2j*pi*k*n/N)
        Synthesis:  x[n] = (1/N) * sum_{k=0}^{N-1} X[k] * exp(+2j*pi*k*n/N)

    How you write it is up to you -- a literal double loop, a precomputed
    table of twiddle factors indexed by (k*n) % N, or a NumPy expression --
    as long as it computes these sums directly and is not secretly an FFT.
    """

    name = "dft"

    def transform(self, x):
        """
        Forward DFT.

        Parameters
        ----------
        x : 1D array_like, length N (real or complex)

        Returns
        -------
        numpy.ndarray of complex128, shape (N,)
        """
        # TODO: implement this method

        # x[n] -> X[k]
        
        N = len(x)
        n = np.arange(N)
        
        X = np.zeros_like(x, dtype=np.complex128)
        
        for k in range(N):
            W_Nk = np.exp(-2j * np.pi * k * (1/N))
            
            X[k] = np.sum(x * W_Nk**n)
            
        return X         


    def inverse(self, spectrum):
        """
        Inverse DFT, including the 1/N factor.

        Parameters
        ----------
        spectrum : 1D array_like, length N (complex)

        Returns
        -------
        numpy.ndarray of complex128, shape (N,)
            Do NOT discard the imaginary part here -- the caller decides when
            it is safe to take .real.
        """
        # TODO: implement this method
        
        # X[k] -> x[n]
        
        X = spectrum
        N = len(X)
        
        k = np.arange(N)
        
        x = np.zeros_like(X, dtype=np.complex128)
        
        for n in range(N):
            W_Nn = np.exp(2j * np.pi * n * (1/N))
            
            x[n] = np.sum(X * W_Nn**k)
            
        return x * (1/N)     

class FFTTransformer(DFTAnalyzer):
    """
    Radix-2 decimation-in-time (Cooley-Tukey) FFT, in O(N log N).

    It inherits from DFTAnalyzer so that both applications can treat the two
    interchangeably: they call ``engine.transform(...)`` and
    ``engine.inverse(...)`` without caring which engine they hold.

    Requirements:
      * Recursive or iterative (with bit-reversal permutation) -- your choice.
      * N must be a power of two; raise ValueError for any other length.
        The caller is responsible for zero-padding up to next_power_of_two.
      * The inverse must reuse the same butterfly machinery (conjugated
        twiddles, or conjugate-transform-conjugate), not a second copy of it.
      * Twiddle factors for a stage are computed once per stage, never once
        per butterfly.
    """

    name = "fft"

    def transform(self, x):
        """Forward FFT. Same contract as DFTAnalyzer.transform."""
        # TODO: implement this method
    
        N = len(x)
        
        if ((N > 0) and (N & (N - 1) != 0)):
            raise ValueError("Length not a power of 2")
        
        X = np.zeros_like(x, dtype=np.complex128)
        
        if N == 1:
            X[0] = x[0] 
            return X
            
            
        x_e = x[0::2]
        x_o = x[1::2]
        
        X_e = self.transform(x_e)
        X_o = self.transform(x_o)
        
        k = np.arange(N//2)
        
        W = np.exp(-2j * np.pi * (1/N) * k)
        
        X_top = X_e + W * X_o
        X_bottom = X_e - W * X_o
        
        X = np.concatenate([X_top,X_bottom])
        
        return X

    def inverse(self, spectrum):
        """Inverse FFT, including the 1/N factor."""
        # TODO: implement this method
    
        # inv_ft(X) = (1/N) * conj(F(conj(x)))
        
        X = spectrum.astype(np.complex128)
        N = len(spectrum)
        
        return (1/N) * np.conj(self.transform(np.conj(X)))


# ---------------------------------------------------------------------------
# BONUS (optional) -- arbitrary-length FFT.
#
# Delete this class if you are not attempting the bonus. If you do attempt it,
# run both tasks with --engine arbitrary and leave those output directories in
# your submission as the evidence.
# ---------------------------------------------------------------------------
class ArbitraryLengthFFT(FFTTransformer):
    """
    Bonus: an O(N log N) transform for ANY length N, not just powers of two.

    Bluestein's chirp-z algorithm is the usual route: rewrite the DFT as a
    convolution of two chirp sequences, and evaluate that convolution with a
    radix-2 FFT of length >= 2N-1. A mixed-radix Cooley-Tukey that factorises
    N is equally acceptable.

    With this engine, Task A no longer has to pad the digit arrays up to a
    power of two, and Task B no longer has to pad the image up to one.
    """

    name = "arbitrary"

    def transform(self, x):
        # TODO (bonus): implement this method
    
        N = len(x)
        if N == 0:
            return np.zeros_like(x, dtype=np.complex128)
        
        if N & (N - 1) == 0:
            return super().transform(x)

        # define the chirp sequence: exp(-j * pi * n^2 / N)
        n = np.arange(N)
        chirp = np.exp(-1j * np.pi * (n**2) / N)

        # modulate the input sequence
        a = x * chirp

        # find an M >= 2N - 1 that is a power of two for the Radix-2 FFT
        M = next_power_of_two(2 * N - 1)
        
        # pad the modulated input with zeros up to M
        a_padded = np.pad(a, (0, M - N))

        # construct the inverse chirp 'b' for circular convolution
        b = np.zeros(M, dtype=np.complex128)
        b[:N] = np.exp(1j * np.pi * (n**2) / N)
        
        # wrap around the negative indices to the end of the array
        for i in range(1, N):
            b[M - i] = b[i]

        # fast convolution through the frequency domain using our Radix-2
        A = super().transform(a_padded)
        B = super().transform(b)
        c = super().inverse(A * B)

        # truncate to length N and demodulate
        X = c[:N] * chirp
        
        return X

    def inverse(self, spectrum):
        # TODO (bonus): implement this method
    
        # inv_ft(X) = (1/N) * conj(F(conj(x)))
        
        X = spectrum.astype(np.complex128)
        N = len(spectrum)
        
        return (1/N) * np.conj(self.transform(np.conj(X)))


class NTTTransformer(DFTAnalyzer):
    """
    Number Theoretic Transform (NTT).

    The NTT replaces the complex root of unity e^(-2*pi*j/N) with a modular 
    root of unity W_N. If p is a prime and g is a primitive root modulo p, 
    then W_N = g^((p-1)/N) mod p acts as the principal N-th root of unity.
    Because all operations are performed modulo a prime, floating-point 
    inaccuracies are completely eliminated.
    
    Process:
    1. Initialize X as a copy of input x, modulo p.
    2. Perform bit-reversal permutation on X to allow in-place computation.
    3. For length = 2, 4, 8, ... N:
         a. half = length / 2
         b. W_len = g^((p-1)/length) mod p (If inverse, use modular inverse of W_len)
         c. For i = 0 to N with step=length:
              w = 1
              For k = 0 to half - 1:
                u = X[i + k]
                v = (X[i + k + half] * w) mod p
                X[i + k] = (u + v) mod p
                X[i + k + half] = (u - v + p) mod p
                w = (w * W_len) mod p
    4. If inverse NTT, multiply the final array by the modular inverse of N.
    """
    name = "ntt"

    def __init__(self, modulus=998244353, primitive_root=3):
        self.modulus = modulus
        self.primitive_root = primitive_root

    def transform(self, x, invert=False):
        """Forward NTT (or Inverse if invert=True)."""
        N = len(x)
        if N == 0:
            return np.zeros(0, dtype=np.int64)
        if N & (N - 1) != 0:
            raise ValueError("Length must be a power of 2")

        # 1. Initialize and apply modulo
        X = np.array(x, dtype=np.int64) % self.modulus

        # 2. Bit-reversal permutation
        j = 0
        for i in range(1, N):
            bit = N >> 1
            while j & bit:
                j ^= bit
                bit >>= 1
            j ^= bit
            if i < j:
                X[i], X[j] = X[j], X[i]

        # 3. Iterative Butterfly
        length = 2
        while length <= N:
            half = length // 2
            
            # Calculate the root of unity for this stage
            wlen = pow(int(self.primitive_root), (self.modulus - 1) // length, self.modulus)
            if invert:
                # Use Fermat's Little Theorem for the modular inverse
                wlen = pow(wlen, self.modulus - 2, self.modulus)

            for i in range(0, N, length):
                w = 1
                for k in range(half):
                    u = X[i + k]
                    v = (X[i + k + half] * w) % self.modulus
                    X[i + k] = (u + v) % self.modulus
                    X[i + k + half] = (u - v + self.modulus) % self.modulus
                    w = (w * wlen) % self.modulus
            length *= 2

        # 4. Final scaling for inverse transform
        if invert:
            inv_N = pow(N, self.modulus - 2, self.modulus)
            X = (X * inv_N) % self.modulus

        return X

    def inverse(self, spectrum):
        """Inverse NTT."""
        return self.transform(spectrum, invert=True)