package com.gresk.modules.curation.application.port.in;

public interface DeleteCuratedListPort {
    void execute(String listId, String userId);
}
