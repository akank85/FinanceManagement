package com.finance.FinanceManagement.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class TwoFactorService {

    private final GoogleAuthenticator googleAuthenticator =
            new GoogleAuthenticator();

    public String generateSecret() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }

    public String generateQrCodeUrl(String secret, String email) {

        try {String issuer = URLEncoder.encode("FinanceManagement", StandardCharsets.UTF_8);
            String account = URLEncoder.encode(email, StandardCharsets.UTF_8);
            return "otpauth://totp/" + issuer + ":" + account + "?secret=" + secret + "&issuer=" + issuer
                    + "&algorithm=SHA1" + "&digits=6" + "&period=30";
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to generate 2FA QR URL",
                    e
            );
        }
    }

    // yeh QR PNG img gen and return Base64
    public String generateQrCode(String qrUrl) throws IOException, WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 4);
        BitMatrix bitMatrix = qrCodeWriter.encode(qrUrl, BarcodeFormat.QR_CODE, 400, 400, hints);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return Base64.getEncoder().encodeToString(
                        outputStream.toByteArray());
    }

    public boolean verifyOtp(String secret, int otp) {
        return googleAuthenticator.authorize(secret, otp);
    }
}
