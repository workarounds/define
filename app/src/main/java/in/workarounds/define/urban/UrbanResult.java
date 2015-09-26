package in.workarounds.define.urban;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UrbanResult
{
    private List<String> tags;
    @SerializedName("result_type")
    private String resultType;
    @SerializedName("list")
    private List<Meaning> meanings;
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

    public List<Meaning> getMeanings()
    {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings)
    {
        this.meanings = meanings;
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
