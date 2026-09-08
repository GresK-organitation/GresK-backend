package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.UploadEpkAssetCommand;
import com.gresk.modules.artist.domain.model.EpkAsset;

public interface UploadEpkAssetPort {
    EpkAsset execute(UploadEpkAssetCommand command);
}
