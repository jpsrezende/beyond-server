package ai.instance.stonespearReach;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai.AIName;
import com.aionemu.gameserver.ai.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import ai.GeneralNpcAI;

/**
 * @author Yeats
 */
@AIName("stonespear_kebbit")
public class KebbitAI extends GeneralNpcAI {

	private Future<?> despawnTask;

	@Override
	public void handleSpawned() {
		super.handleSpawned();
		startDespawnTask();
	}

	private void startDespawnTask() {
		if (despawnTask != null) {
			return;
		}
		despawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (getOwner() != null && !getOwner().getLifeStats().isAlreadyDead()) {
					getOwner().getController().delete();
				}
			}
		}, 15500); // 15,5s
	}

	@Override
	public void handleDied() {
		cancelTask();
		super.handleDied();
		getOwner().getController().delete();
	}

	private void cancelTask() {
		if (despawnTask != null && !despawnTask.isCancelled()) {
			despawnTask.cancel(false);
		}
	}

	@Override
	public boolean ask(AIQuestion question) {
		switch (question) {
			case SHOULD_RESPAWN:
			case SHOULD_REWARD:
			case SHOULD_LOOT:
			case CAN_ATTACK_PLAYER:
				return false;
			default:
				return super.ask(question);
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		// do nothing
	}
}