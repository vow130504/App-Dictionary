package Models;

import Models.Mean;
import Models.WType;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Word {
    private String word;
    private String meann;
    private String pronun;

    private final List<WType> types;


    public Word(String word, String meann, String pronun, List<WType> types) {
        this.word = word;
        this.meann = meann;
        this.pronun = pronun;
        this.types = types;
    }

    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public String getMeann() {
        return meann;
    }
    public void setMeann() { return meann; }
    public String getPronun() {
        return pronun;
    }
    public void setPronun(String pronun) {
        this.pronun = pronun;
    }
    public List<WType> getTypes() {
        return types;
    }
    public void setTypes(List<WType> types) {
        this.types = types;
    }

}
