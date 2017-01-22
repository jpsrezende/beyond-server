package quest.beluslan;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

public class _4542TheSecretoftheSeirenTreasure extends QuestHandler {

	private final static int questId = 4542;
	private final static int[] npc_ids = { 204768, 204743, 204808 };

	public _4542TheSecretoftheSeirenTreasure() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204768).addOnQuestStart(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.isStartable()) {
			if (targetId == 204768) { // Sleipnir
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env, 182215327, 1);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 204743: // Rubelik
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							return false;
						case SELECT_ACTION_1012:
							return sendQuestDialog(env, 1012);
						case SELECT_ACTION_1097:
							return sendQuestDialog(env, 1097);
						case SETPRO1:
							return defaultCloseDialog(env, 0, 1); // 1
					}
					break;
				case 204768: // Sleipnir
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 1)
								return sendQuestDialog(env, 1352);
							if (var == 5)
								return sendQuestDialog(env, 2716);
							return false;
						case SELECT_ACTION_1353:
							return sendQuestDialog(env, 1353);
						case SELECT_ACTION_1438:
							return sendQuestDialog(env, 1438);
						case SETPRO2:
							removeQuestItem(env, 182215327, 1);
							return defaultCloseDialog(env, 1, 2, false, false, 182215328, 1, 0, 0); // 2
						case SELECT_QUEST_REWARD:
							removeQuestItem(env, 182215330, 1);
							return defaultCloseDialog(env, 5, 5, true, true); // reward
						case SELECT_ACTION_2717:
							return sendQuestDialog(env, 2717);
						case SETPRO6:
							changeQuestStep(env, 5, 6, true); // 6
							removeQuestItem(env, 182215330, 1);
							return sendQuestDialog(env, 5); // reward
					}
					break;
				case 204808: // Esnu
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 2)
								return sendQuestDialog(env, 1693);
							if (var == 3)
								return sendQuestDialog(env, 2034);
							if (var == 4)
								return sendQuestDialog(env, 2375);
							return false;
						case SELECT_ACTION_1694:
							return sendQuestDialog(env, 1694);
						case SELECT_ACTION_1779:
							return sendQuestDialog(env, 1779);
						case SETPRO3:
							removeQuestItem(env, 182215328, 1);
							return defaultCloseDialog(env, 2, 3);
						case CHECK_USER_HAS_QUEST_ITEM:
							return checkQuestItems(env, 3, 4, false, 10000, 10001); // 4
						case FINISH_DIALOG:
							if (var == 3)
								defaultCloseDialog(env, 3, 3); // 3
							return false;
						case SELECT_ACTION_2376:
							return sendQuestDialog(env, 2376);
						case SETPRO5:
							removeQuestItem(env, 182215329, 1);
							return defaultCloseDialog(env, 4, 5, false, false, 182215330, 1, 0, 0); // 5
					}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204768) { // Sleipnir
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
