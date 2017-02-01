package ai.instance.rakes;

import com.aionemu.gameserver.ai.AIActions;
import com.aionemu.gameserver.ai.AIName;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import ai.ChestAI;

/**
 * @author xTz
 */
@AIName("steel_rake_key_box")
public class SteelRakeKeyBoxAI extends ChestAI {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead() && getOwner().isSpawned()) {
					AIActions.deleteOwner(SteelRakeKeyBoxAI.this);
				}
			}

		}, 180000);
	}
}