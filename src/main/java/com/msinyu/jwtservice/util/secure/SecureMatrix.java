package com.msinyu.jwtservice.util.secure;

import org.ejml.simple.SimpleMatrix;

/**
 * Immutable and secure representation of a matrix.
 */
public final class SecureMatrix {
    private final SimpleMatrix matrix;

    /**
     * Constructs a SecureMatrix by cloning the input matrix.
     *
     * @param matrix The matrix to encapsulate.
     */
    public SecureMatrix(SimpleMatrix matrix) {
        this.matrix = matrix.copy();
    }

    /**
     * Retrieves a copy of the encapsulated matrix.
     *
     * @return A copy of the matrix.
     */
    public SimpleMatrix getMatrix() {
        return matrix.copy();
    }

    /**
     * Clears the matrix data by overwriting it.
     */
    public void clear() {
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                matrix.set(i, j, 0.0);
            }
        }
    }
}
