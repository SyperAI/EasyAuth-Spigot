package ua.starman.easylogin.utils.translator;

import ua.starman.easylogin.EasyAuth;

import java.util.List;

public final class Translation {
    private final String section;
    private final Translator translator = EasyAuth.translator;

    public Translation(String section) {
        this.section = section + ".";
    }

    public String getString(String path) {
        return translator.getString(section + path);
    }

    public List<String> getStringList(String path) {
        return translator.getStringList(section + path);
    }
}
