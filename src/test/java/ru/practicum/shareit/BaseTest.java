package ru.practicum.shareit;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {

    protected final String            xShareUserId = "X-Sharer-User-Id";
    protected final DateTimeFormatter formatter    = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    protected UserDto buildUserDto(Long id, String email, String name){
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setEmail(email);
        userDto.setName(name);
        return userDto;
    }

    protected static ItemDto buildItemDto(Long id, String name, String description, Boolean available){
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    protected Item buildItem(Long id, String name, String description, boolean available, User owner){
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return item;
    }

    protected static BookingDto buildBookingDto(Long id, Long itemId, Long bookerId, LocalDateTime start, LocalDateTime end, BookingState status){
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setBookerId(bookerId);
        bookingDto.setStatus(status);
        return bookingDto;
    }

    protected static Booking buildBooking(Long id, Item item, User booker, LocalDateTime start, LocalDateTime end, BookingState status){
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }


    protected User buildUser(Long id, String email, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    protected LocalDateTime getDateFromString(String dateString) {
        return LocalDateTime.parse(dateString, formatter);
    }

    protected CommentDto buildCommentDto(Long id, String text, LocalDateTime created, String name){
        CommentDto commentDto = new CommentDto();
        commentDto.setId(id);
        commentDto.setText(text);
        commentDto.setCreated(created);
        commentDto.setAuthorName(name);
        return commentDto;
    }

    protected ItemRequestDto buildItemRequestDto(Long id, Long requester, String description){
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(id);
        itemRequestDto.setRequester(requester);
        itemRequestDto.setDescription(description);
        return itemRequestDto;
    }
}
