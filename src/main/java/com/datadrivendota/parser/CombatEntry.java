package com.datadrivendota.parser;

import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.wire.common.proto.DotaUserMessages;

/**
 * Created by ben on 8/4/16.
 */
public class CombatEntry {
    public Integer tick_time;
    public Integer offset_time;
    public String type;
    public String subtype;
    public Integer team;
    public String key;
    public Integer value;
    public Integer player_slot;

    public String attacker_name;
    public Boolean attacker_illusion;
    public Boolean attacker_hero;
    public String attacker_source;

    public String target;
    public String target_source;
    public Boolean target_hero;
    public Boolean target_illusion;

    public String inflictor;
    public Integer gold_reason;
    public Integer xp_reason;
    public Integer gold;
    public Integer lh;
    public Integer xp;
    public Float x;
    public Float y;

    public CombatEntry(Integer tick_time){
        this.tick_time = tick_time;
    }

    public void define(CombatLogEntry cle){
        switch (cle.getType()) {

            case DOTA_COMBATLOG_DAMAGE:
                //damage
                this.type = "damage";
                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                this.value = cle.getValue();
                break;

            case DOTA_COMBATLOG_HEAL:
                //healing

                this.type = "healing";
                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();


                this.value = cle.getValue();
                break;

            case DOTA_COMBATLOG_MODIFIER_ADD:
                //gain buff/debuff
                // this.attacker_name = cle.getAttackerName();
                // this.attacker_illusion = cle.isAttackerIllusion();
                // this.attacker_hero = cle.isAttackerHero();
                // this.attacker_source = cle.getDamageSourceName(); // ??

                // this.target = cle.getTargetName();
                // this.target_illusion = cle.isTargetIllusion();
                // this.target_hero = cle.isTargetHero();
                // this.target_source = cle.getTargetSourceName();

                // this.inflictor = cle.getInflictorName();
                // this.x = cle.getLocationX();
                // this.y = cle.getLocationY();

                // this.type = "modifier";
                // data.combatadd(entry);
                break;
            case DOTA_COMBATLOG_MODIFIER_REMOVE:
                // lose buff/debuff
                // No need for buffs atm.
                // this.type = "modifier_lost";
                // this.attacker_name = cle.getAttackerName();
                // this.attacker_illusion = cle.isAttackerIllusion();
                // this.attacker_hero = cle.isAttackerHero();
                // this.attacker_source = cle.getDamageSourceName(); // ??

                // this.target = cle.getTargetName();
                // this.target_illusion = cle.isTargetIllusion();
                // this.target_hero = cle.isTargetHero();
                // this.target_source = cle.getTargetSourceName();
                break;
            case DOTA_COMBATLOG_DEATH:
                //kill

                this.type = "kills";
                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                break;
            case DOTA_COMBATLOG_ABILITY:
                //ability use
                this.type = "ability_uses";

                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                break;
            case DOTA_COMBATLOG_ITEM:
                //item use
                this.type = "item_uses";

                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                break;
            case DOTA_COMBATLOG_GOLD:
                //gold gain/loss
                this.type = "gold_reasons";
                this.key = String.valueOf(cle.getGoldReason());
                this.value = cle.getValue();

                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                break;
            case DOTA_COMBATLOG_GAME_STATE:
                //state
                //System.err.println(cle.getValue());
                //if the value is out of bounds, just make it the value itself

                this.type = "state";

                switch(cle.getValue()){
                    case 1: this.key = "WAITING_FOR_LOADERS"; break;
                    case 2: this.key = "PICKING"; break;
                    case 3: this.key = "START"; break;
                    case 4: this.key = "PRE_GAME"; break;
                    case 5: this.key = "PLAYING"; break;
                    case 6: this.key = "POST_GAME"; break;
                    default: this.key = String.format("UNKNOWN (%d)", cle.getValue()); break;
                }
                this.value = Integer.valueOf(tick_time);
                break;
            case DOTA_COMBATLOG_XP:
                //xp gain
                this.type = "xp_reasons";
                this.key = String.valueOf(cle.getXpReason());
                this.value = cle.getValue();

                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                break;
            case DOTA_COMBATLOG_PURCHASE:
                //purchase
                this.type = "purchase";
                this.key = cle.getValueName();

                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName();

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                break;
            case DOTA_COMBATLOG_BUYBACK:
                //buyback
                this.type = "buyback_log";
                this.player_slot = cle.getValue();
                break;
            case DOTA_COMBATLOG_PLAYERSTATS:
                //player stats
                //TODO: don"t really know what this does, attacker seems to be a hero, target can be an item or hero?!
                //System.err.println(cle);
                // this.type = "player_stats";
                // this.attacker_name = cle.getAttackerName();
                // this.key = cle.getTargetName();
                // this.value = cle.getValue();
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
                this.type = "multi_kills";
                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                this.key = String.valueOf(cle.getValue());
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
                this.type = "kill_streaks";
                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                this.key = String.valueOf(cle.getValue());
                break;
            case DOTA_COMBATLOG_TEAM_BUILDING_KILL:
                // target_name: 286
                // value: 3
                // timestamp: 3049.8005
                // attacker_team: 0
                // target_team: 3

                //team building kill
                //System.err.println(cle);
                this.type = "team_building_kill";
                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                //0 is other?
                //1 is tower?
                //2 is rax?
                //3 is ancient
                this.key = String.valueOf(cle.getValue());
                break;
            case DOTA_COMBATLOG_FIRST_BLOOD:
                //first blood
                this.type="first_blood";
                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                break;
            case DOTA_COMBATLOG_MODIFIER_REFRESH:
                //modifier refresh
                this.type="modifier_refresh";

                this.attacker_name = cle.getAttackerName();
                this.attacker_illusion = cle.isAttackerIllusion();
                this.attacker_hero = cle.isAttackerHero();
                this.attacker_source = cle.getDamageSourceName(); // ??

                this.target = cle.getTargetName();
                this.target_illusion = cle.isTargetIllusion();
                this.target_hero = cle.isTargetHero();
                this.target_source = cle.getTargetSourceName();

                this.inflictor = cle.getInflictorName();
                this.x = cle.getLocationX();
                this.y = cle.getLocationY();

                break;

            default:
                DotaUserMessages.DOTA_COMBATLOG_TYPES type = cle.getType();
                if (type!=null){
                    this.type = type.name();
                    System.err.format("%s (%s): %s\n", type.name(), type.ordinal(), cle);
                }
                else{
                    System.err.format("unknown combat log type: %s\n", cle.getType());
                    System.err.println(cle);
                }
                break;
        }

    }

