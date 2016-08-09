package com.datadrivendota.parser;

import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.Entities;
import skadistats.clarity.processor.runner.Context;

/**
 * Created by ben on 8/5/16.
 */
public class Util {
    public static String IdxToString(Integer idx){
        if(idx<10){
            return "000".concat(idx.toString());
        }
        else{
            return "00".concat(idx.toString());
        }
    }

    public static <T> T getEntityProperty(Entity e, String property, Integer idx) {
        if (e == null) {
            return null;
        }
        if (idx != null) {
            property = property.replace("%i", IdxToString(idx));
        }
        FieldPath fp = e.getDtClass().getFieldPathForName(property);
        return e.getPropertyForFieldPath(fp);
    }

    public static Integer getItem(Context ctx, Entity e, Integer idx){
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

}
