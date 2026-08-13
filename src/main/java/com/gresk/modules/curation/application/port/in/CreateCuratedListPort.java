package com.gresk.modules.curation.application.port.in;

import com.gresk.modules.curation.application.command.CreateCuratedListCommand;
import com.gresk.modules.curation.domain.model.CuratedList;

public interface CreateCuratedListPort {
    CuratedList execute(CreateCuratedListCommand command);
}
