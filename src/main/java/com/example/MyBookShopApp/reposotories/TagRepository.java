package com.example.MyBookShopApp.reposotories;

import com.example.MyBookShopApp.data.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    List<Tag> findAll();

    Tag findTagByTagName(String tagName);

    Tag findFirstByOrderByUsagesDesc();

    @Query(value = "SELECT sum(usages) FROM Tag")
    int sumTagUsages();
}
