package com.chat.user;

/**
 * @author markhuang
 * @since 2020/8/27 17:53
 */
public class CurrentUser {

    private String id;

    private String name;

    private String avatarLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }
}
