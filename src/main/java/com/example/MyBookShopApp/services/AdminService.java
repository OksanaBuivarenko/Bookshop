package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.data.BookstoreUser;
import com.example.MyBookShopApp.data.Genre;
import com.example.MyBookShopApp.data.Tag;
import com.example.MyBookShopApp.data.book.*;
import com.example.MyBookShopApp.dto.BookDto;
import com.example.MyBookShopApp.reposotories.AuthorRepository;
import com.example.MyBookShopApp.reposotories.BookstoreUserRepository;
import com.example.MyBookShopApp.reposotories.GenreRepository;
import com.example.MyBookShopApp.reposotories.TagRepository;
import com.example.MyBookShopApp.reposotories.book.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@AllArgsConstructor
@Slf4j
@Service
public class AdminService {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;
    private final AuthorRepository authorRepository;
    private final BookRatingRepository bookRatingRepository;
    private final Book2AuthorRepository book2AuthorRepository;
    private final Book2GenreRepository book2GenreRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookstoreUserRepository bookstoreUserRepository;
    private final Book2UserRepository book2UserRepository;
    private final MyService myService;
    private final BookRatingGradeRepository bookRatingGradeRepository;

    public Book createBook(BookDto bookDto, String genre) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setImage(bookDto.getImage());
        book.setDescription(bookDto.getDescription());
        book.setIsBestseller(bookDto.getBestseller());
        book.setPrice((double) bookDto.getPrice());
        book.setPriceOld(bookDto.getDiscount());
        book.setPubDate(new Date());
        book.setSlug(String.valueOf(book.hashCode()));
        book.setRatingPopularity(0);

        List<Genre> genreList = new ArrayList<>();
        genreList.add(genreRepository.findGenreByGenreName(genre));
        book.setGenres(genreList);
        book.setTags(getTagList(bookDto.getTag()));
        bookRepository.save(book);
        log.info("Book with title " + book.getTitle() + " successfully created");

        BookRating bookRating = new BookRating();
        bookRating.setBook(book);
        bookRatingRepository.save(bookRating);
        log.info("BookRating successfully created");

        for (int i = 0; i < 5; i++) {
            BookRatingGrade  bookRatingGrade = new BookRatingGrade();
            bookRatingGrade.setBookRating(bookRating);
            bookRatingGradeRepository.save(bookRatingGrade);
        }

