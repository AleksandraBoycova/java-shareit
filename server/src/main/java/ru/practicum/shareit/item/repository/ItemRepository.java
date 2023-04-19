package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestIdIn(List<Long> requestId);

    Page<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(String nameLike, String descriptionLike, Pageable pageable);
}
