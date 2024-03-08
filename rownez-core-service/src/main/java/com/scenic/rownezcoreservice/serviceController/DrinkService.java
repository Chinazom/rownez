package com.scenic.rownezcoreservice.serviceController;

import com.scenic.rownezcoreservice.entity.Drink;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.DrinkDTO;
import com.scenic.rownezcoreservice.repository.DrinkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

@Service
public class DrinkService {

    private final DrinkRepo drinkRepository;

    @Autowired
    public DrinkService(DrinkRepo drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    public void addDrink(List<DrinkDTO> drinkDTOs) {
        if (drinkDTOs == null){
            throw new ApiException("Drink menu cannot be empty", HttpStatus.BAD_REQUEST);
        }
        for (DrinkDTO drinkDTO: drinkDTOs) {
            Drink drink = new Drink(drinkDTO.getName(), drinkDTO.getPrice(),drinkDTO.getCategory());
            drinkRepository.save(drink);
        }
    }

    public boolean updateDrink(Long id, DrinkDTO drinkDTO) {
        if (drinkDTO == null ){
            throw new ApiException("Drink menu cannot be empty", HttpStatus.BAD_REQUEST);
        }
        Optional<Drink> drink = drinkRepository.findById(id);
        if (drink.isEmpty()){
            return false;
        }
        drink.get().setName(drinkDTO.getName());
        drink.get().setPrice(drinkDTO.getPrice());
        drink.get().setCategory(drinkDTO.getCategory());
        drinkRepository.save(drink.get());
        return true;
    }

    @Transactional
    public boolean deleteDrink(Long id) {
        Optional<Drink> drink = drinkRepository.findById(id);
        if (drink.isEmpty()){
            return false;
        }
        drinkRepository.delete(drink.get());
        return true;
    }

    public Map<String, List<Drink>> getAllDrinks() {
        return drinkRepository.findAll().stream()
                .collect(groupingBy(Drink::getCategory));
    }
}

