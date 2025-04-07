package tr.com.kaanhangunay.examples.hotel_management.config;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OttTokenGenerationSuccessHandler implements OneTimeTokenGenerationSuccessHandler {

  private final boolean isDevProfile;

  public OttTokenGenerationSuccessHandler(Environment environment) {
    isDevProfile = !Arrays.asList(environment.getActiveProfiles()).contains("prod");
  }

  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken)
      throws IOException, ServletException {

    log.info("Token: {}", oneTimeToken.getTokenValue());

    String qrContent = oneTimeToken.getTokenValue();

    try {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);
      BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(qrImage, "png", baos);
      byte[] qrBytes = baos.toByteArray();
      String base64Qr = Base64.getEncoder().encodeToString(qrBytes);

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      String token = isDevProfile ? ", \"token\":\"" + oneTimeToken.getTokenValue() + "\"" : "";
      String jsonResponse =
          String.format("{\"qr\":\"data:image/png;base64,%s\"%s}", base64Qr, token);
      response.getWriter().write(jsonResponse);

    } catch (WriterException e) {
      throw new ServletException("QR Code could not be generated", e);
    }
  }
}
