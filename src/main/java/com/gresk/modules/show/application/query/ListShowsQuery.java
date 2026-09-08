package com.gresk.modules.show.application.query;

import com.gresk.modules.show.domain.model.ShowStatus;

public record ListShowsQuery(String promoterId, ShowStatus status) {
}
