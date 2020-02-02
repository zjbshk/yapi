package cn.infomany.model;

public class YapiProjectInfo {

    private Integer _id;
    private String name;
    private String basepath;
    private String project_type;

    private Integer uid;
    private Integer group_id;
    private Integer add_time;
    private Integer up_time;

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasepath() {
        return basepath;
    }

    public void setBasepath(String basepath) {
        this.basepath = basepath;
    }

    public String getProject_type() {
        return project_type;
    }

    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Integer getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Integer add_time) {
        this.add_time = add_time;
    }

    public Integer getUp_time() {
        return up_time;
    }

    public void setUp_time(Integer up_time) {
        this.up_time = up_time;
    }
}
