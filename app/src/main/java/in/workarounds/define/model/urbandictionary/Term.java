package in.workarounds.define.model.urbandictionary;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import in.workarounds.define.model.DictResult;

public class Term
{
    private List<String> tags;
    @SerializedName("result_type")
    private String resultType;
    @SerializedName("list")
    private List<Definition> definitions;
    private List<String> sounds;

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }

    public String getResultType()
    {
        return resultType;
    }

    public void setResultType(String resultType)
    {
        this.resultType = resultType;
    }

    public List<Definition> getDefinitions()
    {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions)
    {
        this.definitions = definitions;
    }

    public List<String> getSounds()
    {
        return sounds;
    }

    public void setSounds(List<String> sounds)
    {
        this.sounds = sounds;
    }

}
