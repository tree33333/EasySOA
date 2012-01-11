package org.easysoa.records.correlation;

// TODO : merge with TemplateField and add informations about param (form, path, query ...position..)
// to retrieve it more easily in the template builder.

public class CandidateField {
    
    // kind : id ?
    // annot : found in GET, count... ??

    private String kind;
    private String path;
    private String name;
    private String value;
    private String type;
    //private CandidateField finerCandidate;// ??
    //private String valueGetExpression;
    //private String valueSetExpression;
    
    public CandidateField(String path, String value) {
        this.kind = "json"; // "path", "content" ??
        this.path = path;
        this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
        this.value = value;
    }
    
    public CandidateField(String path, String value, String type) {
        this(path, value);
        this.type = type;
    }
    
    public String getValue(Object message) {
        return null;
    }
    //public void setValue(String value, Object message) {
    //    
    //}
    public void templatize(Object message) {
        
    }

    public String getKind() {
        return kind;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
    
    public String toString() {
        return "[" + this.getPath() + "=" + this.getValue() + "]";
    }
}
