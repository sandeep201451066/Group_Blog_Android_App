package com.example.sandeepkumar.shareidea;

/**
 * Created by Sandeep Kumar on 2/16/2017.
 */

public class Blog {
    String image,post,title,username;
    Blog(){

    }
Blog(String image, String title, String post,String username){
    this.title=title;
    this.post=post;
    this.image=image;
    this.username=username;
}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public String getPost() {
        return post;
    }

    public String getTitle() {
        return title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
