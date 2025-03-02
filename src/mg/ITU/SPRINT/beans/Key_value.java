package mg.ITU.SPRINT.beans;

import java.util.ArrayList;

public class Key_value {
    
    String key;
    String value;

    public Key_value (String key, String valeur) {
        this.value = valeur;
        this.key = key;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "="+ value;
    }
    public static Key_value get_param (String key, ArrayList <Key_value> list_keyvalue) {
        Key_value kv = null;
        if (list_keyvalue != null) {
            for (Key_value key_value : list_keyvalue) {
                if (key_value.key.equals(key)) {
                    kv = key_value;
                    break;
                }
            }
        }
        return kv;
    }
}
