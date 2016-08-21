package com.datadrivendota.parser;

import com.google.protobuf.ByteString;

import skadistats.clarity.Clarity;
import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.model.Entity;
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
    private FileBox filebox;

    public Parser(Replay replay) {
        this.replay = replay;
    }

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
            tick_time =  Math.round((float) Util.getEntityProperty(gamerulesproxy, "m_pGameRules.m_fGameTime", null));
        }

        if(next_time == -1000 && tick_time != -1000 ){
            next_time = tick_time;
        }

        // If we are on a tick that is at least next_time away and the player exists
        if(tick_time >= next_time && playerresource!=null){

            for (int i =0; i<10; i++) {
                StateEntry state = new StateEntry(i, tick_time);

                state.addPlayerState(playerresource, i);
                Entity side_resource;
                Integer lookup_i;
                if (i<5) {
                    side_resource = radiant;
                    lookup_i = i;
                }else{
                    side_resource = dire;
                    lookup_i = i-5;
                }

                state.addSideState(side_resource, lookup_i);

                int hero_handle = Util.getEntityProperty(playerresource, "m_vecPlayerTeamData.%i.m_hSelectedHero", i);

                Entity hero = ctx.getProcessor(Entities.class).getByHandle(hero_handle);
                if (hero != null) {
                    state.addHeroState(hero, ctx);
                    this.filebox.addSlotIndex(i, (Integer) Util.getEntityProperty(hero, "m_pEntity.m_nameStringableIndex", null));
                }
                this.filebox.addState(state);

            }

            // Don't log any more ticks until we are at least interval away.
            next_time = tick_time + interval;
        }

    }

    @OnStringTableEntry("EntityNames")
    public void onStringTableEntry(Context ctx, StringTable stringTable, int rowIndex, String key, ByteString bs) {
        this.filebox.addStringTableEntry(rowIndex, key);
    }

    @OnCombatLogEntry
    public void onCombatLogEntry(Context ctx, CombatLogEntry cle) {

        Integer time = Math.round(cle.getTimestamp());
        CombatEntry entry = new CombatEntry(time);
        entry.define(cle);
        this.filebox.addCombat(entry);

    }

    /**
     *
     * @param filebox the place to stuff data that will get handled & uploaded
     * @return
     */
    public FileBox run(FileBox filebox) throws IOException {
        // Setup business logic from original com.datadrivendota.parser.Parser.run here.
        // Mostly calling `CDemoFileInfo info = Clarity.infoForFile(replay.getFilename());`

        this.filebox = filebox;
        System.out.println("Parsing");
        long tStart = System.currentTimeMillis();
        String filename = this.replay.getFilename();
        new SimpleRunner(new MappedFileSource(filename)).runWith(this);
        long tMatch = System.currentTimeMillis() - tStart;

        CDemoFileInfo info = Clarity.infoForFile(filename);

        Pattern p = Pattern.compile("(?<=game_winner: )\\d");
        Matcher m = p.matcher(info.toString());

        if (m.find()) {
            filebox.addWinner(m.group(0));
        }
        System.out.println("Done parsing");

        return this.filebox;
    }

}