        List<Author> authors = getAuthorList(bookDto.getAuthor());
        createBook2AuthorFromList(book, authors);
        return book;
    }

    public void removeBook(String slug) {
        Book book = bookRepository.findBookBySlug(slug);
        String title = book.getTitle();
        bookRepository.delete(book);
        log.info("Book with title " + title + " successfully removed");
    }

    public Book changeBook(Book bookDto) {
        Book book = bookRepository.findBookBySlug(bookDto.getSlug());
        if (!bookDto.getTitle().equals(book.getTitle())) {
            book.setTitle(bookDto.getTitle());
        }
        if (!bookDto.getDescription().equals(book.getDescription())) {
            book.setDescription(bookDto.getDescription());
        }
        if (!bookDto.getPrice().equals(book.getPrice())) {
            book.setPrice(bookDto.getPrice());
        }
        if (!bookDto.getPriceOld().equals(book.getPriceOld())) {
            book.setPriceOld(bookDto.getPriceOld());
        }
        if (!bookDto.getIsBestseller().equals(book.getIsBestseller())) {
            book.setIsBestseller(bookDto.getIsBestseller());
        }
        if (!bookDto.getAuthorsDto().equals(book.authorsTostring())) {
            changeBookAuthorsList(book, bookDto.getAuthorsDto());
        }
        if (!bookDto.getTagsDto().equals(book.tagsTostring())) {
            changeBookTagsList(book, bookDto);
        }
        if (!bookDto.getGenresDto().equals(book.genresTostring())) {
            changeBookGenresList(book, bookDto);
        }
        bookRepository.save(book);
        return book;
    }

    private void changeBookGenresList(Book book, Book bookDto) {
        List<Book2Genre> book2GenresList = book2GenreRepository.findAllByBookId(book.getId());
        for (Book2Genre book2Genre : book2GenresList) {
            book2GenreRepository.delete(book2Genre);
        }
        List<Genre> genres = getGenreList(bookDto.getGenresDto());
        createBook2GenresFromList(book, genres);
    }

    private void createBook2GenresFromList(Book book, List<Genre> genres) {
        for (int i = 0; i < genres.size(); i++) {
            Book2Genre book2Genre = new Book2Genre();
            book2Genre.setBookId(book.getId());
            book2Genre.setGenreId(genres.get(i).getId());
            book2GenreRepository.save(book2Genre);
            log.info("New Book2Genre  successfully created");
        }
    }

    private List<Genre> getGenreList(String genresList) {
        List<Genre> genreList = new ArrayList<>();
        String[] genres = genresList.split("[,;]+");
        for (int i = 0; i < genres.length; i++) {
            Genre genre = genreRepository.findGenreByGenreName(genres[i].trim());
            if (genre != null) {
                genreList.add(genre);
            } else {
                Genre newGenre = new Genre();
                newGenre.setGenreName(genres[i]);
                newGenre.setSlug(String.valueOf(newGenre.hashCode()));
                genreRepository.save(newGenre);
                log.info("Genre with name " + newGenre.getGenreName() + " successfully created");
                genreList.add(newGenre);
            }
        }
        return genreList;
    }

    private void changeBookTagsList(Book book, Book bookDto) {
        book.getTags().clear();
        book.setTags(getTagList(bookDto.getTagsDto()));
    }

    private void changeBookAuthorsList(Book book, String authorsToString) {
        List<Book2Author> book2AuthorList = book2AuthorRepository.findAllByBookId(book.getId());
        for (Book2Author book2Author : book2AuthorList) {
            book2AuthorRepository.delete(book2Author);
        }
        List<Author> authors = getAuthorList(authorsToString);
        createBook2AuthorFromList(book, authors);
    }

    public void createBook2AuthorFromList(Book book, List<Author> authors) {
        for (int i = 0; i < authors.size(); i++) {
            Book2Author book2Author = new Book2Author();
            book2Author.setBook(book);
            book2Author.setAuthor(authors.get(i));
            book2Author.setSortIndex(i + 1);
            book2AuthorRepository.save(book2Author);
            log.info("New Book2Author  successfully created");
        }
    }

    private List<Author> getAuthorList(String authorsList) {
        List<Author> authorList = new ArrayList<>();
        String[] authors = authorsList.split("[,;]+");
        for (int i = 0; i < authors.length; i++) {
            Author author = authorRepository.findAuthorByName(authors[i].trim());
            if (author != null) {
                authorList.add(author);
            } else {
                Author newAuthor = new Author();
                newAuthor.setName(authors[i]);
                newAuthor.setSlug(String.valueOf(newAuthor.hashCode()));
                authorRepository.save(newAuthor);
                log.info("Author with name " + newAuthor.getName() + " successfully created");
                authorList.add(newAuthor);
            }
        }
        return authorList;
    }

    public List<Tag> getTagList(String tagDtoList) {
        List<Tag> tagList = new ArrayList<>();
        String[] tags = tagDtoList.split("[,;\\s]+");
        for (int i = 0; i < tags.length; i++) {
            Tag tag = tagRepository.findTagByTagName(tags[i].trim());
            if (tag != null) {
                tagList.add(tag);
            } else {
                Tag newTag = new Tag();
                newTag.setTagName(tags[i]);
                tagRepository.save(newTag);
                log.info("Tag with name " + newTag.getTagName() + " successfully created");
                tagList.add(newTag);
            }
        }
        return tagList;
    }

    public Author changeAuthor(Author authorDto) {
        Author author = authorRepository.findAuthorBySlug(authorDto.getSlug());
        if (!authorDto.getName().equals(author.getName())) {
            author.setName(authorDto.getName());
        }
        if (!authorDto.getDescription().equals(author.getDescription())) {
            author.setDescription(authorDto.getDescription());
        }
        authorRepository.save(author);
        log.info("Author with id " + author.getId() + " successfully changed");
        return author;
    }

    public void removeReview(int reviewId) {
        BookReview review = bookReviewRepository.findBookReviewById(reviewId);
        bookReviewRepository.delete(review);
        log.info("Review with id " + reviewId + " successfully delete");
    }

    public void blockUser(int userId) {
        BookstoreUser user = bookstoreUserRepository.findBookstoreUserById(userId);
        user.setIsBlocked(1);
        bookstoreUserRepository.save(user);
        log.info("User with id is blocked");
    }

    public void addPromoBookAllUsers(String slug) {
        Book book = bookRepository.findBookBySlug(slug);
        List<BookstoreUser> userList = bookstoreUserRepository.findAll();
        addPromoBook(book, userList);
    }

    public void addPromoBookByBookCount(String slug, int bookCount) {
        Book book = bookRepository.findBookBySlug(slug);
        List<BookstoreUser> userListForBookCount = new ArrayList<>();
        List<BookstoreUser> userList = bookstoreUserRepository.findAll();
        for (BookstoreUser user : userList) {
            int userBookCount = myService.getPaidBookList(user).size() + myService.getArchiveBookList(user).size();
            if (userBookCount >= bookCount) {
                userListForBookCount.add(user);
            }
        }
        if (userListForBookCount.size() == 0) {
            log.info("Пользователей с таким количеством книг не найдено");
        } else {
            addPromoBook(book, userListForBookCount);
        }
    }

    public void addPromoBookByCheckAmount(String slug, int checkAmount) {
        Book book = bookRepository.findBookBySlug(slug);
        List<BookstoreUser> userListForBookCount = new ArrayList<>();
        List<BookstoreUser> userList = bookstoreUserRepository.findAll();
        for (BookstoreUser user : userList) {
            List<Book> userBooks = new ArrayList<>();
            userBooks.addAll(myService.getPaidBookList(user));
            userBooks.addAll(myService.getArchiveBookList(user));
            int userCheckAmount = 0;
            for (Book userBook : userBooks) {
                userCheckAmount += userBook.discountPrice();
            }
            if (userCheckAmount >= checkAmount) {
                userListForBookCount.add(user);
            }
        }
        if (userListForBookCount.size() == 0) {
            log.info("Пользователей с такой суммой покупок не найдено");
        } else {
            addPromoBook(book, userListForBookCount);
        }
    }

    public void addPromoBook(Book book, List<BookstoreUser> userList) {
        for (BookstoreUser user : userList) {
            Book2User book2User = book2UserRepository.findBook2UserByBookIdAndUserId(book.getId(), user.getId());
            if (book2User == null) {
                Book2User newBook2User = new Book2User();
                newBook2User.setUser(user);
                newBook2User.setBook(book);
                newBook2User.setTime(LocalDateTime.now());
                newBook2User.setTypeId(6);
                book2UserRepository.save(newBook2User);
                log.info("New Book2User successfully created");
            } else {
                if (book2User.getTypeId() == 3 || book2User.getTypeId() == 3) {
                    log.info("Книга " + book.getTitle() + "уже есть у пользователя " + user.getName());
                } else {
                    book2User.setTypeId(6);
                    book2UserRepository.save(book2User);
                    log.info("Для книги " + book.getTitle() + " был изменен статус у пользователя  " + user.getName());
                }
            }
        }
    }
}
