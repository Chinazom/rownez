package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.RoomOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomOrderRepo extends JpaRepository<RoomOrder, UUID> {
}

