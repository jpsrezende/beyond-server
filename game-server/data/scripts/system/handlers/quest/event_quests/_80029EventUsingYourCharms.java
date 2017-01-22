package quest.event_quests;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Rolandas
 */

public class _80029EventUsingYourCharms extends QuestHandler {

	private final static int questId = 80029;

	public _80029EventUsingYourCharms() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799766).addOnQuestStart(questId);
		qe.registerQuestNpc(799766).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.isStartable())
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799766) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else if (env.getDialog() == DialogAction.QUEST_ACCEPT_1)
					return sendQuestDialog(env, 2375);
				else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
					defaultCloseDialog(env, 0, 0, true, true);
					return sendQuestDialog(env, 5);
				} else if (env.getDialog() == DialogAction.SELECTED_QUEST_NOREWARD)
					return sendQuestRewardDialog(env, 799766, 5);
				else
					return sendQuestStartDialog(env);
			}
		}
		return sendQuestRewardDialog(env, 799766, 0);
	}

}
