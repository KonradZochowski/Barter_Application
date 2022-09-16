package org.studia.barterapplication.models;

/**
 * String with key is auxiliary class to hold post title and its id
 */
public class StringWithKey {
    String string;
    String key;

    public StringWithKey() {
    }

    public StringWithKey(String string, String key) {
        this.string = string;
        this.key = key;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return string;
    }
}
