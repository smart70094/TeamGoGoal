package com.teamgogoal.utils;

public class ConfigUtils {
    private static final boolean IS_DEBUG = false;

    private static final String LOCAL_SERVER_URL = "3a80b685263f.ngrok.io";
    private static final String HEROKU_SERVER_URL = "teamgogoal.herokuapp.com";
    public static final String SERVER_URL = IS_DEBUG ? LOCAL_SERVER_URL : HEROKU_SERVER_URL;
}
