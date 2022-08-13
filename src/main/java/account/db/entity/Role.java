package account.db.entity;

import java.util.HashMap;
import java.util.Map;

public enum Role {
    ROLE_USER("USER",1),
    ROLE_ACCOUNTANT("ACCOUNTANT",1),
    ROLE_ADMINISTRATOR("ADMINISTRATOR",0),

    ROLE_AUDITOR("AUDITOR",1);



    public final String label;
    private final int group;

    public int getGroup(){
        return group;
    }

    Role(String label,int group) {
        this.label = label;
        this.group = group;
    }
    private static final Map<String, Role> BY_LABEL = new HashMap<>();

    static {
        for (Role e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    public static Role valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

}
