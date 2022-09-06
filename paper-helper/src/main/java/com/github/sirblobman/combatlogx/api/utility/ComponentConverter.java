package com.github.sirblobman.combatlogx.api.utility;

import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.adventure.adventure.text.serializer.gson.GsonComponentSerializer;

public final class ComponentConverter {
    public static Component normalToShaded(net.kyori.adventure.text.Component component) {
        String gson = normalToGSON(component);
        return gsonToShaded(gson);
    }

    public static net.kyori.adventure.text.Component shadedToNormal(Component component) {
        String gson = shadedToGSON(component);
        return gsonToNormal(gson);
    }

    public static String normalToGSON(net.kyori.adventure.text.Component component) {
        net.kyori.adventure.text.serializer.gson.GsonComponentSerializer normalSerializer =
                net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson();
        return normalSerializer.serialize(component);
    }

    public static net.kyori.adventure.text.Component gsonToNormal(String gson) {
        net.kyori.adventure.text.serializer.gson.GsonComponentSerializer normalSerializer =
                net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson();
        return normalSerializer.deserialize(gson);
    }

    public static String shadedToGSON(Component component) {
        GsonComponentSerializer shadedSerializer = GsonComponentSerializer.gson();
        return shadedSerializer.serialize(component);
    }

    public static Component gsonToShaded(String gson) {
        GsonComponentSerializer shadedSerializer = GsonComponentSerializer.gson();
        return shadedSerializer.deserialize(gson);
    }
}
