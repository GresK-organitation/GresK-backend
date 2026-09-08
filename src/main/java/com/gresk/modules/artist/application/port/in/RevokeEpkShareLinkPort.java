package com.gresk.modules.artist.application.port.in;

public interface RevokeEpkShareLinkPort {
    void execute(String shareLinkId, String promoterId);
}
