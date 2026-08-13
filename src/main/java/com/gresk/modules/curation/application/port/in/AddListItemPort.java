package com.gresk.modules.curation.application.port.in;

import com.gresk.modules.curation.application.command.AddListItemCommand;
import com.gresk.modules.curation.domain.model.CuratedList;

public interface AddListItemPort {
    CuratedList execute(AddListItemCommand command);
}
