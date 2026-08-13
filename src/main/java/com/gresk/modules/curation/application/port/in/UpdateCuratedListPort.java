package com.gresk.modules.curation.application.port.in;

import com.gresk.modules.curation.application.command.UpdateCuratedListCommand;
import com.gresk.modules.curation.domain.model.CuratedList;

public interface UpdateCuratedListPort {
    CuratedList execute(UpdateCuratedListCommand command);
}
