package VotazioneFotografica.Model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ControlSquadra {
    private String username;
    private String realname;
    private boolean hasUploaded;
    private String path;
    private boolean hasVoted;
    private String istituto;
    private HashMap<String,Float> votiGiudici;
    private int totaleDaSquadre;

    public ControlSquadra(String username, String realname, boolean hasUploaded, String path, boolean hasVoted, String istituto, HashMap<String, Float> votiGiudici, int totaleDaSquadre) {
        this.username = username;
        this.realname = realname;
        this.hasUploaded = hasUploaded;
        this.path = path;
        this.hasVoted = hasVoted;
        this.istituto = istituto;
        this.votiGiudici = votiGiudici;
        this.totaleDaSquadre = totaleDaSquadre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public boolean isHasUploaded() {
        return hasUploaded;
    }

    public void setHasUploaded(boolean hasUploaded) {
        this.hasUploaded = hasUploaded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getIstituto() {
        return istituto;
    }

    public void setIstituto(String istituto) {
        this.istituto = istituto;
    }

    public HashMap<String, Float> getVotiGiudici() {
        return votiGiudici;
    }

    public void setVotiGiudici(HashMap<String, Float> votiGiudici) {
        this.votiGiudici = votiGiudici;
    }

    public int getTotaleDaSquadre() {
        return totaleDaSquadre;
    }

    public void setTotaleDaSquadre(int totaleDaSquadre) {
        this.totaleDaSquadre = totaleDaSquadre;
    }

    public float getMediaGiudici() {
        Collection<Float> values=votiGiudici.values();
        float media=0;
        int count=0;
        for(float i:values) {
            media+=i;
            count++;
        }
        if(count==0) return 0;
        else return media/count;
    }

    public float getSommaGiudici() {
        Collection<Float> values=votiGiudici.values();
        float somma=0;
        for(float i:values) {
            somma+=i;
        }
        return somma;
    }

    @Override
    public String toString() {
        return "ControlSquadra{" +
                "username='" + username + '\'' +
                ", realname='" + realname + '\'' +
                ", hasUploaded=" + hasUploaded +
                ", path='" + path + '\'' +
                ", hasVoted=" + hasVoted +
                ", istituto='" + istituto + '\'' +
                ", votiGiudici=" + votiGiudici +
                ", totaleDaSquadre=" + totaleDaSquadre +
                '}';
    }
}