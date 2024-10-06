package com.msinyu.jwtservice.util;

import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Utility class for vector operations.
 */
@Component
public class VectorUtil {

    private static final int VECTOR_SIZE = 128;

    /**
     * Converts a hash string to a numerical vector.
     *
     * @param hash Hash string.
     * @return Numerical vector.
     */
    public double[] hashToVector(String hash) {
        double[] vector = new double[VECTOR_SIZE];
        byte[] hashBytes = hash.getBytes();
        for (int i = 0; i < VECTOR_SIZE; i++) {
            int byteValue = hashBytes[i % hashBytes.length] & 0xFF;
            vector[i] = byteValue / 255.0;
        }
        return vector;
    }

    /**
     * Applies a non-linear transformation to the vector.
     *
     * @param vector Input vector.
     * @return Transformed vector.
     */
    public double[] nonLinearTransform(double[] vector) {
        double[] transformed = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            transformed[i] = Math.log(1 + vector[i]);
        }
        return transformed;
    }

    /**
     * Calculates the derivative of the vector.
     *
     * @param vector Input vector.
     * @return Derivative vector.
     */
    public double[] calculateDerivative(double[] vector) {
        double[] derivative = new double[vector.length - 1];
        for (int i = 1; i < vector.length; i++) {
            derivative[i - 1] = vector[i] - vector[i - 1];
        }
        return derivative;
    }

    /**
     * Converts a double array to a binary array.
     *
     * @param vector Input double array.
     * @return Binary array.
     */
    public byte[] toBinaryArray(double[] vector) {
        StringBuilder binary = new StringBuilder();
        for (double v : vector) {
            binary.append(v >= 0 ? '1' : '0');
        }
        byte[] bytes = new byte[binary.length()];
        for (int i = 0; i < binary.length(); i++) {
            bytes[i] = (byte) binary.charAt(i);
        }
        return bytes;
    }

    /**
     * Encodes a byte array to a Base64 string.
     *
     * @param data Byte array.
     * @return Base64 encoded string.
     */
    public String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
