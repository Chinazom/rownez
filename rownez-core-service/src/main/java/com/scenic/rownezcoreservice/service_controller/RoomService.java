package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.RoomCategory;
import com.scenic.rownezcoreservice.model.RoomPOJO;
import com.scenic.rownezcoreservice.repository.RoomRepo;
import com.scenic.rownezcoreservice.service.storage.GcpFileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepo roomRepo;
    private final GcpFileStorage fileStorage;
    /*public void addRoom (RoomPOJO roomPOJO){
        Optional<Room> existingRoom = roomRepo.findById(roomPOJO.getNumber());
        if (existingRoom.isPresent()){
            throw new ApiException("Room with that name already exist",  HttpStatus.CONFLICT);
        }
        Room room = new Room(roomPOJO.getNumber(),roomPOJO.getCapacity(),roomPOJO.getCategory(),
                roomPOJO.getPrice(), roomPOJO.getFloor(),true, "www.to-be-implemented");
        roomRepo.save(room);

    }*/

    @Transactional
    public void updatePriceByCategory(RoomCategory category, Double price) {
        if (price.isNaN() || price <= 0.0){
            throw new ApiException("The room price is too low", HttpStatus.NOT_ACCEPTABLE);
        }
        roomRepo.updatePrice(price,category);
    }

    public void updateRoomDetails(RoomPOJO room) {
        Room existingRoom =roomRepo.findById(room.getNumber()).orElseThrow(() -> new ApiException("The room number does not exist", HttpStatus.NOT_FOUND));
       /* if (room.getFloor() != 0 || !room.isAvailability()){
            throw new ApiException( "Room floor/Availability cannot be updated", HttpStatus.BAD_REQUEST);
        }*/
        if (room.getCapacity() != 0){
            existingRoom.setCapacity(room.getCapacity());
        }
        if (room.getCategory() != null){
            existingRoom.setCategory(room.getCategory());
        }
        if (room.getPrice() > 0.0){
            existingRoom.setPrice(room.getPrice());
        }
        roomRepo.save(existingRoom);
    }
    public RoomRepo getRoomRepo() {
        return roomRepo;
    }

    public void addRoomByFloorAndCategory(RoomCategory category, int numOfPeople, int numOrRooms, List<String> nameOfRoomsList, double price, int floor, MultipartFile[] files) {

        if (numOrRooms <= 0){
            throw new ApiException( "Number of rooms required", HttpStatus.BAD_REQUEST);
        }
        if (numOrRooms != nameOfRoomsList.size()){
            throw new ApiException( "check that the number of rooms correspond to number of names provided", HttpStatus.BAD_REQUEST);
        }
        String roomImageDirectory = "room-images" + "/" + category.name().toLowerCase();
        // enable this when the Google cloud billing is sorting
        /*for (MultipartFile file:  files){
            try {
                fileStorage.uploadFile(file, category.name().toLowerCase());
            } catch (FileStorageServiceException e) {
                throw new ApiException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }*/
        nameOfRoomsList.forEach(name -> {
            Optional<Room> existingRoom = roomRepo.findById(name);
            if (existingRoom.isPresent()){
                throw new ApiException("Room with that name already exist",  HttpStatus.CONFLICT);
            }
            Room room = new Room(name, numOfPeople, category,
                    price, floor,true, roomImageDirectory);
            roomRepo.save(room);
        });
    }

    public void updateRoomDetails(RoomCategory category, int numOfPeople, List<String> nameOfRoomsList, double price, int floor, MultipartFile[] files) {
        nameOfRoomsList.forEach(name -> {
            Room existingRoom =roomRepo.findById(name).orElseThrow(() -> new ApiException("The room number does not exist", HttpStatus.NOT_FOUND));
            //check if file is present to upload by first deleting the existing images
            /* enable this when the Google cloud billing is sorting
            if (files != null){
                //delete old images
                fileStorage.delFile(existingRoom.getStoragePath());
                for (MultipartFile file:  files){
                    try {
                        fileStorage.uploadFile(file, category.name().toLowerCase());
                    } catch (FileStorageServiceException e) {
                        throw new ApiException(e.getMessage(), HttpStatus.BAD_REQUEST);
                    }
                }
                existingRoom.setStoragePath("room-images" + "/" + category);
            }*/
            if (numOfPeople != 0){
                existingRoom.setCapacity(numOfPeople);
            }
            if (category != null){
                existingRoom.setCategory(category);
            }
            if (price >= 0.0){
                existingRoom.setPrice(price);
            }
            existingRoom.setFloor(floor);
            roomRepo.save(existingRoom);
        });
    }

    public Iterable<Room> getRoomsByCategoryAndAvailable(RoomCategory category, boolean availableRoom) {
        return getRoomRepo().findAllByCategoryAndAvailability(category,availableRoom);
    }
}
