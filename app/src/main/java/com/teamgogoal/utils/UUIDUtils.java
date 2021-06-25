package com.teamgogoal.utils;

import java.util.UUID;

public class UUIDUtils {

    public static String getUUID() {
        UUID uuid  =  UUID.randomUUID();
        String id=uuid.toString();
        id=id.replace("-", "");

        return id;
    }
}
