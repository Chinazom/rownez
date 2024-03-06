package com.scenic.rownezcoreservice.serviceController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.scenic.rownezcoreservice.entity.LaundryCart;
import com.scenic.rownezcoreservice.entity.LaundryItem;
import com.scenic.rownezcoreservice.entity.Room;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.ItemPojo;
import com.scenic.rownezcoreservice.model.ItemType;
import com.scenic.rownezcoreservice.model.LaundryCartPojo;
import com.scenic.rownezcoreservice.repository.LaundryCartRepo;
import com.scenic.rownezcoreservice.repository.LaundryItemRepo;
import com.scenic.rownezcoreservice.repository.RoomRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LaundryService {
    private final LaundryItemRepo laundryItemRepo;
    private final LaundryCartRepo laundryCartRepo;
    private final RoomRepo roomRepo;

    public LaundryService(LaundryItemRepo laundryItemRepo, LaundryCartRepo laundryCartRepo, RoomRepo roomRepo) {
        this.laundryItemRepo = laundryItemRepo;
        this.laundryCartRepo = laundryCartRepo;
        this.roomRepo = roomRepo;
    }

    public Iterable<LaundryItem> getAllLaundries() {
        return laundryItemRepo.findAll();
    }
    public void updateLaundryItemPrice (ItemType item, double cost){
        Optional<LaundryItem> laundryItem = laundryItemRepo.findById(item);
        if (laundryItem.isEmpty()){
            throw new ApiException("Laundry Item not found");
        }
        laundryItem.get().setCost(cost);
        laundryItemRepo.save(laundryItem.get());
    }

    public void addLaundryToCart(String roomNumber, LaundryCartPojo cart) {
        Room room =roomRepo.findById(roomNumber).orElseThrow(() -> new ApiException("The room number does not exist", HttpStatus.NOT_FOUND));
        // check for existing cart
        Optional<LaundryCart> laundryCartOptional = laundryCartRepo.findById(roomNumber);
        // create new cart when no cart is found
        if(laundryCartOptional.isEmpty()){
            laundryCartRepo.save(new LaundryCart(room.getRoomNumber(), cart.getItems().toString()));
        }else {
            // update existing cart
            try {

                List<String> updatedItems = cart.getItems();
                ObjectMapper mapper = new ObjectMapper();
                List<ItemPojo> oldCartItem = mapper.readValue(laundryCartOptional.get().getItems(), TypeFactory.defaultInstance().constructCollectionType(List.class, ItemPojo.class));
                for(ItemPojo itemPojo: oldCartItem){
                    String itemStr = mapper.writeValueAsString(itemPojo);
                    updatedItems.add(itemStr);
                }
                laundryCartOptional.get().setItems(updatedItems.toString());
                laundryCartRepo.save(laundryCartOptional.get());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }

    }
    @Transactional
    public void updateLaundryToCart(String roomNumber, LaundryCartPojo cart) {
        LaundryCart laundryCart = laundryCartRepo.findById(roomNumber).orElseThrow(()-> new ApiException("Laundry basket is empty", HttpStatus.NOT_FOUND));
        laundryCartRepo.updateByItemsAndRoomNumber(cart.getItems().toString(), laundryCart.getRoomNumber());
    }
    public Optional<LaundryCart> getLaundryToCart(String roomNumber) {
        return laundryCartRepo.findById(roomNumber);
    }
    @Transactional
    public void clearLaundryCart(String roomNumber) {
        LaundryCart cart = laundryCartRepo.findById(roomNumber).orElseThrow(()-> new ApiException("Laundry basket is empty", HttpStatus.NOT_FOUND));
        laundryCartRepo.deleteById(cart.getRoomNumber());
    }
}
