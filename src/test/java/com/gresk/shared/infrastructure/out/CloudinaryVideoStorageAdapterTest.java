package com.gresk.shared.infrastructure.out;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.gresk.shared.domain.exception.VideoStorageException;
import com.gresk.shared.domain.valueobject.VideoUploadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudinaryVideoStorageAdapterTest {

    @Mock private Cloudinary cloudinary;
    @Mock private Uploader   uploader;

    private CloudinaryVideoStorageAdapter adapter;

    private final MockMultipartFile file =
            new MockMultipartFile("file", "clip.mp4", "video/mp4", new byte[]{1, 2, 3});

    @BeforeEach
    void setUp() {
        adapter = new CloudinaryVideoStorageAdapter(cloudinary);
    }

    @Test
    void subeUnVideoYDevuelveLaDuracionRecortadaPorCloudinary() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of(
                "public_id", "journal/videos/clip123",
                "eager", List.of(Map.of("duration", 15.0))
        ));

        VideoUploadResult result = adapter.upload(file, "journal/videos");

        assertEquals("journal/videos/clip123", result.assetId().value());
        assertEquals(15, result.durationSeconds());
    }

    @Test
    void rechazaYBorraElVideoSiSuperaLosQuinceSegundos() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of(
                "public_id", "journal/videos/clip999",
                "duration", 42.0
        ));
        when(uploader.destroy(any(String.class), anyMap())).thenReturn(Map.of("result", "ok"));

        assertThrows(VideoStorageException.class, () -> adapter.upload(file, "journal/videos"));
    }

    @Test
    void rechazaFicheroVacio() {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.mp4", "video/mp4", new byte[0]);

        assertThrows(VideoStorageException.class, () -> adapter.upload(empty, "journal/videos"));
    }
}
