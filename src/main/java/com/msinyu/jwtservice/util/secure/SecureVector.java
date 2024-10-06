package com.msinyu.jwtservice.util.secure;

/**
 * Immutable and secure representation of a vector.
 */
public final class SecureVector {
    private final double[] vector;

    /**
     * Constructs a SecureVector by cloning the input array.
     *
     * @param vector The vector to encapsulate.
     */
    public SecureVector(double[] vector) {
        this.vector = vector.clone();
    }

    /**
     * Retrieves a copy of the encapsulated vector.
     *
     * @return A copy of the vector.
     */
    public double[] getVector() {
        return vector.clone();
    }

    /**
     * Clears the vector data by overwriting it.
     */
    public void clear() {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = 0.0;
        }
    }
}
