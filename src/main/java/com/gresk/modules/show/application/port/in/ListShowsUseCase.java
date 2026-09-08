package com.gresk.modules.show.application.port.in;

import com.gresk.modules.show.application.query.ListShowsQuery;
import com.gresk.modules.show.domain.model.Show;

import java.util.List;

public interface ListShowsUseCase {
    List<Show> execute(ListShowsQuery query);
}
