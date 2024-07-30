package com.scenic.rownezcoreservice.service_controller;

import com.scenic.rownezcoreservice.entity.Food;
import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.model.FoodDTO;
import com.scenic.rownezcoreservice.repository.FoodRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

@Service
public class FoodService {

    private final FoodRepo foodRepository;

    @Autowired
    public FoodService(FoodRepo foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Transactional
    public void addFoodList(List<FoodDTO> foodDTOList) {
        if (foodDTOList == null){
            throw new ApiException("Food menu cannot be empty", HttpStatus.BAD_REQUEST);
        }
        for (FoodDTO foodDTO: foodDTOList) {
            Optional<Food> food = foodRepository.findByName(foodDTO.getName());
            if(food.isPresent()){
                throw new ApiException("Food item already exist please",HttpStatus.BAD_REQUEST);
            }
            if (foodDTO.getPrice() <=0 || foodDTO.getPrepTime() <= 0){
                throw new ApiException("Food price or prep time can not be zero",HttpStatus.BAD_REQUEST);
            }
            foodRepository.save(new Food(foodDTO.getName(), foodDTO.getPrice(),foodDTO.getCategory(),foodDTO.getPrepTime()));
        }
    }

    public boolean updateFood(Long id, FoodDTO foodDTO) {
        if (foodDTO == null ){
            throw new ApiException("Food menu cannot be empty", HttpStatus.BAD_REQUEST);
        }
        Optional<Food> food = foodRepository.findById(id);
        if (food.isEmpty()){
            return false;
        }
        food.get().setName(foodDTO.getName());
        food.get().setPrice(foodDTO.getPrice());
        food.get().setCategory(foodDTO.getCategory());
        food.get().setPrepTime(foodDTO.getPrepTime());
        foodRepository.save(food.get());
        return true;
    }
    @Transactional
    public boolean deleteFood(Long id) {
        Optional<Food> food = foodRepository.findById(id);
        if (food.isEmpty()){
            return false;
        }
        foodRepository.delete(food.get());
        return true;
    }

    public Map<String, List<Food>> getAllFood() {
        return foodRepository.findAll().stream()
                .collect(groupingBy(Food::getCategory));
    }
}

