package quest.altgard;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.animations.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/**
 * @author Artur
 * @Modified Majka
 */
public class _24016AStrangeNewThread extends QuestHandler {

	public _24016AStrangeNewThread() {
		super(24016);
	}

	@Override
	public void register() {
		int[] npcs = { 203557, 700140, 700141 };
		qe.registerOnQuestCompleted(questId);
		qe.registerOnLevelChanged(questId);
		qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(154, questId);
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		qe.registerQuestNpc(233876).addOnKillEvent(questId);
		qe.registerQuestNpc(210753).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203557: // Suthran
					if (var == 0) {
						if (player.getPlayerClass() == PlayerClass.RIDER) { // Path for Rider
							if (dialog == DialogAction.QUEST_SELECT) {
								return sendQuestDialog(env, 1011);
							} else if (dialog == DialogAction.SELECT_ACTION_1013) {
								playQuestMovie(env, 219);
								return sendQuestDialog(env, 1013);
							}
						} else { // Path for other classes
							if (dialog == DialogAction.QUEST_SELECT) {
								return sendQuestDialog(env, 1693);
							} else if (dialog == DialogAction.SELECT_ACTION_1695) {
								playQuestMovie(env, 66);
								return sendQuestDialog(env, 1695);
							}
						}

						if (dialog == DialogAction.SETPRO1) {
							TeleportService2.teleportTo(player, 220030000, 2467.6052f, 2548.0076f, 316.12375f, (byte) 63, TeleportAnimation.FADE_OUT_BEAM);
							changeQuestStep(env, 0, 1, false); // 1
							return closeDialogWindow(env);
						}
					}
					break;
				case 700140: // Gate Guardian Stone
					if (var == 2) {
						if (dialog == DialogAction.USE_OBJECT) {
							if (player.getPlayerClass() == PlayerClass.RIDER) { // Spawn for Rider
								QuestService.addNewSpawn(320030000, player.getInstanceId(), 233876, (float) 260.12, (float) 234.93, (float) 216.00, (byte) 90); // Officer
																																																																								// Tavasha
								return useQuestObject(env, 2, 3, false, false); // 3
							} else { // Spawn for other classes
								QuestService.addNewSpawn(320030000, player.getInstanceId(), 210753, (float) 260.12, (float) 234.93, (float) 216.00, (byte) 90); // Kuninasha
								return useQuestObject(env, 2, 13, false, false); // 13
							}
						}
					}
					break;
				case 700141: // Abyss Gate
					if (var == 4 || var == 14) {
						changeQuestStep(env, var, var, true);
						return playQuestMovie(env, 154);
					}
					break;
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203557) { // Suthran
				if (dialog == DialogAction.USE_OBJECT) {
					if (player.getPlayerClass() == PlayerClass.RIDER) { // Reward for Rider
						return sendQuestDialog(env, 2034);
					} else { // Reward for other classes
						return sendQuestDialog(env, 1352);
					}
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 2 && player.getWorldId() != 320030000) {
				changeQuestStep(env, var, 1, false);
				return true;
			} else if (var == 1 && player.getWorldId() == 320030000) {
				changeQuestStep(env, 1, 2, false); // 2
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		if (player.getPlayerClass() == PlayerClass.RIDER) { // Kill for Rider
			return defaultOnKillEvent(env, 233876, 3, 4); // 4
		} else { // Kill for other classes
			return defaultOnKillEvent(env, 210753, 13, 14); // 4
		}
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 2) {
				qs.setQuestVar(1);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (movieId == 154 && qs != null) {
			TeleportService2.teleportTo(env.getPlayer(), 220030000, 1683.2405f, 1757.608f, 259.44543f, (byte) 64, TeleportAnimation.FADE_OUT_BEAM);
			return true;
		}
		return false;
	}

	@Override
	public void onQuestCompletedEvent(QuestEnv env) {
		int[] quests = { 24015, 24014, 24013, 24012, 24011 };
		defaultOnQuestCompletedEvent(env, quests);
	}

	@Override
	public void onLevelChangedEvent(Player player) {
		int[] quests = { 24015, 24014, 24013, 24012, 24011 };
		defaultOnLevelChangedEvent(player, quests);
	}
}
