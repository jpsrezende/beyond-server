package quest.haramel;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author zhkchi
 * @modified Majka
 */
public class _28500OdellaOdellaWhereArtThou extends QuestHandler {

	private final static int questId = 28500;

	public _28500OdellaOdellaWhereArtThou() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203560, 203649, 730306, 730307, 799522 };
		qe.registerQuestNpc(203560).addOnQuestStart(questId);
		qe.registerOnEnterZone(ZoneName.get("DF1A_SENSORYAREA_Q28500_206151_3_220030000"), questId);
		qe.registerOnMovieEndQuest(217, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.isStartable()) {
			if (targetId == 203560) { // Morn
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 203649: // Gulkalla
					switch (dialog) {
						case QUEST_SELECT:
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							return false;
						case SETPRO1:
							return defaultCloseDialog(env, 0, 1); // 1
					}
					break;
				case 730306: // Discarded Odium Piece
					switch (dialog) {
						case USE_OBJECT:
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							return false;
						case SETPRO2:
							return defaultCloseDialog(env, 1, 2); // 2
					}
					break;
				case 730307: // Discarded Odium Pile
					switch (dialog) {
						case USE_OBJECT:
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
							return false;
						case SETPRO3:
							playQuestMovie(env, 217);
							return defaultCloseDialog(env, 2, 3); // 3
					}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799522) { // Moorilerk
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) { // Investigate the Abandoned Relic Site.

		if (zoneName == ZoneName.get("DF1A_SENSORYAREA_Q28500_206151_3_220030000")) {

			Player player = env.getPlayer();
			if (player == null) {
				return false;
			}

			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);

				if (var == 3) {
					playQuestMovie(env, 217);
					player.getMoveController().abortMove();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		Player player = env.getPlayer();

		if (player == null) {
			return false;
		}

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				changeQuestStep(env, 3, 3, true); // reward
				return true;
			}
		}
		return false;
	}
}
