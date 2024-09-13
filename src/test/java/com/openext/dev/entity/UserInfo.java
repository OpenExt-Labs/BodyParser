package com.openext.dev.entity;

import com.openext.dev.annotations.RequestParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInfo {
    @RequestParam(name = "name", required = true)
    private String name;

    @RequestParam(name = "age", required = true)
    private int age;

    @RequestParam(name = "hobbies", required = true, message = "Hobbies are required")
    private List<String> hobbies;

    @RequestParam(name = "favoriteNumbers", defaultValue = "1,2,3")
    private List<Integer> favoriteNumbers;

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobbies=" + hobbies +
                ", favoriteNumbers=" + favoriteNumbers +
                '}';
    }
}
