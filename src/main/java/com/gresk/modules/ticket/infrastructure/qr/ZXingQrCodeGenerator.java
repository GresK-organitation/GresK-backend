package com.gresk.modules.ticket.infrastructure.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.gresk.modules.ticket.domain.model.QrCode;
import com.gresk.modules.ticket.domain.model.TicketId;
import com.gresk.modules.ticket.domain.port.out.QrCodeGenerator;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ZXingQrCodeGenerator implements QrCodeGenerator {

    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;

    @Override
    public QrCode generate(TicketId ticketId) {
        return QrCode.of(ticketId.value().toString());
    }

    @Override
    public byte[] renderToImage(QrCode qrCode) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(qrCode.value(), BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to render QR code to image", e);
        }
    }
}
