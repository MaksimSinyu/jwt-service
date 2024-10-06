package com.msinyu.jwtservice.util;

import com.msinyu.jwtservice.util.secure.SecureMatrix;
import org.ejml.simple.SimpleMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class for advanced mathematical operations.
 */
@Component
public class MathUtil {

    @Autowired
    private EncryptionUtil encryptionUtil;

    /**
     * Generates a secure matrix from the input string.
     *
     * @param input Input string (e.g., password hash).
     * @return SecureMatrix instance.
     * @throws Exception If encryption fails.
     */
    public SecureMatrix generateSecureMatrix(String input) throws Exception {
        double[][] data = new double[128][128];
        byte[] inputBytes = input.getBytes();
        int index = 0;
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                data[i][j] = (inputBytes[index % inputBytes.length] & 0xFF) / 255.0;
                index++;
            }
        }
        SimpleMatrix matrix = new SimpleMatrix(data);
        return new SecureMatrix(matrix);
    }

    /**
     * Applies a non-linear transformation to the matrix using element-wise logarithm.
     *
     * @param secureMatrix The input SecureMatrix.
     * @return Transformed SecureMatrix.
     */
    public SecureMatrix nonLinearTransform(SecureMatrix secureMatrix) {
        SimpleMatrix matrix = secureMatrix.getMatrix().copy();

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                matrix.set(i, j, Math.log(1 + matrix.get(i, j)));
            }
        }

        return new SecureMatrix(matrix);
    }

    /**
     * Calculates the derivative of the matrix by computing differences between adjacent rows and columns.
     *
     * @param secureMatrix The input SecureMatrix.
     * @return Derivative SecureMatrix.
     */
    public SecureMatrix calculateDerivative(SecureMatrix secureMatrix) {
        SimpleMatrix matrix = secureMatrix.getMatrix();

        SimpleMatrix rowDiff = new SimpleMatrix(matrix.numRows() - 1, matrix.numCols());
        for (int i = 1; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                rowDiff.set(i - 1, j, matrix.get(i, j) - matrix.get(i - 1, j));
            }
        }

        // Calculate difference between adjacent columns
        SimpleMatrix colDiff = new SimpleMatrix(rowDiff.numRows(), rowDiff.numCols() - 1);
        for (int i = 0; i < rowDiff.numRows(); i++) {
            for (int j = 1; j < rowDiff.numCols(); j++) {
                colDiff.set(i, j - 1, rowDiff.get(i, j) - rowDiff.get(i, j - 1));
            }
        }

        return new SecureMatrix(colDiff);
    }

    /**
     * Converts the matrix to a binary array, encrypts it, and encodes it in Base64.
     *
     * @param secureMatrix The input SecureMatrix.
     * @return Encrypted binary array as a Base64 string.
     * @throws Exception If encryption fails.
     */
    public String matrixToBinaryArray(SecureMatrix secureMatrix) throws Exception {
        SimpleMatrix matrix = secureMatrix.getMatrix();

        StringBuilder binary = new StringBuilder();
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                binary.append(matrix.get(i, j) >= 0 ? '1' : '0');
            }
            binary.append(';'); // Delimiter for rows
        }

        String binaryString = binary.toString();
        String encryptedBinary = encryptionUtil.encrypt(binaryString);
        return encryptedBinary;
    }
}
