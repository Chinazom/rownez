package com.scenic.rownezcoreservice.repository;

import com.scenic.rownezcoreservice.entity.RoomToBeCleaned;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoomToBeCleanedRepo extends CrudRepository<RoomToBeCleaned, Integer> {

}
