package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Sql("/data.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentRepositoryTest extends BaseTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void save() {
        Comment comment = new Comment();
        comment.setAuthor(buildUser(2L, "email", "name"));
        comment.setText("new comment");
        Comment savedItemRequest = commentRepository.save(comment);
        assertEquals(4, savedItemRequest.getId());
    }

    @Test
    void findAll() {
        List<Comment> comments = commentRepository.findAll();
        assertEquals(3, comments.size());
    }

    @Test
    void findById() {
        Optional<Comment> itemRequest = commentRepository.findById(2L);
        assertTrue(itemRequest.isPresent());
        assertEquals("Add comment from user2", itemRequest.get().getText());
    }
}