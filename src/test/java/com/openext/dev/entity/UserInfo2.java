package com.openext.dev.entity;

import com.openext.dev.annotations.RequestParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInfo2 {
    @RequestParam(name = "name", defaultValue = "Alice")
    private String name;

    @RequestParam(name = "age", required = true)
    private int age;

    @RequestParam(name = "hobbies", required = true, message = "Hobbies are required")
    private List<String> hobbies;

    @RequestParam(name = "favoriteNumbers", defaultValue = "1,3,3")
    private List<Integer> favoriteNumbers;

    @Override
    public String toString() {
        return "UserInfo2{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobbies=" + hobbies +
                ", favoriteNumbers=" + favoriteNumbers +
                '}';
    }
}
