package ai.instance.rentusBase;

import com.aionemu.gameserver.ai.AIActions;
import com.aionemu.gameserver.ai.AIName;
import com.aionemu.gameserver.ai.NpcAI;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author xTz
 */
@AIName("flame_smash")
public class FlameSmashAI extends NpcAI {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		starLifeTask();
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), getNpcId() == 283008 ? 20540 : 20539, 60, getOwner()).useNoAnimationSkill();
				}
			}

		}, 500);
	}

	private void starLifeTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				despawn();
			}

		}, 7000);
	}

	private void despawn() {
		if (!isAlreadyDead()) {
			AIActions.deleteOwner(this);
		}
	}
}