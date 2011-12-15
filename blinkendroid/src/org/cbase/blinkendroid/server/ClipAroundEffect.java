package org.cbase.blinkendroid.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClipAroundEffect implements ITouchEffect {

    private static final Logger logger = LoggerFactory.getLogger(ClipAroundEffect.class);
    private static final String EFFECT_NAME = "Clip Around";
    private PlayerManager playerManager = null;

    public ClipAroundEffect(PlayerManager pMgr) {
	this.playerManager = pMgr;
    }

    public void showEffect(PlayerClient playerClient) {
	try {
	    // light up row and column
	    int x = playerClient.x;
	    int y = playerClient.y;
	    int maxX = playerManager.getMaxX();
	    int maxY = playerManager.getMaxY();

	    logger.error("touch " + x + "," + y);
	    int i = 1;

	    clip(x, y + 1);
	    clip(x - 1, y + 1);
	    clip(x - 1, y);
	    clip(x - 1, y - 1);
	    clip(x, y - 1);
	    clip(x + 1, y - 1);
	    clip(x + 1, y);
	    clip(x + 1, y + 1);

	} catch (Exception e) {
	    logger.error("touch failed", e);
	}
    }

    private void clip(int x, int y) {
	PlayerClient pc = playerManager.getPlayer(x, y);
	if (null != pc)
	    pc.getBlinkenProtocol().clip((float) 0.0, (float) 0.0, (float) 1.0, (float) 1.0);
    }
    
    @Override
    public String toString() {
	return EFFECT_NAME;
    }
}
