package com.jvmrally.lambda.jdk;

import com.jvmrally.lambda.db.tables.pojos.Jeps;
import com.jvmrally.lambda.db.tables.records.JepsRecord;
import org.jsoup.select.Elements;

/**
 * JEP
 */
public class Jep {

    private int id;
    private String title;
    private JepType jepType;
    private Status status;
    private String release;
    private String component;
    private String url;

    public Jep(int id, String title, JepType type, Status status, String release, String component,
            String url) {
        this.id = id;
        this.title = title;
        this.jepType = type;
        this.status = status;
        this.release = release;
        this.component = component;
        this.url = url;
    }

    /**
     * Creates a Jep object from the database pojo
     * 
     * @param jep
     */
    public Jep(Jeps jep) {
        this.id = jep.getId();
        this.title = jep.getTitle();
        this.jepType = JepType.valueOf(jep.getJepType());
        this.status = Status.valueOf(jep.getJepStatus());
        this.release = jep.getRelease();
        this.component = jep.getComponent();
        this.url = jep.getJepUrl();
    }

    /**
     * Converts the Jep to a database record for batch inserting
     * 
     * @return
     */
    public JepsRecord toRecord() {
        return new JepsRecord(id, title, jepType.name(), status.name(), release, component, url);
    }

    /**
     * Creates a new Jep from the html source
     * 
     * @param columns
     * @return
     */
    public static Jep buildJepFromHtml(Elements columns) {
        JepType type = JepType.get(columns.get(0).text().charAt(0));
        Status status = Status.get(columns.get(1).select("span").attr("title").substring(8));
        String release = columns.get(2).text();
        String component = buildComponent(columns);
        int id = Integer.parseInt(columns.get(6).text());
        String title = columns.get(7).text();
        String url = "https://openjdk.java.net/jeps/" + columns.get(7).select("a").attr("href");
        return new Jep(id, title, type, status, release, component, url);
    }

    private static String buildComponent(Elements columns) {
        StringBuilder sb = new StringBuilder();
        sb.append(columns.get(3).text());
        sb.append(columns.get(4).text());
        sb.append(columns.get(5).text());
        return sb.toString();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the jepType
     */
    public JepType getJepType() {
        return jepType;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return the release
     */
    public String getRelease() {
        return release;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return component;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "JEP [component=" + component + ", id=" + id + ", jepType=" + jepType + ", release="
                + release + ", status=" + status + ", title=" + title + ", url=" + url + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((component == null) ? 0 : component.hashCode());
        result = prime * result + id;
        result = prime * result + ((jepType == null) ? 0 : jepType.hashCode());
        result = prime * result + ((release == null) ? 0 : release.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return prime * result + ((url == null) ? 0 : url.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Jep other = (Jep) obj;
        if (component == null) {
            if (other.component != null)
                return false;
        } else if (!component.equals(other.component))
            return false;
        if (id != other.id)
            return false;
        if (jepType != other.jepType)
            return false;
        if (release == null) {
            if (other.release != null)
                return false;
        } else if (!release.equals(other.release))
            return false;
        if (status != other.status)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }
}
