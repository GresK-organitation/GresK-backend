package com.gresk.modules.artist.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record UploadEpkAssetRequest(@NotBlank String type, @NotBlank String label) {}
