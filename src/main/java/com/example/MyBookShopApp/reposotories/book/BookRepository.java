package com.example.MyBookShopApp.reposotories.book;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.data.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Book findBookById(int id);

    @Query("from Book")
    List<Book> customFindAllBooks();

    List<Book> findBooksByTitleContaining(String bookTitle);

    List<Book> findBooksByPriceOldBetween(Integer min, Integer max);

    List<Book> findBooksByPriceOldIs(Integer price);

    @Query("from Book where isBestseller=1")
    List<Book> getBestsellers();

    @Query(value = "SELECT * FROM books WHERE discount = (SELECT MAX(discount) FROM books)", nativeQuery = true)
    List<Book> getBooksWithMaxDiscount();

    Page<Book> findBooksByTitleContainingIgnoreCase(String bookTitle, Pageable nextPage);

    Book findBookBySlug(String slug);

    List<Book> findBooksBySlugIn(String[] slugs);

    Page<Book> findBooksByPubDateBetweenOrderByPubDateDesc(Date from, Date end, Pageable nextPage);

    Page<Book> findAllByOrderByRatingPopularityDesc(Pageable nextPage);

    Page<Book> findBooksByTagsTagName(String tag, Pageable nextPage);

    List<Book> findBooksByTagsTagName(String tag);

    List<Book> findBooksByGenresGenreName(String genre);

    List<Book> findBooksByUsersId(int userId);

    Page<Book> findBookByUsersIdAndBook2UsersTypeIdOrderByBook2UsersTimeAsc(int userId, int typeId, Pageable nextPage);

    Page<Book> findAllByOrderByPubDateAsc(Pageable nextPage);

    List<Book> findBooksByAuthorsSlug(String slug);

    Page<Book> findBookByAuthorsSlug(String slug, Pageable nextPage);

    Page<Book> findBooksByGenresGenreName(String genre, Pageable nextPage);
}
