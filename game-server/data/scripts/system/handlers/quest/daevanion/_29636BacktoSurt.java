package quest.daevanion;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Pad
 */
public class _29636BacktoSurt extends QuestHandler {

	private static final int questId = 29636;
	private static final int[] npcIds = { 205164, 205150 }; // BuBu Khaaan & Surt
	private static final int[] mobIds = { 214491, 214492, 214489, 214490 };

	public _29636BacktoSurt() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(npcIds[0]).addOnQuestStart(questId);
		for (int npcId : npcIds) {
			qe.registerQuestNpc(npcId).addOnTalkEvent(questId);
		}
		for (int mobId : mobIds) {
			qe.registerQuestNpc(mobId).addOnKillEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.isStartable()) {
			if (targetId == npcIds[0]) { // BuBu Khaaan
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == npcIds[1]) { // Surt
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var1 = qs.getQuestVarById(1);
			int targetId = env.getTargetId();
			for (int mobId : mobIds) {
				if (targetId == mobId) {
					if (qs.getQuestVarById(0) == 0) {
						if (0 <= var1 && var1 < 9) {
							changeQuestStep(env, var1, var1 + 1, false, 1); // @1: 1 - 9
							return true;
						} else if (var1 == 9) {
							qs.setQuestVarById(0, 1); // 1
							qs.setStatus(QuestStatus.REWARD); // Reward
							updateQuestStatus(env);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