    // Helper to find the horn time
    public boolean isPlaying(){
        return this.type == "state" && this.key == "PLAYING";
    }

    // Helper to find the end time
    public boolean isEnd(){
        return this.type == "state" && this.key == "POST_GAME";
    }

    public Integer getTick_time() {
        return tick_time;
    }
    public Integer getPlayer_slot() {return player_slot;}
    public void setOffset_time(Integer offset) {this.offset_time = this.tick_time - offset;}

    @Override
    public String toString() {
        return "CombatEntry{" +
                "tick_time=" + tick_time +
                ", offset_time=" + offset_time +
                ", type='" + type + '\'' +
                ", subtype='" + subtype + '\'' +
                ", team=" + team +
                ", key='" + key + '\'' +
                ", value=" + value +
                ", player_slot=" + player_slot +
                ", attacker_name='" + attacker_name + '\'' +
                ", attacker_illusion=" + attacker_illusion +
                ", attacker_hero=" + attacker_hero +
                ", attacker_source='" + attacker_source + '\'' +
                ", target='" + target + '\'' +
                ", target_source='" + target_source + '\'' +
                ", target_hero=" + target_hero +
                ", target_illusion=" + target_illusion +
                ", inflictor='" + inflictor + '\'' +
                ", gold_reason=" + gold_reason +
                ", xp_reason=" + xp_reason +
                ", gold=" + gold +
                ", lh=" + lh +
                ", xp=" + xp +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
