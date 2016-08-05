package com.datadrivendota.parser;

import com.google.protobuf.ByteString;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import skadistats.clarity.Clarity;
import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.processor.entities.Entities;
import skadistats.clarity.processor.entities.UsesEntities;
import skadistats.clarity.processor.gameevents.OnCombatLogEntry;
import skadistats.clarity.processor.reader.OnTickStart;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.processor.stringtables.OnStringTableEntry;
import skadistats.clarity.source.MappedFileSource;
import skadistats.clarity.wire.common.proto.Demo.CDemoFileInfo;
import skadistats.clarity.wire.common.proto.DotaUserMessages;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The interface with the parsing library.
 *
 * Instead of the current terrible DS implementation, use the com.datadrivendota.parser.FileBox abstraction.
 *
 * Created by ben on 7/22/16.
 */
public class Parser {
    private Replay replay;
    private int tick;
    private int tick_time = -1000;

    private int interval = 1;
    private int next_time = -1000;

    public Parser(Replay replay) {
        this.replay = replay;
    }

    private final PeriodFormatter GAMETIME_FORMATTER = new PeriodFormatterBuilder()
            .minimumPrintedDigits(2)
            .printZeroAlways()
            .appendHours()
            .appendLiteral(":")
            .appendMinutes()
            .appendLiteral(":")
            .appendSeconds()
            .appendLiteral(".")
            .appendMillis3Digit()
            .toFormatter();

    public String IdxToString(Integer idx){
        if(idx<10){
            return "000".concat(idx.toString());
        }
        else{
            return "00".concat(idx.toString());
        }
    }

    public <T> T getEntityProperty(Entity e, String property, Integer idx) {
        if (e == null) {
            return null;
        }
        if (idx != null) {
            property = property.replace("%i", IdxToString(idx));
        }
        FieldPath fp = e.getDtClass().getFieldPathForName(property);
        return e.getPropertyForFieldPath(fp);
    }

    public Integer getItem(Context ctx, Entity e, Integer idx){
        if (e == null) {
            return null;
        }
        String item = "m_hItems.%i";
        int item_handle = e.getPropertyForFieldPath(
                e.getDtClass().getFieldPathForName(
                        item.replace("%i", IdxToString(idx))
                )
        );
        Entity item_entity = ctx.getProcessor(Entities.class).getByHandle(item_handle
        );
        if(item_entity!=null){

            int item_name_id = item_entity.getPropertyForFieldPath(
                    item_entity.getDtClass().getFieldPathForName(
                            "m_pEntity.m_nameStringableIndex"
                    )
            );
            return item_name_id;
        }
        return null;

    }

    // @OnEntitySpawned
    // public void onSpawned(Context ctx, Entity e) {

    //     String entity_name = e.getDtClass().getDtName();
    //     if (entity_name.toString().equals("CDOTA_NPC_Observer_Ward")|
    //         entity_name.toString().equals("CDOTA_NPC_Observer_Ward_TrueSight")
    //     ) {

    //     }
    // }

    // @OnEntityDied
    // public void onDied(Context ctx, Entity e) {

    //     String entity_name = e.getDtClass().getDtName();
    //     // System.out.printf("%06d: %s at index %d has spawned\n", ctx.getTick(), e.getDtClass().getDtName(), e.getIndex());
    //     if (entity_name.toString().equals("CDOTA_NPC_Observer_Ward")|
    //         entity_name.toString().equals("CDOTA_NPC_Observer_Ward_TrueSight")
    //     ) {

    //         if (entity_name.toString().equals("CDOTA_NPC_Observer_Ward")) {
    //             String type = "obs";
    //         } else{
    //             String type = "truesight";
    //         }

    //     }
    // }

