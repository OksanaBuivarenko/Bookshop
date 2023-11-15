package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.book.Book;
import com.example.MyBookShopApp.reposotories.book.BookRepository;
import com.example.MyBookShopApp.data.Tag;
import com.example.MyBookShopApp.reposotories.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Service
public class TagService {
    public final TagRepository tagRepository;
    public final BookRepository bookRepository;

    public List<Tag> getTagData() {
        return tagRepository.findAll();
    }

    public Page<Book> getPageBooksOfTag(String tag, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByTagsTagName(tag, nextPage);
    }

    public List<Tag> classForTagCount() {
        int maxTagUsages = tagRepository.findFirstByOrderByUsagesDesc().getUsages();
        int tagXS = maxTagUsages / 4;
        int tagSM = maxTagUsages / 4 * 2;
        int tagMD = maxTagUsages / 4 * 3;
        List<Tag> classList = new ArrayList<>();
        for (Tag tag : getTagData()) {
            if (tag.getUsages() <= tagXS) {
                tag.setClassTag("Tag_xs");
            }
            if (tag.getUsages() > tagXS && tag.getUsages() <= tagSM) {
                tag.setClassTag("Tag_sm");
            }
            if (tag.getUsages() > tagSM && tag.getUsages() <= tagMD) {
                tag.setClassTag("Tag_md");
            }
            if (tag.getUsages() > tagMD) {
                tag.setClassTag("Tag_lg");
            }
            classList.add(tag);
        }
        return classList;
    }

    public void addTagUsages(String tagName) {
        Tag tag = tagRepository.findTagByTagName(tagName);
        tag.setUsages(tag.getUsages() + 1);
        tagRepository.save(tag);
    }
}
