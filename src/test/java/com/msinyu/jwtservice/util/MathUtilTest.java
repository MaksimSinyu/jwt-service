package com.msinyu.jwtservice.util;

import com.msinyu.jwtservice.util.secure.SecureMatrix;
import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class MathUtilTest {

    @Autowired
    private MathUtil mathUtil;

    @Autowired
    private EncryptionUtil encryptionUtil;

    private String sampleInput = "SamplePasswordHash123!";

    @Test
    public void testGenerateSecureMatrix() throws Exception {
        SecureMatrix secureMatrix = mathUtil.generateSecureMatrix(sampleInput);
        SimpleMatrix matrix = secureMatrix.getMatrix();

        assertThat(matrix.numRows()).isEqualTo(128);
        assertThat(matrix.numCols()).isEqualTo(128);
    }

    @Test
    public void testNonLinearTransform() throws Exception {
        SecureMatrix secureMatrix = mathUtil.generateSecureMatrix(sampleInput);
        SecureMatrix transformedMatrix = mathUtil.nonLinearTransform(secureMatrix);
        SimpleMatrix matrix = transformedMatrix.getMatrix();

        // Verify that transformation has been applied
        assertThat(matrix.get(0, 0)).isGreaterThan(0.0);
    }

    @Test
    public void testCalculateDerivative() throws Exception {
        SecureMatrix secureMatrix = mathUtil.generateSecureMatrix(sampleInput);
        SecureMatrix transformedMatrix = mathUtil.nonLinearTransform(secureMatrix);
        SecureMatrix derivativeMatrix = mathUtil.calculateDerivative(transformedMatrix);
        SimpleMatrix matrix = derivativeMatrix.getMatrix();

        // Derivative matrix should have one less row and column
        assertThat(matrix.numRows()).isEqualTo(127);
        assertThat(matrix.numCols()).isEqualTo(127);
    }

    @Test
    public void testMatrixToBinaryArray() throws Exception {
        SecureMatrix secureMatrix = mathUtil.generateSecureMatrix(sampleInput);
        SecureMatrix transformedMatrix = mathUtil.nonLinearTransform(secureMatrix);
        SecureMatrix derivativeMatrix = mathUtil.calculateDerivative(transformedMatrix);
        String encryptedBinary = mathUtil.matrixToBinaryArray(derivativeMatrix);

        assertThat(encryptedBinary).isNotNull();
        String decryptedBinary = encryptionUtil.decrypt(encryptedBinary);
        assertThat(decryptedBinary).matches("[01;]+");
    }
}
