package com.gresk.modules.artist.application.port.in;

import com.gresk.modules.artist.application.command.AddEpkAssetVersionCommand;
import com.gresk.modules.artist.domain.model.EpkAsset;

public interface AddEpkAssetVersionPort {
    EpkAsset execute(AddEpkAssetVersionCommand command);
}