    @OnTickStart
    @UsesEntities
    public void onTickStart(Context ctx, boolean synthetic) {

        // Setup
        tick = ctx.getTick();
        Entity gamerulesproxy = ctx.getProcessor(Entities.class).getByDtName("CDOTAGamerulesProxy");
        Entity dire = ctx.getProcessor(Entities.class).getByDtName("CDOTA_DataDire");
        Entity radiant = ctx.getProcessor(Entities.class).getByDtName("CDOTA_DataRadiant");
        Entity playerresource = ctx.getProcessor(Entities.class).getByDtName("CDOTA_PlayerResource");

        if (gamerulesproxy != null) {
            tick_time =  Math.round(
                    (float) getEntityProperty(
                            gamerulesproxy, "m_pGameRules.m_fGameTime", null
                    )
            );
        }

        if(next_time == -1000 && tick_time != -1000 ){
            next_time = tick_time;
        }

        // Conditional data lookup
        if(tick_time >= next_time && playerresource!=null){

            for (int i =0; i<10; i++) {
                StateEntry state = new StateEntry();

                state.player_slot = i;
                state.tick_time = tick_time;

                state.hero_id = getEntityProperty(
                        playerresource,
                        "m_vecPlayerTeamData.%i.m_nSelectedHeroID",
                        i
                );
                state.kills = getEntityProperty(
                        playerresource,
                        "m_vecPlayerTeamData.%i.m_iKills",
                        i
                );
                state.assists = getEntityProperty(
                        playerresource,
                        "m_vecPlayerTeamData.%i.m_iAssists",
                        i
                );
                state.deaths = getEntityProperty(
                        playerresource,
                        "m_vecPlayerTeamData.%i.m_iDeaths",
                        i
                );

                Entity side_resource;
                Integer lookup_i;
                if (i<5) {
                    side_resource = radiant;
                    lookup_i = i;
                }else{
                    side_resource = dire;
                    lookup_i = i-5;
                }

                state.total_earned_gold = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iTotalEarnedGold",
                        lookup_i
                );
                state.reliable_gold = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iReliableGold",
                        lookup_i
                );
                state.unreliable_gold = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iUnreliableGold",
                        lookup_i
                );
                state.xp = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iTotalEarnedXP",
                        lookup_i
                );
                state.shared_gold = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iSharedGold",
                        lookup_i
                );
                state.hero_kill_gold = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iHeroKillGold",
                        lookup_i
                );
                state.creep_kill_gold = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iCreepKillGold",
                        lookup_i
                );
                state.income_gold = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iIncomeGold",
                        lookup_i
                );
                state.denies = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iDenyCount",
                        lookup_i
                );
                state.last_hits = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iLastHitCount",
                        lookup_i
                );
                state.nearby_creep_deaths = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iNearbyCreepDeathCount",
                        lookup_i
                );
                state.misses = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iMissCount",
                        lookup_i
                );
                state.healing = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_fHealing",
                        lookup_i
                );
                state.tower_kills = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iTowerKills",
                        lookup_i
                );
                state.roshan_kills = getEntityProperty(
                        side_resource,
                        "m_vecDataTeam.%i.m_iRoshanKills",
                        lookup_i
                );


                int hero_handle = getEntityProperty(
                        playerresource,
                        "m_vecPlayerTeamData.%i.m_hSelectedHero",
                        i
                );

                Entity hero = ctx.getProcessor(
                        Entities.class
                ).getByHandle(hero_handle);

                if (hero != null) {
                    //System.err.println(e);
                    state.x = getEntityProperty(
                            hero,
                            "CBodyComponent.m_cellX",
                            null
                    );
                    state.y = getEntityProperty(
                            hero,
                            "CBodyComponent.m_cellY",
                            null
                    );

                    // System.out.println(getEntityProperty(
                    //     hero,
                    //     "m_nTotalDamageTaken",
                    //     null
                    // ));
                    state.health = getEntityProperty(
                            hero,
                            "m_iHealth",
                            null
                    );
                    state.max_health = getEntityProperty(
                            hero,
                            "m_iMaxHealth",
                            null
                    );
                    state.mana = getEntityProperty(
                            hero,
                            "m_flMana",
                            null
                    );
                    state.max_mana = getEntityProperty(
                            hero,
                            "m_flMaxMana",
                            null
                    );
                    state.max_mana = getEntityProperty(
                            hero,
                            "m_flMaxMana",
                            null
                    );

                    state.damage_max = getEntityProperty(
                            hero,
                            "m_iDamageMax",
                            null
                    ) ;
                    state.damage_min = getEntityProperty(
                            hero,
                            "m_iDamageMin",
                            null
                    ) ;

                    state.damage_bonus = getEntityProperty(
                            hero,
                            "m_iDamageBonus",
                            null
                    );
                    state.strength = getEntityProperty(
                            hero,
                            "m_flStrength",
                            null
                    );
                    state.agility = getEntityProperty(
                            hero,
                            "m_flAgility",
                            null
                    );
                    state.intelligence = getEntityProperty(
                            hero,
                            "m_flIntellect",
                            null
                    );
                    state.strength_total = getEntityProperty(
                            hero,
                            "m_flStrengthTotal",
                            null
                    );
                    state.agility_total = getEntityProperty(
                            hero,
                            "m_flAgilityTotal",
                            null
                    );
                    state.intelligence_total = getEntityProperty(
                            hero,
                            "m_flIntellectTotal",
                            null
                    );
                    state.respawn_time = getEntityProperty(
                            hero,
                            "m_flRespawnTime",
                            null
                    );
                    state.recent_damage = getEntityProperty(
                            hero,
                            "m_iRecentDamage",
                            null
                    );
                    state.armor = getEntityProperty(
                            hero,
                            "m_flPhysicalArmorValue",
                            null
                    );
                    state.magic_resist_pct = getEntityProperty(
                            hero,
                            "m_flMagicalResistanceValue",
                            null
                    );
                    state.lifestate = getEntityProperty(
                            hero,
                            "m_lifeState",
                            null
                    );

                    state.item_0 = getItem(ctx, hero, 0);
                    state.item_1 = getItem(ctx, hero, 1);
                    state.item_2 = getItem(ctx, hero, 2);
                    state.item_3 = getItem(ctx, hero, 3);
                    state.item_4 = getItem(ctx, hero, 4);
                    state.item_5 = getItem(ctx, hero, 5);

                }
                data.stateadd(state);

            }

            // Cleanup
            next_time = tick_time + interval;
        }

    }

    @OnStringTableEntry("EntityNames")
    public void onStringTableEntry(Context ctx, StringTable stringTable, int rowIndex, String key, ByteString bs) {
        StringTableEntry ste = new StringTableEntry();
        ste.idx = rowIndex;
        ste.value = key;
        data.strtbladd(ste);
    }

    @OnCombatLogEntry
    public void onCombatLogEntry(Context ctx, CombatLogEntry cle) {

        Integer time = Math.round(cle.getTimestamp());
        StateEntry entry = new StateEntry(time);
        switch (cle.getType()) {

            case DOTA_COMBATLOG_DAMAGE:
                //damage
                entry.type = "damage";
                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                entry.value = cle.getValue();
                data.combatadd(entry);
                break;

            case DOTA_COMBATLOG_HEAL:
                //healing

                entry.type = "healing";
                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();


                entry.value = cle.getValue();
                data.combatadd(entry);
                break;

            case DOTA_COMBATLOG_MODIFIER_ADD:
                //gain buff/debuff
                // entry.attacker_name = cle.getAttackerName();
                // entry.attacker_illusion = cle.isAttackerIllusion();
                // entry.attacker_hero = cle.isAttackerHero();
                // entry.attacker_source = cle.getDamageSourceName(); // ??

                // entry.target = cle.getTargetName();
                // entry.target_illusion = cle.isTargetIllusion();
                // entry.target_hero = cle.isTargetHero();
                // entry.target_source = cle.getTargetSourceName();

                // entry.inflictor = cle.getInflictorName();
                // entry.x = cle.getLocationX();
                // entry.y = cle.getLocationY();

                // entry.type = "modifier";
                // data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_MODIFIER_REMOVE:
                // lose buff/debuff
                // No need for buffs atm.
                // entry.type = "modifier_lost";
                // entry.attacker_name = cle.getAttackerName();
                // entry.attacker_illusion = cle.isAttackerIllusion();
                // entry.attacker_hero = cle.isAttackerHero();
                // entry.attacker_source = cle.getDamageSourceName(); // ??

                // entry.target = cle.getTargetName();
                // entry.target_illusion = cle.isTargetIllusion();
                // entry.target_hero = cle.isTargetHero();
                // entry.target_source = cle.getTargetSourceName();
                break;
            case DOTA_COMBATLOG_DEATH:
                //kill

                entry.type = "kills";
                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_ABILITY:
                //ability use
                entry.type = "ability_uses";

                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_ITEM:
                //item use
                entry.type = "item_uses";

                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_GOLD:
                //gold gain/loss
                entry.type = "gold_reasons";
                entry.key = String.valueOf(cle.getGoldReason());
                entry.value = cle.getValue();

                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_GAME_STATE:
                //state
                //System.err.println(cle.getValue());
                //if the value is out of bounds, just make it the value itself

                entry.type = "state";

                switch(cle.getValue()){
                    case 1: entry.key = "WAITING_FOR_LOADERS"; break;
                    case 2: entry.key = "PICKING"; break;
                    case 3: entry.key = "START"; break;
                    case 4: entry.key = "PRE_GAME"; break;
                    case 5: entry.key = "PLAYING"; break;
                    case 6: entry.key = "POST_GAME"; break;
                    default: entry.key = String.format("UNKNOWN (%d)", cle.getValue()); break;
                }
                entry.value = Integer.valueOf(time);
                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_XP:
                //xp gain
                entry.type = "xp_reasons";
                entry.key = String.valueOf(cle.getXpReason());
                entry.value = cle.getValue();

                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_PURCHASE:
                //purchase
                entry.type = "purchase";
                entry.key = cle.getValueName();

                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_BUYBACK:
                //buyback
                entry.type = "buyback_log";
                entry.slot = cle.getValue();
                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_PLAYERSTATS:
                //player stats
                //TODO: don"t really know what this does, attacker seems to be a hero, target can be an item or hero?!
                //System.err.println(cle);
                // entry.type = "player_stats";
                // entry.attacker_name = cle.getAttackerName();
                // entry.key = cle.getTargetName();
                // entry.value = cle.getValue();
                //data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_MULTIKILL:
                //multikill
                // DOTA_COMBATLOG_MULTIKILL (15): type: DOTA_COMBATLOG_MULTIKILL
                // target_name: 3
                // target_source_name: 2
                // attacker_name: 3
                // value: 3
                // timestamp: 3024.8489

                //System.err.println(cle);
                entry.type = "multi_kills";
                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                entry.key = String.valueOf(cle.getValue());
                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_KILLSTREAK:
                //killstreak
                // DOTA_COMBATLOG_KILLSTREAK (16): type: DOTA_COMBATLOG_KILLSTREAK
                // target_name: 3
                // target_source_name: 2
                // attacker_name: 3
                // value: 7
                // timestamp: 3024.8489
                //System.err.println(cle);
                entry.type = "kill_streaks";
                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                entry.key = String.valueOf(cle.getValue());
                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_TEAM_BUILDING_KILL:
                // target_name: 286
                // value: 3
                // timestamp: 3049.8005
                // attacker_team: 0
                // target_team: 3

                //team building kill
                //System.err.println(cle);
                entry.type = "team_building_kill";
                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                //0 is other?
                //1 is tower?
                //2 is rax?
                //3 is ancient
                entry.key = String.valueOf(cle.getValue());
                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_FIRST_BLOOD:
                //first blood
                entry.type="first_blood";
                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();


                data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_MODIFIER_REFRESH:
                //modifier refresh
                entry.type="modifier_refresh";

                entry.attacker_name = cle.getAttackerName();
                entry.attacker_illusion = cle.isAttackerIllusion();
                entry.attacker_hero = cle.isAttackerHero();
                entry.attacker_source = cle.getDamageSourceName(); // ??

                entry.target = cle.getTargetName();
                entry.target_illusion = cle.isTargetIllusion();
                entry.target_hero = cle.isTargetHero();
                entry.target_source = cle.getTargetSourceName();

                entry.inflictor = cle.getInflictorName();
                entry.x = cle.getLocationX();
                entry.y = cle.getLocationY();

                break;

            default:
                DotaUserMessages.DOTA_COMBATLOG_TYPES type = cle.getType();
                if (type!=null){
                    entry.type = type.name();
                    System.err.format("%s (%s): %s\n", type.name(), type.ordinal(), cle);
                    data.combatadd(entry);
                }
                else{
                    System.err.format("unknown combat log type: %s\n", cle.getType());
                    System.err.println(cle);
                }
                break;
        }

    }

    /**
     *
     * @param filebox the place to stuff data that will get handled & uploaded
     * @return
     */
    public FileBox run(FileBox filebox) throws IOException {
        // Setup business logic from original com.datadrivendota.parser.Parser.run here.
        // Mostly calling `CDemoFileInfo info = Clarity.infoForFile(replay.getFilename());`

        System.out.println("Parsing");
        long tStart = System.currentTimeMillis();
        String filename = this.replay.getFilename();
        new SimpleRunner(new MappedFileSource(filename)).runWith(this);
        long tMatch = System.currentTimeMillis() - tStart;

        CDemoFileInfo info = Clarity.infoForFile(filename);

        Pattern p = Pattern.compile("(?<=game_winner: )\\d");
        Matcher m = p.matcher(info.toString());

        if (m.find()) {
            filebox.addwinner(m.group(0));
        }
        System.out.println("Done parsing");

        return filebox;
    }

    // The various helper fns and @OnEntities etc logic here
    // Instead of the stateadd etc logic in the original, user handlers on Filebox to add info
}
